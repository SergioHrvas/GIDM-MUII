"""Cambio de organos3

Revision ID: 0105a81d82a4
Revises: b4fc250a386f
Create Date: 2025-04-11 23:53:47.384576

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '0105a81d82a4'
down_revision: Union[str, None] = 'b4fc250a386f'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None

def upgrade():
    # Renombrar el tipo existente (creando uno temporal)
    op.execute("ALTER TYPE organtype RENAME TO organtype_old")
    
    # Crear el nuevo tipo con todos los valores
    op.execute("""CREATE TYPE organtype AS ENUM (
        'heart',
        'intestine',
        'brain',
        'lungs',
        'magic'
    )""")
    
    # Actualizar la columna para usar el nuevo tipo
    op.execute("""ALTER TABLE cards 
                 ALTER COLUMN organ TYPE organtype 
                 USING organ::text::organtype""")
    
    # Eliminar el tipo antiguo
    op.execute("DROP TYPE organtype_old")