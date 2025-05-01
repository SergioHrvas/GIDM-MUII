from operator import or_
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
        virus = 0,
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

def player_has_organ_to_cure_infect(db: Session, player_id, tipo: str):

    db_organ = db.query(Organ).filter(Organ.player_id == player_id).first()
    
    if db_organ:
        return True
    else:
        return False

    
def player_can_steal(db: Session, player_id: int, player_to: int, tipo: OrganType):

    # Buscar el registro de organ
    db_organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == tipo).first()

    db_organ2 = db.query(Organ).filter(Organ.player_id == player_to, Organ.tipo == tipo).first()


    if db_organ2:
        # Si está inmunizado, no puedo robarlo
        if db_organ2.cure == 3:
            return False
        if db_organ:
            return False
        else:
            return True
    else:
        return False

def add_virus_to_organ(db: Session, player_to: int, card_tipo: str, organ_to_infect: str):    
    # Buscar el registro del órgano que se va a infectar
    db_organ = db.query(Organ).filter(Organ.player_id == player_to, Organ.tipo == organ_to_infect).first()


    # # Después, si hay órgano:
    if(db_organ):
        # # # Si el órgano está inmunizado, no se puede infectar
        if db_organ.cure == 3:
            print("El órgano está inmunizado")
            return False
        # # # Si el órgano tiene una cura de tipo magic (2), se le quita la cura
        elif db_organ.cure == 2:
            db_organ.cure = 0
            db.commit()
            db.refresh(db_organ)
        # # # Si el órgano tiene una cura de su tipo (1), se le quita la cura si el tipo de órgano es igual al tipo de carta
        elif db_organ.cure == 1:
            if (db_organ.tipo == card_tipo) or (db_organ.tipo == OrganType.magic) or (card_tipo == OrganType.magic):
                db_organ.cure = 0
                db.commit()
                db.refresh(db_organ)
            else:
                print("Error al añadir virus al órgano")
                return False
        # # # Si el órgano no tiene cura ni virus, se le añade el virus:
        elif db_organ.cure == 0:
            if db_organ.virus == 0:
                if db_organ.tipo != OrganType.magic:
                    if (db_organ.tipo != card_tipo and card_tipo != OrganType.magic and db_organ.tipo != OrganType.magic):
                        print ("Error al añadir viruss al órgano")
                        return False
                    # Si el tipo de carta es magic, se le añade el virus (= 2)
                    if card_tipo == OrganType.magic:
                        db_organ.virus = 2
                    # Si el tipo de carta es del tipo del órgano, se le añade el virus (= 1)
                    elif db_organ.tipo == card_tipo:
                        db_organ.virus = 1
                
                elif db_organ.tipo == OrganType.magic:
                    # Si el tipo de carta es magic, se le añade el virus (= 2)
                    if card_tipo == OrganType.magic:
                        db_organ.virus = 2
                    # Si el tipo de carta es del tipo del órgano, se le añade el virus (= 1)
                    else:
                        db_organ.virus = 1
                        if card_tipo == OrganType.heart:
                            db_organ.magic_organ = 1
                        if card_tipo == OrganType.brain:
                            db_organ.magic_organ = 2
                        if card_tipo == OrganType.intestine:
                            db_organ.magic_organ = 3
                        if card_tipo == OrganType.lungs:
                            db_organ.magic_organ = 4

                db.commit()
                db.refresh(db_organ)
            # # # Si el órgano ya tiene el virus, se elimina el órgano
            elif (db_organ.virus == 1) or (db_organ.virus == 2):
                if (db_organ.tipo != card_tipo and card_tipo != OrganType.magic and db_organ.tipo != OrganType.magic):
                    print ("Error al añadir virus al órgano")
                    return False
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
    

