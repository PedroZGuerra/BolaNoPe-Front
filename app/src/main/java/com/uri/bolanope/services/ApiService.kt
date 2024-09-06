package com.uri.bolanope.services

import com.uri.bolanope.model.UserModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("user/{id}")
    fun getUserById(@Path("id") id: String): Call<UserModel>

    @POST("user/")
    fun postUser(@Body body: UserModel): Call<UserModel>

    @PUT("user/{id}")
    fun putUserById(@Path("id") id: String , @Body body: UserModel): Call<UserModel>

    @DELETE("user/{id}")
    fun deleteUserById(@Path("id") id: String): Call<UserModel>
}