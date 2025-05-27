from fastapi import APIRouter, Depends, HTTPException, Request, status
from sqlalchemy.orm import Session
from models.user import User
from utils.auth import get_current_user
from database import SessionLocal, init_db
from crud.user import get_user, create_user, login, modify_user
from schemas.user import UserBase, UserLogin, UserResponse, AuthResponse, UserUpdate
from utils.db import get_db

router = APIRouter()


@router.post("/", response_model=UserResponse)
def create_new_user(user: UserBase, db: Session = Depends(get_db)):
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

@router.put("/{user_id}", response_model=UserResponse)
async def update_user(
    user_id: int,
    user: UserUpdate,
    request: Request,  # Añade esto
    db: Session = Depends(get_db),
    current_user: UserBase = Depends(get_current_user)
):
    print("Headers recibidos:", request.headers)  # Verifica los headers
    modify_user(db, user_id, user)
    return get_user(db, user_id)
    modify_user(db, user_id, user)

    db_user = get_user(db, user_id)

    return db_user

@router.delete("/{user_id}", response_model=UserResponse)
def delete_user(user_id: int, db: Session = Depends(get_db), current_user: UserBase = Depends(get_current_user)):
    db_user = get_user(db, user_id)
    if db_user is None:
        raise HTTPException(status_code=404, detail="User not found")
    
    db.delete(db_user)
    db.commit()
    return db_user