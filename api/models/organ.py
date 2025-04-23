from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Boolean, CheckConstraint
from sqlalchemy.types import Enum as SQLAlchemyEnum
from database import Base
from schemas.status import StatusEnum
from sqlalchemy.orm import relationship
from sqlalchemy.dialects.postgresql import JSONB  # Usar JSONB en PostgreSQL
from enum import Enum
from schemas.organtype import OrganType



# 📌 Tabla de órganos
class Organ(Base):
    __tablename__ = 'organs'

    id = Column(Integer, primary_key=True, index=True)
    player_id = Column(Integer, ForeignKey("players.id", ondelete="CASCADE"))
    tipo = Column(SQLAlchemyEnum(OrganType), nullable=False)
    virus = Column(Integer)
    cure = Column(Integer)
    magic_organ = Column(Integer, default=0)

    # Relación con Player (ahora directamente, sin BodyPlayer)
    player = relationship("Player", back_populates="organs")

    __table_args__ = (
        CheckConstraint('cure BETWEEN 0 AND 2', name='cure_value'),
    )