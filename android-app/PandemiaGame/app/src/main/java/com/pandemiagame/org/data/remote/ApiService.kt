package com.pandemiagame.org.data.remote

import retrofit2.http.*

data class LoginRequest(val username: String, val password: String, val grant_type: String = "password")
data class LoginResponse(val access_token: String)

data class User(val id: Int, val name: String, val email: String)
data class Post(val userId: Int, val title: String, val body: String)

interface ApiService {
    @FormUrlEncoded
    @POST("token")
    suspend fun login(@Field("username") username: String,
                      @Field("password") password: String,
                      @Field("grant_type") grant_type: String = "password"): LoginResponse
    @GET("users")
    suspend fun getUsers(): List<User>

    @POST("posts")
    suspend fun createPost(@Body post: Post): Post
}
