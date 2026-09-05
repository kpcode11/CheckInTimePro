package com.example.studentmanager.controller;

import com.example.studentmanager.dao.AttendanceDao;
import com.example.studentmanager.dao.SubjectDao;
import com.example.studentmanager.model.AttendanceRecord;
import com.example.studentmanager.model.Subject;
import com.example.studentmanager.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AttendanceController {
    @FXML private ComboBox<Subject> subjectComboBox;
    @FXML private DatePicker datePicker;
    @FXML private RadioButton presentRadio;
    @FXML private RadioButton absentRadio;
    @FXML private GridPane heatmapGrid;
    @FXML private Label statsLabel;

    private final SubjectDao subjectDao = new SubjectDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();
    
    @FXML
    public void initialize() {
        ToggleGroup group = new ToggleGroup();
        presentRadio.setToggleGroup(group);
        absentRadio.setToggleGroup(group);
        presentRadio.setSelected(true);
        
        datePicker.setValue(LocalDate.now());

        var user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            List<Subject> subjects = subjectDao.getSubjectsForUser(user.username());
            subjectComboBox.getItems().setAll(subjects);
            
            subjectComboBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Subject item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.subjectName());
                }
            });
            subjectComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Subject item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.subjectName());
                }
            });

            subjectComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                if (newV != null) {
                    loadHeatmap(newV.subjectName(), user.username());
                }
            });
            
            if (!subjects.isEmpty()) {
                subjectComboBox.getSelectionModel().selectFirst();
            }
        }
    }

    @FXML
    public void handleLogAttendance() {
        Subject subject = subjectComboBox.getSelectionModel().getSelectedItem();
        LocalDate date = datePicker.getValue();
        if (subject == null || date == null) return;
        
        var user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;
        
        boolean attended = presentRadio.isSelected();
        AttendanceRecord record = new AttendanceRecord(0, user.username(), subject.subjectName(), attended, date);
        
        if (attendanceDao.logAttendance(record)) {
            loadHeatmap(subject.subjectName(), user.username());
        }
    }

    private void loadHeatmap(String subjectName, String username) {
        heatmapGrid.getChildren().clear();
        
        List<AttendanceRecord> records = attendanceDao.getAttendanceForUser(username);
        List<AttendanceRecord> subjectRecords = records.stream()
                .filter(r -> r.subjectName().equals(subjectName))
                .toList();
                
        long presentCount = subjectRecords.stream().filter(AttendanceRecord::attended).count();
        double pct = subjectRecords.isEmpty() ? 0 : ((double) presentCount / subjectRecords.size()) * 100;
        statsLabel.setText(String.format("Classes: %d | Present: %d | Attendance: %.1f%%", 
                subjectRecords.size(), presentCount, pct));
                
        // Build simple 7x5 grid for last 35 days
        LocalDate startDate = LocalDate.now().minusDays(34);
        
        for (int i = 0; i < 35; i++) {
            LocalDate current = startDate.plusDays(i);
            
            StackPane block = new StackPane();
            block.setPrefSize(30, 30);
            block.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 4;");
            
            Optional<AttendanceRecord> match = subjectRecords.stream()
                    .filter(r -> r.recordDate().equals(current))
                    .findFirst();
                    
            if (match.isPresent()) {
                if (match.get().attended()) {
                    block.setStyle("-fx-background-color: -color-success-emphasis; -fx-background-radius: 4;");
                } else {
                    block.setStyle("-fx-background-color: -color-danger-emphasis; -fx-background-radius: 4;");
                }
            }
            
            Tooltip.install(block, new Tooltip(current.toString() + (match.isPresent() ? (match.get().attended() ? " (Present)" : " (Absent)") : " (No Data)")));
            
            int col = i / 5;
            int row = i % 5;
            heatmapGrid.add(block, col, row);
        }
    }
}
