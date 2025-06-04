package com.pandemiagame.org.data.remote.models.game

import com.google.gson.annotations.SerializedName

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