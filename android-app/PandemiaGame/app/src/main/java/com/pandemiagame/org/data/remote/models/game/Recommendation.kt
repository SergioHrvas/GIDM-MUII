package com.pandemiagame.org.data.remote.models.game

import com.google.gson.annotations.SerializedName

data class CardProbability(
    @SerializedName("card_id") val idCard: Int,
    @SerializedName("card_name") val nameCard: String,
    @SerializedName("win_probability") val winProb: Double,
)

data class Recommendation(
    @SerializedName("player_id") val idPlayer: Int,
    @SerializedName("recommendations") val recommendations: List<CardProbability>
)
