from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy import null
from sqlalchemy.orm import Session
from models.playercard import PlayerCard
from models.game import Game
from data_analysis.recommendation import train_move_based_model
from models.card import Card
from utils.auth import get_current_user
from database import SessionLocal, init_db
from crud.game import get_game, create_game, do_move_game, get_player_games
from schemas.game import GameBase, GameResponse
from schemas.move import Move, MoveResponse
from utils.db import get_db
import pandas as pd
from models.move import Move as MoveModel

router = APIRouter()

@router.post("/", response_model=GameResponse)
def create_new_game(game: GameBase, current_user: int = Depends(get_current_user),db: Session = Depends(get_db)):
    return create_game(game, current_user.id, db)

@router.get("/my-games", response_model=list[GameResponse])
def get_my_games(current_user: int = Depends(get_current_user), db: Session = Depends(get_db)):
    games = get_player_games(current_user.id, db)

    print(games[0].date)
    if games is None:
        raise HTTPException(status_code=404, detail="Games not found")
    if len(games) == 0:
        raise HTTPException(status_code=404, detail="No games found")
    
    return games


@router.get("/{game_id}/moves", response_model=list[MoveResponse])
def get_moves(game_id: int, db: Session = Depends(get_db)):
    moves = get_game(game_id, db).moves

    print(len(moves))
    if moves is None:
        raise HTTPException(status_code=404, detail="Moves not found")
    return moves
    


@router.get("/{game_id}", response_model=GameResponse)
def get_game_by_id(game_id: int, db: Session = Depends(get_db)):
    game = get_game(game_id, db)
    if game is None:
        raise HTTPException(status_code=404, detail="Game not found")

    return game

@router.post("/{game_id}/move", response_model=GameResponse)
def do_move(game_id: int, player_id: int, move: Move, db: Session = Depends(get_db)):
    do_move_game(game_id, player_id, move, db)

    game = get_game(game_id, db)
    if game is None:
        raise HTTPException(status_code=404, detail="Game not found")
    return game

@router.get("/recommend/cards")
async def recommend_cards(
    player_id: int,
    db: Session = Depends(get_db),
):
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
        num_virus = last_move.num_virus
        num_cure = last_move.num_cure
        num_protected = last_move.num_protected
        num_organs = last_move.num_organs
        move_sequence = db.query(MoveModel).filter(
            MoveModel.game_id == game.id,
            MoveModel.player_id == player_id
        ).count() + 1
    else:
        num_virus = num_cure = num_protected = num_organs = 0
        move_sequence = 1




    # Predecir probabilidad de victoria para cada carta
    recommendations = []
    for card in all_cards:
        input_data = pd.DataFrame([{
            'card_id': card.id,
            'tipo': str(card.tipo or ''),
            'move_sequence': move_sequence,
            'num_virus': num_virus,
            'num_cure': num_cure,
            'num_protected': num_protected,
            'num_organs': num_organs
        }])

        # directamente pasas al pipeline
        proba = model.predict_proba(input_data)[0][1]
        recommendations.append((card.id, proba))

    # Ordenar por probabilidad descendente
    recommendations.sort(key=lambda x: -x[1])

    # Filtrar solo las cartas que tiene el jugador en mano
    player_cards = db.query(PlayerCard).filter(PlayerCard.player_id == player_id).all()
    player_cards_ids = [pc.card_id for pc in player_cards]
    print(recommendations)

    recommendations = [(card_id, prob) for card_id, prob in recommendations if card_id in player_cards_ids]
    
    return {
        "player_id": player_id,
        "recommendations": [{
            "card_id": card_id,
            "win_probability": round(prob, 4)
        } for card_id, prob in recommendations[:3]]
    }