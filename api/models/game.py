from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from sqlalchemy.types import Enum as SQLAlchemyEnum
from database import Base
from schemas.status import StatusEnum
from sqlalchemy.orm import relationship
from sqlalchemy.dialects.postgresql import JSONB  # Usar JSONB en PostgreSQL

class Game(Base):
    __tablename__ = 'games'

    id = Column(Integer, primary_key=True)
    status = Column(SQLAlchemyEnum(StatusEnum, name="status_enum"), nullable=False)  # ✅ Enum corregido
    turn = Column(Integer)
    num_turns = Column(Integer)
    turns = Column(JSONB)
    winner = Column(Integer)
    date = Column(DateTime)
    deck_id = Column(Integer, ForeignKey("decks.id"), unique=True)
    deck = relationship("Deck", back_populates="game", uselist=False)