from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from sqlalchemy.types import Enum as SQLAlchemyEnum
from database import Base
from schemas.status import StatusEnum
from sqlalchemy.orm import relationship
from sqlalchemy.dialects.postgresql import JSONB  # Usar JSONB en PostgreSQL

# Primero define Card
from models.card import Card

# 📌 Tabla intermedia para relación muchos a muchos con ubicación de la carta
class PlayerCard(Base):
    __tablename__ = 'player_cards'

    id = Column(Integer, primary_key=True, index=True)
    player_id = Column(Integer, ForeignKey("players.id", ondelete="CASCADE"))
    card_id = Column(Integer, ForeignKey("cards.id", ondelete="CASCADE"))

    # Relación inversa a Player
    player = relationship("Player", back_populates="cards")

    # Relación con Card (cartas en mano)
    card = relationship("Card", back_populates="player_cards")