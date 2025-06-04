package com.pandemiagame.org.data.remote.models.game

import com.google.gson.annotations.SerializedName
import com.pandemiagame.org.data.remote.models.game.InfectData

data class Move(
    @SerializedName("action") val action: String,
    @SerializedName("card") val card: Int? = null,
    @SerializedName("discards") val discards: List<Int>? = null,
    @SerializedName("organ_to_pass") val organToPass: String? = null,
    @SerializedName("infect") val infect: InfectData? = null
)