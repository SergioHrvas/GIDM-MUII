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
    organ: str  # Nombre del órgano (corazon, pulmon, estomago, cerebro, multicolor)
    card_type: CardType = CardType.NONE  # Tipo de carta (virus, cure, none)
    card_details: Optional[Dict[str, Any]] = None  # Detalles adicionales de la carta (opcional)


# Esquema base sin ID (para crear o actualizar un jugador)
class PlayerBase(BaseModel):
    hand_cards: List[Dict[str, Any]] = []  # Lista de cartas en la mano
    body_cards: List[OrganCard] = Field(default_factory=list)  # Cartas en el cuerpo (órganos adquiridos)
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
