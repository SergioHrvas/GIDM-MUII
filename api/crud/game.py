from operator import or_
from fastapi import Query
from sqlalchemy.orm import Session
from models.playercard import PlayerCard
from models.user import User
from schemas.status import StatusEnum
from models.organ import Organ
from models.game import Game
from models.player import Player
from models.deckcard import DeckCard
from models.card import Card
from schemas.game import GameBase
from datetime import datetime
from schemas.move import Move
from models.move import Move as MoveModel

import numpy as np
from crud.playercard import remove_card_from_player, discard_my_cards, discard_cards
from crud.organ import add_organ_to_player, player_has_organ, player_can_steal, add_virus_to_organ, add_cure_to_organ, player_has_organ_to_cure_infect, steal_card, change_body, change_organs, infect_players
from crud.deckcard import initialize_deck, steal_to_deck
import random

def create_game(game: GameBase, current_user: int, db: Session):
    if(len(game.players) <= 0):
        return "Error"
    db_game = Game(
        status = StatusEnum('pending'),  # Conversión explícita
        winner=0,
        turns=0,
        turn=0,
        num_turns=0,
        date=datetime.now(),
        multiplayer=game.multiplayer
    )
    db.add(db_game)
    db.commit()
    db.refresh(db_game)
    
    players = []
    
    # Creamos players
    for i in range(len(game.players)):  
        if game.multiplayer:
            db_user = db.query(User).filter(User.id == int(game.players[i])).first()
            if db_user is None:
                return "Error"            
            db_player = Player(name=db_user.username, game_id=db_game.id, user_id=int(game.players[i]))
        else:
            db_player = Player(name=game.players[i], game_id=db_game.id, user_id=current_user)

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


def get_organ_status(db, player_id, organ_type):
    organ = db.query(Organ).filter(
        Organ.player_id == player_id,
        Organ.tipo == organ_type
    ).first()
    
    if not organ:
        return 0
    
    if organ.cure == 3:
        return 3
    elif organ.cure in (1, 2):
        return 2
    elif organ.virus in (1, 2):
        return 1
    else:
        return 0


