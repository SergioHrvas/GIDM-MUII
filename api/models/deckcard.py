from sqlalchemy import Column, Integer, ForeignKey
from sqlalchemy.orm import relationship

from api.database import Base

# Modelo Carta-Mazo
class DeckCard(Base):
    __tablename__ = "deck_cards"

    id = Column(Integer, primary_key=True, autoincrement=True)  # Clave primaria única
    game_id = Column(Integer, ForeignKey("games.id"))
    card_id = Column(Integer, ForeignKey("cards.id"))

    # Relación con Game
    game = relationship("Game", back_populates="deck_cards")

    # Relación con Card
    card = relationship("Card", back_populates="deck_cards")