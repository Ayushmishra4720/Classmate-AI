import datetime
from sqlalchemy import (
    Column, Integer, String, Float, Text, Boolean, DateTime, ForeignKey
)
from sqlalchemy.orm import relationship
from .database import Base

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    email = Column(String(255), unique=True, index=True, nullable=False)
    hashed_password = Column(String(255), nullable=True)
    full_name = Column(String(255), nullable=False, default="Ayush")
    is_active = Column(Boolean, default=True, nullable=False)
    created_at = Column(DateTime, default=datetime.datetime.utcnow, nullable=False)
    updated_at = Column(DateTime, default=datetime.datetime.utcnow, onupdate=datetime.datetime.utcnow, nullable=False)

    # Relationships
    profile = relationship("StudentProfile", back_populates="user", uselist=False, cascade="all, delete-orphan")
    attendance_records = relationship("Attendance", back_populates="user", cascade="all, delete-orphan")
    assignments = relationship("Assignment", back_populates="user", cascade="all, delete-orphan")


class StudentProfile(Base):
    __tablename__ = "student_profile"

    id = Column(Integer, primary_key=True, index=True)
    student_id = Column(String(100), unique=True, index=True, nullable=False)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), unique=True, nullable=False, index=True)
    
    university = Column(String(255), nullable=True, default="National Institute of Technology")
    major = Column(String(255), nullable=False, default="Computer Science & Engineering")
    minor = Column(String(255), nullable=True, default="Data Science & Artificial Intelligence")
    enrollment_year = Column(Integer, nullable=False, default=2022)
    expected_graduation_year = Column(Integer, nullable=False, default=2026)
    gpa = Column(Float, nullable=False, default=8.85)
    academic_goals = Column(Text, nullable=True, default="Maintain 9.0+ CGPA, publish AI Systems research, and crack top-tier tech placements.")
    preferred_learning_style = Column(String(100), nullable=True, default="Visual & Hands-on Coding")
    extracurricular_activities = Column(Text, nullable=True, default="Lead of Open Source Club, Competitive Programmer, Hackathon Mentor")
    
    semester = Column(Integer, nullable=False, default=6)
    bio = Column(Text, nullable=True, default="Classmate AI Student | Aspiring AI & Systems Engineer")
    theme_mode = Column(String(50), nullable=False, default="SYSTEM") # SYSTEM, LIGHT, DARK, OLED
    theme_palette = Column(String(50), nullable=False, default="INDIGO") # INDIGO, EMERALD, SUNSET, VIOLET, OCEAN
    
    created_at = Column(DateTime, default=datetime.datetime.utcnow, nullable=False)
    updated_at = Column(DateTime, default=datetime.datetime.utcnow, onupdate=datetime.datetime.utcnow, nullable=False)

    # Relationships
    user = relationship("User", back_populates="profile")


class Attendance(Base):
    __tablename__ = "attendance"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    
    subject_name = Column(String(255), nullable=False)
    subject_code = Column(String(50), nullable=False)
    attended_classes = Column(Integer, nullable=False, default=0)
    total_classes = Column(Integer, nullable=False, default=0)
    target_percentage = Column(Float, nullable=False, default=75.0)
    professor = Column(String(255), nullable=True, default="")
    room = Column(String(100), nullable=True, default="")
    color_hex = Column(String(20), nullable=True, default="#4F46E5")

    created_at = Column(DateTime, default=datetime.datetime.utcnow, nullable=False)
    updated_at = Column(DateTime, default=datetime.datetime.utcnow, onupdate=datetime.datetime.utcnow, nullable=False)

    # Relationships
    user = relationship("User", back_populates="attendance_records")


class Assignment(Base):
    __tablename__ = "assignment"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)

    title = Column(String(255), nullable=False)
    subject = Column(String(255), nullable=False)
    due_date = Column(DateTime, nullable=False)
    priority = Column(String(50), nullable=False, default="MEDIUM") # HIGH, MEDIUM, LOW
    status = Column(String(50), nullable=False, default="PENDING")   # PENDING, IN_PROGRESS, COMPLETED
    description = Column(Text, nullable=True, default="")

    created_at = Column(DateTime, default=datetime.datetime.utcnow, nullable=False)
    updated_at = Column(DateTime, default=datetime.datetime.utcnow, onupdate=datetime.datetime.utcnow, nullable=False)

    # Relationships
    user = relationship("User", back_populates="assignments")