def do_move_game(game_id: int, player_id: int, move: Move, db: Session):
    # Obtenemos el juego
    game = db.query(Game).filter(Game.id == game_id).first()

    if game.winner != 0:
        return game
    
    done = False
    print("as")

    # Obtenemos el jugador
    player = db.query(Player).filter(Player.id == game.turn).first()
    
    if player_id != player.id:
        return "Error"

    card = db.query(Card).filter(Card.id == move.card).first()

    lungs_estado = get_organ_status(db, player_id, 'lungs')
    intestine_estado = get_organ_status(db, player_id, 'intestine')
    heart_estado = get_organ_status(db, player_id, 'heart')
    brain_estado = get_organ_status(db, player_id, 'brain')
    magic_estado = get_organ_status(db, player_id, 'magic')

    if move.action == "surrender":
        if(len(game.turns) == 2):
            # El jugador se rinde
            game.winner = game.turns[(game.turns.index(player_id) + 1) % 2]
            game.status = StatusEnum('finished')
    if move.action == "discard":
        # Borramos las cartas
        discard_my_cards(db, player_id, move.discards)
        done = True

        # Añadimos las nuevas cartas del mazo
        steal_to_deck(db, game, player_id, len(move.discards))

        # Creamos el movimiento
        db_move = MoveModel(
            player_id=player_id,
            game_id=game_id,
            date=datetime.now(),
            action="discard",
            data=move.discards,
            lungs_estado=lungs_estado,
            intestine_estado=intestine_estado,
            heart_estado=heart_estado,
            brain_estado=brain_estado,
            magic_estado=magic_estado,

        )
        db.add(db_move)
        db.commit()
        db.refresh(db_move)

    elif move.action == "card":
        # Hacemos el movimiento
        if card.tipo == "organ":
            has_organ = player_has_organ(db, player_id, card.organ_type)
            if has_organ == False:
                done = add_organ_to_player(db, player_id, card.organ_type)
                    
        if card.tipo == "virus":
            has_organ = player_has_organ_to_cure_infect(db, move.infect.player1, card.organ_type)
            if has_organ == True:
                done = add_virus_to_organ(db, move.infect.player1, card.organ_type, move.infect.organ1)
        elif card.tipo == "cure":
            has_organ = player_has_organ_to_cure_infect(db, player_id, card.organ_type)
            if has_organ == True:
                done = add_cure_to_organ(db, player_id, card.organ_type, move.infect.organ1)
                    
        elif card.tipo == "action":
            if card.name == "Steal Organ":
                can_steal = player_can_steal(db, player_id, move.infect.player1, move.infect.organ1)
                if can_steal == True:
                    done = steal_card(db, player_id, move.infect.player1, move.infect.organ1)
                    
            elif card.name == "Change Body":
                change_body(db, player_id, move.infect.player1)
                done = True
                
            elif card.name == "Infect Player":
                done = infect_players(db, player_id, move.infect)
                
            elif card.name == "Exchange Card":
                done = change_organs(db, player_id, move.organ_to_pass, move.infect.player1, move.infect.organ1)

            elif card.name == "Discard Cards":
                # Creamos el movimiento
                db_move = MoveModel(
                    player_id=player_id,
                    game_id=game_id,
                    card_id=move.card,
                    date=datetime.now(),
                    action="card",
                    data=move.infect.dict() if move.infect else None,
                    lungs_estado=lungs_estado,
                    intestine_estado=intestine_estado,
                    heart_estado=heart_estado,
                    brain_estado=brain_estado,
                    magic_estado=magic_estado,
                )
                db.add(db_move)

                for p in game.turns:
                    if p != player_id:
                        discards = []

                        # Obtenemos las cartas del jugador
                        player_cards = db.query(PlayerCard).filter(PlayerCard.player_id == p).all()
                        
                        for c in player_cards:
                            discards.append(c.card_id)
                            

                        discard_my_cards(db, p, discards)

                        steal_to_deck(db, game, p, 3)

                        db_move = MoveModel(
                            player_id=p,
                            game_id=game_id,
                            date=datetime.now(),
                            action="discard",
                            data=discards,
                            lungs_estado=get_organ_status(db, p, 'lungs'),
                            intestine_estado=get_organ_status(db, p, 'intestine'),
                            heart_estado=get_organ_status(db, p, 'heart'),
                            brain_estado=get_organ_status(db, p, 'brain'),
                            magic_estado=get_organ_status(db, p, 'magic'),
                        )

                        db.add(db_move)
                        db.commit()
                        db.refresh(db_move)

                        
                # Pasamos el turno al siguiente jugador
                num_turns = len(game.turns) - 1
                game.num_turns+=num_turns
                game.turn = player_id
                
                done = True
        
        if done:
            remove_card_from_player(db, player_id, move.card)
            steal_to_deck(db, game, player_id, 1)

            if card.name != "Discard Cards":
                # Creamos el movimiento
                db_move = MoveModel(
                    player_id=player_id,
                    game_id=game_id,
                    card_id=move.card,
                    date=datetime.now(),
                    action="card",
                    data=move.infect.dict() if move.infect else None,
                    lungs_estado=lungs_estado,
                    intestine_estado=intestine_estado,
                    heart_estado=heart_estado,
                    brain_estado=brain_estado,
                    magic_estado=magic_estado,
                )
                db.add(db_move)
                db.commit()
                db.refresh(db_move)

    # Revisamos si hay un ganador
    review_winner(db, game)
    
    if game.winner != 0:
        done = False

    if done:
        # Pasamos el turno al siguiente jugador
        game.num_turns+=1
        num_turns = len(game.turns)
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
        organs = db.query(Organ).filter(Organ.player_id == player.id, Organ.virus == 0).all()
        if len(organs) >= 4:
            game.winner = player.id
            game.status = StatusEnum('finished')  # Conversión explícita
            db.commit()
            db.refresh(game)


def get_player_games(id_user: int, db:Session):
    # Obtenemos los jugadores del usuario
    games = db.query(Game).join(Player, Game.players).filter(Player.user_id == id_user).all()
    
    return games




