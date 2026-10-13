from datetime import datetime
from typing import Optional, List
from pydantic import BaseModel, EmailStr, Field

# --- Student Profile Schemas ---
class StudentProfileBase(BaseModel):
    student_id: str = Field(..., example="NIT-CS-2026-042", description="Unique institutional student roll/id")
    major: str = Field(..., example="Computer Science & Engineering")
    minor: Optional[str] = Field(None, example="Data Science & Artificial Intelligence")
    university: Optional[str] = Field("National Institute of Technology", example="National Institute of Technology")
    enrollment_year: int = Field(..., ge=1900, le=2100, example=2022)
    expected_graduation_year: int = Field(..., ge=1900, le=2100, example=2026)
    gpa: float = Field(..., ge=0.0, le=10.0, example=8.85)
    academic_goals: Optional[str] = Field(None, example="Maintain 9.0+ CGPA and crack tier-1 AI placements")
    preferred_learning_style: Optional[str] = Field("Visual & Hands-on Coding", example="Visual & Hands-on Coding")
    extracurricular_activities: Optional[str] = Field(None, example="Open Source Club Lead, Hackathon Mentor")
    semester: Optional[int] = Field(6, ge=1, le=12)
    bio: Optional[str] = Field(None, example="Aspiring AI Engineer")
    theme_mode: Optional[str] = Field("SYSTEM", example="DARK") # SYSTEM, LIGHT, DARK, OLED
    theme_palette: Optional[str] = Field("INDIGO", example="INDIGO") # INDIGO, EMERALD, SUNSET, VIOLET, OCEAN

class StudentProfileCreate(StudentProfileBase):
    pass

class StudentProfileUpdate(BaseModel):
    student_id: Optional[str] = None
    major: Optional[str] = None
    minor: Optional[str] = None
    university: Optional[str] = None
    enrollment_year: Optional[int] = None
    expected_graduation_year: Optional[int] = None
    gpa: Optional[float] = None
    academic_goals: Optional[str] = None
    preferred_learning_style: Optional[str] = None
    extracurricular_activities: Optional[str] = None
    semester: Optional[int] = None
    bio: Optional[str] = None
    theme_mode: Optional[str] = None
    theme_palette: Optional[str] = None

class StudentProfileOut(StudentProfileBase):
    id: int
    user_id: int
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True

# --- User Schemas ---
class UserBase(BaseModel):
    email: EmailStr
    full_name: str

class UserCreate(UserBase):
    password: str

class UserOut(UserBase):
    id: int
    is_active: bool
    created_at: datetime
    profile: Optional[StudentProfileOut] = None

    class Config:
        from_attributes = True

# --- Attendance Schemas ---
class AttendanceBase(BaseModel):
    subject_name: str
    subject_code: str
    attended_classes: int = 0
    total_classes: int = 0
    target_percentage: float = 75.0
    professor: Optional[str] = ""
    room: Optional[str] = ""
    color_hex: Optional[str] = "#4F46E5"

class AttendanceCreate(AttendanceBase):
    pass

class AttendanceUpdate(BaseModel):
    attended_classes: Optional[int] = None
    total_classes: Optional[int] = None
    target_percentage: Optional[float] = None

class AttendanceOut(AttendanceBase):
    id: int
    user_id: int
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True

# --- Assignment Schemas ---
class AssignmentBase(BaseModel):
    title: str
    subject: str
    due_date: datetime
    priority: str = "MEDIUM"
    status: str = "PENDING"
    description: Optional[str] = ""

class AssignmentCreate(AssignmentBase):
    pass

class AssignmentUpdate(BaseModel):
    title: Optional[str] = None
    priority: Optional[str] = None
    status: Optional[str] = None
    description: Optional[str] = None

class AssignmentOut(AssignmentBase):
    id: int
    user_id: int
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True
