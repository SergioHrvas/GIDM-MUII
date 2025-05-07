from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from utils.auth import get_current_user
from database import SessionLocal, init_db
from crud.game import get_game, create_game, do_move_game, get_player_games
from schemas.game import GameBase, GameResponse
from schemas.move import Move
from utils.db import get_db
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
    
