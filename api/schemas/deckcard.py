from pydantic import BaseModel
from typing import List, Dict, Any, Optional

# Esquema base sin ID (para crear o actualizar un jugador)
class DeckBase(BaseModel):
    deck_cards: List[Dict[str, Any]] = []  # Lista de cartas en el mazo
    discard_cards: List[Dict[str, Any]] = []  # Lista de cartas descartadas
    game_id: int  # ID del juego al que pertenece

# Esquema para CREAR un jugador (sin ID, se genera en la BD)
class DeckCreate(DeckBase):
    pass

# Esquema para RESPUESTA (incluye el ID del jugador)
class DeckResponse(DeckBase):
    id: int  # El ID es obligatorio en la respuesta

    class Config:
        from_attributes = True  # Permite mapear desde SQLAlchemy
