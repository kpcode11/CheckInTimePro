# StudentManager Pro — Implementation Plan

This is a ground-up build of a JavaFX desktop application for students, using Supabase/PostgreSQL for the backend.

## User Review Required

> [!IMPORTANT]
> Please review the phases below. I will proceed with Phase 0 and Phase 1 once you approve.
>
> Note that you will need to create a Supabase project and provide the database connection string in a `.env` file as we progress.

## Open Questions

- Do you already have the Supabase database connection details, or will you set that up during Phase 0?
- Is there any specific AtlantaFX theme (Primer, Nord, Dracula, Cupertino) you prefer for the initial default?

## Proposed Changes / Phases

We will follow an iterative MVP approach, building the foundation first.

### Phase 0: Environment Setup
- Validate JDK 25 and Maven.
- Set up Supabase project and retrieve connection strings.

### Phase 1: Project Scaffolding
#### [NEW] `pom.xml`
- Setup Maven project `com.example:studentmanagerpro`.
- Add dependencies: JavaFX, AtlantaFX, Ikonli, ControlsFX, PostgreSQL JDBC, HikariCP, dotenv-java, jBCrypt, PDFBox.
#### [NEW] `src/main/java/module-info.java`
- Export and require necessary modules.
#### [NEW] `src/main/java/com/example/studentmanager/MainApp.java`
- Minimal JavaFX entry point with an AtlantaFX theme.

### Phase 2: Database Layer
- Execute provided SQL schema in Supabase.
- Setup `.env` for credentials.
#### [NEW] `src/main/java/com/example/studentmanager/util/DatabaseConnection.java`
- HikariCP connection pooling setup.
#### [NEW] `src/main/java/com/example/studentmanager/model/*`
- Create records: `User`, `Subject`, `Task`, `AttendanceRecord`.
#### [NEW] `src/main/java/com/example/studentmanager/dao/*`
- Create DAOs for entities using standard JDBC.

### Phase 3: Authentication Feature
#### [NEW] `src/main/java/com/example/studentmanager/util/PasswordHasher.java`
- jBCrypt wrapper.
#### [NEW] `src/main/java/com/example/studentmanager/service/AuthService.java`
- Login and signup logic.
#### [NEW] UI Files
- `Login.fxml`, `SignUp.fxml`, and respective controllers.

### Future Phases (Iterative Execution)
- **Phase 4:** App Shell & Navigation
- **Phase 5:** Dashboard (Charts & Widgets)
- **Phase 6:** Subjects Module
- **Phase 7:** Task Manager (Kanban)
- **Phase 8:** Attendance Tracker (Heatmap)
- **Phase 9:** Reports (PDF Export)
- **Phase 10:** Settings
- **Phases 11-13:** Testing, Polish, and Packaging.

## Verification Plan

### Automated Tests
- Unit tests for services and DAOs using JUnit 5 and Mockito.
- UI tests via TestFX.

### Manual Verification
- Manually run `./mvnw javafx:run` at each milestone to verify the UI.
- Verify database entries in Supabase dashboard.

---
## Progress Log

### [Completed] Phase 0 & Phase 1 (Project Scaffolding)
- Attempted to validate Java and Maven but found they are not installed (or not in PATH) on this machine.
- Manually generated `pom.xml` with all necessary dependencies and plugins configured for Java 25.
- Created `module-info.java` mapping modules appropriately.
- Scaffolded `com.example.studentmanager.MainApp` and configured the **Dracula** theme from AtlantaFX.
- Written the provided Supabase connection string to the `.env` file.

### [Completed] Phase 2 (Database Layer)
- Configured HikariCP connection pooling via `DatabaseConnection.java` utilizing `dotenv-java`.
- Designed application state structures using modern Java Records: `User`, `Subject`, `Task`, `AttendanceRecord`, `UserSettings`.
- Created Data Access Objects (`UserDao`, `SubjectDao`, `TaskDao`, `AttendanceDao`, `UserSettingsDao`) wrapping native JDBC logic.

### [Completed] Phase 3 (Authentication Feature)
- Engineered `PasswordHasher.java` utilizing the `jBCrypt` library for secure salted password hashing.
- Developed `AuthService.java` to handle business logic for secure user login and registration.
- Structured `SessionManager.java` as a singleton to manage the global user session state.
- Designed `Login.fxml` and `SignUp.fxml` integrating AtlantaFX themes to build centered, responsive login cards with dynamic input validation and a real-time password strength meter.
- Implemented `LoginController.java` and `SignUpController.java` to bind FXML views seamlessly to the backend logic.

