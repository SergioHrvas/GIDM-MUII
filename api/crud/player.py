from sqlalchemy.orm import Session
from models.user import User
from schemas.user import UserCreate

def create_player(player: PlayerCreate, db: Session = Depends(get_db)):
    db_player = Player(hand_cards=player.hand_cards, body_cards=player.body_cards)
    db.add(db_player)
    db.commit()
    db.refresh(db_player)
    return db_player  # Se convierte automáticamente en PlayerResponse

    

    