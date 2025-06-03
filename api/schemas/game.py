from pydantic import BaseModel, Field, model_validator
from datetime import datetime
from api.schemas.player import PlayerResponse
from typing import List, Optional

class CardBase(BaseModel):
    id: int
    name: str
    tipo: str

class GameBase(BaseModel):
    players: List[str]
    multiplayer: bool = False

class GameResponse(GameBase):
    id: int
    turn: int
    date: datetime
    status: str
    num_turns: int
    turns: List[int] # Para el campo JSONB
    winner: Optional[int] = None
    cards: List[CardBase] = Field(default_factory=list)  # Campo calculado
    multiplayer: bool = False

    @model_validator(mode='after')
    def compute_cards(self) -> 'GameResponse':
        if hasattr(self, 'deck_cards'):
            self.cards = [
                CardBase.model_validate(dc.card)
                for dc in self.deck_cards
                if hasattr(dc, 'card') and dc.card is not None
            ]
        return self
        
    players: List[PlayerResponse] = [] # Relación con Player


    class Config:
        from_attributes = True