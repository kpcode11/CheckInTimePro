# StudentManager Pro — From-Scratch Build Guide (Supabase/PostgreSQL Edition)

A ground-up, phase-wise plan to build a portfolio-quality JavaFX desktop
application for students to track subjects, tasks, and attendance — using a
hosted Supabase/PostgreSQL database instead of a locally-installed MySQL
server. This means anyone who wants to run your finished app doesn't need to
install and configure a database themselves — a real plus for a portfolio
piece.

---

## 0. Tech Stack (current, as of 2026)

| Layer              | Choice                                          | Notes |
|---------------------|--------------------------------------------------|-------|
| Language            | Java 25 (LTS)                                    | Current LTS release; use records, sealed classes, pattern matching |
| UI toolkit          | JavaFX 26                                        | Latest JavaFX release line |
| Build tool          | Maven 3.9+ (with wrapper)                        | `./mvnw` — no local install required |
| Theming             | AtlantaFX                                        | Flat modern themes (Primer, Nord, Dracula, Cupertino), instant dark/light toggle |
| Icons               | Ikonli (FontAwesome6 + Material2 packs)          | Scalable vector icons, no PNG assets |
| Extra UI controls   | ControlsFX                                       | Toast notifications, range sliders, breadcrumbs, calendar popups |
| Charts              | Built-in `javafx.scene.chart` (+ Canvas for the heatmap) | Pie/Bar/Line charts out of the box |
| **Database**        | **PostgreSQL, hosted on Supabase**               | Free tier, no local server to install/maintain, reachable from anywhere |
| JDBC driver         | `org.postgresql:postgresql`                      | Standard PostgreSQL JDBC driver |
| Connection pooling  | HikariCP                                         | Fast, production-grade pooling |
| Password security   | jBCrypt                                          | Salted password hashing (independent of Supabase Auth — you're rolling your own) |
| PDF export          | Apache PDFBox                                    | Generate term reports |
| Config management   | `dotenv-java`                                    | Loads `.env` cleanly instead of manual `System.getenv()` parsing |
| Testing             | JUnit 5 + Mockito + TestFX                       | Unit tests for logic, UI tests for controllers |
| Packaging           | `jpackage` (bundled with JDK)                    | Native `.exe` / `.dmg` / `.deb` |
| Version control/CI  | Git + GitHub Actions                             | Automated build/test on every push |

> **Note on Supabase Auth:** Supabase offers a built-in authentication
> service. This guide keeps your own `AuthService` + jBCrypt approach (matches
> the original project and is a better learning exercise), just pointing at
> Supabase purely as a **hosted Postgres database**. If you'd rather learn
> Supabase's Auth API instead of rolling your own, that's a valid alternative
> path — ask if you want that version of the guide instead.

---

## 1. Full Feature Specification

Unchanged from the original plan — the database swap doesn't affect what the
app does, only how it stores data.

### 1.1 Authentication
- Sign up with username, password, and password confirmation
- Live password strength meter (weak/medium/strong) while typing
- Passwords stored as salted BCrypt hashes — never plaintext
- Login with error feedback (wrong password vs. unknown user, shown distinctly)
- "Remember me" checkbox that persists a session token locally
- Logout clears the session and returns to the login screen

### 1.2 App Shell
- Persistent left sidebar (icons + labels): Dashboard, Subjects, Tasks, Attendance, Reports, Settings, Logout
- Active nav item highlighted
- Light/dark theme toggle, persisted between launches
- Smooth fade/slide transition when switching views
- Responsive layout at different window sizes

### 1.3 Dashboard
- Greeting header with student's name and current date
- Summary cards: overall average %, tasks due today, current attendance %
- Pie chart: marks distribution across subjects
- Bar chart: percentage per subject vs. target percentage
- "Today's tasks" widget with quick complete-checkbox
- "Upcoming" widget listing the next 3 reminders

### 1.4 Subjects Management
- Add/edit/delete a subject (name, marks obtained, total marks, target minimum percentage)
- Table view with computed percentage column and a `ProgressBar` per row
- Color-coded row status: green (meets target), amber (near target), red (below target)
- Sort and search/filter

### 1.5 Task Manager
- Kanban board: To Do / In Progress / Done
- Create a task with name, category, date, time, priority, reminder offset
- Drag-and-drop cards between columns updates status
- Priority shown as a colored chip
- Background scheduler fires toast notifications at reminder time
- Filter by category/priority

### 1.6 Attendance Tracker
- Calendar-style heatmap per month: green = present, red = absent, gray = no record
- Click a day to mark present/absent, including backfilling
- Circular progress indicator per subject
- Warning banner if attendance drops below a configurable threshold

### 1.7 Reports
- One-click PDF export: marks table, attendance summary, generation date
- Save-file dialog for destination

### 1.8 Settings
- Change password (requires current password)
- Edit target percentage per subject
- Edit attendance warning threshold
- Toggle notifications and theme

---

## 2. Phase-by-Phase Build Plan

### Phase 0 — Environment Setup
1. Install JDK 25 and confirm with `java -version`
2. **Create a free Supabase account at supabase.com and a new project.** No local database install needed. Supabase provisions a Postgres database for you immediately.
3. From your Supabase project, go to **Project Settings → Database** and copy the connection string (host, port, database name, user, password)
4. (Optional but handy) Install a Postgres GUI client like **DBeaver** or **TablePlus** for browsing tables — or just use Supabase's built-in **SQL Editor** in the browser, which needs no install at all
5. Install an IDE with JavaFX support (IntelliJ IDEA Community) and Scene Builder
6. Create a new empty Git repo, add a `.gitignore` for Java/Maven/`.env`
7. **Milestone:** Supabase project created, connection details in hand, JDK confirmed.

### Phase 1 — Project Scaffolding
1. Generate a Maven project with `groupId=com.example`, `artifactId=studentmanagerpro`
2. Add JavaFX + AtlantaFX + Ikonli + ControlsFX + **`org.postgresql:postgresql`** + HikariCP dependencies to `pom.xml`
3. Set up `module-info.java` with the required JavaFX/AtlantaFX/ControlsFX/Ikonli modules
4. Create the package skeleton: `model/`, `dao/`, `service/`, `controller/`, `util/`
5. Write a minimal `MainApp.java` that opens a blank window styled with an AtlantaFX theme
6. **Milestone:** `./mvnw javafx:run` opens a themed blank window.

### Phase 2 — Database Layer
1. Paste the schema SQL (section 3 below) into the **Supabase SQL Editor** and run it — this creates all your tables directly inside your existing Supabase-provisioned database (no `CREATE DATABASE` step needed, unlike MySQL)
2. Add `.env.sample` documenting `DB_URL`, `DB_USER`, `DB_PASS` in **Postgres JDBC format** (see section 4)
3. Build `DatabaseConnection.java` using HikariCP with `driverClassName=org.postgresql.Driver`, reading config via `dotenv-java`
4. **Important:** Supabase requires SSL — make sure your JDBC URL includes `?sslmode=require`
5. Write model classes as Java **records**: `User`, `Subject`, `Task`, `AttendanceRecord`
6. Write DAO classes (`UserDao`, `SubjectDao`, `TaskDao`, `AttendanceDao`) using standard JDBC/PreparedStatement calls — these look almost identical to MySQL versions, just against a Postgres connection
7. Write DAO unit tests against your Supabase database (or a separate free Supabase project used purely for testing)
8. **Milestone:** a small `main()` test script can insert/fetch a row through each DAO, visible live in the Supabase Table Editor in your browser.

### Phase 3 — Authentication Feature
1. Build `PasswordHasher` (wraps jBCrypt)
2. Build `AuthService` (signup validation, login check, "remember me" token)
3. Design `Login.fxml` / `SignUp.fxml` as centered cards with shadow, rounded corners, inline validation
4. Wire `LoginController` / `SignUpController` to `AuthService`
5. Build `SessionManager` (singleton holding current logged-in user)
6. Add the password strength meter
7. **Milestone:** sign up a new user, log out, log back in — check the `users` table in Supabase's Table Editor to see the hashed row appear.

### Phase 4 — App Shell & Navigation
1. Build `MainShell.fxml`: sidebar + content `StackPane`
2. Build a `Router`/`ViewManager` util that swaps center content with a fade transition
3. Wire sidebar buttons to Dashboard/Subjects/Tasks/Attendance/Reports/Settings
4. Add the light/dark theme toggle
5. **Milestone:** navigation works with smooth transitions and theme toggle works app-wide.

### Phase 5 — Dashboard
1. Build `GradeService` and reuse `TaskDao`/`AttendanceDao` for widgets
2. Build summary cards bound to service data
3. Add `PieChart` and `BarChart`
4. Add "today's tasks" and "upcoming" widgets
5. **Milestone:** Dashboard reflects real data pulled live from Supabase.

### Phase 6 — Subjects Module
1. Build `SubjectsController` with a `TableView<Subject>` bound to `SubjectDao`
2. Add computed percentage column + `ProgressBar` cell factory
3. Add color-coded row styling
4. Build add/edit dialogs
5. Add search/filter
6. **Milestone:** full CRUD, reflected live in Dashboard charts.

### Phase 7 — Task Manager
1. Build the Kanban layout (three `VBox` columns in a scrollable `HBox`)
2. Implement drag-and-drop, updating `status` via `TaskDao`
3. Build the add-task dialog
4. Build `ReminderService` (`ScheduledExecutorService` + ControlsFX toasts)
5. Add category/priority filters
6. **Milestone:** reminders fire correctly, drag-and-drop persists to Supabase.

### Phase 8 — Attendance Tracker
1. Build `AttendanceService`
2. Build the calendar heatmap `GridPane`
3. Add circular progress indicators
4. Add the low-attendance warning banner
5. **Milestone:** marking days updates the heatmap and percentage immediately.

### Phase 9 — Reports
1. Build `ReportService` using PDFBox
2. Wire "Export Report" to a `FileChooser` save dialog
3. **Milestone:** export produces a correctly formatted PDF.

### Phase 10 — Settings
1. Build the Settings screen (change password, target percentages, threshold, toggles)
2. **Milestone:** settings persist to Supabase and take effect immediately.

### Phase 11 — Testing
1. Unit tests: `AuthService`, `GradeService`, `AttendanceService` (Mockito for DAO mocks)
2. DAO integration tests against a dedicated test Supabase project (keep test data separate from your dev project)
3. TestFX UI tests: login flow, add-a-subject flow, add-a-task flow
4. **Milestone:** `mvn test` passes a meaningful suite.

### Phase 12 — Polish
1. Empty states for every list/table
2. Friendly error banners for connection failures (e.g., no internet reaching Supabase), with a retry button
3. Consistent spacing/typography via shared `style.css`
4. App icon, window title, taskbar branding
5. **Milestone:** no screen ever shows a blank/broken state.

### Phase 13 — Packaging & CI
1. Run `jpackage` for a native installer
2. Add a GitHub Actions workflow: build + test on every push (store Supabase test credentials as GitHub Actions secrets, never commit them)
3. Write the final `README.md`: features, screenshots/GIFs, setup instructions (including "create your own free Supabase project and paste your connection string" since reviewers won't have yours), architecture diagram
4. **Milestone:** a stranger can clone the repo, create their own free Supabase project, follow the README, and run the app.

---

## 3. Database Schema (PostgreSQL / Supabase syntax)

Run this directly in the **Supabase SQL Editor** — no `CREATE DATABASE` needed,
Supabase already gives you one.

```sql
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE subjects (
  id SERIAL PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  subject_name VARCHAR(100) NOT NULL,
  marks_obtained INT NOT NULL,
  marks_total INT NOT NULL,
  target_percentage INT DEFAULT 75
);

CREATE TABLE tasks (
  id SERIAL PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  task_name VARCHAR(255) NOT NULL,
  category VARCHAR(50),
  task_date DATE,
  task_time VARCHAR(10),
  priority VARCHAR(20),
  reminder_minutes_before INT,
  status VARCHAR(20) DEFAULT 'TODO'
);

CREATE TABLE attendance_records (
  id SERIAL PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  subject_name VARCHAR(100),
  attended BOOLEAN,
  record_date DATE,
  UNIQUE (username, subject_name, record_date)
);

CREATE TABLE user_settings (
  username VARCHAR(100) PRIMARY KEY,
  theme VARCHAR(20) DEFAULT 'LIGHT',
  attendance_threshold INT DEFAULT 75,
  notifications_enabled BOOLEAN DEFAULT TRUE
);
```

**Differences from MySQL you should know:**
- `SERIAL` replaces `AUTO_INCREMENT`
- No `CREATE DATABASE` step — Supabase already provisioned one (usually named `postgres`)
- Table names in Postgres are case-sensitive if quoted; keep everything lowercase (as above) to avoid surprises

---

## 4. Connection Configuration

`.env.sample`:

```text
DB_URL=jdbc:postgresql://YOUR-PROJECT-REF.supabase.co:5432/postgres?sslmode=require
DB_USER=postgres
DB_PASS=your-supabase-db-password
```

Find these exact values under **Supabase → Project Settings → Database →
Connection string → JDBC**. Supabase will show you a ready-made JDBC string —
just copy it into `DB_URL` and drop the password into `DB_PASS`.

`pom.xml` driver dependency (replaces `mysql-connector-j`):

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.4</version>
</dependency>
```

HikariCP setup snippet:

```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl(dotenv.get("DB_URL"));
config.setUsername(dotenv.get("DB_USER"));
config.setPassword(dotenv.get("DB_PASS"));
config.setDriverClassName("org.postgresql.Driver");
HikariDataSource dataSource = new HikariDataSource(config);
```

---

## 5. Folder Structure

```
StudentManagerPro/
├─ mvnw*
├─ pom.xml
├─ .env.sample
├─ .github/workflows/build.yml
├─ src/
│  ├─ main/
│  │  ├─ java/com/example/studentmanager/
│  │  │  ├─ MainApp.java
│  │  │  ├─ model/          (User, Subject, Task, AttendanceRecord — records)
│  │  │  ├─ dao/            (UserDao, SubjectDao, TaskDao, AttendanceDao)
│  │  │  ├─ service/        (AuthService, GradeService, ReminderService, AttendanceService, ReportService)
│  │  │  ├─ controller/     (one per screen)
│  │  │  └─ util/           (DatabaseConnection, SessionManager, Router, PasswordHasher)
│  │  ├─ resources/com/example/studentmanager/
│  │  │  ├─ fxml/
│  │  │  ├─ css/style.css
│  │  │  └─ images/
│  │  └─ module-info.java
│  └─ test/java/com/example/studentmanager/
│     ├─ dao/
│     ├─ service/
│     └─ ui/
```

---

## 6. Commit Milestones (suggested)

1. `chore: project scaffolding + JavaFX + AtlantaFX boots`
2. `feat: Supabase/Postgres database layer with HikariCP + DAOs`
3. `feat: secure signup/login with bcrypt`
4. `feat: app shell with sidebar nav + theme toggle`
5. `feat: dashboard with live charts`
6. `feat: subjects CRUD with progress indicators`
7. `feat: kanban task manager + reminder notifications`
8. `feat: attendance heatmap + threshold warnings`
9. `feat: PDF report export`
10. `feat: settings screen`
11. `test: unit + DAO + TestFX coverage`
12. `chore: jpackage installers + CI + final README`

---

## 7. Reference Links

- JavaFX: https://openjfx.io/
- AtlantaFX: https://github.com/mkpaz/atlantafx
- Ikonli: https://kordamp.org/ikonli/
- ControlsFX: https://github.com/controlsfx/controlsfx
- Supabase docs: https://supabase.com/docs
- Supabase connection strings: https://supabase.com/docs/guides/database/connecting-to-postgres
- PostgreSQL JDBC driver: https://jdbc.postgresql.org/
- HikariCP: https://github.com/brettwooldridge/HikariCP
- jBCrypt: https://github.com/jeremyh/jBCrypt
- Apache PDFBox: https://pdfbox.apache.org/
- dotenv-java: https://github.com/cdimascio/dotenv-java
- TestFX: https://github.com/TestFX/TestFX
- jpackage: https://docs.oracle.com/en/java/javase/25/jpackage/
