from sqlalchemy.orm import Session
from models.game import Game
from models.player import Player
from models.deck import Deck
from schemas.game import GameCreate
from datetime import datetime

def create_game(game: GameCreate, db: Session):
    db_game = Game(
        status=game.status,
        winner=0,
        turns=0,
        turn=0,
        date=datetime.now()  
    )
    db.add(db_game)
    db.commit()
    db.refresh(db_game)
    
    # Creamos players
    for i in range(game.players):  
        db_player = Player(name="", game_id=db_game.id)
        db.add(db_player)

    # Creamos el mazo
    db_deck = Deck(deck_cards="[]", discard_cards="[]")
    db.add(db_deck)
    db.commit()
    db.refresh(db_deck)

    # Asignamos el mazo al juego
    db_game.deck_id = db_deck.id
    db.commit()
    db.refresh(db_game)

    return db_game  # Se convierte automáticamente en GameResponse


def get_game(game_id: int, db: Session):
    return db.query(Game).filter(Game.id == game_id).first()
