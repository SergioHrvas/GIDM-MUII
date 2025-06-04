package com.pandemiagame.org.data.remote.models.game

import com.google.gson.annotations.SerializedName

// Modelo de Carta
data class Card(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("tipo") val type: String
)