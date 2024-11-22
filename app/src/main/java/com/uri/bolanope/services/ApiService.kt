package com.uri.bolanope.services

import com.uri.bolanope.model.AcceptRequestBody
import com.uri.bolanope.model.AllRatingModel
import com.uri.bolanope.model.CommentModel
import com.uri.bolanope.model.CreateUserResponseModel
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.model.LoginModel
import com.uri.bolanope.model.MostReservedTimesModel
import com.uri.bolanope.model.NotificationModel
import com.uri.bolanope.model.PostRatingModel
import com.uri.bolanope.model.RatingModel
import com.uri.bolanope.model.RegisterStudentModel
import com.uri.bolanope.model.RequestModel
import com.uri.bolanope.model.ReserveModel
import com.uri.bolanope.model.StudentModel
import com.uri.bolanope.model.StudentsCount
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.model.TokenModel
import com.uri.bolanope.model.TourneyModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.model.addTeamToTourneyBody
import com.uri.bolanope.model.getNumberOfTeamRequestsModel
import com.uri.bolanope.model.tourneyAverageParticipantsModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("user/")
    fun getAllUsers(): Call<List<UserModel>>

    @GET("user/")
    fun getAllUsersByRole(
        @Query("role") role: String,
    ): Call<List<UserModel>>

    @GET("user/{id}")
    fun getUserById(@Path("id") id: String): Call<UserModel>

    @GET("user/{id}/students/count")
    fun getStudentsByTeacher(@Path("id") id: String): Call<StudentsCount>


    @Multipart
    @POST("user/")
    fun postUser(
        @Part("email") email: RequestBody,
        @Part("name") name: RequestBody,
        @Part("password") password: RequestBody,
        @Part("cpf") cpf: RequestBody,
        @Part("birth") birth: RequestBody,
        @Part("cep") cep: RequestBody,
        @Part file_url: MultipartBody.Part?,
    ): Call<CreateUserResponseModel>

    @Multipart
    @POST("user/professor")
    fun postProfessor(
        @Part("email") email: RequestBody,
        @Part("name") name: RequestBody,
        @Part("password") password: RequestBody,
        @Part("cpf") cpf: RequestBody,
        @Part("birth") birth: RequestBody,
        @Part("cep") cep: RequestBody,
        @Part file_url: MultipartBody.Part?,
        @Header("Authorization") token: String
    ): Call<CreateUserResponseModel>

    @Multipart
    @PUT("user/{id}")
    fun putUserById(
        @Path("id") id: String,
        @Part("email") email: RequestBody,
        @Part("name") name: RequestBody,
        @Part("birth") birth: RequestBody,
        @Part("cep") cep: RequestBody,
        @Part file_url: MultipartBody.Part?,
    ): Call<UserModel>

    @Multipart
    @PUT("user/{id}")
    fun changeUserPassword(
        @Path("id") id: String,
        @Part("password") password: RequestBody,
    ): Call<UserModel>

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

    @GET("team/leader")
    fun getTeamsByLeader(
        @Header("Authorization") token: String
    ): Call<List<TeamModel>?>

    @GET("team/members/{id}")
    fun getTeamsByMember(
        @Path("id") id: String,
    ): Call<List<TeamModel>?>

    @Multipart
    @POST("team/")
    fun createTeam(
        @Part("description") description: RequestBody?,
        @Part("leader_id") leader_id: RequestBody?,
        @Part members_id: List<MultipartBody.Part>?,
        @Part("name") name: RequestBody?,
        @Part file: MultipartBody.Part?,
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

    @POST("request/")
    fun createTeamRequest(
        @Body
        body: com.uri.bolanope.model.RequestBody,
        @Header("Authorization")
        authHeader: String
    ): Call<RequestModel>

    @POST("request/{id}")
    fun acceptTeamRequest(
        @Path("id")
        id: String,
        @Body
        action: AcceptRequestBody,
        @Header("Authorization")
        authHeader: String
    ): Call<RequestModel>

    @GET("request/team/{id}")
    fun getTeamRequests(
        @Path("id")
        id: String,
        @Header("Authorization")
        authHeader: String
    ): Call<List<RequestModel>>

    @GET("request/pending/{id}")
    fun getNumberOfTeamRequests(
        @Path("id")
        id: String,
        @Header("Authorization")
        authHeader: String
    ): Call<getNumberOfTeamRequestsModel>

    @GET("tourney/")
    fun getAllTourneys(): Call<List<TourneyModel>>

    @POST("tourney/{id}/addteam")
    fun addTeamToTourney(
        @Path("id")
        id: String,
        @Body body: addTeamToTourneyBody,
    ): Call<TourneyModel>

    @HTTP(method = "DELETE", path = "tourney/{id}/removeteam", hasBody = true)
    fun removeTeamFromTourney(
        @Path("id") id: String,
        @Body body: addTeamToTourneyBody,
    ): Call<Void>

    @GET("tourney/{id}")
    fun getTourneyById(@Path("id") id: String): Call<TourneyModel>

    @GET("tourney/{id}/teams")
    fun getTeamsByTourneyId(
        @Path("id") id: String
    ): Call<List<TeamModel>>

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

    @POST("notification/")
    fun sendNotification(
        @Body
        body: NotificationModel,
    ): Call<NotificationModel>

    @GET("notification/user/{id}")
    fun getNotification(
        @Path("id")
        id: String
    ): Call<List<NotificationModel>>

    @PUT("notification/{id}/read")
    fun readNotification(
        @Path("id")
        id: String
    ): Call<NotificationModel>

    @GET("comment/team/{id}")
    fun getComments(
        @Path("id")
        id: String,
        @Header("Authorization")
        token: String
    ): Call<List<CommentModel>>

    @POST("comment/")
    fun createComment(
        @Body
        body: CommentModel,
        @Header("Authorization")
        token: String
    ): Call<CommentModel>

    @DELETE("comment/{id}")
    fun deleteComment(
        @Path("id")
        id: String,
        @Header("Authorization")
        token: String
    ): Call<Void>

    @GET("tourney/average-participants")
    fun getTourneyAverage(): Call<tourneyAverageParticipantsModel>

    @GET("reserve/most-reserved-times")
    fun getMostReservedTimes(): Call<List<MostReservedTimesModel>>

    @POST("student")
    fun createStudent(
        @Body
        body: RegisterStudentModel,
    ): Call<StudentModel>

    @GET("rating/field/{id}")
    fun getFieldRating(
        @Path("id")
        id: String,
        @Header("Authorization")
    token: String
    ): Call<RatingModel>

    @POST("rating/")
    fun postFieldRating(
        @Body
        body: PostRatingModel,
        @Header("Authorization")
        token: String
    ): Call<PostRatingModel>

    @GET("rating/")
    fun getAllRating(): Call<List<AllRatingModel>>
}