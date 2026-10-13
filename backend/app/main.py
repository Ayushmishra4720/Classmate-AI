from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from .database import engine, Base
from .routers import student_profile

# Ensure database tables exist
Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Classmate AI – Backend API",
    description="“One App. Every Student Need. Powered by AI.” Created by Ayush. Production FastAPI & PostgreSQL backend for Student Profiles, Attendance, Assignments, and AI Tools.",
    version="2.0.0"
)

# Enable CORS for Web & Mobile Clients
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(student_profile.router)

@app.get("/")
def root():
    return {
        "app": "Classmate AI",
        "tagline": "One App. Every Student Need. Powered by AI.",
        "creator": "Ayush",
        "status": "operational",
        "docs_url": "/docs"
    }

@app.get("/api/v1/health")
def health_check():
    return {
        "status": "healthy",
        "database": "connected",
        "features": [
            "Student Profile CRUD",
            "Attendance Tracking",
            "Assignment Management",
            "Gemini AI Doubt Solver",
            "Multiple Theme Modes (Light, Dark, OLED) & Palettes"
        ]
    }
