"""Cambios varios

Revision ID: 9ddbc22c647d
Revises: 194b803add96
Create Date: 2025-03-02 11:35:12.847104

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import postgresql

# revision identifiers, used by Alembic.
revision: str = '9ddbc22c647d'
down_revision: Union[str, None] = '194b803add96'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('decks')
    op.drop_constraint('games_deck_id_key', 'games', type_='unique')
    op.drop_constraint('games_deck_id_fkey', 'games', type_='foreignkey')
    op.drop_column('games', 'deck_id')
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('games', sa.Column('deck_id', sa.INTEGER(), autoincrement=False, nullable=True))
    op.create_foreign_key('games_deck_id_fkey', 'games', 'decks', ['deck_id'], ['id'])
    op.create_unique_constraint('games_deck_id_key', 'games', ['deck_id'])
    op.create_table('decks',
    sa.Column('id', sa.INTEGER(), autoincrement=True, nullable=False),
    sa.Column('deck_cards', postgresql.JSONB(astext_type=sa.Text()), autoincrement=False, nullable=True),
    sa.Column('discard_cards', postgresql.JSONB(astext_type=sa.Text()), autoincrement=False, nullable=True),
    sa.PrimaryKeyConstraint('id', name='decks_pkey')
    )
    # ### end Alembic commands ###
