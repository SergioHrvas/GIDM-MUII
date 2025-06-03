from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from api.database import Base
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

    # Lungs (0: none, 1: normal, 2: virus, 3: cure, 4: protected)
    lungs_estado = Column(Integer, default=0)

    # Intestine (0: none, 1: normal, 2: virus, 3: cure, 4: protected)
    intestine_estado = Column(Integer, default=0)

    # Heart (0: none, 1: normal, 2: virus, 3: cure, 4: protected)
    heart_estado = Column(Integer, default=0)

    # Brain (0: none, 1: normal, 2: virus, 3: cure, 4: protected)
    brain_estado = Column(Integer, default=0)

    # Magic (0: none, 1: normal, 2: virus, 3: cure, 4: protected)
    magic_estado = Column(Integer, default=0)

