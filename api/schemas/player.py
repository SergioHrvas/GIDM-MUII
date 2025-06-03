from pydantic import BaseModel
from typing import List, Optional
from enum import Enum
from api.schemas.user import UserBase

class CardBase(BaseModel):
    id: int
    name: str
    tipo: str

# Esquema para una carta asociada a un órgano
class OrganCard(BaseModel):
    tipo: str  # Nombre del órgano (corazon, pulmon, estomago, cerebro, multicolor)
    virus: int
    cure: int
    magic_organ: int # Tipo de virus/cura asociado (1 = corazon, 2 = cerebro, 3 = intestinos, 4 = pulmones)

class PlayerCard(BaseModel):
    card: CardBase

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
    user: Optional[UserBase] = None  # ID del usuario (opcional)
    
    class Config:
        from_attributes = True  # Permite mapear desde SQLAlchemy
