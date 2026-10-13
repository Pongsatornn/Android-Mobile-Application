# Student Management API

FastAPI + PostgreSQL backend สำหรับ StudentManagementApp (Lab 2)

## 1. เตรียม Database

### เครื่องนี้: PostgreSQL รันใน Docker
container ชื่อ `student-postgres` (user `postgres` / รหัส `password` / port 5432)
ข้อมูลเก็บใน volume `student-pgdata` และสร้าง `student_db` ไว้แล้ว

```
docker start student-postgres                                     # เปิด (ถ้ายังไม่รัน)
docker exec -it student-postgres psql -U postgres -d student_db   # เข้า psql
```

### ติดตั้งแบบปกติ
รัน `init.sql` ใน pgAdmin หรือ psql:

```
psql -U postgres -c "CREATE DATABASE student_db;"
psql -U postgres -d student_db -f init.sql
```
(บรรทัด `CREATE DATABASE` ใน init.sql จะ error ว่ามีอยู่แล้ว ไม่เป็นไร)

## 2. ติดตั้งและรัน
```
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
uvicorn main:app --reload --host 0.0.0.0
```

แก้ user/password ใน `database.py` ให้ตรงกับเครื่อง หรือตั้ง environment variable:
```
set DATABASE_URL=postgresql+psycopg2://postgres:<password>@localhost:5432/student_db
```

`--host 0.0.0.0` จำเป็นเฉพาะตอนใช้มือถือจริง (Emulator ใช้ `10.0.2.2` ได้เลย)

## 3. ทดสอบ
Swagger UI: http://127.0.0.1:8000/docs

| HTTP   | Endpoint         | หน้าที่              |
|--------|------------------|---------------------|
| GET    | /students        | อ่านข้อมูลทั้งหมด     |
| GET    | /students/{id}   | อ่านข้อมูลหนึ่งคน     |
| POST   | /students        | เพิ่มข้อมูล           |
| PUT    | /students/{id}   | แก้ไขข้อมูล           |
| DELETE | /students/{id}   | ลบข้อมูล             |
