from sqlalchemy.orm import Session
from models.game import Game
from models.player import Player
from models.deck import Deck
from schemas.game import GameCreate
from datetime import datetime
from schemas.move import Move

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

def do_move_game(game_id: int, player_id, move: Move, db: Session):
    # Obtenemos el juego
    game = db.query(Game).filter(Game.id == game_id).first()

    # Obtenemos el mazo
    deck = db.query(Deck).filter(Deck.id == game.deck_id).first()

    # Obtenemos el jugador
    player = db.query(Player).filter(Player.id == game.turn).first()

    # if(player_id != player):
    #     return "Errror"


    # Hacemos el movimiento
    if move.action == "add_body":
        print(move)
        print(player)
        # player.body_cards.append(move.card)
        # player.hand_cards.remove(move.card)

    """
    elif move.action == "discard":
        player.hand_cards.remove(move.discards)
        deck.discard_cards.append(move.discards)
    elif move.action == "virus":
        player.hand_cards.remove(move.virus)
        move.player_to_virus.body_cards.append(move.virus)
    elif move.action == "health":
        player.hand_cards.remove(move.health)
        move.player_to_health.body_cards.append(move.health)
    elif move.action == "steal":
        player.hand_cards.remove(move.steal)
        card = move.player_to_steal.body_cards.get(move.steal)
        move.player_to_steal.body_cards.remove(move.steal)
        player.body_cards.append(card)
    elif move.action == "change_body":
        player.hand_cards.remove(move.change_body)
        body = player.body_cards
        player.body_cards = move.player_to_change.body_cards
        move.player_to_change.body_cards = body
    """

    game.turns += 1



    db.commit()
    db.refresh(game)

    if player:
        db.refresh(player)
    return game