from sqlalchemy.orm import Session
from models.playercard import PlayerCard
from models.player import Player
from models.card import Card

def remove_card_from_player(db: Session, player_id: int, card_id: int):
    # Buscar el registro de player_cards que relaciona el jugador con la carta
    player_card = db.query(PlayerCard).filter(PlayerCard.player_id == player_id, PlayerCard.card_id == card_id).first()
    
    if player_card:
        db.delete(player_card)  # Eliminar el registro de la relación
        db.commit()  # Confirmar la transacción
    else:
        print("No se encontró la carta para el jugador.")

def discard_cards(db: Session, player_id: int):
    # Buscar el registro de player_cards que relaciona el jugador con la carta
    player_cards = db.query(PlayerCard).filter(PlayerCard.player_id == player_id).all()
    
    if len(player_cards) > 0:
        for player_card in player_cards:
            db.delete(player_card)  # Eliminar el registro de la relación
        db.commit()  # Confirmar la transacción
    else:
        print("No habia cartas para borrar.")
