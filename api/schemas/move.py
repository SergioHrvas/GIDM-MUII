from pydantic import BaseModel
from typing import Optional

class Move(BaseModel):
    action: str  # Tipo de acción (add_body, discard, virus, health, steal, etc.)
    card: Optional[dict] = None  # Carta involucrada en la acción
    player_to_virus: Optional[int] = None  # ID del jugador afectado por un virus
    player_to_health: Optional[int] = None  # ID del jugador afectado por una cura
    player_to_steal: Optional[int] = None  # ID del jugador al que se le roba una carta
    discards: Optional[dict] = None  # Carta a descartar