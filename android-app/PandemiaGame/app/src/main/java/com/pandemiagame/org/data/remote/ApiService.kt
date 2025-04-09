package com.pandemiagame.org.data.remote

import retrofit2.http.*
import java.util.Date

data class LoginResponse(val access_token: String)

data class User(val id: Int, val name: String, val email: String)
data class Post(val userId: Int, val title: String, val body: String)

data class GameRequest(val players: Int, val status: String, val date: String = Date().toString())
data class GameResponse(val id: Int, val status: String, val date: String)

interface ApiService {
    @FormUrlEncoded
    @POST("token")
    suspend fun login(@Field("username") username: String,
                      @Field("password") password: String,
                      @Field("grant_type") grant_type: String = "password"): LoginResponse


    @POST("game")
    suspend fun createGame(@Header("Authorization") auth: String, @Body req: GameRequest): GameResponse


    @GET("users")
    suspend fun getUsers(): List<User>

    @POST("posts")
    suspend fun createPost(@Body post: Post): Post
}
