package com.pandemiagame.org.data.remote.models.game

import com.google.gson.annotations.SerializedName

data class Organ(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("virus") val virus: Int,
    @SerializedName("cure") val cure: Int,
    @SerializedName("magic_organ") val magic_organ: Int
)