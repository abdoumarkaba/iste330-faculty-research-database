# Faculty Research Database

A Java Swing desktop application for managing faculty research abstracts and student-faculty matching based on research keywords.

## Architecture

This application follows a **3-tier layered architecture**:

```
┌─────────────────────────────────────┐
│  Presentation Layer (gui)             │  ← Swing UI components
│  LoginFrame, FacultyPanel, etc.     │
├─────────────────────────────────────┤
│  Data Access Layer (dao)            │  ← Database operations
│  FacultyDAO, StudentDAO, etc.       │
├─────────────────────────────────────┤
│  Model Layer (model)                │  ← Entity POJOs
│  Faculty, Student, PublicUser       │
└─────────────────────────────────────┘
```

## Project Structure

```
faculty-research-db/
├── src/
│   ├── Main.java              # Application entry point
│   ├── model/                 # Entity classes
│   │   ├── Faculty.java
│   │   ├── Student.java
│   │   └── PublicUser.java
│   ├── dao/                   # Database access objects
│   │   ├── DBConnection.java  # Connection pool (static)
│   │   ├── FacultyDAO.java
│   │   ├── StudentDAO.java
│   │   └── PublicUserDAO.java
│   ├── gui/                   # Swing UI components
│   │   ├── LoginFrame.java    # Authentication screen
│   │   ├── FacultyPanel.java  # Faculty dashboard
│   │   ├── StudentPanel.java  # Student search interface
│   │   └── PublicPanel.java   # Public search (no login)
│   └── test/                  # Backend test classes
│       ├── DBConnectionTest.java
│       ├── FacultyDAOTest.java
│       ├── StudentDAOTest.java
│       └── PublicUserDAOTest.java
├── sql/
│   └── schema.sql             # Database creation script
├── db.properties              # MySQL credentials
├── directions.txt             # End-user setup guide
└── mysql-connector-java.jar   # JDBC driver
```

## Database Schema

The MySQL database `faculty_research_db` contains:

| Table | Purpose |
|-------|---------|
| `faculty` | Faculty member profiles |
| `students` | Student profiles |
| `faculty_keywords` | Research interest tags for faculty |
| `student_keywords` | Research interest tags for students |
| `faculty_abstracts` | Published research abstracts |

## Building and Running

### Prerequisites
- Java JDK 8+
- MySQL Server
- MySQL Connector/J (provided)

### Setup

1. **Initialize database:**
   ```bash
   mysql -u root -p < sql/schema.sql
   ```

2. **Configure credentials:**
   Edit `db.properties`:
   ```properties
   host=localhost
   user=your_username
   password=your_password
   ```

3. **Compile:**
   ```bash
   javac -cp .:mysql-connector-java.jar src/Main.java src/dao/*.java src/model/*.java src/gui/*.java src/test/*.java -d out
   cp db.properties out/
   ```

4. **Run:**
   ```bash
   java -cp out:mysql-connector-java.jar Main
   ```

5. **Test backend:**
   ```bash
   java -cp out:mysql-connector-java.jar test.DBConnectionTest
   java -cp out:mysql-connector-java.jar test.FacultyDAOTest
   java -cp out:mysql-connector-java.jar test.StudentDAOTest
   java -cp out:mysql-connector-java.jar test.PublicUserDAOTest
   ```

## Key Design Patterns

- **DAO Pattern**: Each entity has a corresponding DAO for database operations
- **Singleton Pattern**: `DBConnection` manages a single connection configuration
- **MVC Separation**: Models (entities), Views (GUI), Controllers (DAO logic)

## User Roles

| Role | Authentication | Capabilities |
|------|---------------|--------------|
| Faculty | ID validation via `FacultyDAO.getFacultyById()` | CRUD abstracts, search students by keyword, match students to faculty interests |
| Student | ID validation via `StudentDAO.isValidStudentId()` | Search faculty by keyword/abstract, view all faculty |
| Public | None required | Read-only search across faculty and students by keyword |

## Key Backend Features

- Faculty can search students whose keywords match faculty's interests or abstracts
- Students can search faculty by keyword OR abstract content
- Public users can search both students and faculty with a single keyword
- All DAO methods include proper error handling and null safety

## UML Documentation

- Discard, needs update