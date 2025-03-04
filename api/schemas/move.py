from pydantic import BaseModel
from typing import List, Optional
from schemas.organtype import OrganType
from schemas.infect import Infect

class Move(BaseModel):
    action: str  # Tipo de acción (card, discard)
    card: int = None  # Carta involucrada en la acción
    player_to: Optional[int] = None  # ID del jugador afectado por un virus
    discards: Optional[List[int]] = None  # Lista de IDs de cartas a descartar
    organ_to_steal: Optional[OrganType] = None
    organ_to_pass: Optional[OrganType] = None
    infect: Optional[Infect] = None