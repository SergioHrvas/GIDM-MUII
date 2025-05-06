package com.pandemiagame.org.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.*
import java.util.Date

data class LoginResponse(val access_token: String, val user: User)

data class User(val id: Int, val username: String, val email: String)

data class GameRequest(val players: List<String>, val status: String, val multiplayer: Boolean)

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
    @SerializedName("players") val players: List<Player>,
    @SerializedName("multiplayer") val multiplayer: Boolean)

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
    @SerializedName("organs") val organs: List<Organ>,
    @SerializedName("user") val user: User? = null
)

data class Organ(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("virus") val virus: Int,
    @SerializedName("cure") val cure: Int,
    @SerializedName("magic_organ") val magic_organ: Int
)

data class Move(
    @SerializedName("action") val action: String,
    @SerializedName("card") val card: Int? = null,
    @SerializedName("discards") val discards: List<Int>? = null,
    @SerializedName("organ_to_pass") val organToPass: String? = null,
    @SerializedName("infect") val infect: InfectData? = null
)

data class InfectData(
    @SerializedName("player1") val player1: Int?,
    @SerializedName("organ1") val organ1: String? = null,
    @SerializedName("organ1_from") val organ1from: String? = null,
    @SerializedName("player2") val player2: Int? = null,
    @SerializedName("organ2") val organ2: String? = null,
    @SerializedName("organ2_from") val organ2from: String? = null,
    @SerializedName("player3") val player3: Int? = null,
    @SerializedName("organ3") val organ3: String? = null,
    @SerializedName("organ3_from") val organ3from: String? = null,
    @SerializedName("player4") val player4: Int? = null,
    @SerializedName("organ4") val organ4: String? = null,
    @SerializedName("organ4_from") val organ4from: String? = null,
    @SerializedName("player5") val player5: Int? = null,
    @SerializedName("organ5") val organ5: String? = null,
    @SerializedName("organ5_from") val organ5from: String? = null,
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

    @GET("user/users")
    suspend fun getUsers(@Header("Authorization") auth: String): List<User>

    @GET("game/{game_id}")
    suspend fun getGame(@Header("Authorization") auth: String, @Path("game_id") gameId: Int): GameResponse

    @POST("game/{game_id}/move")
    suspend fun doMove(@Header("Authorization") auth: String,  @Path("game_id") gameId: Int, @Query("player_id") playerId: Int, @Body move: Move): GameResponse

}
