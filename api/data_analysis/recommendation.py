from sklearn.compose import ColumnTransformer
from sklearn.discriminant_analysis import StandardScaler
from sklearn.ensemble import GradientBoostingClassifier, RandomForestClassifier
from sklearn.model_selection import train_test_split
import pandas as pd
import numpy as np
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder
from sklearn.utils import resample
from sqlalchemy.orm import Session
import pandas as pd
import numpy as np
import pandas as pd

def train_move_based_model(db: Session):
    # Consulta para obtener datos de movimientos relevantes
    query = """
    SELECT 
        m.card_id,
        c.tipo as tipo,
        ROW_NUMBER() OVER (PARTITION BY m.game_id, m.player_id ORDER BY m.date) as move_sequence,
        m.num_virus,
        m.num_cure,
        m.num_protected,
        m.num_organs,
        CASE WHEN g.winner = p.id THEN 1 ELSE 0 END as is_win
    FROM moves m
    JOIN games g ON m.game_id = g.id
    JOIN players p ON m.player_id = p.id
    JOIN cards c ON c.id = m.card_id
    WHERE m.card_id IS NOT NULL
    AND g.winner IS NOT NULL
    AND m.action = 'card'
    """
    df = pd.read_sql(query, db.bind)


    print(df)


    # Combina las clases mayoritaria y minoritaria
    df_majority = df[df['is_win'] == 0]
    df_minority = df[df['is_win'] == 1]

    if(len(df_majority) == 0 or len(df_minority) == 0):
        raise ValueError("No hay datos suficientes para entrenar el modelo")
    
    elif (len(df_majority) > len(df_minority)):

        # Submuestreo de la clase mayoritaria
        df_majority_downsampled = resample(df_majority, 
                                        replace=False,    # No permite reemplazo
                                        n_samples=len(df_minority),  # Igualamos el número de muestras
                                        random_state=42)  # Fijamos una semilla para reproducibilidad

        # Concatenar las clases
        df = pd.concat([df_majority_downsampled, df_minority])

    # Revisar el balance
    print(df['is_win'].value_counts())

    # Preprocesamiento
    X = df[['card_id', 'tipo', 'move_sequence', 'num_virus', 'num_cure', 'num_protected', 'num_organs']]
    y = df['is_win']
        
    # Transformadores para diferentes tipos de características
    preprocessor = ColumnTransformer(
        transformers=[
            ('cat', OneHotEncoder(handle_unknown='ignore'), ['card_id']),
            ('tipo', OneHotEncoder(handle_unknown='ignore'), ['tipo']),
            ('num', StandardScaler(), ['move_sequence', 'num_virus', 'num_cure', 'num_protected', 'num_organs'])
        ],
    )
    
    pipeline = Pipeline([
        ('preprocessor', preprocessor),
        ('classifier', GradientBoostingClassifier(
            max_depth=3,
            n_estimators=100,
            min_samples_leaf=5,
            subsample=0.8,
            random_state=42
        ))
    ])

    # Entrenamiento
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    print(f"X_train: {X_train}")
    pipeline.fit(X_train, y_train)
    
    # Evaluación
    accuracy = pipeline.score(X_test, y_test)
    print(f"Model accuracy: {accuracy:.2f}")
    
    # Evaluación
    accuracy_train = pipeline.score(X_train, y_train)
    print(f"Model accuracy train: {accuracy_train:.2f}")
    
    return pipeline, accuracy


