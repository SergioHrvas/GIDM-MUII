from fastapi import FastAPI
from database import init_db
from routers import user, game, player, auth

app = FastAPI()

# Inicializar la base de datos
init_db()

# Incluir rutas
app.include_router(user.router, prefix="/user", tags=["User"])
app.include_router(game.router, prefix="/game", tags=["Game"])
app.include_router(auth.router, tags=["Auth"])

@app.get("/")
def read_root():
    return {"message": "¡Bienvenido a la API!"}