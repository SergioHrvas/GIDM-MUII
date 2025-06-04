from sqlalchemy.orm import Session
from api.data_analysis.recommendation import train_move_based_model
from api.models.playercard import PlayerCard
from api.models.user import User
from api.schemas.status import StatusEnum
from api.models.organ import Organ
from api.models.game import Game
from api.models.player import Player
from api.models.card import Card
from api.schemas.game import GameBase
from datetime import datetime
from api.schemas.move import Move
from api.models.move import Move as MoveModel

import numpy as np
import pandas as pd

from api.crud.playercard import remove_card_from_player, discard_my_cards, discard_cards
from api.crud.organ import add_organ_to_player, player_has_organ, player_can_steal, add_virus_to_organ, add_cure_to_organ, player_has_organ_to_cure_infect, steal_card, change_body, change_organs, infect_players
from api.crud.deckcard import initialize_deck, steal_to_deck

# Función para crear una partida
def create_game(game: GameBase, current_user: int, db: Session):
    # Si no hay jugadores, no se puede crear el juego

    if(len(game.players) <= 0):
        return "Error. No hay jugadores para crear el juego."
    
    # Creamos el juego base
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

    # Creamos los jugadores    
    players = []
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

    # Mezclamos los jugadores y creamos los turnos
    np.random.shuffle(players)
    db_game.turns = [player.id for player in players]
    db_game.turn = db_game.turns[0]
    db.commit()
    db.refresh(db_game)

    # Inicializamos el tablero con las cartas del mazo
    initialize_deck(db, db_game)
    
    return db_game  # Se convierte automáticamente en GameResponse


# Función para obtener un juego por id
def get_game(game_id: int, db: Session):

    # Consulta a la base de datos del juego que tenga el id {game_id}
    return db.query(Game).filter(Game.id == game_id).first()


# Función para obtener el estado de un órgano
def get_organ_status(db, player_id, organ_type):

    # Obtenemos el órgano del jugador {player_id} de tipo {organ_type}
    organ = db.query(Organ).filter(
        Organ.player_id == player_id,
        Organ.tipo == organ_type
    ).first()
    
    # Si no hay órgano, devolvemos 0
    if not organ:
        return 0
    
    if organ.cure == 3: # Si el órgano está inmunizado, devolvemos 3
        return 3 
    elif organ.cure in (1, 2): # Si el órgano tiene una cura, devolvemos 2
        return 2 
    elif organ.virus in (1, 2): # Si el órgano tiene un virus, devolvemos 1
        return 1
    else: # En cualquier otro caso, devolvemos 0
        return 0


# Función para hacer un movimiento en el juego
def do_move_game(game_id: int, player_id: int, move: Move, db: Session):
    
    # Obtenemos el juego
    game = db.query(Game).filter(Game.id == game_id).first()

    # Si hay ganador, devolvemos el juego (no se hace el movimiento)
    if game.winner != 0:
        return game
    
    # Inicializamos "done" a false
    done = False
    
    # Obtenemos el jugador del turno actual
    player = db.query(Player).filter(Player.id == game.turn).first()
    
    # Si el jugador que realiza el movimiento no coincide con el del turno actual, no puede hacer el movimiento
    if player_id != player.id:
        return "Error. No es su turno."

    # Obtenemos la carta jugada
    card = db.query(Card).filter(Card.id == move.card).first()

    # Obtenemos los estados de los órganos
    lungs_estado = get_organ_status(db, player_id, 'lungs')
    intestine_estado = get_organ_status(db, player_id, 'intestine')
    heart_estado = get_organ_status(db, player_id, 'heart')
    brain_estado = get_organ_status(db, player_id, 'brain')
    magic_estado = get_organ_status(db, player_id, 'magic')

    # Si el jugador se rinde y son dos jugadores
    if move.action == "surrender":
        if(len(game.turns) == 2):
            # Fijamos como ganador a la otra persona
            game.winner = game.turns[(game.turns.index(player_id) + 1) % 2]
            game.status = StatusEnum('finished')
    # Si el jugador decide descartar cartas
    if move.action == "discard":
        # Descartamos las cartas
        discard_my_cards(db, player_id, move.discards)

        # Añadimos las nuevas cartas del mazo a la mano del jugador
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
        
        # Marcamos el movimiento como hecho
        done = True

    # Si el jugador juega una carta
    elif move.action == "card":
        
        # Si es tipo órgano
        if card.tipo == "organ":
            has_organ = player_has_organ(db, player_id, card.organ_type)
            if has_organ == False:
                done = add_organ_to_player(db, player_id, card.organ_type)
        
        # Si es tipo virus
        elif card.tipo == "virus":
            has_organ = player_has_organ_to_cure_infect(db, move.infect.player1, card.organ_type)
            if has_organ == True:
                done = add_virus_to_organ(db, move.infect.player1, card.organ_type, move.infect.organ1)
        
        # Si es tipo cura
        elif card.tipo == "cure":
            has_organ = player_has_organ_to_cure_infect(db, player_id, card.organ_type)
            if has_organ == True:
                done = add_cure_to_organ(db, player_id, card.organ_type, move.infect.organ1)
        
        # Si es tipo acción
        elif card.tipo == "action":

            # Si es robar carta
            if card.name == "Steal Organ":
                can_steal = player_can_steal(db, player_id, move.infect.player1, move.infect.organ1)
                if can_steal == True:
                    done = steal_card(db, player_id, move.infect.player1, move.infect.organ1)
            
            # Si es cambiar cuerpo
            elif card.name == "Change Body":
                change_body(db, player_id, move.infect.player1)
                done = True
                
            # Si es infectar jugador
            elif card.name == "Infect Player":
                done = infect_players(db, player_id, move.infect)
                
            # Si es intercambiar carta
            elif card.name == "Exchange Card":
                done = change_organs(db, player_id, move.organ_to_pass, move.infect.player1, move.infect.organ1)

            # Si es descartar cartas
            elif card.name == "Discard Cards":

                # Creamos el movimiento de descarte
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

                # Para cada jugador que le toque el turno y que no sea el jugador actual
                for p in game.turns:
                    if p != player_id:

                        discards = []

                        # Obtenemos las cartas del jugador
                        player_cards = db.query(PlayerCard).filter(PlayerCard.player_id == p).all()
                        
                        # Añadimos la carta a los descartes
                        for c in player_cards:
                            discards.append(c.card_id)
                        
                        # Descartamos las cartas
                        discard_my_cards(db, p, discards)

                        # Roban del mazo
                        steal_to_deck(db, game, p, 3)

                        # Hacen movimiento de descartar
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
                
                # Marcamos done a True
                done = True
        
        # Si el movimiento se ha hecho
        if done:
            # Eliminamos la carta del jugador
            remove_card_from_player(db, player_id, move.card)
            
            # Robamos una carta del mazo
            steal_to_deck(db, game, player_id, 1)

            # Creamos el movimiento si no es Discard (ya se ha creado anteriormente)
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
        if(card and card.name == "Discard Cards"):
            # Fijamos el turno de nuevo al jugador y aumentamos el número de turnos hechos
            num_turns = len(game.turns)
            game.num_turns+=num_turns
            game.turn = player_id
        else:
            # Pasamos el turno al siguiente jugador
            game.num_turns+=1
            num_turns = len(game.turns)
            game.turn = game.turns[game.num_turns % num_turns]

    db.commit()
    db.refresh(game)

    # Refrescamos el jugador
    if player:
        db.refresh(player)

    return game


