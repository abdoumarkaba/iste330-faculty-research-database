-- Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
-- Date: 2026-04-13 | ISTE 330

DROP DATABASE IF EXISTS faculty_research_db;
CREATE DATABASE faculty_research_db;
USE faculty_research_db;

CREATE TABLE faculty (
    faculty_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    building VARCHAR(50),
    office_number VARCHAR(20),
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NULL COMMENT 'PBKDF2 hashed password - NULL means account not activated'
);

CREATE TABLE faculty_abstracts (
    abstract_id INT PRIMARY KEY AUTO_INCREMENT,
    faculty_id INT NOT NULL,
    abstract_type ENUM('book','speaking') NOT NULL,
    abstract_text TEXT,
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id) ON DELETE CASCADE
);

CREATE TABLE faculty_keywords (
    keyword_id INT PRIMARY KEY AUTO_INCREMENT,
    faculty_id INT NOT NULL,
    keyword VARCHAR(50) NOT NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id) ON DELETE CASCADE
);

CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NULL COMMENT 'PBKDF2 hashed password - NULL means account not activated'
);

CREATE TABLE student_keywords (
    keyword_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    keyword VARCHAR(50) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE TABLE public_users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    organization_name VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE public_keywords (
    keyword_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    keyword VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES public_users(user_id) ON DELETE CASCADE
);

-- Sample Data
INSERT INTO faculty (first_name, last_name, building, office_number, email) VALUES
('John', 'Doe', 'GOL', '2200', 'john.doe@rit.edu'),
('Jane', 'Smith', 'GOL', '2310', 'jane.smith@rit.edu'),
('Bob', 'Lee', 'GOL', '2405', 'bob.lee@rit.edu');

INSERT INTO faculty_keywords (faculty_id, keyword) VALUES
(1, 'AI'), (1, 'Machine Learning'), (1, 'Data Science'),
(2, 'Security'), (2, 'Networking'), (2, 'Cryptography'),
(3, 'HCI'), (3, 'UX Design'), (3, 'Usability');

INSERT INTO faculty_abstracts (faculty_id, abstract_type, abstract_text) VALUES
(1, 'book', 'A comprehensive study on artificial intelligence in modern applications.'),
(1, 'speaking', 'Presented at IEEE Conference 2025 on advancements in machine learning.'),
(2, 'book', 'Cybersecurity threats in distributed systems.'),
(2, 'speaking', 'Talk on secure network protocols at DEF CON.'),
(3, 'book', 'Human-Computer Interaction: Principles and Best Practices.'),
(3, 'speaking', 'Designing accessible user interfaces for diverse populations.');

INSERT INTO students (first_name, last_name, email) VALUES
('Alice', 'Brown', 'alice.brown@rit.edu'),
('Charlie', 'Green', 'charlie.green@rit.edu'),
('Diana', 'White', 'diana.white@rit.edu');

INSERT INTO student_keywords (student_id, keyword) VALUES
(1, 'AI'), (1, 'Data Analysis'), (1, 'Visualization'),
(2, 'Security'), (2, 'Penetration Testing'), (2, 'Firewalls'),
(3, 'HCI'), (3, 'Prototyping'), (3, 'User Research');

INSERT INTO public_users (organization_name, contact_email) VALUES
('TechCorp Inc.', 'contact@techcorp.com'),
('Research Foundation', 'info@research.org');

INSERT INTO public_keywords (user_id, keyword) VALUES
(1, 'AI'),
(2, 'Security');
