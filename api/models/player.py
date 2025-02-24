from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.dialects.postgresql import JSONB  # Usar JSONB en PostgreSQL
from database import Base

class Player(Base):
    __tablename__ = 'players'

    id = Column(Integer, primary_key=True, index=True)
    hand_cards = Column(JSONB, default=[])  # Cartas en la mano
    body_cards = Column(JSONB, default=[])  # Cartas jugadas en el cuerpo
    name = Column(String)  # Nombre del jugador
    game_id = Column(Integer, ForeignKey('games.id'))  # ID del juego al que pertenece