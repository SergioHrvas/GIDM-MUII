from pydantic import BaseModel
from typing import List, Dict, Any, Optional

# Esquema base sin ID (para crear o actualizar un jugador)
class PlayerBase(BaseModel):
    hand_cards: List[Dict[str, Any]] = []  # Lista de cartas en la mano
    body_cards: List[Dict[str, Any]] = []  # Lista de cartas en el cuerpo
    name: str  # Nombre del jugador
    game_id: int  # ID del juego al que pertenece

# Esquema para CREAR un jugador (sin ID, se genera en la BD)
class PlayerCreate(PlayerBase):
    pass

# Esquema para RESPUESTA (incluye el ID del jugador)
class PlayerResponse(PlayerBase):
    id: int  # El ID es obligatorio en la respuesta

    class Config:
        from_attributes = True  # Permite mapear desde SQLAlchemy
