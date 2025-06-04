package com.pandemiagame.org.data.remote.models.game

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class MoveResponse(
    @SerializedName("action") val action: String,
    @SerializedName("card") val card: Card? = null,
    @SerializedName("player") val player: Player,
    @SerializedName("game_id") val game: Int,
    @SerializedName("date") val date: String? = null,
    @SerializedName("data") val data: JsonElement? = null
)