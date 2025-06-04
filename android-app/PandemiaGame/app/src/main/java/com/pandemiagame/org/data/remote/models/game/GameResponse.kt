package com.pandemiagame.org.data.remote.models.game

import com.google.gson.annotations.SerializedName


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
