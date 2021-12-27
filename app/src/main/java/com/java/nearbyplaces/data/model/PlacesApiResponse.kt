package com.java.nearbyplaces.data.model

import com.google.gson.annotations.SerializedName

data class PlacesApiResponse(
    @SerializedName("results")
    val placesList:List<PlaceDetails>,
    @SerializedName("status")
    val statusMessage:String,
    @SerializedName("next_page_token")
    val nextToken:String,
    @SerializedName("error_message")
    val errorMessage:String
)
