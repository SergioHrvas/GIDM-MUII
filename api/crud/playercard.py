from typing import List, Optional
from sqlalchemy.orm import Session
from api.models.game import Game
from api.models.playercard import PlayerCard
from api.models.player import Player
from api.crud.deckcard import steal_to_deck
from sqlalchemy import select, delete

# Función para eliminar carta del jugador
def remove_card_from_player(db: Session, player_id: int, card_id: int):
    # Buscar el registro de player_cards que relaciona el jugador con la carta
    player_card = db.query(PlayerCard).filter(PlayerCard.player_id == player_id, PlayerCard.card_id == card_id).first()
    
    # Si la carta existe
    if player_card:
        db.delete(player_card)  # Eliminar el registro de la relación
        db.commit()  # Confirmar la transacción
    else:
        print("No se encontró la carta para el jugador.")

# Función para descartar mis cartas
def discard_my_cards(db: Session, player_id: int, discards: Optional[List[int]] = None):
    # Si no se proporciona ninguna carta, no hacer nada
    if discards is None:
        return

    # Para cada ID de carta en la lista, eliminar solo una carta que coincida
    for card_id in discards:
        # Subconsulta para seleccionar el ID de una fila que coincida
        subquery = (
            select(PlayerCard.id)
            .filter(
                PlayerCard.player_id == player_id,
                PlayerCard.card_id == card_id
            )
            .limit(1)
            .scalar_subquery()
        )

        # Eliminar la fila con el ID seleccionado en la subconsulta
        db.execute(
            delete(PlayerCard).where(PlayerCard.id == subquery))
    db.commit()