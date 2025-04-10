"""Cambios

Revision ID: 3e0545ffbaeb
Revises: 0105a81d82a4
Create Date: 2025-04-12 00:23:18.435524

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '3e0545ffbaeb'
down_revision: Union[str, None] = '0105a81d82a4'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('organs', sa.Column('tipo', sa.Enum('heart', 'intestine', 'brain', 'lungs', 'magic', name='organtype'), nullable=False))
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('organs', 'tipo')
    # ### end Alembic commands ###
