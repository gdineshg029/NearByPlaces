package com.java.nearbyplaces.ui.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.java.nearbyplaces.R
import com.java.nearbyplaces.data.model.PlaceDetails
import com.java.nearbyplaces.databinding.PlaceItemViewBinding

class PlacesAdapter(var placesList: MutableList<PlaceDetails>?) :
    RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    fun updateList(newPlacesList: MutableList<PlaceDetails>) {
        placesList?.clear()
        placesList = newPlacesList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = PlaceItemViewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        placesList?.let { holder.bind(it?.get(position)) }
    }

    override fun getItemCount() = (placesList?.size) ?: 0

    class PlaceViewHolder(val placeItemViewBinding: PlaceItemViewBinding) :
        RecyclerView.ViewHolder(placeItemViewBinding.rootLayout) {

        fun bind(placeDetails: PlaceDetails) {
            placeItemViewBinding.titleTextview.text = placeDetails.placeName
            placeItemViewBinding.addressTextview.text = placeDetails.placeAddress

            placeItemViewBinding.placeRatingTextview.text =
                "Place Rating : ${placeDetails.placeRating}"
            placeItemViewBinding.userRatingTextview.text =
                "User Reviews : ${placeDetails.userReviews}"

            Glide.with(itemView.context)
                .load(placeDetails.iconUrl)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher)
                .into(placeItemViewBinding.placeImageview)
        }
    }
}