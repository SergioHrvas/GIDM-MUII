package com.pandemiagame.org.data.remote.models.game

import com.google.gson.annotations.SerializedName
import com.pandemiagame.org.data.remote.models.user.User


data class Player(
    @SerializedName("name") val name: String,
    @SerializedName("game_id") val gameId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("cards") val playerCards: List<CardWrapper>,
    @SerializedName("organs") val organs: List<Organ>,
    @SerializedName("user") val user: User? = null
)
