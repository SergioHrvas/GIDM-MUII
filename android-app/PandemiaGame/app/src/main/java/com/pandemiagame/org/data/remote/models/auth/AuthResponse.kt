package com.pandemiagame.org.data.remote.models.auth

import com.google.gson.annotations.SerializedName
import com.pandemiagame.org.data.remote.models.user.User

data class AuthResponse(
    @SerializedName("valid") val valid: Boolean,
    @SerializedName("user") val user: User
)