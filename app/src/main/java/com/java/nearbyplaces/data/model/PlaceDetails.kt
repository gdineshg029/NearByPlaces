package com.java.nearbyplaces.data.model

import com.google.gson.annotations.SerializedName

data class PlaceDetails(
        @SerializedName("formatted_address")
        val placeAddress:String,
        @SerializedName("name")
        val placeName:String,
        @SerializedName("rating")
        val placeRating:Double,
        @SerializedName("user_ratings_total")
        val userReviews:Int,
        @SerializedName("icon")
        val iconUrl:String,
)
