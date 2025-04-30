from pydantic import BaseModel, EmailStr, Field, field_validator, model_validator
from datetime import datetime
from models.card import Card
from schemas.deckcard import DeckBase
from schemas.player import PlayerResponse
from schemas.status import StatusEnum
from typing import Any, Dict, List, Optional

class CardBase(BaseModel):
    id: int
    name: str
    tipo: str


class GameBase(BaseModel):
    status: StatusEnum
    date: datetime
    
class GameCreate(GameBase):
    players: List[str]
    pass

class GameResponse(GameBase):
    id: int
    turn: int
    num_turns: int
    turns: List[int]  # Para el campo JSONB
    winner: Optional[int] = None
    cards: List[CardBase] = Field(default_factory=list)  # Campo calculado

    @model_validator(mode='after')
    def compute_cards(self) -> 'GameResponse':
        if hasattr(self, 'deck_cards'):
            self.cards = [
                CardBase.model_validate(dc.card)
                for dc in self.deck_cards
                if hasattr(dc, 'card') and dc.card is not None
            ]
        return self
        
    players: List[PlayerResponse] = []        # Relación con Player


    class Config:
        from_attributes = True