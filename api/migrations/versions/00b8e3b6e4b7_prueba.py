"""Prueba

Revision ID: 00b8e3b6e4b7
Revises: fef988d6fc82
Create Date: 2025-02-26 19:06:56.866170

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '00b8e3b6e4b7'
down_revision: Union[str, None] = 'fef988d6fc82'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    pass


def downgrade() -> None:
    pass
