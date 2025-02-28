from sqlalchemy.orm import Session
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


def add_virus_to_organ(db: Session, player_id: int, tipo: str):
    # Buscar el registro de organ
    db_organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == tipo).first()

    if(db_organ):
        if db_organ.cure == 2:
            print("El órgano está inmunizado")
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
        else:
            print("Error al añadir virus al órgano")
    else:
        print("Error al encontrar el órgano")
    
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
            else:
                print("Error al añadir cura al órgano")
        else:
            print("Error al añadir cura al órgano")
    else:
        print("Error al encontrar el órgano")
    

def steal_card(db: Session, player_id, player_to, tipo: OrganType):
    db_organ2 = db.query(Organ).filter(Organ.player_id == player_to, Organ.tipo == tipo).first()

    if db_organ2:
        db_organ2.player_id = player_id
        db.commit()
        db.refresh(db_organ2)
    else:
        print("Error al robar carta.")

def change_body(db: Session, player_id, player_to):
    db_organs_player_from = db.query(Organ).filter(Organ.player_id == player_id).all()
    db_organs_player_to = db.query(Organ).filter(Organ.player_id == player_to).all()

    for organ in db_organs_player_from:
        organ.player_id = player_to
        db.commit()
        
    for organ in db_organs_player_to:
        organ.player_id = player_id
        db.commit()