package com.example.studentmanager.service;

import com.example.studentmanager.model.Subject;
import com.example.studentmanager.model.Task;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PdfExportService {

    public void exportReport(String username, File destination, List<Subject> subjects, List<Task> tasks) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                // We use standard PDFBox fonts which don't require external loading in basic scenarios
                contentStream.setFont(new PDType1Font(FontName.HELVETICA_BOLD), 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("CheckInTime - Academic Report");
                
                contentStream.setFont(new PDType1Font(FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("Student: " + username);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Date: " + LocalDate.now());
                
                // Subjects
                contentStream.setFont(new PDType1Font(FontName.HELVETICA_BOLD), 14);
                contentStream.newLineAtOffset(0, -40);
                contentStream.showText("Subject Performance:");
                
                contentStream.setFont(new PDType1Font(FontName.HELVETICA), 12);
                for (Subject s : subjects) {
                    contentStream.newLineAtOffset(0, -20);
                    double pct = s.marksTotal() > 0 ? ((double) s.marksObtained() / s.marksTotal()) * 100 : 0;
                    contentStream.showText("- " + s.subjectName() + ": " + s.marksObtained() + "/" + s.marksTotal() + 
                                         String.format(" (%.1f%%) [Target: %d%%]", pct, s.targetPercentage()));
                }
                
                // Tasks
                contentStream.setFont(new PDType1Font(FontName.HELVETICA_BOLD), 14);
                contentStream.newLineAtOffset(0, -40);
                contentStream.showText("Pending Tasks:");
                
                contentStream.setFont(new PDType1Font(FontName.HELVETICA), 12);
                for (Task t : tasks) {
                    if (!"DONE".equals(t.status())) {
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText("- " + t.taskName() + " (" + t.status() + ") Due: " + 
                                             (t.taskDate() != null ? t.taskDate() : "N/A"));
                    }
                }

                contentStream.endText();
            }
            
            document.save(destination);
        }
    }
}
