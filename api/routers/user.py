from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from models.user import User
from utils.auth import get_current_user
from database import SessionLocal, init_db
from crud.user import get_user, create_user, login
from schemas.user import UserBase, UserLogin, UserCreate, UserResponse, AuthResponse
from utils.db import get_db

router = APIRouter()


@router.post("/", response_model=UserResponse)
def create_new_user(user: UserCreate, db: Session = Depends(get_db)):
    return create_user(db, user)


@router.get("/users", response_model=list[UserResponse])
def read_all_users(db: Session = Depends(get_db)):
    db_users = db.query(User).all()
    if db_users is None:
        raise HTTPException(status_code=404, detail="Users not found")
    return db_users


@router.get("/{user_id}", response_model=UserResponse)
def read_user(user_id: int, db: Session = Depends(get_db), current_user: UserBase = Depends(get_current_user)):
    db_user = get_user(db, user_id)
    if db_user is None:
        raise HTTPException(status_code=404, detail="User not found")
    return db_user