from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
from api.database import init_db
from api.routers import user, game, auth
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

# Inicializar la base de datos
init_db()

# Para las imágenes
app.mount("/static", StaticFiles(directory="api/static"), name="static")

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
    uvicorn.run(app, host="0.0.0.0", port=8000)