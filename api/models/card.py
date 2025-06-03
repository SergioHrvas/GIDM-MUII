from sqlalchemy import Column, Integer, String
from sqlalchemy.types import Enum as SQLAlchemyEnum
from api.database import Base
from sqlalchemy.orm import relationship
from api.schemas.organtype import OrganType

# Modelo Carta (Card)
class Card(Base):
    __tablename__ = 'cards'

    id = Column(Integer, primary_key=True, index=True)
    
    name = Column(String, nullable=False)
    
    tipo = Column(String, nullable=False) # Tipo de carta (virus, órgano, acción o cura)

    organ_type = Column(SQLAlchemyEnum(OrganType)) # Tipo de órgano de la carta

    # Relación inversa con PlayerCard
    player_cards = relationship("PlayerCard", back_populates="card")

    # Relación inversa con DeckCard
    deck_cards = relationship("DeckCard", back_populates="card")

    # Relación inversa con Move
    moves = relationship("Move", back_populates="card")