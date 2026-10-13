from fastapi import FastAPI
from fastapi import Depends
from fastapi import HTTPException
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session

from auth import authenticate_user
from auth import create_access_token
from auth import create_default_user
from auth import get_current_user

from database import Base
from database import engine
from database import SessionLocal
from database import get_db

from models import Student
from models import User
from schemas import StudentCreate
from schemas import StudentResponse
from schemas import TokenResponse


Base.metadata.create_all(bind=engine)

with SessionLocal() as db:
    create_default_user(db)

app = FastAPI(
    title="Student Management API",
    version="1.0"
)


def find_student(student_id: int, db: Session) -> Student:
    student = db.query(Student).filter(
        Student.id == student_id
    ).first()

    if student is None:
        raise HTTPException(
            status_code=404,
            detail="Student not found"
        )

    return student


@app.get("/")
def root():
    return {
        "message": "Student Management API"
    }


# Login: รับ username / password แบบ Form (OAuth2 Password Flow)
# ถูกต้อง -> ส่ง JWT กลับ, ผิด -> 401
@app.post(
    "/token",
    response_model=TokenResponse
)
def login(
    form: OAuth2PasswordRequestForm = Depends(),
    db: Session = Depends(get_db)
):
    user = authenticate_user(db, form.username, form.password)
    if user is None:
        raise HTTPException(
            status_code=401,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"}
        )

    return {
        "access_token": create_access_token(user.username),
        "token_type": "bearer"
    }


# ---------- Protected API: ต้องแนบ Authorization: Bearer <token> ----------

# GET = SELECT
@app.get(
    "/students",
    response_model=list[StudentResponse]
)
def get_students(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    return db.query(Student).order_by(Student.id).all()


@app.get(
    "/students/{student_id}",
    response_model=StudentResponse
)
def get_student(
    student_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    return find_student(student_id, db)


# POST = INSERT
@app.post(
    "/students",
    response_model=StudentResponse
)
def create_student(
    student: StudentCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    new_student = Student(
        student_code=student.student_code,
        name=student.name,
        major=student.major
    )

    db.add(new_student)
    db.commit()
    db.refresh(new_student)

    return new_student


# PUT = UPDATE
@app.put(
    "/students/{student_id}",
    response_model=StudentResponse
)
def update_student(
    student_id: int,
    student: StudentCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    existing = find_student(student_id, db)

    existing.student_code = student.student_code
    existing.name = student.name
    existing.major = student.major

    db.commit()
    db.refresh(existing)

    return existing


# DELETE = DELETE
@app.delete(
    "/students/{student_id}"
)
def delete_student(
    student_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    student = find_student(student_id, db)

    db.delete(student)
    db.commit()

    return {
        "message": "Student deleted"
    }
