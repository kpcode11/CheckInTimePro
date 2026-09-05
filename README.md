# CheckInTime

<div align="center">

<img src="src/main/resources/com/example/studentmanager/icon.jpg" alt="CheckInTime" width="120"/>

**A portfolio-quality JavaFX desktop app to track subjects, tasks, and attendance — powered by a live Supabase (PostgreSQL) database.**

[![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)](https://jdk.java.net/25/)
[![JavaFX](https://img.shields.io/badge/JavaFX-26-blue?logo=java)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-red?logo=apachemaven)](https://maven.apache.org/)
[![Supabase](https://img.shields.io/badge/Database-Supabase-3ECF8E?logo=supabase)](https://supabase.com/)
[![Tests](https://img.shields.io/badge/Tests-10%20passing-success?logo=junit5)](https://junit.org/junit5/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

##  Features

| Module | What it does |
|--------|-------------|
|  **Auth** | Secure login & sign-up with jBCrypt-hashed passwords |
|  **Dashboard** | Live stat cards (average marks, attendance %, tasks due today), PieChart & BarChart |
|  **Subjects** | Full CRUD — add, edit, delete subjects with marks tracking and per-subject progress bars |
|  **Task Manager** | Kanban board (To Do / In Progress / Done) with drag-and-drop task cards |
|  **Attendance** | Calendar heatmap showing attended vs missed days per subject |
|  **PDF Reports** | One-click term report generation via Apache PDFBox |
|  **Settings** | Dark/light theme toggle (Dracula ↔ NordLight), user preferences saved to DB |
|  **Error Banner** | Real-time Supabase connection health check with a dismissible banner |

---

##  Screenshots

> Run the app locally to see it in action — the Dracula dark theme is set as default.

---

##  Tech Stack

| Layer | Choice |
|-------|--------|
| Language | Java 25 |
| UI | JavaFX 26 |
| Theme | AtlantaFX (Dracula) |
| Icons | Ikonli (FontAwesome 5 + Material 2) |
| Extra controls | ControlsFX |
| Database | PostgreSQL on Supabase (free tier) |
| JDBC | PostgreSQL JDBC + HikariCP |
| Password security | jBCrypt |
| PDF export | Apache PDFBox 3.x |
| Config | dotenv-java |
| Testing | JUnit 5 + Mockito + TestFX |
| Build | Maven 3.9+ |
| Packaging | jpackage (bundled in JDK) |

---

##  Getting Started

### Prerequisites

- **Java 25** — [Download](https://jdk.java.net/25/)
- **Maven 3.9+** — [Download](https://maven.apache.org/download.cgi)
- A **Supabase** project (free tier at [supabase.com](https://supabase.com))

### 1. Clone the repo

```bash
git clone https://github.com/kpcode11/CheckInTimePro.git
cd CheckInTimePro
```

### 2. Configure your database

Copy the sample env file and fill in your Supabase credentials:

```bash
cp .env.sample .env
```

Edit `.env`:

```env
DB_URL=jdbc:postgresql://db.<your-project-ref>.supabase.co:5432/postgres?sslmode=require
DB_USER=postgres
DB_PASS=your_supabase_password
```

### 3. Set up the database schema

Run the following SQL in your **Supabase SQL Editor**:

```sql
CREATE TABLE users (
    id         SERIAL PRIMARY KEY,
    username   TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE subjects (
    id             SERIAL PRIMARY KEY,
    username       TEXT NOT NULL,
    subject_name   TEXT NOT NULL,
    marks_obtained INTEGER DEFAULT 0,
    marks_total    INTEGER DEFAULT 100,
    target_percent INTEGER DEFAULT 75
);

CREATE TABLE tasks (
    id          SERIAL PRIMARY KEY,
    username    TEXT NOT NULL,
    task_name   TEXT NOT NULL,
    category    TEXT,
    task_date   DATE,
    task_time   TEXT,
    priority    TEXT DEFAULT 'MEDIUM',
    reminder_min INTEGER,
    status      TEXT DEFAULT 'TODO'
);

CREATE TABLE attendance (
    id           SERIAL PRIMARY KEY,
    username     TEXT NOT NULL,
    subject_name TEXT NOT NULL,
    attended     BOOLEAN NOT NULL,
    attend_date  DATE NOT NULL
);

CREATE TABLE settings (
    username TEXT PRIMARY KEY,
    theme    TEXT DEFAULT 'DARK',
    notifs_enabled BOOLEAN DEFAULT TRUE
);
```

### 4. Run the app

```bash
mvn clean javafx:run
```

---

##  Running Tests

```bash
mvn clean test
```

**10 tests** across 3 suites, all passing:

| Suite | Type | Tests |
|-------|------|------:|
| `AuthServiceTest` | Unit (Mockito) | 5 |
| `GradeServiceTest` | Unit (Mockito) | 4 |
| `LoginUITest` | UI (TestFX) | 1 |

---

##  Building a Portable Distribution

Creates a self-contained `CheckInTime/` folder with a bundled JRE — no Java installation needed on the target machine:

```bash
mvn -P package-win package verify -DskipTests
```

Output: `target/installer/CheckInTime/CheckInTime.exe`

> **Want a `.exe` installer?** Install [WiX Toolset](https://wixtoolset.org/) and change `app-image` → `exe` in the `package-win` profile in `pom.xml`.

---

##  Project Structure

```
src/
├── main/
│   ├── java/com/example/studentmanager/
│   │   ├── MainApp.java              # Entry point
│   │   ├── controller/               # FXML controllers
│   │   ├── dao/                      # Database access (JDBC)
│   │   ├── model/                    # Java records (User, Task, Subject…)
│   │   ├── service/                  # Business logic (Auth, Grade, PDF…)
│   │   └── util/                     # Router, SessionManager, DB pool
│   └── resources/
│       ├── fxml/                     # UI layout files
│       └── css/style.css             # Global style overrides
└── test/
    └── java/com/example/studentmanager/
        ├── service/AuthServiceTest.java
        ├── service/GradeServiceTest.java
        └── ui/LoginUITest.java
```

---

##  Security Notes

- **Never commit `.env`** — it's in `.gitignore` by default.
- Passwords are stored as **bcrypt hashes**, never plaintext.
- Supabase connection uses **SSL** (`?sslmode=require`).

---

##  License

This project is licensed under the **MIT License** — see [LICENSE](LICENSE) for details.

---

<div align="center">
Built with ☕ Java + JavaFX · Backed by 🐘 Supabase
</div>
