from sqlalchemy.orm import Session
from models.user import User
from schemas.user import UserCreate

def create_user(user: UserCreate, db: Session):
    fake_hashed_password = user.password + "nohashed"
    user = User(
        username=user.username,
        email=user.email,
        password=fake_hashed_password
    )
    db.add(user)
    db.commit()
    db.refresh(user)
    return user

def get_user(db: Session, id: int):
    return db.query(User).filter(User.id == id).first()
    