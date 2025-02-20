from fastapi import FastAPI
from database import init_db
from routers import users

app = FastAPI()

# Inicializar la base de datos
init_db()

# Incluir rutas
app.include_router(users.router, prefix="/users", tags=["Users"])

@app.get("/")
def read_root():
    return {"message": "¡Bienvenido a la API!"}