package com.digitalwardrobe.data

import retrofit2.Call
import retrofit2.http.GET

interface GetWearablesRemote {
    @GET("/todos")
    fun getAllWearables(): Call<List<Wearable>>
}