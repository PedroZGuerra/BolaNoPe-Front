package com.uri.bolanope.services

import com.uri.bolanope.model.CreateUserResponseModel
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.model.LoginModel
import com.uri.bolanope.model.ReserveModel
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.model.TokenModel
import com.uri.bolanope.model.TourneyModel
import com.uri.bolanope.model.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("user/")
    fun getAllUsers(): Call<List<UserModel>>

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

    @POST("reserve/")
    fun postReserve(@Body body: ReserveModel): Call<ReserveModel>

    @GET("field/")
    fun getAllFields(): Call<List<FieldModel>>

    @GET("reserve/field/{id}")
    fun getFieldHistory(@Path("id") id: String): Call<List<ReserveModel>>

    @DELETE("field/{id}")
    fun deleteField(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String
    ): Call<Void>

    @Multipart
    @POST("field/")
    fun postFieldWithImage(
        @Part("name") name: RequestBody,
        @Part("location") location: RequestBody,
        @Part("value_hour") valueHour: RequestBody,
        @Part("obs") obs: RequestBody?,
        @Part("open_time") openTime: RequestBody,
        @Part("close_time") closeTime: RequestBody,
        @Part("available") available: RequestBody,
        @Part file_url: MultipartBody.Part?,
        @Header("Authorization") token: String
    ): Call<FieldModel>

    @Multipart
    @PUT("field/{id}")
    fun putFieldWithImage(
        @Path("id") id: String,
        @Part("name") name: RequestBody,
        @Part("location") location: RequestBody,
        @Part("value_hour") valueHour: RequestBody,
        @Part("obs") obs: RequestBody?,
        @Part("open_time") openTime: RequestBody,
        @Part("close_time") closeTime: RequestBody,
        @Part("available") available: RequestBody,
        @Part file: MultipartBody.Part?,
        @Header("Authorization") authHeader: String
    ): Call<FieldModel>

    @GET("team/{id}")
    fun getTeamById(@Path("id") id: String): Call<TeamModel?>

    @GET("team/")
    fun getAllTeams(): Call<List<TeamModel>?>

    @POST("team/")
    fun createTeam(
        @Body body: TeamModel,
        @Header("Authorization") token: String
    ): Call<TeamModel?>

    @DELETE("team/{id}")
    fun deleteTeam(
        @Path("id")
        id: String,
        @Header("Authorization")
        authHeader: String
    ): Call<Void>

    @PUT("team/{id}")
    fun updateTeam(
        @Path("id")
        id: String,
        @Body
        body: TeamModel,
        @Header("Authorization")
        authHeader: String
    ): Call<TeamModel>

    @GET("tourney/")
    fun getAllTourneys(): Call<List<TourneyModel>>

    @GET("tourney/{id}")
    fun getTourneyById(@Path("id") id: String): Call<TourneyModel>

    @POST("tourney/")
    fun createTourney(
        @Body body: TourneyModel,
        @Header("Authorization") token: String
    ): Call<TourneyModel?>

    @PUT("tourney/{id}")
    fun updateTourney(
        @Path("id")
        id: String,
        @Body
        body: TourneyModel,
        @Header("Authorization")
        token: String
    ): Call<TourneyModel?>
}