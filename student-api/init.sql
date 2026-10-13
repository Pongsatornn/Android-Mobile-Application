-- PART A: รันใน psql หรือ pgAdmin
-- STEP 1: สร้าง Database (รันตอนเชื่อมต่อกับ database "postgres")
CREATE DATABASE student_db;

-- STEP 2-3: เชื่อมต่อ student_db ก่อน (psql: \c student_db) แล้วรันส่วนนี้
CREATE TABLE IF NOT EXISTS students (
    id SERIAL PRIMARY KEY,
    student_code VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    major VARCHAR(100) NOT NULL
);

INSERT INTO students
(student_code, name, major)
VALUES
('65001', 'Somchai Jaidee', 'Computer Science'),
('65002', 'Somsri Dee', 'Information Technology'),
('65003', 'Anan Kong', 'Data Science');

SELECT * FROM students;
