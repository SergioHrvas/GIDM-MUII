"""Quitado cuerpo

Revision ID: 8f4d01d8dfe0
Revises: ad4899b9a0ce
Create Date: 2025-02-26 13:56:11.757644

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '8f4d01d8dfe0'
down_revision: Union[str, None] = 'ad4899b9a0ce'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('organs', sa.Column('player_id', sa.Integer(), nullable=True))
    op.create_foreign_key(None, 'organs', 'players', ['player_id'], ['id'], ondelete='CASCADE')
    op.drop_column('organs', 'body_id')
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('organs', sa.Column('body_id', sa.INTEGER(), autoincrement=False, nullable=True))
    op.drop_constraint(None, 'organs', type_='foreignkey')
    op.drop_column('organs', 'player_id')
    # ### end Alembic commands ###
