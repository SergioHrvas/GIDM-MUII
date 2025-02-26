from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.dialects.postgresql import JSONB  # Usar JSONB en PostgreSQL
from database import Base
from sqlalchemy.orm import relationship

# Primero define PlayerCard y Organ
from models.playercard import PlayerCard
from models.organ import Organ


class Player(Base):
    __tablename__ = 'players'

    id = Column(Integer, primary_key=True, index=True)


    name = Column(String)  # Nombre del jugador
    game_id = Column(Integer, ForeignKey('games.id', ondelete="CASCADE"))  # ID del juego al que pertenece

    # Relación con Game
    game = relationship("Game", back_populates="players")

    # Relación con PlayerCard (cartas en mano)
    cards = relationship("PlayerCard", back_populates="player", cascade="all, delete-orphan")

    # Relación con los órganos (cuerpo)
    organs = relationship("Organ", back_populates="player", cascade="all, delete-orphan")
