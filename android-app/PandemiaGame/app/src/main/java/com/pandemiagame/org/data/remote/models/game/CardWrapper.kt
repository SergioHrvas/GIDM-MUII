package com.pandemiagame.org.data.remote.models.game

import com.google.gson.annotations.SerializedName

data class CardWrapper(
    @SerializedName("card") val card: Card
)