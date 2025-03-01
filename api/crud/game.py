from sqlalchemy.orm import Session
from models.game import Game
from models.player import Player
from models.deck import DeckCard
from models.card import Card
from schemas.game import GameCreate
from datetime import datetime
from schemas.move import Move
import numpy as np
from crud.playercard import remove_card_from_player, discard_cards
from crud.organ import add_organ_to_player, player_has_organ, player_can_steal, add_virus_to_organ, add_cure_to_organ, steal_card, change_body, change_organs, infect_players
from crud.deck import initialize_deck
import random

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

    db.commit()
    db.refresh(db_game)

    initialize_deck(db, db_game)

    
    return db_game  # Se convierte automáticamente en GameResponse


def get_game(game_id: int, db: Session):
    return db.query(Game).filter(Game.id == game_id).first()

def do_move_game(game_id: int, player_id: int, move: Move, db: Session):
    # Obtenemos el juego
    game = db.query(Game).filter(Game.id == game_id).first()

    # Obtenemos el mazo
    # deck = db.query(Deck).filter(Deck.id == game.deck_id).first()

    # Obtenemos el jugador
    player = db.query(Player).filter(Player.id == game.turn).first()
    
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
            has_organ = player_has_organ(db, player_id, card.organ_type)

            if has_organ == False:
                add_organ_to_player(db, player_id, card.organ_type)
                remove_card_from_player(db, player_id, move.card)

        if card.tipo == "virus":
            has_organ = player_has_organ(db, move.player_to, card.organ_type)
            
            if has_organ == True:
                add_virus_to_organ(db, move.player_to, card.organ_type)
                remove_card_from_player(db, player_id, move.card)

        elif card.tipo == "cure":
            has_organ = player_has_organ(db, player_id, card.organ_type)
            
            if has_organ == True:
                add_cure_to_organ(db, player_id, card.organ_type)
                remove_card_from_player(db, player_id, move.card)

        elif card.tipo == "action":
            if card.name == "Steal Organ":
                can_steal = player_can_steal(db, player_id, move.player_to, move.organ_to_steal)
                if can_steal == True:
                    steal_card(db, player_id, move.player_to, move.organ_to_steal)
                    remove_card_from_player(db, player_id, move.card)  

            elif card.name == "Change Body":
                change_body(db, player_id, move.player_to)
                remove_card_from_player(db, player_id, move.card)  

            elif card.name == "Infect Player":
                infect_players(db, player_id, move.infect)
                remove_card_from_player(db, player_id, move.card)  

            elif card.name == "Exchange Card":
                done = change_organs(db, player_id, move.organ_to_pass, move.player_to, move.organ_to_steal)
                if done:
                    remove_card_from_player(db, player_id, move.card)  

            elif card.name == "Discard Cards":
                #discard_cards(db, game_id, player_id)
                #remove_card_from_player(db, player_id, move.card)
                pass
        """


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