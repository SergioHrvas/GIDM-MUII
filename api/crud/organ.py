from sqlalchemy.orm import Session
from models.organ import Organ
from models.player import Player
from models.card import Card

def remove_organ_from_player(db: Session, player_id: int, tipo: str):
    # Buscar el registro de player_cards que relaciona el jugador con la carta
    db_organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == tipo).first()
    
    if db_organ:
        db.delete(db_organ)  # Eliminar el registro de la relación
        db.commit()  # Confirmar la transacción
    else:
        print("No se encontró el órgano para el jugador.")

def add_organ_to_player(db: Session, player_id, tipo: str):
    db_organ = Organ(
        player_id = player_id,
        tipo = tipo
    )

    if db_organ:
        db.add(db_organ)  # Eliminar el registro de la relación
        db.commit()  # Confirmar la transacción
        db.refresh(db_organ)
    else:
        print("No se encontró el órgano para el jugador.")

