"""Añadido tipo de organo enum

Revision ID: fef988d6fc82
Revises: ab7ed77b4596
Create Date: 2025-02-26 15:06:42.366224

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'fef988d6fc82'
down_revision: Union[str, None] = 'ab7ed77b4596'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade():
    # Añade el nuevo valor al enum
    op.execute("ALTER TYPE organtype ADD VALUE 'magic'")

def downgrade():
    # Elimina el nuevo valor del enum (esto puede ser complicado en PostgreSQL)
    op.execute("ALTER TYPE organtype DROP VALUE 'magic'")
