from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from sqlalchemy.types import Enum as SQLAlchemyEnum
from database import Base
from sqlalchemy.dialects.postgresql import JSONB  # Usar JSONB en PostgreSQL
from sqlalchemy.orm import relationship

class Deck(Base):
    __tablename__ = 'decks'

    id = Column(Integer, primary_key=True)
    deck_cards = Column(JSONB)
    discard_cards = Column(JSONB)
    game = relationship("Game", back_populates="deck", uselist=False) 