from pydantic import BaseModel
from typing import Optional

class Move(BaseModel):
    action: str  # Tipo de acción (card, discard)
    card: int = None  # Carta involucrada en la acción
    player_to: Optional[int] = None  # ID del jugador afectado por un virus
    discards: Optional[dict] = None  # Carta a descartar