from sqlalchemy import Boolean, Column, Integer, DateTime
from sqlalchemy.types import Enum as SQLAlchemyEnum
from api.database import Base
from api.schemas.status import StatusEnum
from sqlalchemy.orm import relationship
from sqlalchemy.dialects.postgresql import JSONB  # Usar JSONB en PostgreSQL

# Modelo Partida
class Game(Base):
    __tablename__ = 'games'

    id = Column(Integer, primary_key=True)
    status = Column(SQLAlchemyEnum(StatusEnum, name="status_enum"), nullable=False)
    turn = Column(Integer)
    num_turns = Column(Integer)
    turns = Column(JSONB)
    winner = Column(Integer)
    date = Column(DateTime)

    multiplayer = Column(Boolean, nullable=False)    

    # Relación con DeckCard
    deck_cards = relationship("DeckCard", back_populates="game", cascade="all, delete-orphan")

    # Propiedad para acceder a las cartas con repeticiones
    @property
    def cards(self):
        return [dc.card for dc in self.deck_cards]
    
    players = relationship("Player", back_populates="game", cascade="all, delete-orphan")

    # Relación con Move
    moves = relationship("Move", back_populates="game", cascade="all, delete-orphan")