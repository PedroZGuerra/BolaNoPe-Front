package com.uri.bolanope.services

import com.uri.bolanope.model.UserModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("user/{id}")
    fun getUserById(@Path("id") id: String): Call<UserModel>
}