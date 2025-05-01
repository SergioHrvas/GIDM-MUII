from pydantic import BaseModel
from typing import Optional
from schemas.organtype import OrganType

class Infect(BaseModel):
    player1: int
    organ1: OrganType = None  
    organ1_from: Optional[OrganType] = None
    player2: Optional[int] = None
    organ2: Optional[OrganType] = None  
    organ2_from: Optional[OrganType] = None
    player3: Optional[int] = None
    organ3: Optional[OrganType] = None  
    organ3_from: Optional[OrganType] = None
    player4: Optional[int] = None
    organ4: Optional[OrganType] = None 
    organ4_from: Optional[OrganType] = None
    player5: Optional[int] = None
    organ5: Optional[OrganType] = None  
    organ5_from: Optional[OrganType] = None

    def dict(self, **kwargs):
        data = super().dict(**kwargs)
        result = {}
        
        for key, value in data.items():
            if value is not None:  # Excluimos todos los valores None
                if key.startswith('organ') and isinstance(value, OrganType):
                    result[key] = value.value  # Convertimos Enum a string
                else:
                    result[key] = value
        
        return result