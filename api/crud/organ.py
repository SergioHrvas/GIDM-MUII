from sqlalchemy.orm import Session
from models.playercard import PlayerCard
from models.organ import Organ
from models.player import Player
from models.card import Card
from schemas.organtype import OrganType


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
        tipo = tipo,
        virus = False,
        cure = 0
    )

    if db_organ:
        db.add(db_organ)  # Eliminar el registro de la relación
        db.commit()  # Confirmar la transacción
        db.refresh(db_organ)
    else:
        print("No se encontró el órgano para el jugador.")
        return False
    
    return True


def player_has_organ(db: Session, player_id, tipo: str):
    # Buscar el registro de organ
    db_organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == tipo).first()

    if db_organ:
        return True
    else:
        return False


def player_can_steal(db: Session, player_id: int, player_to: int, tipo: OrganType):

    # Buscar el registro de organ
    db_organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == tipo).first()

    db_organ2 = db.query(Organ).filter(Organ.player_id == player_to, Organ.tipo == tipo).first()

    # Si está inmunizado, no puedo robarlo
    if db_organ2.cure == 2:
        return False
    if db_organ:
        return False
    else:
        if db_organ2:
            return True
        else:
            return False


def add_virus_to_organ(db: Session, player_to: int, card_tipo: str, organ_to_infect: str):
    # Buscar el registro de organ
    if(card_tipo != "magic"):
        db_organ = db.query(Organ).filter(Organ.player_id == player_to, Organ.tipo == card_tipo).first()
    else:
        db_organ = db.query(Organ).filter(Organ.player_id == player_to, Organ.tipo == organ_to_infect).first()

    if(db_organ):
        if db_organ.cure == 2:
            print("El órgano está inmunizado")
            return False
        elif db_organ.cure == 1:
            db_organ.cure = 0
            db.commit()
            db.refresh(db_organ)
        elif db_organ.cure == 0:
            if db_organ.virus == False:
                db_organ.virus = True
                db.commit()
                db.refresh(db_organ)
            elif db_organ.virus == True:
                #Eliminamos el órgano
                db.delete(db_organ)
            else:
                print("Error al añadir virus al órgano")
                return False
        else:
            print("Error al añadir virus al órgano")
            return False
    else:
        print("Error al encontrar el órgano")
        return False
    
    return True
    

def add_cure_to_organ(db: Session, player_id: int, tipo: str):
    # Buscar el registro de organ
    db_organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == tipo).first()
    if(db_organ):
        if db_organ.virus == True:
            db_organ.virus = False
        elif db_organ.virus == False:
            if ( db_organ.cure == 0 ) or ( db_organ.cure == 1 ):
                db_organ.cure += 1
                db.commit()
                db.refresh(db_organ)
            elif db_organ.cure == 2:
                print("El órgano ya está inmunizado")
                return False
            else:
                print("Error al añadir cura al órgano")
                return False
        else:
            print("Error al añadir cura al órgano")
            return False
    else:
        print("Error al encontrar el órgano")
        return False

    return True

    

def steal_card(db: Session, player_id, player_to, tipo: OrganType):
    db_organ2 = db.query(Organ).filter(Organ.player_id == player_to, Organ.tipo == tipo).first()

    if db_organ2:
        db_organ2.player_id = player_id
        db.commit()
        db.refresh(db_organ2)
    else:
        print("Error al robar carta.")
        return False
    return True


def change_body(db: Session, player_id, player_to):
    db_organs_player_from = db.query(Organ).filter(Organ.player_id == player_id).all()
    db_organs_player_to = db.query(Organ).filter(Organ.player_id == player_to).all()

    for organ in db_organs_player_from:
        organ.player_id = player_to
        db.commit()
        
    for organ in db_organs_player_to:
        organ.player_id = player_id
        db.commit()


def change_organs(db: Session, player_id, type_from, player_to, type_to):
    has_organ_player_from = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == type_to).first()
    has_organ_player_to = db.query(Organ).filter(Organ.player_id == player_to, Organ.tipo == type_from).first()

    if(has_organ_player_from or has_organ_player_to):
        return False
    else:
        db_organ_player_from = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == type_from).first()
        db_organ_player_to = db.query(Organ).filter(Organ.player_id == player_to, Organ.tipo == type_to).first()

        if db_organ_player_from and db_organ_player_to:
            if db_organ_player_from.cure == 2:
                return False
            elif db_organ_player_to.cure == 2:
                return False
            else:
                player = db_organ_player_from.player_id
                db_organ_player_from.player_id = db_organ_player_to.player_id
                db_organ_player_to.player_id = player
                            
                db.commit()
                db.refresh(db_organ_player_from)
                db.refresh(db_organ_player_to)
                return True
        else:
            return False


def remove_virus_to_organ(db: Session, player_id, organtype):
    organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == organtype, Organ.virus == True).first()

    if organ:
        organ.virus = False;


def can_infect(db: Session, player_id, player_to, organtype):
    # Miro si tengo ese órgano infectado
    my_infected_organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == organtype, Organ.virus == True)

    if my_infected_organ:
        # Miro si tiene el órgano para infectar
        organ_to_infect = db.query(Organ).filter(Organ.player_id == player_to, Organ.tipo == organtype, Organ.cure != 2)

        if organ_to_infect:
            return True
        else:
            return False
    else:
        return False



def infect_players(db: Session, player_id, infect):
    if infect.player1 and infect.organ1:
        caninfect = can_infect(db, player_id, infect.player1, infect.organ1)

        if caninfect:
            add_virus_to_organ(db, infect.player1, infect.organ1)
            remove_virus_to_organ(db, player_id, infect.organ1)
            
    if infect.player2 and infect.organ2:
        caninfect = can_infect(db, player_id, infect.player2, infect.organ2)

        if caninfect:
            add_virus_to_organ(db, infect.player2, infect.organ2)
            remove_virus_to_organ(db, player_id, infect.organ2)

    if infect.player3 and infect.organ3:
        caninfect = can_infect(db, player_id, infect.player3, infect.organ3)

        if caninfect:
            add_virus_to_organ(db, infect.player3, infect.organ3)
            remove_virus_to_organ(db, player_id, infect.organ3)

    if infect.player4 and infect.organ4:
        caninfect = can_infect(db, player_id, infect.player4, infect.organ4)

        if caninfect:
            add_virus_to_organ(db, infect.player4, infect.organ4)
            remove_virus_to_organ(db, player_id, infect.organ4)

    if infect.player5 and infect.organ5:
        caninfect = can_infect(db, player_id, infect.player5, infect.organ5)

        if caninfect:
            add_virus_to_organ(db, infect.player5, infect.organ5)
            remove_virus_to_organ(db, player_id, infect.organ5)
