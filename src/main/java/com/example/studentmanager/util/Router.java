package com.example.studentmanager.util;

import com.example.studentmanager.MainApp;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Router {
    private static Stage primaryStage;
    private static StackPane mainContentArea;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void setMainContentArea(StackPane contentArea) {
        mainContentArea = contentArea;
    }

    public static void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/example/studentmanager/fxml/" + fxmlPath));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1000, 700);
            var css = MainApp.class.getResource("/com/example/studentmanager/css/style.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void navigateContent(String fxmlPath) {
        if (mainContentArea == null) return;
        
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/example/studentmanager/fxml/" + fxmlPath));
            Node node = loader.load();
            
            node.setOpacity(0);
            mainContentArea.getChildren().setAll(node);
            
            FadeTransition ft = new FadeTransition(Duration.millis(300), node);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
