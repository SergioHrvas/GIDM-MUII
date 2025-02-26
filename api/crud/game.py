from sqlalchemy.orm import Session
from models.game import Game
from models.player import Player
from models.deck import Deck
from models.card import Card
from schemas.game import GameCreate
from datetime import datetime
from schemas.move import Move
import numpy as np
from crud.playercard import remove_card_from_player, discard_cards
from crud.organ import add_organ_to_player

def create_game(game: GameCreate, db: Session):
    db_game = Game(
        status=game.status,
        winner=0,
        turns=0,
        turn=0,
        num_turns=0,
        date=datetime.now()  
    )
    db.add(db_game)
    db.commit()
    db.refresh(db_game)
    
    players = []
    # Creamos players
    for i in range(game.players):  
        db_player = Player(name="", game_id=db_game.id)
        db.add(db_player)
        db.commit()
        db.refresh(db_player) 
        players.append(db_player)

    np.random.shuffle(players)
    db_game.turns = [player.id for player in players]
    db_game.turn = db_game.turns[0]

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

def do_move_game(game_id: int, player_id: int, move: Move, db: Session):
    # Obtenemos el juego
    game = db.query(Game).filter(Game.id == game_id).first()

    # Obtenemos el mazo
    deck = db.query(Deck).filter(Deck.id == game.deck_id).first()

    # Obtenemos el jugador
    player = db.query(Player).filter(Player.id == game.turn).first()
    print(game.turn)
    
    if player_id != player.id:
        return "Error"

    card = db.query(Card).filter(Card.id == move.card).first()

    if move.action == "discard":
        # Borramos las cartas
        discard_cards(db, player_id)

        # Añadimos tres nuevas cartas del mazo
        ## PENDIENTE

    elif move.action == "card":
        # Hacemos el movimiento
        if card.tipo == "organ":
            print("ORGAAAAAAAAN")
            remove_card_from_player(db, player_id, move.card)
            add_organ_to_player(db, player_id, card.organ_type)

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


        game.num_turns+=1
        num_turns = len(game.turns)
        print(f"Número de turnos: {num_turns}")

        #VER
        game.turn = game.turns[game.num_turns % num_turns]

        print(game.turn)

        db.commit()
        db.refresh(game)

        if player:
            db.refresh(player)    """
    return game