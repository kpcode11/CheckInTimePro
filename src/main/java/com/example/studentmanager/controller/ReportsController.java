package com.example.studentmanager.controller;

import com.example.studentmanager.service.GradeService;
import com.example.studentmanager.service.PdfExportService;
import com.example.studentmanager.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;

public class ReportsController {

    @FXML private Label statusLabel;
    
    private final GradeService gradeService = new GradeService();
    private final PdfExportService pdfExportService = new PdfExportService();

    @FXML
    public void handleExportPDF() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Academic_Report_" + user.username() + ".pdf");
        
        File file = fileChooser.showSaveDialog(statusLabel.getScene().getWindow());
        
        if (file != null) {
            try {
                pdfExportService.exportReport(
                    user.username(), 
                    file, 
                    gradeService.getSubjects(user.username()), 
                    gradeService.getTasks(user.username())
                );
                
                statusLabel.setText("Successfully exported report to: " + file.getAbsolutePath());
                statusLabel.setStyle("-fx-text-fill: -color-success-emphasis;");
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Success");
                alert.setHeaderText(null);
                alert.setContentText("PDF Report has been generated successfully.");
                alert.showAndWait();
                
            } catch (Exception e) {
                statusLabel.setText("Failed to export PDF: " + e.getMessage());
                statusLabel.setStyle("-fx-text-fill: -color-danger-emphasis;");
                e.printStackTrace();
            }
        }
    }
}
