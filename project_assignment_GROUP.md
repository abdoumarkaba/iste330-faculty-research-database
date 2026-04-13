# School of Information — ISTE 330
## Faculty Research Database
**iSchool | Spring 2026**

---

Department faculty regularly engage in research and publish their results. They are often looking to work with other faculty and students, but it is difficult to keep track of what each person is working on and interests to find collaborators.

Also, students are looking for faculties with similar interests to guide their capstone projects and thesis.

A database application might help address this problem. If we were to store abstracts of each professor's interests, along with student interests, both students and fellow faculty could easily search the database to see if there are opportunities for collaboration. There could also be a portal for local business, media outlets, and other members of the public community to use the database to find speakers for current topics. In essence, we are talking about a digital library on a small scale.

---

## Project

Your job is to develop a **backend AND frontend (GUI)** that will provide functions and reports that would be meaningful for a user who built the system. Each group will design and implement an appropriate database to support their backend code. The database must be **MySQL**.

**Requirements (minimum)** — One/Two database will be shared by all user types:

- **"Faculty"** users will have the ability to insert, update, and delete their entries **and Search in student entries.**
- **"Student"** and **"Public"** users will have the ability to search and view all entries.
- **The Java Program should take ALL the inputs and display outputs using GUI OR CML.**

---

## Deliverables

For each due date only make **ONE SUBMISSION** to the Assignment/dropbox on the day the task was due.

**Failure to meet a due date will result in a penalty of 25% per day late** for a given task. Also, you will NOT be able to re-do an assignment/task once it is turned in for grading. The penalty of 25% off per day counts every day, including Friday, Saturday, Sunday and holidays. 4 days beyond the assigned due date the project is not permitted to be turned in for a grade.

---

### Deliverable 1
**Due Date: April 2, 2026 @ 11:59pm**

**Content:**
- Charts — **MUST USE Lucid Chart**
  - See samples provided in class
  - Gantt chart is also required. You must use the Excel Spreadsheet provided.
- Lots of documentation will be turned in for this due date, including a very detailed Gantt Chart, new problem description, and some charts.

**Specific requirements:**
- Rewrite the problem description using your own words to include all the verbal requirements provided in class
- Create an excellent Gantt Chart
- First draft of a UML chart created in Lucid Chart or Workbench *(Workbench charts will need some touch-up work done in Photoshop)*

---

### Deliverable 2
**Due Date: April 16, 2026 @ 11:59pm**

**Content:**
- Data layer code (**JAVA**)
- Include **directions.txt** — include what Java program contains the main method
- Zip your `.sql`, `.txt`, and all `.java` files
- Comment with your lastname and date
- **(Make sure your name is in comments, at the top, of all assignments!!!!!!!!!!)**

**INPUT FROM FACULTY:**
- Include an abstract file from a faculty's book, or an abstract from a faculty's speaking engagement.
- Allow BOTH the above input data types from the faculty.

**INPUT FROM STUDENT:**
- Include a list of key topics the student wishes to research.
- Each topic from a student is an item of **1 to 3 words**.
- You **MUST** provide an **intersection** of the faculty's abstracts to a student's research interest.
- Then provide to the student the Faculty's name, Building Number and Office number and email address so the student can go make an appointment with the Faculty member who shares their interest.

**Submission Instructions:**

The back-end of your project must be **fully implemented, functional, and tested**.

- Your application should run without errors.
- Core features must work as intended.
- Testing should demonstrate that the back-end logic and database interactions are correct. **All Java code and required project components must be submitted by the due date.**
- This includes back-end code and any supporting files required to run the project.

**Late submissions will not be accepted.**
- No extensions will be granted for this deliverable.

---

### Deliverable 3 — Third and Final Due Date
**Due Date: April 25, 2026 @ 11:59pm (Saturday)**

**Late submissions will not be accepted.**

**Content:**
- Must have **GUI Interface**, or a **VERY GOOD** command line interface.
- Front-end test code
- Menu — Student / Faculty paper — outside interest IN iSchool

**INPUT FROM OUTSIDE THE COLLEGE — MUST INCLUDE A KEY WORD:**
- You must produce an intersection from users outside of the COLLEGE with students currently attending RIT.
- Your presentation layer for the 3rd due date of this project must have a **GUI front end**.

---

#### Purpose of This Assignment

