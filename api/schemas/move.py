from datetime import datetime
from pydantic import BaseModel
from typing import Any, Dict, List, Optional, Union
from schemas.game import CardBase
from schemas.organtype import OrganType
from schemas.infect import Infect

class Move(BaseModel):
    action: str  # Tipo de acción (card, discard)
    card: int = None  # Carta involucrada en la acción
    discards: Optional[List[int]] = None  # Lista de IDs de cartas a descartar
    organ_to_pass: Optional[OrganType] = None
    infect: Optional[Infect] = None

class MoveResponse(BaseModel):
    action: str  # Tipo de acción (card, discard)
    card: Optional[CardBase] = None  # ID de la carta (si aplica)
    player_id: Optional[int] = None  # ID del jugador (si aplica)
    game_id: Optional[int] = None  # ID del juego (si aplica)
    date: Optional[datetime] = None  # Fecha del movimiento (si aplica)
    action: str  # Acción realizada (ejemplo: "descartar", "carta")
    data: Optional[Union[Dict[str, Any], List[Any]]] = None