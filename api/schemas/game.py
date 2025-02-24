from pydantic import BaseModel, EmailStr
from datetime import datetime
from status import StatusEnum
from typing import Optional

class GameBase(BaseModel):
    status: StatusEnum
    date: datetime
    
class GameCreate(GameBase):
    players: int
    pass

class GameResponse(GameBase):
    id: int

    class Config:
        from_attributes = True