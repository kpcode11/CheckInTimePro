package com.example.studentmanager.controller;

import com.example.studentmanager.dao.SubjectDao;
import com.example.studentmanager.model.Subject;
import com.example.studentmanager.util.SessionManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class SubjectsController {
    @FXML private TextField searchField;
    @FXML private TableView<Subject> subjectsTable;
    @FXML private TableColumn<Subject, String> nameCol;
    @FXML private TableColumn<Subject, String> marksCol;
    @FXML private TableColumn<Subject, Integer> targetCol;
    @FXML private TableColumn<Subject, Double> progressCol;

    private final SubjectDao subjectDao = new SubjectDao();
    private ObservableList<Subject> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadData();
        setupSearch();
    }

    private void setupTable() {
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().subjectName()));
        marksCol.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().marksObtained() + " / " + cellData.getValue().marksTotal()
        ));
        targetCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().targetPercentage()).asObject());
        
        progressCol.setCellValueFactory(cellData -> {
            Subject s = cellData.getValue();
            double pct = s.marksTotal() > 0 ? (double) s.marksObtained() / s.marksTotal() : 0.0;
            return new SimpleDoubleProperty(pct).asObject();
        });

        progressCol.setCellFactory(column -> new TableCell<Subject, Double>() {
            private final ProgressBar progressBar = new ProgressBar();
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    progressBar.setProgress(item);
                    progressBar.setMaxWidth(Double.MAX_VALUE);
                    
                    Subject s = getTableView().getItems().get(getIndex());
                    double targetPct = s.targetPercentage() / 100.0;
                    
                    if (item >= targetPct) {
                        progressBar.setStyle("-fx-accent: -color-success-emphasis;");
                    } else if (item >= targetPct - 0.1) {
                        progressBar.setStyle("-fx-accent: -color-warning-emphasis;");
                    } else {
                        progressBar.setStyle("-fx-accent: -color-danger-emphasis;");
                    }
                    
                    setGraphic(progressBar);
                }
            }
        });
    }

    private void loadData() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            masterData.setAll(subjectDao.getSubjectsForUser(user.username()));
            subjectsTable.setItems(masterData);
        }
    }

    private void setupSearch() {
        FilteredList<Subject> filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(subject -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return subject.subjectName().toLowerCase().contains(lowerCaseFilter);
            });
        });
        subjectsTable.setItems(filteredData);
    }

    @FXML
    public void handleAdd() {
        showSubjectDialog(null);
    }

    @FXML
    public void handleEdit() {
        Subject selected = subjectsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showSubjectDialog(selected);
        }
    }

    @FXML
    public void handleDelete() {
        Subject selected = subjectsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.subjectName() + "?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (subjectDao.deleteSubject(selected.id())) {
                        loadData();
                    }
                }
            });
        }
    }

    private void showSubjectDialog(Subject subject) {
        Dialog<Subject> dialog = new Dialog<>();
        dialog.setTitle(subject == null ? "Add Subject" : "Edit Subject");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Subject Name");
        TextField obtainedField = new TextField();
        obtainedField.setPromptText("Marks Obtained");
        TextField totalField = new TextField();
        totalField.setPromptText("Total Marks");
        TextField targetField = new TextField();
        targetField.setPromptText("Target %");

        if (subject != null) {
            nameField.setText(subject.subjectName());
            obtainedField.setText(String.valueOf(subject.marksObtained()));
            totalField.setText(String.valueOf(subject.marksTotal()));
            targetField.setText(String.valueOf(subject.targetPercentage()));
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Obtained:"), 0, 1);
        grid.add(obtainedField, 1, 1);
        grid.add(new Label("Total:"), 0, 2);
        grid.add(totalField, 1, 2);
        grid.add(new Label("Target %:"), 0, 3);
        grid.add(targetField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return new Subject(
                        subject == null ? 0 : subject.id(),
                        SessionManager.getInstance().getCurrentUser().username(),
                        nameField.getText(),
                        Integer.parseInt(obtainedField.getText()),
                        Integer.parseInt(totalField.getText()),
                        Integer.parseInt(targetField.getText())
                    );
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Subject> result = dialog.showAndWait();
        result.ifPresent(newSubject -> {
            if (subject == null) {
                subjectDao.insertSubject(newSubject);
            } else {
                subjectDao.updateSubject(newSubject);
            }
            loadData();
        });
    }
}
