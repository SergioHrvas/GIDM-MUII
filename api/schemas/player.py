from pydantic import BaseModel
from typing import List, Dict, Any, Optional
from enum import Enum
from pydantic.fields import Field


# Enumeración para los tipos de cartas asociadas a un órgano
class CardType(str, Enum):
    VIRUS = "virus"
    CURE = "cure"
    NONE = "none"  # Representa que no hay carta asociada

# Esquema para una carta asociada a un órgano
class OrganCard(BaseModel):
    tipo: str  # Nombre del órgano (corazon, pulmon, estomago, cerebro, multicolor)
    virus: bool
    cure: int


# Esquema para una carta de jugador
class OrganCard(BaseModel):
    tipo: str  # Nombre del órgano (corazon, pulmon, estomago, cerebro, multicolor)
    virus: bool
    cure: int


class PlayerCard(BaseModel):
    card_id: int


# Esquema base sin ID (para crear o actualizar un jugador)
class PlayerBase(BaseModel):
    name: str  # Nombre del jugador
    game_id: int  # ID del juego al que pertenece

# Esquema para CREAR un jugador (sin ID, se genera en la BD)
class PlayerCreate(PlayerBase):
    pass

# Esquema para RESPUESTA (incluye el ID del jugador)
class PlayerResponse(PlayerBase):
    id: int  # El ID es obligatorio en la respuesta
    cards: List[PlayerCard]
    organs: List[OrganCard]
    
    class Config:
        from_attributes = True  # Permite mapear desde SQLAlchemy