# Función para revisar si hay un ganador
def review_winner(db: Session, game):
    # Obtenemos los jugadores del juego
    players = db.query(Player).filter(Player.game_id == game.id)

    # Para cada jugador, obtenemos sus órganos sin virus
    for player in players:
        organs = db.query(Organ).filter(Organ.player_id == player.id, Organ.virus == 0).all()
        # Si tiene 4 o más órganos sin virus, el juego acaba y fijamos ganador
        if len(organs) >= 4:
            game.winner = player.id
            game.status = StatusEnum('finished')  # Conversión explícita
            db.commit()
            db.refresh(game)


# Función para obtener las partidas del usuario
def get_player_games(id_user: int, db:Session):
    # Obtenemos los jugadores del usuario
    games = db.query(Game).join(Player, Game.players).filter(Player.user_id == id_user).all()
    
    return games

# Función para obtener las recomendaciones de carta
def get_recommendations(player_id: int, db: Session):
    # Obtener modelo entrenado (en producción se cargaría desde disco o cache)
    model, _ = train_move_based_model(db)

    # Obtener todas las cartas posibles
    all_cards = db.query(Card).all()

    # Obtener el juego del jugador
    game = db.query(Game).filter(Game.players.any(id=player_id)).first()

    # Obtener el último movimiento del jugador para estimar estado actual
    last_move = db.query(MoveModel)\
        .filter(MoveModel.player_id == player_id)\
        .order_by(MoveModel.date.desc())\
        .first()

    # Si no hay movimientos aún, usar ceros
    if last_move:
        brain_estado = last_move.brain_estado
        lungs_estado = last_move.lungs_estado
        intestine_estado = last_move.intestine_estado
        heart_estado = last_move.heart_estado
        magic_estado = last_move.magic_estado
        move_sequence = db.query(MoveModel).filter(
            MoveModel.game_id == game.id,
            MoveModel.player_id == player_id
        ).count() + 1
    else:
        brain_estado = 0
        lungs_estado = 0
        intestine_estado = 0
        heart_estado = 0
        magic_estado = 0
        move_sequence = 1

    # Predecir probabilidad de victoria para cada carta
    recommendations = []
    for card in all_cards:
        input_data = pd.DataFrame([{
            'card_id': card.id,
            'tipo': str(card.tipo or ''),
            'move_sequence': move_sequence,
            'brain_estado': brain_estado,
            'lungs_estado': lungs_estado,
            'intestine_estado': intestine_estado,
            'heart_estado': heart_estado,
            'magic_estado': magic_estado
        }])

        proba = model.predict_proba(input_data)[0][1]
        recommendations.append((card.id, proba))

    # Ordenar por probabilidad descendente
    recommendations.sort(key=lambda x: -x[1])

    # Filtrar solo las cartas que tiene el jugador en mano
    player_cards = db.query(PlayerCard).filter(PlayerCard.player_id == player_id).all()
    player_cards_ids = [pc.card_id for pc in player_cards]

    recommendations = [(card_id, prob) for card_id, prob in recommendations if card_id in player_cards_ids]

    # Devolvemos las recomendaciones
    return [{
        "card_id": card_id,
        "card_name": db.query(Card).filter(Card.id == card_id).first().name,
        "win_probability": round(prob, 4)
    } for card_id, prob in recommendations[:3]]
