package com.pandemiagame.org.data.remote.models.game

data class GameRequest(val players: List<String>, val status: String, val multiplayer: Boolean)