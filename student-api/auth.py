import hashlib
import hmac
import os
import secrets
from datetime import datetime, timedelta, timezone

import jwt
from fastapi import Depends
from fastapi import HTTPException
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session

from database import get_db
from models import User

# ใช้คีย์นี้เซ็น JWT ห้ามเปิดเผย ตอนใช้งานจริงให้ตั้ง environment variable SECRET_KEY
# HS256 ต้องใช้คีย์ยาวอย่างน้อย 32 bytes
SECRET_KEY = os.getenv(
    "SECRET_KEY",
    "dev-only-secret-key-change-me-before-deploying"
)
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60

# บอก FastAPI ว่า Token ได้จาก POST /token (ทำให้ปุ่ม Authorize ใน Swagger ใช้ได้)
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

PBKDF2_ITERATIONS = 200_000


# เก็บรหัสผ่านแบบ hash + salt ห้ามเก็บรหัสผ่านจริงลง Database
def hash_password(password: str) -> str:
    salt = secrets.token_hex(16)
    digest = hashlib.pbkdf2_hmac(
        "sha256", password.encode(), bytes.fromhex(salt), PBKDF2_ITERATIONS
    ).hex()
    return f"{salt}${digest}"


def verify_password(password: str, stored: str) -> bool:
    salt, digest = stored.split("$")
    check = hashlib.pbkdf2_hmac(
        "sha256", password.encode(), bytes.fromhex(salt), PBKDF2_ITERATIONS
    ).hex()
    return hmac.compare_digest(check, digest)


def authenticate_user(db: Session, username: str, password: str) -> User | None:
    user = db.query(User).filter(User.username == username).first()
    if user is None or not verify_password(password, user.hashed_password):
        return None
    return user


# JWT = Header.Payload.Signature
# Payload เก็บ sub (ชื่อผู้ใช้) และ exp (เวลาหมดอายุ)
def create_access_token(username: str) -> str:
    expire = datetime.now(timezone.utc) + timedelta(
        minutes=ACCESS_TOKEN_EXPIRE_MINUTES
    )
    payload = {"sub": username, "exp": expire}
    return jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)


# ใช้เป็น Depends กับทุก Protected API
# Backend ต้องตรวจ Token เองทุกครั้ง ไม่เชื่อว่าแอปซ่อนหน้าจอให้แล้ว
def get_current_user(
    token: str = Depends(oauth2_scheme),
    db: Session = Depends(get_db)
) -> User:
    credentials_error = HTTPException(
        status_code=401,
        detail="Invalid or expired token",
        headers={"WWW-Authenticate": "Bearer"}
    )

    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username = payload.get("sub")
    except jwt.InvalidTokenError:
        raise credentials_error

    if username is None:
        raise credentials_error

    user = db.query(User).filter(User.username == username).first()
    if user is None:
        raise credentials_error

    return user


# สร้างผู้ใช้ทดสอบ admin / admin123 ถ้ายังไม่มี
def create_default_user(db: Session):
    if db.query(User).filter(User.username == "admin").first() is None:
        db.add(User(
            username="admin",
            hashed_password=hash_password("admin123")
        ))
        db.commit()
