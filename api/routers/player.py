from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database import SessionLocal, init_db
from schemas.player import PlayerCreate, PlayerResponse
from utils.db import get_db

router = APIRouter()
