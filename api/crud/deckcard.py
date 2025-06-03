from sqlalchemy.orm import Session

from api.models.playercard import PlayerCard
from api.models.player import Player
from api.schemas.organtype import OrganType
from api.models.card import Card
from api.models.deckcard import DeckCard

from sqlalchemy.orm import Session

from api.models.card import Card
from api.models.game import Game

import random

def initialize_deck(db: Session, game: Game):
    """
    Inicializa el mazo de cartas para un juego, incluyendo las repeticiones según las reglas especificadas.

    Parámetros:
        db (Session): Sesión de SQLAlchemy.
        game (Game): El juego al que se añadirán las cartas.
    """
    # Limpiar cualquier mazo existente
    db.query(DeckCard).filter(DeckCard.game_id == game.id).delete()
    db.commit()
    
    # Generar el mazo básico (esta función ya debería agregar las cartas a la sesión)
    generate_basic_deck(db, game)
    
    db.commit()
    db.refresh(game)

    # Repartimos las cartas
    draw_cards(db, game)


def draw_cards(db: Session, game: Game):
    # Jugadores del juego
    players = db.query(Player).filter(Player.game_id == game.id)

    for player in players:
        steal_to_deck(db, game, player.id, 3)


def steal_to_deck(db: Session, game: Game, player_id, num):
    # Refrescamos el juego para asegurarnos de que tenemos la información más reciente
    db.refresh(game)

    # Verificamos el número de cartas en el mazo
    current_cards = len(game.deck_cards)

    # Caso 1: No hay suficientes cartas
    if current_cards < num:
      
        # Tomamos todas las cartas restantes
        taken = current_cards

        cards_to_take = db.query(DeckCard)\
                        .filter(DeckCard.game_id == game.id)\
                        .limit(taken)\
                        .all()
        
        for card in cards_to_take:
            db.add(PlayerCard(player_id=player_id, card_id=card.card_id))
            db.delete(card)
        
        db.commit()
        db.refresh(game)
        
        # Regeneramos el mazo completo
        generate_basic_deck(db, game)
        db.commit()
        db.refresh(game)
        
        # Eliminamos UNA copia de cada carta que los jugadores tengan
        players = db.query(Player).filter(Player.game_id == game.id).all()
        for player in players:
            # Obtenemos IDs de cartas únicas que el jugador tiene
            unique_card_ids = {pc.card_id for pc in db.query(PlayerCard.card_id)
                                .filter(PlayerCard.player_id == player.id)
                                .all()}
            
            for card_id in unique_card_ids:
                # Eliminamos solo una copia de cada carta
                deck_card = db.query(DeckCard)\
                             .filter(
                                 DeckCard.game_id == game.id,
                                 DeckCard.card_id == card_id
                             )\
                             .first()
                
                if deck_card:
                    db.delete(deck_card)
        
        db.commit()
        db.refresh(game)
        
        # Tomamos las cartas restantes que faltan
        remaining = num - taken
        if remaining > 0:
            cards_to_take = db.query(DeckCard)\
                            .filter(DeckCard.game_id == game.id)\
                            .limit(remaining)\
                            .all()
            
            for card in cards_to_take:
                db.add(PlayerCard(player_id=player_id, card_id=card.card_id))
                db.delete(card)
            
            db.commit()
            db.refresh(game)
    
    # Caso 2: Hay suficientes cartas
    else:
        cards_to_take = db.query(DeckCard)\
                        .filter(DeckCard.game_id == game.id)\
                        .limit(num)\
                        .all()
        
        for card in cards_to_take:
            db.add(PlayerCard(player_id=player_id, card_id=card.card_id))
            db.delete(card)
        
        db.commit()
        db.refresh(game)
    
    # Verificación final
    final_count = len(game.deck_cards)
    return min(num, final_count + num)  # Retorna el número real de cartas robadas


def generate_basic_deck(db: Session, game: Game):   
    # Limpiar el mazo actual si existe
    game.deck_cards = []
    
    # Obtener todas las cartas de la base de datos
    virus_cards = db.query(Card).filter(Card.tipo == "virus").all()
    cure_cards = db.query(Card).filter(Card.tipo == "cure").all()
    organ_cards = db.query(Card).filter(Card.tipo == "organ").all()
    action_cards = db.query(Card).filter(Card.tipo == "action").all()

    # Crear lista temporal para las nuevas cartas
    new_deck_cards = []

    # Añadir cartas de virus
    for card in virus_cards:
        count = 1 if card.organ_type == OrganType.magic else 4
        new_deck_cards.extend([DeckCard(game_id=game.id, card_id=card.id) for _ in range(count)])

    # Añadir cartas de cura
    for card in cure_cards:
        new_deck_cards.extend([DeckCard(game_id=game.id, card_id=card.id) for _ in range(4)])

    # Añadir cartas de órgano
    for card in organ_cards:
        count = 1 if card.organ_type == OrganType.magic else 5
        new_deck_cards.extend([DeckCard(game_id=game.id, card_id=card.id) for _ in range(count)])

    # Añadir cartas de acción
    for card in action_cards:
        if card.name == "Infect Player":
            count = 2
        elif card.name in ("Steal Organ", "Exchange Card"):
            count = 3
        else:
            count = 1
        new_deck_cards.extend([DeckCard(game_id=game.id, card_id=card.id) for _ in range(count)])

    # Barajar las cartas
    random.shuffle(new_deck_cards)
    
    # Añadir todas las cartas a la sesión y al juego
    db.add_all(new_deck_cards)
    game.deck_cards = new_deck_cards
    