### [Completed] Phase 4 (App Shell & Navigation)
- Created `Router.java` to handle seamless, dynamic transitions between views using JavaFX `FadeTransition`.
- Designed the primary application layout in `MainShell.fxml` using a responsive sidebar (`VBox`) and an expanding content area (`StackPane`).
- Integrated smooth navigation hooks for Dashboard, Subjects, Tasks, Attendance, Reports, and Settings.
- Implemented a persistent Light/Dark mode toggle interacting directly with AtlantaFX's `Dracula` and `NordLight` themes.
- Updated the main entry flow: the application launches into the Login view and securely transitions to the Main Shell upon successful authentication.

### [Completed] Phase 5 (Dashboard)
- Implemented `GradeService.java` to aggregate user data, calculate overall averages, sum tasks due today, and compute attendance percentages based on data sourced from `SubjectDao`, `TaskDao`, and `AttendanceDao`.
- Designed a sophisticated `Dashboard.fxml` layout, comprising dynamic summary cards for high-level metrics.
- Integrated standard JavaFX charts (`PieChart` for Marks Distribution and `BarChart` for Subject Performance) bound to the `GradeService`.
- Added embedded `ListView` widgets to surface "Today's Tasks" and the next 3 upcoming tasks dynamically.
- Developed `DashboardController.java` to tie the UI to backend services upon initialization for the logged-in user.

### [Completed] Phase 6 (Subjects Module)
- Updated `SubjectDao.java` to include full CRUD functionality by adding `updateSubject` and `deleteSubject` operations.
- Built `Subjects.fxml` encompassing a `TableView` with custom columns, a search bar, and CRUD action buttons.
- Developed `SubjectsController.java` integrating observable lists and filtered lists for real-time search functionality.
- Implemented a custom `ProgressBarTableCell` factory that dynamically colors progress bars (green, yellow, red) based on whether the student's marks meet their defined target percentage.
- Engineered a modal `Dialog` interface for seamlessly adding and editing Subject records within the module.

### [Completed] Phase 7 (Task Manager)
- Updated `TaskDao.java` to support full CRUD, including a dedicated `updateTaskStatus` method for Kanban drag-and-drop.
- Built a Kanban-style layout in `Tasks.fxml` using three responsive `VBox` columns (TODO, IN PROGRESS, DONE) wrapped inside a `ScrollPane`.
- Developed `TasksController.java` to render tasks as styled cards and implemented full Drag-and-Drop capability between columns, saving status changes instantly to Supabase.
- Configured a new modal `Dialog` in `TasksController.java` allowing users to quickly create and assign tasks.
- Created `ReminderService.java` using a `ScheduledExecutorService` as the foundation for background task reminder notifications.

### [Completed] Phase 8 (Attendance Tracker)
- Modified `AttendanceDao.java` to support an intelligent UPSERT strategy (`UPDATE` fallback to `INSERT`), ensuring duplicate records for the same day/subject are handled cleanly.
- Designed `Attendance.fxml` with an intuitive input form mapping `ComboBox` and `DatePicker` controls to the logging process.
- Implemented `AttendanceController.java` to dynamically generate a 35-day Heatmap grid.
- Configured the Heatmap grid to dynamically color code blocks (Green for Present, Red for Absent) and added interactive `Tooltip` hovers to show specific dates and statuses.

### [Completed] Phase 9 (Reports)
- Implemented `PdfExportService.java` using Apache PDFBox to generate a robust, natively formatted PDF document containing user statistics, subject grades, and pending tasks.
- Engineered `Reports.fxml` with a clean, centralized layout presenting a large "Export to PDF" call-to-action button paired with an Ikonli PDF icon.
- Created `ReportsController.java` to bridge the UI to the export logic. Integrated the JavaFX `FileChooser` dialog to let the user pick their desired save path and filename.
- Added visual feedback (success/error messages directly in the UI and a native JavaFX `Alert` popup) following the export operation.

### [Completed] Phase 10 (Settings)
- Enhanced `UserSettingsDao.java` to support an UPSERT (`UPDATE` fallback to `INSERT`) operation, ensuring settings are correctly saved or updated per user.
- Constructed `Settings.fxml` featuring a clean, grid-based layout using standard JavaFX controls (`ComboBox`, `Spinner`, `CheckBox`) for user preferences (Theme, Attendance Threshold, Notifications).
- Implemented `SettingsController.java` to load saved preferences automatically on startup and handle saving modifications back to the Supabase database.
- Integrated a native JavaFX `Alert` to provide instant user feedback upon saving preferences.
