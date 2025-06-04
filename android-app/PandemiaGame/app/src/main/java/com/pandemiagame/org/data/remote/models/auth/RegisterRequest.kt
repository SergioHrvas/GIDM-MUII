package com.pandemiagame.org.data.remote.models.auth

data class RegisterRequest(
    val username: String,
    val password: String,
    val last_name: String,
    val email: String,
    val name: String
)
