"""Initial migration: users, student_profile, attendance, assignment

Revision ID: 0001_initial_schema
Revises: 
Create Date: 2026-08-18 10:00:00.000000

"""
from typing import Sequence, Union
from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision: str = '0001_initial_schema'
down_revision: Union[str, None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # 1. Create users table
    op.create_table(
        'users',
        sa.Column('id', sa.Integer(), nullable=False, autoincrement=True),
        sa.Column('email', sa.String(length=255), nullable=False),
        sa.Column('hashed_password', sa.String(length=255), nullable=True),
        sa.Column('full_name', sa.String(length=255), nullable=False, server_default='Ayush'),
        sa.Column('is_active', sa.Boolean(), nullable=False, server_default=sa.text('true')),
        sa.Column('created_at', sa.DateTime(), nullable=False, server_default=sa.func.now()),
        sa.Column('updated_at', sa.DateTime(), nullable=False, server_default=sa.func.now()),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_users_id'), 'users', ['id'], unique=False)
    op.create_index(op.f('ix_users_email'), 'users', ['email'], unique=True)

    # 2. Create student_profile table
    op.create_table(
        'student_profile',
        sa.Column('id', sa.Integer(), nullable=False, autoincrement=True),
        sa.Column('student_id', sa.String(length=100), nullable=False),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('university', sa.String(length=255), nullable=True, server_default='National Institute of Technology'),
        sa.Column('major', sa.String(length=255), nullable=False, server_default='Computer Science & Engineering'),
        sa.Column('minor', sa.String(length=255), nullable=True, server_default='Data Science & Artificial Intelligence'),
        sa.Column('enrollment_year', sa.Integer(), nullable=False, server_default='2022'),
        sa.Column('expected_graduation_year', sa.Integer(), nullable=False, server_default='2026'),
        sa.Column('gpa', sa.Float(), nullable=False, server_default='8.85'),
        sa.Column('academic_goals', sa.Text(), nullable=True),
        sa.Column('preferred_learning_style', sa.String(length=100), nullable=True, server_default='Visual & Hands-on Coding'),
        sa.Column('extracurricular_activities', sa.Text(), nullable=True),
        sa.Column('semester', sa.Integer(), nullable=False, server_default='6'),
        sa.Column('bio', sa.Text(), nullable=True),
        sa.Column('theme_mode', sa.String(length=50), nullable=False, server_default='SYSTEM'),
        sa.Column('theme_palette', sa.String(length=50), nullable=False, server_default='INDIGO'),
        sa.Column('created_at', sa.DateTime(), nullable=False, server_default=sa.func.now()),
        sa.Column('updated_at', sa.DateTime(), nullable=False, server_default=sa.func.now()),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_student_profile_id'), 'student_profile', ['id'], unique=False)
    op.create_index(op.f('ix_student_profile_student_id'), 'student_profile', ['student_id'], unique=True)
    op.create_index(op.f('ix_student_profile_user_id'), 'student_profile', ['user_id'], unique=True)

    # 3. Create attendance table
    op.create_table(
        'attendance',
        sa.Column('id', sa.Integer(), nullable=False, autoincrement=True),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('subject_name', sa.String(length=255), nullable=False),
        sa.Column('subject_code', sa.String(length=50), nullable=False),
        sa.Column('attended_classes', sa.Integer(), nullable=False, server_default='0'),
        sa.Column('total_classes', sa.Integer(), nullable=False, server_default='0'),
        sa.Column('target_percentage', sa.Float(), nullable=False, server_default='75.0'),
        sa.Column('professor', sa.String(length=255), nullable=True, server_default=''),
        sa.Column('room', sa.String(length=100), nullable=True, server_default=''),
        sa.Column('color_hex', sa.String(length=20), nullable=True, server_default='#4F46E5'),
        sa.Column('created_at', sa.DateTime(), nullable=False, server_default=sa.func.now()),
        sa.Column('updated_at', sa.DateTime(), nullable=False, server_default=sa.func.now()),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_attendance_id'), 'attendance', ['id'], unique=False)
    op.create_index(op.f('ix_attendance_user_id'), 'attendance', ['user_id'], unique=False)

    # 4. Create assignment table
    op.create_table(
        'assignment',
        sa.Column('id', sa.Integer(), nullable=False, autoincrement=True),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('title', sa.String(length=255), nullable=False),
        sa.Column('subject', sa.String(length=255), nullable=False),
        sa.Column('due_date', sa.DateTime(), nullable=False),
        sa.Column('priority', sa.String(length=50), nullable=False, server_default='MEDIUM'),
        sa.Column('status', sa.String(length=50), nullable=False, server_default='PENDING'),
        sa.Column('description', sa.Text(), nullable=True, server_default=''),
        sa.Column('created_at', sa.DateTime(), nullable=False, server_default=sa.func.now()),
        sa.Column('updated_at', sa.DateTime(), nullable=False, server_default=sa.func.now()),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_assignment_id'), 'assignment', ['id'], unique=False)
    op.create_index(op.f('ix_assignment_user_id'), 'assignment', ['user_id'], unique=False)


def downgrade() -> None:
    # Drop tables in reverse topological order
    op.drop_index(op.f('ix_assignment_user_id'), table_name='assignment')
    op.drop_index(op.f('ix_assignment_id'), table_name='assignment')
    op.drop_table('assignment')

    op.drop_index(op.f('ix_attendance_user_id'), table_name='attendance')
    op.drop_index(op.f('ix_attendance_id'), table_name='attendance')
    op.drop_table('attendance')

    op.drop_index(op.f('ix_student_profile_user_id'), table_name='student_profile')
    op.drop_index(op.f('ix_student_profile_student_id'), table_name='student_profile')
    op.drop_index(op.f('ix_student_profile_id'), table_name='student_profile')
    op.drop_table('student_profile')

    op.drop_index(op.f('ix_users_email'), table_name='users')
    op.drop_index(op.f('ix_users_id'), table_name='users')
    op.drop_table('users')
