package com.example.studentmanager.controller;

import com.example.studentmanager.dao.TaskDao;
import com.example.studentmanager.model.Task;
import com.example.studentmanager.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

public class TasksController {
    @FXML private VBox todoColumn;
    @FXML private VBox inProgressColumn;
    @FXML private VBox doneColumn;
    
    private final TaskDao taskDao = new TaskDao();
    private List<Task> masterTasks;

    @FXML
    public void initialize() {
        setupDragAndDrop(todoColumn, "TODO");
        setupDragAndDrop(inProgressColumn, "IN_PROGRESS");
        setupDragAndDrop(doneColumn, "DONE");
        loadData();
    }

    private void loadData() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;
        
        masterTasks = taskDao.getTasksForUser(user.username());
        
        todoColumn.getChildren().clear();
        inProgressColumn.getChildren().clear();
        doneColumn.getChildren().clear();
        
        for (Task t : masterTasks) {
            VBox card = createCard(t);
            switch (t.status()) {
                case "TODO": todoColumn.getChildren().add(card); break;
                case "IN_PROGRESS": inProgressColumn.getChildren().add(card); break;
                case "DONE": doneColumn.getChildren().add(card); break;
            }
        }
        
        addEmptyStateIfNeeded(todoColumn, "No tasks here. Relax!");
        addEmptyStateIfNeeded(inProgressColumn, "Drop a task here to start working.");
        addEmptyStateIfNeeded(doneColumn, "Nothing done yet. Get to work!");
    }

    private void addEmptyStateIfNeeded(VBox column, String text) {
        if (column.getChildren().isEmpty()) {
            Label emptyLabel = new Label(text);
            emptyLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-style: italic; -fx-padding: 20;");
            column.getChildren().add(emptyLabel);
        }
    }

    private VBox createCard(Task t) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: -color-bg-default; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        Label title = new Label(t.taskName());
        title.setStyle("-fx-font-weight: bold;");
        Label category = new Label(t.category() != null ? t.category() : "No Category");
        category.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 11px;");
        
        Label date = new Label(t.taskDate() != null ? t.taskDate().toString() : "");
        
        card.getChildren().addAll(title, category, date);
        
        // Drag Setup
        card.setOnDragDetected(event -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(t.id()));
            db.setContent(content);
            event.consume();
        });
        
        return card;
    }

    private void setupDragAndDrop(VBox column, String statusTarget) {
        column.setOnDragOver(event -> {
            if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        column.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                int taskId = Integer.parseInt(db.getString());
                taskDao.updateTaskStatus(taskId, statusTarget);
                success = true;
                loadData();
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    @FXML
    public void handleAddTask() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Add Task");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField catField = new TextField();
        DatePicker datePicker = new DatePicker();

        grid.add(new Label("Task Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(catField, 1, 1);
        grid.add(new Label("Due Date:"), 0, 2);
        grid.add(datePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Task(0, SessionManager.getInstance().getCurrentUser().username(),
                        nameField.getText(), catField.getText(), datePicker.getValue(),
                        null, "MEDIUM", null, "TODO");
            }
            return null;
        });

        dialog.showAndWait().ifPresent(t -> {
            taskDao.insertTask(t);
            loadData();
        });
    }
}
