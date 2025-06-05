package com.pandemiagame.org.data.remote

import com.pandemiagame.org.data.remote.models.auth.AuthResponse
import com.pandemiagame.org.data.remote.models.auth.LoginResponse
import com.pandemiagame.org.data.remote.models.auth.RegisterRequest
import com.pandemiagame.org.data.remote.models.game.GameRequest
import com.pandemiagame.org.data.remote.models.game.GameResponse
import com.pandemiagame.org.data.remote.models.game.Move
import com.pandemiagame.org.data.remote.models.game.MoveResponse
import com.pandemiagame.org.data.remote.models.game.Recommendation
import com.pandemiagame.org.data.remote.models.user.User
import com.pandemiagame.org.data.remote.models.user.UserUpdating
import retrofit2.http.*

interface ApiService {
    // POST
    @FormUrlEncoded
    @POST("token")
    suspend fun login(@Field("username") email: String,
                      @Field("password") password: String,
                      @Field("grant_type") grantType: String = "password"): LoginResponse

    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): User

    @POST("game")
    suspend fun createGame(@Header("Authorization") auth: String, @Body req: GameRequest): GameResponse

    @POST("game/{game_id}/move")
    suspend fun doMove(@Header("Authorization") auth: String,  @Path("game_id") gameId: Int, @Query("player_id") playerId: Int, @Body move: Move): GameResponse

    // GET
    @GET("game/{game_id}/moves")
    suspend fun getMoves(@Header("Authorization") auth: String, @Path("game_id") gameId: Int): List<MoveResponse>

    @GET("/auth/verify")
    suspend fun verifyToken(@Header("Authorization") auth: String): AuthResponse

    @GET("game/my-games")
    suspend fun getMyGames(@Header("Authorization") auth: String): List<GameResponse>

    @GET("user/users")
    suspend fun getUsers(@Header("Authorization") auth: String): List<User>

    @GET("game/{game_id}")
    suspend fun getGame(@Header("Authorization") auth: String, @Path("game_id") gameId: Int): GameResponse

    @GET("game/recommend")
    suspend fun getRecommendations(@Header("Authorization") auth: String,@Query("player_id") playerId: Int): Recommendation

    // PUT
    @PUT("/user/{user_id}")
    suspend fun updateUser(@Header("Authorization") auth: String, @Path("user_id") userId: Int, @Body user: UserUpdating): User

}
