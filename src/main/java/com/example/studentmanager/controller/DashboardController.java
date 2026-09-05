package com.example.studentmanager.controller;

import com.example.studentmanager.model.Subject;
import com.example.studentmanager.model.Task;
import com.example.studentmanager.service.GradeService;
import com.example.studentmanager.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.LocalDate;
import java.util.List;

public class DashboardController {
    @FXML private Label greetingLabel;
    @FXML private Label averageMarksLabel;
    @FXML private Label tasksDueLabel;
    @FXML private Label attendanceLabel;
    
    @FXML private PieChart marksPieChart;
    @FXML private BarChart<String, Number> subjectBarChart;
    
    @FXML private ListView<String> todayTasksListView;
    @FXML private ListView<String> upcomingTasksListView;

    private final GradeService gradeService = new GradeService();

    @FXML
    public void initialize() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;
        
        greetingLabel.setText("Hello, " + user.username() + "! Here is your overview for " + LocalDate.now() + ".");
        
        loadSummaryCards(user.username());
        loadCharts(user.username());
        loadTasks(user.username());
    }
    
    private void loadSummaryCards(String username) {
        double avg = gradeService.getOverallAverage(username);
        long tasks = gradeService.getTasksDueTodayCount(username);
        double attendance = gradeService.getAttendancePercentage(username);
        
        averageMarksLabel.setText(String.format("%.1f%%", avg));
        tasksDueLabel.setText(String.valueOf(tasks));
        attendanceLabel.setText(String.format("%.1f%%", attendance));
    }
    
    private void loadCharts(String username) {
        List<Subject> subjects = gradeService.getSubjects(username);
        
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName("Current %");
        XYChart.Series<String, Number> targetSeries = new XYChart.Series<>();
        targetSeries.setName("Target %");
        
        for (Subject s : subjects) {
            double percent = s.marksTotal() > 0 ? ((double) s.marksObtained() / s.marksTotal()) * 100 : 0;
            pieData.add(new PieChart.Data(s.subjectName(), s.marksObtained()));
            
            barSeries.getData().add(new XYChart.Data<>(s.subjectName(), percent));
            targetSeries.getData().add(new XYChart.Data<>(s.subjectName(), s.targetPercentage()));
        }
        
        marksPieChart.setData(pieData);
        subjectBarChart.getData().addAll(barSeries, targetSeries);
    }
    
    private void loadTasks(String username) {
        List<Task> allTasks = gradeService.getTasks(username);
        LocalDate today = LocalDate.now();
        
        ObservableList<String> todayList = FXCollections.observableArrayList();
        allTasks.stream()
            .filter(t -> today.equals(t.taskDate()) && !"DONE".equalsIgnoreCase(t.status()))
            .forEach(t -> todayList.add(t.taskName() + " (" + t.category() + ")"));
            
        todayTasksListView.setItems(todayList);
        
        ObservableList<String> upcomingList = FXCollections.observableArrayList();
        gradeService.getUpcomingTasks(username, 3)
            .forEach(t -> upcomingList.add(t.taskName() + " - " + t.taskDate()));
            
        upcomingTasksListView.setItems(upcomingList);
    }
}
