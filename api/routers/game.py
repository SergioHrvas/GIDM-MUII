from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database import SessionLocal, init_db
from crud.game import get_game, create_game
from schemas.game import GameCreate, GameResponse

router = APIRouter()

# Obtener una sesión de base de datos
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.post("/", response_model=GameResponse)
def create_new_game(game: GameCreate, db: Session = Depends(get_db)):
    return create_game(game, db)

@router.get("/{game_id}", response_model=GameResponse)
def get_game_by_id(game_id: int, db: Session = Depends(get_db)):
    game = get_game(game_id, db)
    if game is None:
        raise HTTPException(status_code=404, detail="Game not found")
    return game