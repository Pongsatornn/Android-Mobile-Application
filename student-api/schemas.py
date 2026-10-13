from pydantic import BaseModel, ConfigDict


class StudentBase(BaseModel):
    student_code: str
    name: str
    major: str


class StudentCreate(StudentBase):
    pass


class StudentResponse(StudentBase):

    id: int

    # ให้ Pydantic อ่านค่าจาก SQLAlchemy object ได้
    model_config = ConfigDict(from_attributes=True)


# รูปแบบ JSON ที่ POST /token ส่งกลับ (ชื่อ field ตามมาตรฐาน OAuth2)
class TokenResponse(BaseModel):
    access_token: str
    token_type: str
