from sklearn.compose import ColumnTransformer
from sklearn.discriminant_analysis import StandardScaler
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder
from sklearn.utils import resample
from sqlalchemy.orm import Session
import pandas as pd

def train_move_based_model(db: Session):
    # Consulta para obtener datos de movimientos relevantes
    query = """
    SELECT 
        m.card_id,
        c.tipo as tipo,
        ROW_NUMBER() OVER (PARTITION BY m.game_id, m.player_id ORDER BY m.date) as move_sequence,
        m.brain_estado,
        m.lungs_estado,
        m.intestine_estado,
        m.heart_estado,
        m.magic_estado,
        CASE WHEN g.winner = p.id THEN 1 ELSE 0 END as is_win
    FROM moves m
    JOIN games g ON m.game_id = g.id
    JOIN players p ON m.player_id = p.id
    JOIN cards c ON c.id = m.card_id
    WHERE m.card_id IS NOT NULL
    AND g.winner IS NOT NULL
    AND g.winner != 0
    AND m.action = 'card'
    """
    df = pd.read_sql(query, db.bind)

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
                                        random_state=20)  # Fijamos una semilla para reproducibilidad

        # Concatenar las clases
        df = pd.concat([df_majority_downsampled, df_minority])

    # Revisar el balance
    # print(df['is_win'].value_counts())

    # Convertir columna tipo a entero
    df['tipo'] = df['tipo'].astype('category').cat.codes


    
    # print(df.head())
    # Matriz de correlación
    # corr = df.corr()
    # print(corr)
    # Visualizar la matriz de correlación
    # import seaborn as sns
    # import matplotlib.pyplot as plt
    # plt.figure(figsize=(10, 8))
    # sns.heatmap(corr, annot=True, fmt=".2f", cmap='coolwarm')
    # plt.title('Matriz de Correlación')
    # plt.show()

    # Podemos eliminar la columna 'tipo' si no es relevante o tiene alta correlación con otras variables
    df.drop(columns=['tipo'], inplace=True)


    # Verificar si hay valores nulos
    #  if df.isnull().values.any():
    #      print("Hay valores nulos en el DataFrame")
    #      print(df.isnull().sum())
    #  else:
    #      print("No hay valores nulos en el DataFrame")

    # Preprocesamiento
    X = df[['card_id', 'move_sequence', 'brain_estado', 'lungs_estado', 'intestine_estado', 'heart_estado', 'magic_estado']]
    y = df['is_win']
        
    # Transformadores para diferentes tipos de características
    preprocessor = ColumnTransformer(
        transformers=[
            ('cat', OneHotEncoder(handle_unknown='ignore'), ['card_id']),
            # ('tipo', OneHotEncoder(handle_unknown='ignore'), ['tipo']),
            ('num', StandardScaler(), ['move_sequence', 'brain_estado', 'lungs_estado', 'intestine_estado', 'heart_estado', 'magic_estado'])
        ],
    )

    """

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
    """
    
    pipeline = Pipeline([
        ('preprocessor', preprocessor),
        ('classifier', RandomForestClassifier(
            n_estimators=100,
            max_depth=10,
            min_samples_split=3,
            random_state=42
        ))
    ])

    """

    pipeline = Pipeline([
        ('preprocessor', preprocessor),
        ('classifier', DecisionTreeClassifier(
            max_depth=3,
            min_samples_leaf=5,
            random_state=42
        ))
        
    ])
    """

    # Entrenamiento
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # print(f"========== X_train: ==========\n {X_train}")
    pipeline.fit(X_train, y_train)
    
    # Evaluación
    accuracy = pipeline.score(X_test, y_test)
    # print(f"Model accuracy: {accuracy:.2f}")
    
    # Evaluación
    # accuracy_train = pipeline.score(X_train, y_train)
    # print(f"Model accuracy train: {accuracy_train:.2f}")
    
    return pipeline, accuracy


