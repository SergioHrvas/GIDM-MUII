"""Añadido tipo de organo a carta2

Revision ID: c8f9caa3653c
Revises: 19b568dfced9
Create Date: 2025-02-26 15:01:30.235371

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'c8f9caa3653c'
down_revision: Union[str, None] = '19b568dfced9'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    pass
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    pass
    # ### end Alembic commands ###
