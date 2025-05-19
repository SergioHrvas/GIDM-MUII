from sqlalchemy.orm import Session
from models.player import Player
from models.game import Game
from models.user import User
from schemas.user import UserCreate, UserBase
from utils.auth import hash_password, verify_password, create_access_token, get_user_by_email

def create_user(db: Session, user: UserCreate):
    hashed_password = hash_password(user.password)
    user = User(
        username=user.username,
        email=user.email,
        password=hashed_password
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
    
    print(winned_games)

    # Append number of winned games to user
    user.winned_games = winned_games

    return user
    
def login(db: Session, user: UserBase):
    usuario = get_user_by_email(db, user.email)


    if not usuario:
        return None
    else:
        loged = verify_password(user.password, usuario.password)

        if(loged):
            access_token = create_access_token({"sub": usuario.username, "email": user.email})
            return {
                "access_token": access_token,
                "token_type": "bearer",
                "user": usuario
            }
        else:
            return None
    

