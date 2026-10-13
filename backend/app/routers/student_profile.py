from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, status, Response
from sqlalchemy.orm import Session
from ..database import get_db
from ..models import User, StudentProfile
from ..schemas import StudentProfileCreate, StudentProfileUpdate, StudentProfileOut
from ..auth import get_current_user

router = APIRouter(prefix="/api/v1/profile", tags=["Student Profile"])

@router.post("", response_model=StudentProfileOut, status_code=status.HTTP_201_CREATED)
def create_student_profile(
    profile_in: StudentProfileCreate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Create a student profile linked to the authenticated user.
    """
    # Check if user already has a profile
    existing_user_profile = db.query(StudentProfile).filter(StudentProfile.user_id == current_user.id).first()
    if existing_user_profile:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Student profile already exists for this authenticated user. Use PUT to update."
        )

    # Check if student_id is already in use by another user
    existing_student_id = db.query(StudentProfile).filter(StudentProfile.student_id == profile_in.student_id).first()
    if existing_student_id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Student ID '{profile_in.student_id}' is already registered in the system."
        )

    profile_data = profile_in.model_dump()
    db_profile = StudentProfile(**profile_data, user_id=current_user.id)
    db.add(db_profile)
    db.commit()
    db.refresh(db_profile)
    return db_profile


@router.get("", response_model=StudentProfileOut)
def get_my_student_profile(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Read the student profile of the authenticated user.
    """
    profile = db.query(StudentProfile).filter(StudentProfile.user_id == current_user.id).first()
    if not profile:
        # Create a default seeded profile if none exists for demo convenience
        profile = StudentProfile(
            student_id="NIT-CS-2026-042",
            user_id=current_user.id,
            university="National Institute of Technology",
            major="Computer Science & Engineering",
            minor="Data Science & Artificial Intelligence",
            enrollment_year=2022,
            expected_graduation_year=2026,
            gpa=8.85,
            academic_goals="Maintain 9.0+ CGPA, publish AI Systems research, and crack top-tier tech placements.",
            preferred_learning_style="Visual & Hands-on Coding",
            extracurricular_activities="Lead of Open Source Club, Competitive Programmer, Hackathon Mentor",
            semester=6,
            bio="Classmate AI Student | Aspiring AI & Systems Engineer",
            theme_mode="SYSTEM",
            theme_palette="INDIGO"
        )
        db.add(profile)
        db.commit()
        db.refresh(profile)
    return profile


@router.put("", response_model=StudentProfileOut)
def update_my_student_profile(
    profile_update: StudentProfileUpdate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Update the student profile of the authenticated user.
    """
    profile = db.query(StudentProfile).filter(StudentProfile.user_id == current_user.id).first()
    if not profile:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Student profile not found for this user. Create one first."
        )

    update_data = profile_update.model_dump(exclude_unset=True)

    # Check for student_id collision if changed
    if "student_id" in update_data and update_data["student_id"] != profile.student_id:
        existing = db.query(StudentProfile).filter(
            StudentProfile.student_id == update_data["student_id"],
            StudentProfile.id != profile.id
        ).first()
        if existing:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Student ID '{update_data['student_id']}' is already assigned to another student."
            )

    for field, value in update_data.items():
        setattr(profile, field, value)

    db.commit()
    db.refresh(profile)
    return profile


@router.delete("", status_code=status.HTTP_204_NO_CONTENT)
def delete_my_student_profile(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Delete the student profile of the authenticated user.
    """
    profile = db.query(StudentProfile).filter(StudentProfile.user_id == current_user.id).first()
    if not profile:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No student profile exists for this user."
        )

    db.delete(profile)
    db.commit()
    return Response(status_code=status.HTTP_204_NO_CONTENT)


@router.get("/by-student-id/{student_id}", response_model=StudentProfileOut)
def get_student_profile_by_student_id(
    student_id: str,
    db: Session = Depends(get_db)
):
    """
    Public lookup of a student profile by university student_id / roll number.
    """
    profile = db.query(StudentProfile).filter(StudentProfile.student_id == student_id).first()
    if not profile:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Student profile with ID '{student_id}' not found."
        )
    return profile
