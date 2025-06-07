from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from api.utils.auth import get_current_user
from api.crud.game import get_game, create_game, do_move_game, get_player_games, get_recommendations
from api.schemas.game import GameBase, GameResponse
from api.schemas.move import Move, MoveResponse
from api.utils.db import get_db

router = APIRouter()

# POST(/game) - Ruta para crear una partida
@router.post("/", response_model=GameResponse)
def create_new_game(game: GameBase, current_user: int = Depends(get_current_user),db: Session = Depends(get_db)):

    #Creamos el juego
    return create_game(game, current_user.id, db)


# GET(/game/my-games) - Ruta para obtener los juegos del jugador del token
@router.get("/my-games", response_model=list[GameResponse])
def get_my_games(current_user: int = Depends(get_current_user), db: Session = Depends(get_db)):

    # Obtenemos los juegos del jugador actual
    games = get_player_games(current_user.id, db)

    if games is None:
        raise HTTPException(status_code=404, detail="Games not found")
    if len(games) == 0:
        raise HTTPException(status_code=404, detail="No games found")
    
    return games


# GET(/game/recommend) - Ruta para obtener las recomendaciones para un jugador
@router.get("/recommend")
async def recommend_cards(player_id: int, db: Session = Depends(get_db)):

    return {
        "player_id": player_id,
        "recommendations": get_recommendations(player_id, db) # Obtenemos las recomendaciones
    }

# GET(/game/moves) - Ruta para obtener los movimientos de una partida
@router.get("/{game_id}/moves", response_model=list[MoveResponse])
def get_moves(game_id: int, db: Session = Depends(get_db)):

    # Obtenemos los movimientos del juego
    moves = get_game(game_id, db).moves

    if moves is None:
        raise HTTPException(status_code=404, detail="Moves not found")
    
    print(len(moves))
    return moves

def model_to_dict(model):
    if not hasattr(model, '__table__'):  # No es un modelo SQLAlchemy
        return model
    
    result = {}
    for column in model.__table__.columns:
        result[column.name] = getattr(model, column.name)
    
    # Añadir relaciones (opcional)
    for relationship in model.__mapper__.relationships:
        related_obj = getattr(model, relationship.key)
        if related_obj is not None:
            if relationship.uselist:  # Relación uno-a-muchos
                result[relationship.key] = [model_to_dict(obj) for obj in related_obj]
            else:  # Relación uno-a-uno
                result[relationship.key] = model_to_dict(related_obj)
    return result

# GET(/game/{id}) - Ruta para obtener una partida
@router.get("/{game_id}", response_model=GameResponse)
def get_game_by_id(game_id: int, db: Session = Depends(get_db)):

    # Obtenemos el juego
    game = get_game(game_id, db)

    if game is None:
        raise HTTPException(status_code=404, detail="Game not found")
    
    return game


# POST(/game/{id}/move) - Ruta para hacer un movimiento
@router.post("/{game_id}/move", response_model=GameResponse)
def do_move(game_id: int, player_id: int, move: Move, db: Session = Depends(get_db)):

    # Hacemos el movimiento
    do_move_game(game_id, player_id, move, db)

    # Obtenemos el juego tras el movimiento
    game = get_game(game_id, db)

    if game is None:
        raise HTTPException(status_code=404, detail="Game not found")
    
    return game
