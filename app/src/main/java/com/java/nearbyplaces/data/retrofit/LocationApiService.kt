package com.java.nearbyplaces.data.retrofit

import com.java.nearbyplaces.BuildConfig
import com.java.nearbyplaces.data.model.PlaceDetails
import com.java.nearbyplaces.data.model.PlacesApiResponse
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface LocationApiService {

    @GET("textsearch/json")
    fun getNearByPlaces(
        @Query("key") key:String,
        @Query("query") query:String) : Observable<PlacesApiResponse>

    @GET("textsearch/json")
    fun getNearByPlacesByLocation(
        @Query("key") key:String,
        @Query("query") query:String,
        @Query("location") location:String) : Observable<PlacesApiResponse>


    companion object{
        private const val BASE_URL = BuildConfig.BASE_URL
        private var locationApiService : LocationApiService ?= null
        private var httpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
        private var okHttpClient: OkHttpClient =
            OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
                .build()
        operator fun invoke(): LocationApiService {

            if( locationApiService == null){
                synchronized(this){
                    if(locationApiService == null){
                        val retrofit = Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(okHttpClient)
                            .build()
                        locationApiService = retrofit.create(LocationApiService::class.java)
                    }
                }

            }
            return locationApiService!!
        }
    }



}
