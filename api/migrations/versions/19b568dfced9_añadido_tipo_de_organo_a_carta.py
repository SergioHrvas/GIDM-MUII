"""Añadido tipo de organo a carta

Revision ID: 19b568dfced9
Revises: 8f4d01d8dfe0
Create Date: 2025-02-26 14:58:35.211232

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '19b568dfced9'
down_revision: Union[str, None] = '8f4d01d8dfe0'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('cards', sa.Column('organ_type', sa.Enum('heart', 'stomach', 'brain', 'liver', name='organtype'), nullable=True))
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('cards', 'organ_type')
    # ### end Alembic commands ###
