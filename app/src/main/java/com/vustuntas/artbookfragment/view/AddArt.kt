package com.vustuntas.artbookfragment.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.vustuntas.artbookfragment.Art
import com.vustuntas.artbookfragment.R
import com.vustuntas.artbookfragment.databinding.FragmentAddArtBinding
import com.vustuntas.artbookfragment.model.ArtDB
import com.vustuntas.artbookfragment.model.ArtDao
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream

class AddArt : Fragment() {
    private lateinit var _binding : FragmentAddArtBinding
    private val binding get() = _binding.root

    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionResultLauncher: ActivityResultLauncher<String>

    private var imageBitmap : Bitmap? = null

    private lateinit var db : ArtDB
    private lateinit var artDao : ArtDao

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddArtBinding.inflate(inflater,container,false)

        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding.artSaveBtn.setOnClickListener { saveArt(view) }
        _binding.backMenu.setOnClickListener { backMenu(view) }
        _binding.artImage.setOnClickListener { selectImage(view) }
        registerLauncher()
        activity?.let {
            db = Room.databaseBuilder(it.applicationContext, ArtDB::class.java,"Art").build()
            artDao = db.artDao()
            arguments?.let {
                val destination = AddArtArgs.fromBundle(it).destination
                val id = AddArtArgs.fromBundle(it).artId
                if(destination.equals("recycler"))
                {
                    _binding.artImage.isEnabled = false
                    _binding.artSaveBtn.visibility = View.GONE
                    _binding.artName.isEnabled = false
                    _binding.artYear.isEnabled = false
                    _binding.artOwner.isEnabled = false

                    getArtInfo(id)
                }

            }

        }
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    fun saveArt(view:View){
        activity?.let {
            val artName = _binding.artName.text.toString()
            val artYear = _binding.artYear.text.toString()
            val artOwner = _binding.artOwner.text.toString()

            if (!artName.equals("") && !artYear.equals("") && !artOwner.equals("") && imageBitmap!=null){
                val kucultulmusBitmap = gorselKucult(imageBitmap!!,300)
                val outPutStream = ByteArrayOutputStream()
                kucultulmusBitmap.compress(Bitmap.CompressFormat.PNG,50,outPutStream)
                val byteArray = outPutStream.toByteArray()

                val artObject = Art(artName,artYear,artOwner,byteArray)

                compositeDisposable.add(
                    artDao.insertArt(artObject)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handlerResponse)
                )
            }
            else{
                Toast.makeText(it,"Lütfen Sanat Bilgilerini Girin",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handlerResponse(){
        activity?.let {
            val action = AddArtDirections.actionAddArtToArtRecyclerView()
            Navigation.findNavController(it, R.id.navHostFragment).navigate(action)
        }
    }
    fun selectImage(view : View){
        activity?.let {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if(ContextCompat.checkSelfPermission(it,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(it,Manifest.permission.READ_MEDIA_IMAGES)){
                        Snackbar.make(view,"İzin verilsin mi?",Snackbar.LENGTH_INDEFINITE).setAction("Evet"){
                            //izin iste
                            permissionResultLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }.show()
                    }
                    else{
                        //izin iste
                        permissionResultLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }
                else{
                    //izin verildi
                    val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }
            }
            else{
                if(ContextCompat.checkSelfPermission(it,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(it,Manifest.permission.READ_EXTERNAL_STORAGE)){
                        Snackbar.make(view,"İzin verilsin mi?",Snackbar.LENGTH_INDEFINITE).setAction("Evet"){
                            //izin iste
                            permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }.show()
                    }
                    else{
                        //izin iste
                        permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
                else{
                    //izin verildi
                    val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }
            }
        }
    }

    fun backMenu(view : View){
        val action = AddArtDirections.actionAddArtToArtRecyclerView()
        Navigation.findNavController(view).navigate(action)
    }

    private fun registerLauncher(){
        activity?.let {
            activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
                if(result.resultCode == RESULT_OK){
                    val imageIntent = result.data
                    if(imageIntent != null){
                        val imageUri = imageIntent.data
                        if(imageUri != null){
                            if(Build.VERSION.SDK_INT >= 28){
                                val source = ImageDecoder.createSource(it.contentResolver,imageUri)
                                imageBitmap = ImageDecoder.decodeBitmap(source)
                                _binding.artImage.setImageBitmap(imageBitmap)
                            }
                            else{
                                imageBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver,imageUri)
                                if (imageBitmap!=null){
                                    _binding.artImage.setImageBitmap(imageBitmap)
                                }
                            }
                        }
                    }
                }
            }

            permissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
                if(result){
                    val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }
                else{
                    Toast.makeText(it,"İzin verilmedi...",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun gorselKucult(secilenGorsel:Bitmap,maxBoyut:Int) : Bitmap{
        val bitmapOrani = secilenGorsel.width / secilenGorsel.height
        var width = secilenGorsel.width
        var height = secilenGorsel.height
        if(bitmapOrani>1){
            val kucultulmusYukseklik = (height*maxBoyut) / width
            height = kucultulmusYukseklik
            width = maxBoyut
        }
        else {
            val kucultulmusGenislik = (width * maxBoyut) / height
            width = kucultulmusGenislik
            height = maxBoyut
        }
        return Bitmap.createScaledBitmap(secilenGorsel,width,height,false)
    }

    private fun getArtInfo(artId : Int){
        compositeDisposable.add(
            artDao.getSelectArt(artId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleShowInfoArt)
        )
    }

    private fun handleShowInfoArt(art:Art){
        val imageByteArray = art.artImage
        println(art.artName)
        val bitmap = BitmapFactory.decodeByteArray(imageByteArray,0,imageByteArray.size,null)
        if(bitmap!= null){
            _binding.artImage.setImageBitmap(bitmap)
            _binding.artYear.setText( art.artYear.toString())
            _binding.artName.setText(art.artName.toString())
            _binding.artOwner.setText(art.artOwner.toString())
        }

    }
}