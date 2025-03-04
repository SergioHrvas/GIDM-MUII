from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from sqlalchemy.types import Enum as SQLAlchemyEnum
from models.deck import DeckCard
from database import Base
from schemas.status import StatusEnum
from sqlalchemy.orm import relationship
from sqlalchemy.dialects.postgresql import JSONB  # Usar JSONB en PostgreSQL
from schemas.organtype import OrganType

# 📌 Modelo Carta (Card)
class Card(Base):
    __tablename__ = 'cards'

    id = Column(Integer, primary_key=True, index=True)
    
    name = Column(String, nullable=False)
    
    tipo = Column(String, nullable=False)  # Tipo de carta (ejemplo: virus, órgano)

    organ_type = Column(SQLAlchemyEnum(OrganType))

    # Relación inversa con PlayerCard
    player_cards = relationship("PlayerCard", back_populates="card")

    # Relación inversa con DeckCard
    deck_cards = relationship("DeckCard", back_populates="card")