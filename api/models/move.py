from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from database import Base
from sqlalchemy.orm import relationship
from sqlalchemy.dialects.postgresql import JSONB  # Usar JSONB en PostgreSQL

# Modelo Movimiento (Move)
class Move(Base):
    __tablename__ = 'moves'

    id = Column(Integer, primary_key=True, index=True)
    player_id = Column(Integer, ForeignKey("players.id"), nullable=False)
    game_id = Column(Integer, ForeignKey("games.id"), nullable=False)
    card_id = Column(Integer, ForeignKey("cards.id"))
    date = Column(DateTime, nullable=False)
    action = Column(String, nullable=False)  # Acción realizada (ejemplo: "descartar", "carta")
    data = Column(JSONB, nullable=True)  # Datos adicionales en formato JSONB


    # Relación inversa con Player
    player = relationship("Player", back_populates="moves")

    # Relación inversa con Game
    game = relationship("Game", back_populates="moves")

    # Relación inversa con Card
    card = relationship("Card", back_populates="moves")

    # Nuevas columnas para almacenar el número de órganos en el estado correspondiente
    num_virus = Column(Integer, default=0, nullable=False)  # Número de órganos con virus
    num_cure = Column(Integer, default=0, nullable=False)  # Número de órganos con cura
    num_protected = Column(Integer, default=0, nullable=False)  # Número de órganos protegidos
    num_organs = Column(Integer, default=0, nullable=False)  # Número total de órganos


