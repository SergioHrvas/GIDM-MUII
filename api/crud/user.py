from sqlalchemy.orm import Session
from api.models.player import Player
from api.models.game import Game
from api.models.user import User
from api.schemas.user import UserBase
from api.utils.auth import hash_password, verify_password, create_access_token, get_user_by_email

def create_user(db: Session, user: UserBase):
    hashed_password = hash_password(user.password)
    user = User(
        username=user.username,
        email=user.email,
        password=hashed_password,
        name=user.name,
        last_name=user.last_name,
        image=user.image
    )
    db.add(user)
    db.commit()
    db.refresh(user)
    return user

def get_user(db: Session, id: int):
    # Get a user by ID
    user = db.query(User).filter(User.id == id).first()

    if user is None:
        return None
    
    # Get number of winned games by players' user (player has a user and a game)
    winned_games = db.query(Game).join(Player, Game.players).filter(Game.winner == Player.id, Player.user_id == id).count()    
    
    # Get players' user
    played_games = db.query(Player).filter(Player.user_id == id).count()

    # Append number of winned games to user
    user.winned_games = winned_games

    user.played_games = played_games
    
    return user
    
def login(db: Session, user: UserBase):
    usuario = get_user_by_email(db, user.email)


    if not usuario:
        return None
    else:
        loged = verify_password(user.password, usuario.password)

        # Get number of winned games by players' user (player has a user and a game)
        winned_games = db.query(Game).join(Player, Game.players).filter(Game.winner == Player.id, Game.multiplayer == True, Player.user_id == usuario.id).count()    
        
        # Get players' user
        played_games = db.query(Player).join(Player, Game.players).filter(Game.multiplayer == True, Player.user_id == usuario.id).count()

        # Append number of winned games to user
        usuario.winned_games = winned_games

        usuario.played_games = played_games
        if(loged):
            access_token = create_access_token({"sub": usuario.username, "email": user.email})
            return {
                "access_token": access_token,
                "token_type": "bearer",
                "user": usuario
            }
        else:
            return None
    
    
def modify_user(db: Session, user_id: int, user: UserBase):
    db_user = get_user(db, user_id)
    if db_user is None:
        return None

    if user.username:
        db_user.username = user.username
    if user.email:
        db_user.email = user.email
    if user.password:
        db_user.password = hash_password(user.password)
    if user.name:
        db_user.name = user.name
    if user.last_name:
        db_user.last_name = user.last_name
    if user.image:
        db_user.image = user.image

    
    db.commit()
    db.refresh(db_user)
    
    return db_user
