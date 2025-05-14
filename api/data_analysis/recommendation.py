

from sklearn.compose import ColumnTransformer
from sklearn.discriminant_analysis import StandardScaler
from sklearn.ensemble import GradientBoostingClassifier, RandomForestClassifier
from sklearn.model_selection import train_test_split
import pandas as pd
import numpy as np
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder
from sqlalchemy.orm import Session

def train_move_based_model(db: Session):
    # Consulta para obtener datos de movimientos relevantes
    query = """
    SELECT 
        m.card_id,
        g.multiplayer,
        EXTRACT(HOUR FROM m.date) as hour_of_day,
        COUNT(m.id) OVER (PARTITION BY m.game_id, m.player_id) as moves_in_game,
        ROW_NUMBER() OVER (PARTITION BY m.game_id, m.player_id ORDER BY m.date) as move_sequence,
        CASE WHEN g.winner = p.id THEN 1 ELSE 0 END as is_win
    FROM moves m
    JOIN games g ON m.game_id = g.id
    JOIN players p ON m.player_id = p.id
    WHERE m.card_id IS NOT NULL
      AND g.winner IS NOT NULL
      AND m.action = 'card'
    """
    df = pd.read_sql(query, db.bind)
    

    print(df.describe())
    print(df.head())
    # Preprocesamiento
    X = df[['card_id', 'multiplayer', 'hour_of_day', 'moves_in_game', 'move_sequence']]
    y = df['is_win']
    
    # Transformadores para diferentes tipos de características
    preprocessor = ColumnTransformer(
        transformers=[
            ('cat', OneHotEncoder(handle_unknown='ignore'), ['card_id']),
            ('num', StandardScaler(), ['hour_of_day', 'moves_in_game', 'move_sequence']),
            ('passthrough', 'passthrough', ['multiplayer'])
        ])
    
    # Pipeline completo
    pipeline = Pipeline([
        ('preprocessor', preprocessor),
        ('classifier', GradientBoostingClassifier(
            n_estimators=150,
            learning_rate=0.1,
            max_depth=5,
            random_state=42
        ))
    ])
    
    # Entrenamiento
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    print(X_train.head())
    print(y_train)
    pipeline.fit(X_train, y_train)
    
    # Evaluación
    accuracy = pipeline.score(X_test, y_test)
    print(f"Model accuracy: {accuracy:.2f}")
    return pipeline, accuracy


