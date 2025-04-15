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

data class CardWrapper(
    @SerializedName("card") val card: Card
)

data class Player(
    @SerializedName("name") val name: String,
    @SerializedName("game_id") val gameId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("cards") val playerCards: List<CardWrapper>,
    @SerializedName("organs") val organs: List<Organ>
)
// Órganos del jugador (ajusta según tu implementación real)
data class Organ(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("virus") val virus: Boolean,
    @SerializedName("cure") val cure: Int
)

// Modelo de movimiento
data class Move(
    @SerializedName("action") val action: String,
    @SerializedName("card") val card: Int? = null,
    @SerializedName("player_to") val playerTo: Int? = null,
    @SerializedName("discards") val discards: List<Int>? = null,
    @SerializedName("organ_to_steal") val organToSteal: String? = null,
    @SerializedName("organ_to_pass") val organToPass: String? = null,
    @SerializedName("infect") val infect: InfectData? = null
)

data class InfectData(
    @SerializedName("player1") val player1: Int?,
    @SerializedName("organ1") val organ1: String,
    @SerializedName("player2") val player2: Int? = null,
    @SerializedName("organ2") val organ2: String? = null,
    @SerializedName("player3") val player3: Int? = null,
    @SerializedName("organ3") val organ3: String? = null,
    @SerializedName("player4") val player4: Int? = null,
    @SerializedName("organ4") val organ4: String? = null,
    @SerializedName("player5") val player5: Int? = null,
    @SerializedName("organ5") val organ5: String? = null
)


interface ApiService {
    @FormUrlEncoded
    @POST("token")
    suspend fun login(@Field("username") username: String,
                      @Field("password") password: String,
                      @Field("grant_type") grant_type: String = "password"): LoginResponse


    @POST("game")
    suspend fun createGame(@Header("Authorization") auth: String, @Body req: GameRequest): GameResponse

    @GET("game/my-games")
    suspend fun getMyGames(@Header("Authorization") auth: String): List<GameResponse>

    @GET("users")
    suspend fun getUsers(): List<User>

    @POST("posts")
    suspend fun createPost(@Body post: Post): Post

    @GET("game/{game_id}")
    suspend fun getGame(@Header("Authorization") auth: String, @Path("game_id") gameId: Int): GameResponse

    @POST("game/{game_id}/move")
    suspend fun doMove(@Header("Authorization") auth: String,  @Path("game_id") gameId: Int, @Query("player_id") playerId: Int, @Body move: Move): GameResponse

}
