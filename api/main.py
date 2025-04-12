from fastapi import FastAPI
from database import init_db
from routers import user, game, player, auth
from fastapi.middleware.cors import CORSMiddleware  # ¡Para evitar bloqueos CORS!

app = FastAPI()

# Inicializar la base de datos
init_db()

# Incluir rutas
app.include_router(user.router, prefix="/user", tags=["User"])
app.include_router(game.router, prefix="/game", tags=["Game"])
app.include_router(auth.router, tags=["Auth"])

# Configura CORS (permite todas las conexiones en desarrollo)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/")
def read_root():
    return {"message": "¡Bienvenido a la API!"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)  # ¡Clave para conexiones externas!s