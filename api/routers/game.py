from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from data_analysis.recommendation import train_move_based_model
from models.card import Card
from utils.auth import get_current_user
from database import SessionLocal, init_db
from crud.game import get_game, create_game, do_move_game, get_player_games
from schemas.game import GameBase, GameResponse
from schemas.move import Move, MoveResponse
from utils.db import get_db
import pandas as pd

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
    user_id: int,
    db: Session = Depends(get_db),
    multiplayer: bool = False, num_turns: int = 0,
):
    # Obtener modelo (en producción se cargaría un modelo pre-entrenado)
    model, _ = train_move_based_model(db)
    
    # Obtener todas las cartas posibles
    all_cards = db.query(Card.id).all()
    
    # Predecir probabilidad de victoria para cada carta
    recommendations = []
    for card_id, in all_cards:
        input_data = pd.DataFrame([{
            'card_id': card_id,
            'multiplayer': multiplayer,
            'hour': datetime.now().hour,
            'num_turns': num_turns
        }])
        input_processed = pd.get_dummies(input_data)
        # Asegurar que tenga las mismas columnas que el entrenamiento
        input_processed = input_processed.reindex(columns=model.feature_names_in_, fill_value=0)
        proba = model.predict_proba(input_processed)[0][1]
        recommendations.append((card_id, proba))
    
    # Ordenar por probabilidad descendente
    recommendations.sort(key=lambda x: -x[1])
    print(recommendations)
    return {
        "user_id": user_id,
        "recommendations": [{
            "card_id": card_id,
            "win_probability": round(prob, 4)
        } for card_id, prob in recommendations[:10]]  # Top 10
    }