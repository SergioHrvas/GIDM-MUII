package com.pandemiagame.org.data.remote.models.user

import com.google.gson.annotations.SerializedName

data class UserUpdating(
    var username: String? = null,
    var email: String? = null,
    var name: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    var image: String? = null,
    var password: String? = null
)
