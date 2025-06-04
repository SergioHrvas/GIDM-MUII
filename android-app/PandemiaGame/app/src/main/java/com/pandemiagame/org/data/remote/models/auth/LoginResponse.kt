package com.pandemiagame.org.data.remote.models.auth

import com.pandemiagame.org.data.remote.models.user.User

data class LoginResponse(val access_token: String, val user: User)