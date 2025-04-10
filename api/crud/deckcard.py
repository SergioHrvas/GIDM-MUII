from sqlalchemy.orm import Session

from models.playercard import PlayerCard
from models.player import Player
from schemas.organtype import OrganType
from models.card import Card
from models.deckcard import DeckCard

from sqlalchemy.orm import Session

from models.card import Card
from models.game import Game

import random

def initialize_deck(db: Session, game: Game):
    """
    Inicializa el mazo de cartas para un juego, incluyendo las repeticiones según las reglas especificadas.

    Parámetros:
        db (Session): Sesión de SQLAlchemy.
        game (Game): El juego al que se añadirán las cartas.
    """
   
    generate_basic_deck(db, game)

    print(f"a: {len(game.deck_cards)}")
    # Mezclar las cartas del mazo
    random.shuffle(game.deck_cards)
    
    print(f"b: {len(game.deck_cards)}")
    
    db.add_all(game.deck_cards)  # Asegura que todas las cartas se mantengan en la sesión
    
    print(f"c: {len(game.deck_cards)}")
    db.commit()
    db.refresh(game)
    print(f"c: {len(game.deck_cards)}")


    cartitas = db.query(DeckCard).filter(DeckCard.game_id == game.id).all()

    print(f"cartitas: {len(cartitas)}")

    draw_cards(db, game)


def draw_cards(db: Session, game: Game):
    # Jugadores del juego
    players = db.query(Player).filter(Player.game_id == game.id)

    for player in players:
        steal_to_deck(db, game, player.id, 3)


def steal_to_deck(db: Session, game: Game, player_id, num):
    
    # Primero miramos si quedan suficientes cartas
    rest_card = game.deck_cards

    if(len(rest_card) < num):
        # Se volverá a generar la baraja (otro método para futuro)
        generate_basic_deck(db, game)
        
        # Eliminamos las cartas que quedaban en la baraja
        for card in rest_card:
            db.delete(card)  # Eliminar cartas restantes del mazo

        players = db.query(Player).filter(Player.game_id == game.id).all()

        # Eliminamos en deck las cartas que están en la mano de cada jugador
        for player in players:
            # Obtener las cartas que tiene el jugador en la mano
            cards_player = db.query(PlayerCard).filter(PlayerCard.player_id == player.id).all()

            for player_card in cards_player:
                # Obtener las cartas en el mazo que coincidan con el tipo de la carta en la mano del jugador
                deck_card = (
                    db.query(DeckCard)
                    .join(Card, DeckCard.card_id == Card.id)
                    .filter(
                        DeckCard.game_id == game.id,
                        Card.name == player_card.card.name  # Filtrar por el mismo tipo de carta
                    )
                    .first()
                )
                
                db.delete(deck_card)

            
        
        db.commit()

    else:
        for _ in range(num):
            card = game.deck_cards.pop()
            pc = PlayerCard(
                player_id = player_id,
                card_id = card.card_id
            )
            db.add(pc)
            db.commit()


def generate_basic_deck(db: Session, game: Game):   
    
    # Obtener todas las cartas de la base de datos
    virus_cards = db.query(Card).filter(Card.tipo == "virus").all()
    cure_cards = db.query(Card).filter(Card.tipo == "cure").all()
    organ_cards = db.query(Card).filter(Card.tipo == "organ").all()
    action_cards = db.query(Card).filter(Card.tipo == "action").all()

    # Añadir las cartas de virus al mazo
    for card in virus_cards:
        if card.organ_type != OrganType.magic:
            for _ in range(4):  # 4 copias de cada carta de virus (excepto magic)
                game.deck_cards.append(DeckCard(card=card))
        else:
            game.deck_cards.append(DeckCard(card=card))  # 1 copia de magic

    # Añadir las cartas de cura al mazo
    for card in cure_cards:
        for _ in range(4):  # 4 copias de cada carta de cura
            game.deck_cards.append(DeckCard(card=card))

    # Añadir las cartas de órgano al mazo
    for card in organ_cards:
        if card.organ_type != OrganType.magic:
            for _ in range(5):  # 5 copias de cada carta de órgano (excepto magic)
                game.deck_cards.append(DeckCard(card=card))
        else:
            game.deck_cards.append(DeckCard(card=card))  # 1 copia de magic

    # Añadir las cartas de acción al mazo
    for card in action_cards:
        if card.name == "Infect Player":
            for _ in range(2):  # 2 copias de "Infect Player"
                game.deck_cards.append(DeckCard(card=card))
        elif card.name == "Steal Organ" or card.name == "Exchangeº Card":
            for _ in range(3):  # 3 copias de "Steal Organ" y "Exchange Card"
                game.deck_cards.append(DeckCard(card=card))
        elif card.name == "Discard Cards" or card.name == "Change Body":
            game.deck_cards.append(DeckCard(card=card))  # 1 copia de "Discard Cards" y "Change Body"

    pass