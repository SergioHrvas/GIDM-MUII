from typing import Optional
from pydantic import BaseModel, EmailStr

class UserBase(BaseModel):
    id: int
    username: str
    email: EmailStr
    password: str
    name: Optional[str] = None
    last_name: Optional[str] = None
    image: Optional[str] = None

class UserUpdate(BaseModel):
    username: Optional[str] = None
    email: Optional[EmailStr] = None
    password: Optional[str] = None
    name: Optional[str] = None
    last_name: Optional[str] = None
    image: Optional[str] = None

class UserLogin(BaseModel):
    email: EmailStr
    password: str

class UserResponse(UserBase):
    id: int
    winned_games: Optional[int] = None
    played_games: Optional[int] = None
    token: Optional[str] = None
    
    class Config:
        from_attributes = True

class AuthResponse(BaseModel):
    access_token: str
    token_type: str
    user: UserResponse

class TokenUser(BaseModel):
    email: Optional[str] = None