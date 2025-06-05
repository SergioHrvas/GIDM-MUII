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

# Función para inializar el mazo de cartas
def initialize_deck(db: Session, game: Game):

    # Limpiamos cualquier mazo existente
    db.query(DeckCard).filter(DeckCard.game_id == game.id).delete()
    db.commit()
    
    # Generamos el mazo básico
    generate_basic_deck(db, game)
    
    db.commit()
    db.refresh(game)

    # Repartimos las cartas
    draw_cards(db, game)


# Función para repartir las cartas
def draw_cards(db: Session, game: Game):
    # Obtenemos los jugadores del juego
    players = db.query(Player).filter(Player.game_id == game.id)

    # Cada jugador roba 3 cartas del mazo
    for player in players:
        steal_to_deck(db, game, player.id, 3)


# Función para robar las cartas del mazo
def steal_to_deck(db: Session, game: Game, player_id, num):
    # Refrescamos el juego para asegurarnos de que tenemos la información más reciente
    db.refresh(game)

    # Verificamos el número de cartas en el mazo
    current_cards = len(game.deck_cards)

    # Caso 1: No hay suficientes cartas
    if current_cards < num:
      
        # Tomamos todas las cartas restantes del mazo
        taken = current_cards
        cards_to_take = db.query(DeckCard)\
                        .filter(DeckCard.game_id == game.id)\
                        .limit(taken)\
                        .all()
        
        # Añadimos cada una de las cartas al jugador
        for card in cards_to_take:
            db.add(PlayerCard(player_id=player_id, card_id=card.card_id))
            db.delete(card)
        
        db.commit()
        db.refresh(game)
        
        # Regeneramos el mazo completo
        generate_basic_deck(db, game)
        db.commit()
        db.refresh(game)
        
        # Eliminamos UNA copia de cada carta que los jugadores tengan en sus manos
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
            # Obtenemos las cartas que faltan por robar
            cards_to_take = db.query(DeckCard)\
                            .filter(DeckCard.game_id == game.id)\
                            .limit(remaining)\
                            .all()
            
            # Añadimos las cartas a la mano del jugador
            for card in cards_to_take:
                db.add(PlayerCard(player_id=player_id, card_id=card.card_id))
                db.delete(card)
            
            db.commit()
            db.refresh(game)
    
    # Caso 2: Hay suficientes cartas
    else:
        # Obtenemos las cartas a coger
        cards_to_take = db.query(DeckCard)\
                        .filter(DeckCard.game_id == game.id)\
                        .limit(num)\
                        .all()
        
        # Añadimos cada carta a la mano del jugador
        for card in cards_to_take:
            db.add(PlayerCard(player_id=player_id, card_id=card.card_id))
            db.delete(card)
        
        db.commit()
        db.refresh(game)
    
    final_count = len(game.deck_cards)
    return min(num, final_count + num)  # Devolvemos el número real de cartas robadas


# Función para generar un mazo básico
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

    # Añadir 4 cartas de cada virus (excepto mágico que es 1)
    for card in virus_cards:
        count = 1 if card.organ_type == OrganType.magic else 4
        new_deck_cards.extend([DeckCard(game_id=game.id, card_id=card.id) for _ in range(count)])

    # Añadir 4 cartas de cada cura (excepto mágico que es 1)
    for card in cure_cards:
        print(card.organ_type)
        count = 1 if card.organ_type == OrganType.magic else 4
        new_deck_cards.extend([DeckCard(game_id=game.id, card_id=card.id) for _ in range(count)])

    # Añadir 5 cartas de cada órgano (excepto mágico que es 1)
    for card in organ_cards:
        count = 1 if card.organ_type == OrganType.magic else 5
        new_deck_cards.extend([DeckCard(game_id=game.id, card_id=card.id) for _ in range(count)])

    # Añadir cartas de acción
    for card in action_cards:
        if card.name == "Infect Player": # Añadimos dos cartas de infección
            count = 2
        elif card.name in ("Steal Organ", "Exchange Card"): # Añadimos 3 cartas de robo y 3 de intercambio de órgano
            count = 3
        else: # Añadimos una carta de descartar y una de cambio de cuerpo
            count = 1
        new_deck_cards.extend([DeckCard(game_id=game.id, card_id=card.id) for _ in range(count)])

    # Barajar las cartas
    random.shuffle(new_deck_cards)
    
    # Añadir todas las cartas al juego
    db.add_all(new_deck_cards)
    game.deck_cards = new_deck_cards
    