# Code Quality Guidelines — Faculty Research Database (ISTE 330)
> Instructions for LLM-assisted development on this Java + MySQL project.

---

## Database (MySQL)

**DO:**
- Normalize all tables to at least **3NF** — a non-normalized schema results in a maximum grade of C-
- Use **foreign keys** with explicit `ON DELETE` / `ON UPDATE` rules
- Name tables in singular form and columns in `snake_case` (e.g., `faculty_id`, `student_email`)
- Use `VARCHAR` with appropriate max lengths; use `TEXT` only for abstracts and long-form content
- Store student keywords and faculty keywords in **separate junction/keyword tables** — never as comma-separated strings in a single column
- Write a single `.sql` file that includes `DROP TABLE IF EXISTS`, `CREATE TABLE`, and sample `INSERT` statements in dependency order
- Use `PreparedStatement` for every query — no exceptions

**DO NOT:**
- Store multiple keywords in one column (e.g., `keyword1, keyword2, keyword3` as a single field)
- Use `SELECT *` in production queries — always name columns explicitly
- Hardcode database credentials in source files — use a `db.properties` config file
- Use `Statement` (non-prepared) for any query that accepts user input
- Allow `NULL` in columns that must always have a value — use `NOT NULL` constraints

---

## Java Architecture

**DO:**
- Follow a strict **3-layer architecture**:
  1. `DataLayer` — all JDBC logic, SQL queries, connection management
  2. `BusinessLayer` (or service classes) — validation, matching logic, keyword intersection
  3. `PresentationLayer` — GUI or CLI only; no SQL here, ever
- Put the `main` method in a clearly named class (e.g., `App.java` or `Main.java`) and document this in `directions.txt`
- Use **one class per responsibility** — Faculty, Student, Outside User should each have their own model class
- Close all JDBC resources (`Connection`, `PreparedStatement`, `ResultSet`) in `finally` blocks or use try-with-resources
- Throw and catch meaningful, typed exceptions — do not swallow exceptions silently with empty `catch` blocks

**DO NOT:**
- Put SQL strings inside GUI or presentation classes
- Mix user input handling with database logic in the same method
- Use `System.exit()` inside data or business layer classes
- Ignore checked exceptions or print only `e.printStackTrace()` with no recovery logic
- Create a `God class` that handles all three user types in one file

---

## The Keyword Intersection Logic

**DO:**
- Implement the faculty-student keyword match as a **SQL JOIN or IN clause** — not a loop in Java
- Support matching on both **abstract text** (via `LIKE` or `FULLTEXT`) and **keyword table** entries
- Return the faculty's `name`, `building_number`, `office_number`, and `email` on a student match
- Return the student's `name` and `email` on a faculty match
- Handle the case where a faculty member has **no abstract** gracefully — the system must not crash or return an error

**DO NOT:**
- Pull all rows into Java and filter in-memory when a WHERE clause will do
- Assume every faculty record has an abstract — design queries to work without one
- Return raw `ResultSet` objects outside the `DataLayer` — map them to model objects first

---

## GUI / Presentation Layer

**DO:**
- Present a clear **role-selection screen** at launch: Faculty / Student / Outside User
- Label every input field clearly; include placeholder text or tooltips
- Disable or hide operations the current role is not permitted to perform (e.g., hide insert/update/delete for Student and Public users)
- Display search results in a `JTable` or equivalent structured component — not a raw text area
- Show a confirmation dialog before any `DELETE` or `UPDATE` operation
- Handle empty search results with a user-friendly message (e.g., "No matching faculty found.")

**DO NOT:**
- Allow blank required fields to be submitted without validation
- Display raw SQL error messages to the user — catch exceptions and show plain-language feedback
- Build the entire UI in a single method or class
- Hardcode window dimensions in ways that make the UI unusable at different screen sizes

---

## Input Validation

**DO:**
- Validate that student keywords are **1 to 3 words** each before insert
- Validate that faculty have exactly **3 interest keywords** stored
- Trim whitespace from all user input before processing
- Validate that email fields contain an `@` symbol and a domain before storing
- Reject empty strings for required fields at the presentation layer — not the data layer

**DO NOT:**
- Trust user input at the data layer — always sanitize before it reaches a query
- Allow duplicate keyword entries for the same student or faculty record
- Accept keywords longer than a reasonable max (e.g., 50 characters)

---

## File & Submission Standards

**DO:**
- Include your **last name and date** in a comment block at the top of every `.java` file
- Include a `directions.txt` that states:
  - Which class contains `main`
  - Step-by-step instructions to compile and run
  - Expected input at every menu prompt
  - What output the user should see
- Zip exactly: all `.java` files, the `.sql` file, and `directions.txt`
- Comment non-obvious logic with a single clear line — explain *why*, not *what*

**DO NOT:**
- Submit `.class` files, IDE project folders, or OS metadata files (e.g., `.DS_Store`)
- Leave commented-out dead code in the final submission
- Use ambiguous variable names like `x`, `temp`, `data`, or `str` in non-trivial contexts
- Omit the name comment block — it is an explicit grading requirement

---

## Security & Connection Handling

**DO:**
- Load DB credentials from a `db.properties` file read at runtime
- Use a single shared `DataLayer` instance or connection pool — do not open a new connection per query
- Close connections explicitly; do not rely on garbage collection

**DO NOT:**
- Commit `db.properties` with real credentials to any shared repository
- Leave connections open indefinitely — always close in a `finally` block or try-with-resources
- Use deprecated JDBC drivers or MySQL Connector versions below 8.x

---

## General LLM Behavior for This Project

**When generating code, always:**
- Respect the 3-layer boundary — ask which layer a piece of logic belongs to before writing it
- Generate `PreparedStatement` queries by default — never raw `Statement` with concatenation
- Map `ResultSet` rows to typed model objects (`Faculty`, `Student`, `OutsideUser`) before returning
- Generate the `directions.txt` stub alongside any class that contains `main`
- Add the lastname/date comment block to every new `.java` file generated

**When generating code, never:**
- Put a SQL string inside a GUI class
- Return a `ResultSet` from a `DataLayer` method
- Generate `catch (Exception e) {}` with no body
- Assume an abstract is always present — always write null-safe queries
- Generate a schema with keywords stored as a delimited string in a single column
