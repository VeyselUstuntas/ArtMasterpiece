package com.vustuntas.artbookfragment.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.util.copy
import com.vustuntas.artbookfragment.Art
import com.vustuntas.artbookfragment.R
import com.vustuntas.artbookfragment.adapter.ArtRecyclerAdapter
import com.vustuntas.artbookfragment.databinding.FragmentArtRecyclerViewBinding
import com.vustuntas.artbookfragment.model.ArtDB
import com.vustuntas.artbookfragment.model.ArtDao
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.ArrayList

class ArtRecyclerView : Fragment() {
    private lateinit var _binding : FragmentArtRecyclerViewBinding
    private val binding get() = _binding.root

    private lateinit var artDatabase : ArtDB
    private lateinit var artDao : ArtDao
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArtRecyclerViewBinding.inflate(inflater,container,false)
        return binding.rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding.toolbar.inflateMenu(R.menu.menu_tool_bar)
        _binding.toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            artDatabase = Room.databaseBuilder(it.applicationContext,ArtDB::class.java,"Art").build()
            artDao = artDatabase.artDao()
        }

        getArt()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_tool_bar,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_toolBar_optionsMenu){
            activity?.let {
                val action = ArtRecyclerViewDirections.actionArtRecyclerViewToAddArt("options",0)
                Navigation.findNavController(it, R.id.navHostFragment).navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)

    }

    private fun getArt(){
        activity?.let {
            compositeDisposable.add(
                artDao.getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handlerResponse)
            )
        }
    }
    private fun handlerResponse(artArrayList: List<Art>){
        activity?.let {
            _binding.recyclerView.layoutManager = LinearLayoutManager(it)
            _binding.recyclerView.adapter = ArtRecyclerAdapter(artArrayList)
        }
    }
}