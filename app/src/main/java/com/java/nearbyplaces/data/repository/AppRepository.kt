package com.java.nearbyplaces.data.repository

import com.java.nearbyplaces.BuildConfig
import com.java.nearbyplaces.data.model.LocationDetails
import com.java.nearbyplaces.data.model.PlacesApiResponse
import com.java.nearbyplaces.data.retrofit.LocationApiService
import io.reactivex.Observable

class AppRepository(private val locationApiService: LocationApiService) {

    fun getNearPlacesByLocation(query:String,locationDetails: LocationDetails?) : Observable<PlacesApiResponse> {
        return if(locationDetails == null){
            locationApiService.getNearByPlaces(BuildConfig.PLACES_KEY,query)
        }else {
            locationApiService.getNearByPlacesByLocation(
                BuildConfig.PLACES_KEY, query,
                "${locationDetails?.latitude},${locationDetails?.longitude}"
            )
        }
    }

}