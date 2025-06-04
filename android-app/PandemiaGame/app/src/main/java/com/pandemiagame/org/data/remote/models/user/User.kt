package com.pandemiagame.org.data.remote.models.user

data class User(val id: Int, val username: String, val email: String, val name: String, val last_name: String, var image: String, var winned_games: Int, var played_games: Int, val token: String? = null)
