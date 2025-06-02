package com.pandemiagame.org.data.remote

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.http.*
import java.util.Date

data class LoginResponse(val access_token: String, val user: User)

data class User(val id: Int, val username: String, val email: String, val name: String, val last_name: String, var image: String, var winned_games: Int, var played_games: Int)
data class UserUpdating(
    var username: String? = null,
    var email: String? = null,
    var name: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    var image: String? = null,
    var password: String? = null
)

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


data class MoveResponse(
    @SerializedName("action") val action: String,
    @SerializedName("card") val card: Card? = null,
    @SerializedName("player") val player: Player,
    @SerializedName("game_id") val game: Int,
    @SerializedName("date") val date: String? = null,
    @SerializedName("data") val data: JsonElement? = null
)

data class AuthResponse(
    @SerializedName("valid") val valid: Boolean,
    @SerializedName("user") val user: User
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val last_name: String,
    val email: String,
    val name: String
)

data class CardProbability(
    @SerializedName("card_id") val idCard: Int,
    @SerializedName("card_name") val nameCard: String,
    @SerializedName("win_probability") val winProb: Double,
)
data class Recommendation(
    @SerializedName("player_id") val idPlayer: Int,
    @SerializedName("recommendations") val recommendations: List<CardProbability>
)


interface ApiService {
    @FormUrlEncoded
    @POST("token")
    suspend fun login(@Field("username") email: String,
                      @Field("password") password: String,
                      @Field("grant_type") grant_type: String = "password"): LoginResponse

    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): User

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

    @GET("game/{game_id}/moves")
    suspend fun getMoves(@Header("Authorization") auth: String, @Path("game_id") gameId: Int): List<MoveResponse>

    @GET("/auth/verify")
    suspend fun verifyToken(@Header("Authorization") auth: String): AuthResponse

    @PUT("/user/{user_id}")
    suspend fun updateUser(@Header("Authorization") auth: String, @Path("user_id") userId: Int, @Body user: UserUpdating): User

    @GET("game/recommend")
    suspend fun getRecommendations(@Header("Authorization") auth: String,@Query("player_id") playerId: Int): Recommendation
}
