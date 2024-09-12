package com.uri.bolanope.services

import com.uri.bolanope.model.CreateUserResponseModel
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.model.LoginModel
import com.uri.bolanope.model.TokenModel
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
    fun postUser(@Body body: UserModel): Call<CreateUserResponseModel>

    @PUT("user/{id}")
    fun putUserById(@Path("id") id: String , @Body body: UserModel): Call<UserModel>

    @DELETE("user/{id}")
    fun deleteUserById(@Path("id") id: String): Call<UserModel>

    @POST("auth/")
    fun loginUser(@Body body: LoginModel): Call<TokenModel>

    @GET("field/{id}")
    fun getFieldById(@Path("id") id: String): Call<FieldModel>
}