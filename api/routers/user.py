from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from api.models.user import User
from api.utils.auth import get_current_user
from api.crud.user import get_user, create_user, modify_user
from api.schemas.user import UserBase, UserResponse, UserUpdate
from api.utils.db import get_db

router = APIRouter()

# POST(/user/register) - Ruta para registrar un nuevo usuario
@router.post("/register", response_model=UserResponse)
def create_new_user(user: UserBase, db: Session = Depends(get_db)):

    #Creamos el usuario
    return create_user(db, user)


# GET(/user/users) - Ruta para obtener lista de usuarios
@router.get("/users", response_model=list[UserResponse])
def read_all_users(db: Session = Depends(get_db)):

    # Obtenemos los usuarios
    db_users = db.query(User).all()

    if db_users is None:
        raise HTTPException(status_code=404, detail="Users not found")
    
    return db_users


# GET(/user/{user}) - Ruta para obtene un usuario
@router.get("/{user_id}", response_model=UserResponse)
def read_user(user_id: int, db: Session = Depends(get_db), current_user: UserBase = Depends(get_current_user)):

    # Obtenemos el usuario
    db_user = get_user(db, user_id)

    if db_user is None:
        raise HTTPException(status_code=404, detail="User not found")
    
    return db_user


# PUT(/user/{user}) - Ruta para modificar un usuario
@router.put("/{user_id}", response_model=UserResponse)
async def update_user(user_id: int, user: UserUpdate, db: Session = Depends(get_db), current_user: UserBase = Depends(get_current_user)):
    # Modificamos el usuario
    modify_user(db, user_id, user)

    # Obtenemos el usuario modificado
    db_user = get_user(db, user_id)
    
    return db_user


# DELETE(/user/{user}) - Eliminar usuario
@router.delete("/{user_id}", response_model=UserResponse)
def delete_user(user_id: int, db: Session = Depends(get_db), current_user: UserBase = Depends(get_current_user)):
    
    # Obtenemos el usuario a eliminar
    db_user = get_user(db, user_id)
    
    if db_user is None:
        raise HTTPException(status_code=404, detail="User not found")
    
    # Eliminamos el usuario
    db.delete(db_user)
    db.commit()
    
    return db_user