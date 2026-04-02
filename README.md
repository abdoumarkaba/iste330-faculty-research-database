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
│   └── gui/                   # Swing UI components
│       ├── LoginFrame.java    # Authentication screen
│       ├── FacultyPanel.java  # Faculty dashboard
│       ├── StudentPanel.java  # Student search interface
│       └── PublicPanel.java   # Public search (no login)
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
   javac -cp .:mysql-connector-java.jar src/Main.java src/dao/*.java src/model/*.java src/gui/*.java -d out
   cp db.properties out/
   ```

4. **Run:**
   ```bash
   java -cp out:mysql-connector-java.jar Main
   ```

## Key Design Patterns

- **DAO Pattern**: Each entity has a corresponding DAO for database operations
- **Singleton Pattern**: `DBConnection` manages a single connection configuration
- **MVC Separation**: Models (entities), Views (GUI), Controllers (DAO logic)

## User Roles

| Role | Authentication | Capabilities |
|------|---------------|--------------|
| Faculty | ID validation via `FacultyDAO.getFacultyById()` | CRUD abstracts, search students by keyword |
| Student | ID validation via `StudentDAO.isValidStudentId()` | Search faculty by keyword, view all faculty |
| Public | None required | Read-only search across faculty and students |

## Extending the Application

To add new functionality:

1. **New entity**: Create model class in `src/model/`
2. **Database operations**: Create DAO class in `src/dao/`
3. **UI interface**: Create panel class in `src/gui/`
4. **Wire it up**: Update `LoginFrame` or appropriate panel to integrate

## UML Documentation

See `uml_faculty_research_project.png` for complete class diagram (PlantUML format).