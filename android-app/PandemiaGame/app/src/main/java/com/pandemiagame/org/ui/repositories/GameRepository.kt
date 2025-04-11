package com.pandemiagame.org.ui.repositories

import androidx.compose.runtime.mutableStateListOf
import com.pandemiagame.org.data.remote.GameResponse

class GameRepository {
    // Almacenamiento en memoria (podría ser Room, Retrofit, etc.)
    private val _games = mutableStateListOf<GameResponse>()
    val games: List<GameResponse> get() = _games

    // Crea un nuevo juego y lo guarda
    fun createGame(game: GameResponse) {
        _games.add(game)
    }

    // Obtiene un juego por su ID
    fun getGameById(id: Int): GameResponse? {
        return _games.find { it.id == id }
    }

    // Obtiene todos los juegos
    fun getAllGames(): List<GameResponse> {
        return _games.toList()
    }
}