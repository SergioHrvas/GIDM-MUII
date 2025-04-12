from sqlalchemy.orm import Session
from models.organ import Organ
from models.game import Game
from models.player import Player
from models.deckcard import DeckCard
from models.card import Card
from schemas.game import GameCreate
from datetime import datetime
from schemas.move import Move
import numpy as np
from crud.playercard import remove_card_from_player, discard_my_cards, discard_cards
from crud.organ import add_organ_to_player, player_has_organ, player_can_steal, add_virus_to_organ, add_cure_to_organ, steal_card, change_body, change_organs, infect_players
from crud.deckcard import initialize_deck, steal_to_deck
import random

def create_game(game: GameCreate, current_user: int, db: Session):
    if(game.players <= 0):
        return "Error"
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
        db_player = Player(name="", game_id=db_game.id, user_id=current_user)
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

    # Obtenemos el jugador
    player = db.query(Player).filter(Player.id == game.turn).first()
    
    if player_id != player.id:
        return "Error"

    card = db.query(Card).filter(Card.id == move.card).first()

    if move.action == "discard":
        # Borramos las cartas
        discard_my_cards(db, player_id, move.discards)

        # Añadimos las nuevas cartas del mazo
        steal_to_deck(db, game, player_id, len(move.discards))

    elif move.action == "card":
        # Hacemos el movimiento
        if card.tipo == "organ":
            has_organ = player_has_organ(db, player_id, card.organ_type)

            if has_organ == False:
                add_organ_to_player(db, player_id, card.organ_type)
                remove_card_from_player(db, player_id, move.card)
                
                # Añadimos las nuevas cartas del mazo
                steal_to_deck(db, game, player_id, 1)

                print(f"ORGANO {card.organ_type} PUESTO DE {player_id} EN {game.id}")

        if card.tipo == "virus":
            has_organ = player_has_organ(db, move.player_to, card.organ_type)
            
            if has_organ == True:
                add_virus_to_organ(db, move.infect.player1, card.organ_type, move.infect.organ1)
                remove_card_from_player(db, player_id, move.card)

                # Añadimos las nuevas cartas del mazo
                steal_to_deck(db, game, player_id, 1)

        elif card.tipo == "cure":
            has_organ = player_has_organ(db, player_id, card.organ_type)
            
            if has_organ == True:
                add_cure_to_organ(db, player_id, card.organ_type)
                remove_card_from_player(db, player_id, move.card)

                # Añadimos las nuevas cartas del mazo
                steal_to_deck(db, game, player_id, 1)

        elif card.tipo == "action":
            if card.name == "Steal Organ":
                can_steal = player_can_steal(db, player_id, move.player_to, move.organ_to_steal)
                if can_steal == True:
                    steal_card(db, player_id, move.player_to, move.organ_to_steal)
                    remove_card_from_player(db, player_id, move.card)  

                    # Añadimos las nuevas cartas del mazo
                    steal_to_deck(db, game, player_id, 1)

            elif card.name == "Change Body":
                change_body(db, player_id, move.player_to)
                remove_card_from_player(db, player_id, move.card)  

                # Añadimos las nuevas cartas del mazo
                steal_to_deck(db, game, player_id, 1)
                
            elif card.name == "Infect Player":
                infect_players(db, player_id, move.infect)
                remove_card_from_player(db, player_id, move.card)  

                # Añadimos las nuevas cartas del mazo
                steal_to_deck(db, game, player_id, 1)

            elif card.name == "Exchange Card":
                done = change_organs(db, player_id, move.organ_to_pass, move.player_to, move.organ_to_steal)
                if done:
                    remove_card_from_player(db, player_id, move.card)  

                    # Añadimos las nuevas cartas del mazo
                    steal_to_deck(db, game, player_id, 1)
            elif card.name == "Discard Cards":
                discard_cards(db, game_id, player_id)
                remove_card_from_player(db, player_id, move.card)

                # Añadimos las nuevas cartas del mazo
                steal_to_deck(db, game, player_id, 1)
                pass


        game.num_turns+=1


        num_turns = len(game.turns)

        #VER
        game.turn = game.turns[game.num_turns % num_turns]

        db.commit()
        db.refresh(game)

        if player:
            db.refresh(player)
    return game

def review_winner(db: Session, game):
    # Jugadores del juego
    players = db.query(Player).filter(Player.game_id == game.id)

    for player in players:
        organs = db.query(Organ).filter(Organ.player_id == player.id, Organ.virus == False)
        if len(organs) >= 4:
            game.winner = player.id
            game.status = "finished"
            db.commit()
            db.refresh(game)
            pass


def get_player_games(id_user: int, db:Session):
    # Obtenemos los jugadores del usuario
    games = db.query(Game).join(Player, Game.players).filter(Player.user_id == id_user).all()

    return games




