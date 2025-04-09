package com.pandemiagame.org.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.*
import java.util.Date

data class LoginResponse(val access_token: String)

data class User(val id: Int, val name: String, val email: String)
data class Post(val userId: Int, val title: String, val body: String)

data class GameRequest(val players: Int, val status: String, val date: String = Date().toString())

// Modelo principal del juego
data class GameResponse(
    @SerializedName("status") val status: String,
    @SerializedName("date") val date: String,
    @SerializedName("id") val id: Int,
    @SerializedName("turn") val turn: Int,
    @SerializedName("num_turns") val numTurns: Int,
    @SerializedName("turns") val turns: List<Int>,
    @SerializedName("winner") val winner: Int,
    @SerializedName("cards") val cards: List<Card>,
    @SerializedName("players") val players: List<Player>
)

// Modelo de Carta
data class Card(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("tipo") val type: String
)

// Modelo de Jugador
data class Player(
    @SerializedName("name") val name: String,
    @SerializedName("game_id") val gameId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("cards") val playerCards: List<Card>,
    @SerializedName("organs") val organs: List<Organ>
)

// Órganos del jugador (ajusta según tu implementación real)
data class Organ(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("virus") val virus: Boolean,
    @SerializedName("cure") val cure: Int
)

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
