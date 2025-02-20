from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

# URL for the database POSTGRE
DATABASE_URL = "postgresql://usuario:password@localhost/pandemiagame"

# Create the SQLAlchemy engine
engine = create_engine(DATABASE_URL)

# Create the SQLAlchemy session factory
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Create the base class for our models
Base = declarative_base()

#Crear tablas
def init_db():
    Base.metadata.create_all(bind=engine)