def add_cure_to_organ(db: Session, player_id: int, card_tipo: str, organ_to_cure: str):
    # Buscar el registro de organ
    db_organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == organ_to_cure).first()
    # Si existe el órgano:
    if(db_organ):
        # Si el órgano tiene virus magico (2), se le quita el virus
        if db_organ.virus == 2:
            db_organ.virus = 0
        # Si el órgano tiene virus normal (1), se le quita el virus si el tipo de carta es igual al tipo de órgano
        elif db_organ.virus == 1:
            if (db_organ.tipo == card_tipo) or (db_organ.tipo == OrganType.magic) or (card_tipo == OrganType.magic):
                db_organ.virus = 0
            else:
                print("Error al añadir cura al órgano")
                return False
        # Si el órgano no tiene virus ni cura, se le añade la cura:
        elif db_organ.virus == 0:
            if db_organ.tipo != OrganType.magic:
                # Si el tipo de carta es magic, se le añade la cura (= 2)
                if card_tipo == OrganType.magic:
                    # # Si el órgano no tiene cura, se le añade la cura
                    if db_organ.cure == 0:
                        db_organ.cure = 2
                    # # Si el órgano tiene cura, se inmuniza el órgano
                    elif (db_organ.cure == 1) or (db_organ.cure == 2):
                        db_organ.cure = 3
                    else:
                        print("Error al añadir cura al órgano")
                        return False
                    
                # Si el tipo de carta es del tipo del órgano, se le añade la cura (= 1)
                elif db_organ.tipo == card_tipo:
                    # # Si el órgano no tiene cura, se le añade la cura
                    if db_organ.cure == 0:
                        db_organ.cure = 1
                    # # Si el órgano tiene cura, se inmuniza el órgano
                    elif (db_organ.cure == 1) or (db_organ.cure == 2):
                        db_organ.cure = 3
                    elif db_organ.cure == 3:
                        print("El órgano ya está inmunizado")
                        return False
                    else:
                        print("Error al añadir cura al órgano")
                        return False
                else:
                    print("Error al añadir cura al órgano")
                    return False
            elif db_organ.tipo == OrganType.magic:
                # Si el tipo de carta es magic, se le añade la cura (= 2)
                if card_tipo == OrganType.magic:
                    # # Si el órgano no tiene cura, se le añade la cura
                    if db_organ.cure == 0:
                        db_organ.cure = 2
                    # # Si el órgano tiene cura, se inmuniza el órgano
                    elif (db_organ.cure == 1) or (db_organ.cure == 2):
                        db_organ.cure = 3
                    else:
                        print("Error al añadir cura al órgano")
                        return False
                    
                else:
                    # # Si el órgano no tiene cura, se le añade la cura
                    if db_organ.cure == 0:
                        db_organ.cure = 1
                        if card_tipo == OrganType.heart:
                            db_organ.magic_organ = 1
                        if card_tipo == OrganType.brain:
                            db_organ.magic_organ = 2
                        if card_tipo == OrganType.intestine:
                            db_organ.magic_organ = 3
                        if card_tipo == OrganType.lungs:
                            db_organ.magic_organ = 4
                    # # Si el órgano tiene cura, se inmuniza el órgano
                    elif (db_organ.cure == 1) or (db_organ.cure == 2):
                        db_organ.cure = 3
                    elif db_organ.cure == 3:
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

    print("has_organ_player_from: ", has_organ_player_from)
    print("has_organ_player_to: ", has_organ_player_to)

    if((has_organ_player_from or has_organ_player_to) and type_from != type_to):
        return False
    else:
        db_organ_player_from = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == type_from).first()
        db_organ_player_to = db.query(Organ).filter(Organ.player_id == player_to, Organ.tipo == type_to).first()

        if db_organ_player_from and db_organ_player_to:
            if db_organ_player_from.cure == 3:
                return False
            elif db_organ_player_to.cure == 3:
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
    organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == organtype, or_(Organ.virus == 1, Organ.virus == 2)).first()

    if organ:
        organ.virus = 0


def can_infect(db: Session, player_id, player_to, organtype):
    # Miro si tengo ese órgano infectado
    my_infected_organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == organtype, or_(Organ.virus == 1, Organ.virus == 2))

    if my_infected_organ:
        # Miro si tiene el órgano para infectar
        organ_to_infect = db.query(Organ).filter(Organ.player_id == player_to, Organ.tipo == organtype, Organ.cure != 3)

        if organ_to_infect:
            return True
        else:
            return False
    else:
        return False



def infect_players(db: Session, player_id, infect):
    done = [True, True, True, True, True]
    if infect.player1 and infect.organ1:
        caninfect = can_infect(db, player_id, infect.player1, infect.organ1)
        if caninfect:
            organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == infect.organ1_from).first()
            
            organ_type = organ.tipo
            if organ.virus == 2:
                organ_type = OrganType.magic
                
            done[0] = add_virus_to_organ(db, infect.player1, organ_type, infect.organ1)
            if done[0]:
                remove_virus_to_organ(db, player_id, infect.organ1_from)
            
            
    if infect.player2 and infect.organ2:
        caninfect = can_infect(db, player_id, infect.player2, infect.organ2)

        if caninfect:
            organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == infect.organ2_from).first()

            organ_type = organ.tipo
            if organ.virus == 2:
                organ_type = OrganType.magic

            done[1] = add_virus_to_organ(db, infect.player2, organ_type, infect.organ2)
            if done[1]:
                remove_virus_to_organ(db, player_id, infect.organ2_from)

    if infect.player3 and infect.organ3:
        caninfect = can_infect(db, player_id, infect.player3, infect.organ3)

        if caninfect:
            organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == infect.organ3_from).first()
            organ_type = organ.tipo

            if organ.virus == 2:
                organ_type = OrganType.magic

            done[2] = add_virus_to_organ(db, infect.player3, organ_type, infect.organ3)
            if done[2]:
                remove_virus_to_organ(db, player_id, infect.organ3_from)

    if infect.player4 and infect.organ4:
        caninfect = can_infect(db, player_id, infect.player4, infect.organ4)

        if caninfect:
            organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == infect.organ4_from).first()
            organ_type = organ.tipo

            if organ.virus == 2:
                organ_type = OrganType.magic

            done[3] = add_virus_to_organ(db, infect.player4, organ_type, infect.organ4)
            if done[3]:
                remove_virus_to_organ(db, player_id, infect.organ4_from)

    if infect.player5 and infect.organ5:
        caninfect = can_infect(db, player_id, infect.player5, infect.organ5)

        if caninfect:
            organ = db.query(Organ).filter(Organ.player_id == player_id, Organ.tipo == infect.organ5_from).first()
            organ_type = organ.tipo

            if organ.virus == 2:
                organ_type = OrganType.magic

            done[4] = add_virus_to_organ(db, infect.player5, organ_type, infect.organ5)
            if done[4]:
                remove_virus_to_organ(db, player_id, infect.organ5_from)

    if all(done):
        return True
    else:
        return False