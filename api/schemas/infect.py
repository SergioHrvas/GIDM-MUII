from pydantic import BaseModel
from typing import Optional
from schemas.organtype import OrganType

class Infect(BaseModel):
    player1: int
    organ1: OrganType = None  
    player2: Optional[int] = None
    organ2: Optional[OrganType] = None  
    player3: Optional[int] = None
    organ3: Optional[OrganType] = None  
    player4: Optional[int] = None
    organ4: Optional[OrganType] = None  
    player5: Optional[int] = None
    organ5: Optional[OrganType] = None  