This final submission is designed to simulate **real tasks you will be expected to complete in your first full-time job** as a software or IT professional. By completing these requirements, you are practicing how professionals:

- Deliver finished, working software
- Write clear technical documentation
- Communicate expectations to people who were not part of the development team
- Contribute to group projects with defined requirements and deadlines

---

#### Final Project Requirements

**1. Fully Completed and Functional Project**

- Your final submission must represent a **complete and polished project**.
- The project must **100% meet all requirements** discussed:
  - In class since **March 25, 2026** (including the lecture synopsis)
  - In all written instructions provided by your professor
- Assume this is a **professional deliverable**, not a draft.
- In the workplace, "almost finished" or "mostly working" is not acceptable. The expectation is a complete, tested solution.

**2. Detailed Instructions for Running Your Program**

Your final version **must include extremely clear and detailed instructions** explaining:

- How to run your Java program from start to finish
- What the user should expect at **every menu**
- What input the user should enter for **each prompt**
- What the program does in response to each choice or input

These instructions should be so clear that:
- **Someone who did not work on your project** can successfully run it
- No assumptions are made about prior knowledge

This mirrors real-world expectations, where developers must document their work so others can use, test, or maintain it.

**3. Updated Problem Description (Written by You)**

You must submit an **updated problem description**, written clearly and professionally.

Your problem description must:
- Clearly explain the **goal of the project**
- Describe what problem the system is solving
- Be written so clearly that **any RIT freshman could understand it**
- Stand alone without requiring verbal explanation

This part of the assignment directly tests your **technical writing skills**. In the workplace, you will often be asked to write a problem statement or project description for:
- New systems
- Future development teams
- Colleagues you have never worked with before

This assignment prepares you for that responsibility.

**Professional Expectations**

Treat this submission as if it were being delivered to:
- A manager
- A client
- A new development team you did not choose

Clarity, completeness, and accuracy matter. Meeting deadlines is part of the job.

---

## Grading

A non-normalized database or incomplete functional support will result in at best a **"C-"**.

**Some important goals (Grading Points) of this project include:**

This project is all about creating professional connections between students and faculty and the outside public. This project is also designed to facilitate more open and informed communication between students, faculty, and local organizations.

---

### Requirement 1: Student Search for Faculty Sponsors

Your system **must allow students to search for faculty members** who may be willing to sponsor their capstone or thesis project. This requirement must be implemented as follows:

**Search Capabilities**

Students must be able to search faculty data using both:
- **a) Faculty abstracts** (if available)
- **b) Faculty interests**, represented by **exactly three (3) keywords**

Not all faculty members are required to have abstracts. Your system must still work correctly when an abstract is not present.

**Functional Behavior**

- Faculty abstracts and interest keywords must be:
  - Stored in the database
  - Searchable through the **front-end interface** you create
- After all database tables are populated:
  - Students must be able to use the front-end to locate faculty members
  - Searches should identify faculty whose abstracts and/or interest keywords align with the student's capstone or thesis topic
  - When a match is found, the system should return relevant faculty contact information so students can evaluate potential sponsors

---

### Requirement 2: Faculty Search for Students by Interest

Your system **must allow faculty members to search for students based on shared interests**. To meet this requirement, your project must implement the following:

- Each student must have **exactly three (3) keywords** that represent their academic or research interests.
- These keywords must be:
  - Stored in a **database table**
  - Associated with the correct student record
- Faculty members must be able to:
  - Enter their own interest keywords
  - Search the **student interests table** in the database
- When a faculty member performs a search:
  - The system must **compare faculty interest keywords with student keywords and/or compare the faculty's abstracts stored in the database to the student's keywords of interest.**
  - If a match is found, the system must return the matching student's **contact information** (for example: name and email)
- The goal of this feature is to help faculty identify students who may be a good fit for:
  - Research projects
  - Ongoing academic work
  - Future collaborations

---

### Requirement 3: Outside Business / Public Search

Outside businesses, like the local Henrietta Library, might want to search for students or faculty. The local library might want to offer a workshop on a given topic to the public. Thus, make sure outside businesses, including the library, can search to find a student and/or faculty that has an interest on a given subject.

Your front end should be able to:
- Input and store **1 keyword** related to their interest
- Take that keyword and run the requested search to find students and/or faculty with that similar interest to do a presentation on a given subject
