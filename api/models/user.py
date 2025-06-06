from sqlalchemy import Column, Integer, String
from api.database import Base
from sqlalchemy.orm import relationship

# Modelo Usuario
class User(Base):
    __tablename__ = 'users'

    id = Column(Integer, primary_key=True)
    image = Column(String, nullable=True)
    username = Column(String, unique=True)
    name = Column(String)
    last_name = Column(String)
    password = Column(String)
    email = Column(String, unique=True)

    # Relación con jugadores
    players = relationship("Player", back_populates="user")