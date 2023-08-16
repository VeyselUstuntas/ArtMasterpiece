package com.vustuntas.artbookfragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.vustuntas.artbookfragment.Art
import com.vustuntas.artbookfragment.R
import com.vustuntas.artbookfragment.view.ArtRecyclerView
import com.vustuntas.artbookfragment.view.ArtRecyclerViewDirections

class ArtRecyclerAdapter(val artList: List<Art>) : RecyclerView.Adapter<ArtRecyclerAdapter.ArtVH>() {
    class ArtVH(itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row,parent,false)
        return ArtVH(itemView)
    }

    override fun onBindViewHolder(holder: ArtVH, position: Int) {
        val artObject = artList.get(position)
        holder.itemView.findViewById<TextView>(R.id.recyclerRow_artNameTextView).text = artObject.artName
        holder.itemView.setOnClickListener {
            val artId = artObject.ID
            val action = ArtRecyclerViewDirections.actionArtRecyclerViewToAddArt("recycler",artId)
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return artList.size
    }
}