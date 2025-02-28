from pydantic import BaseModel
from typing import Optional
from schemas.organtype import OrganType

class Infect(BaseModel):
    player1: int
    organ1: OrganType = None  
    player2: int
    organ2: OrganType = None  
    player3: int
    organ3: OrganType = None  
    player4: int
    organ4: OrganType = None  
    player5: int
    organ5: OrganType = None  
