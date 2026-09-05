package com.example.studentmanager;

import atlantafx.base.theme.Dracula;
import com.example.studentmanager.util.Router;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());

        Router.init(stage);
        stage.setTitle("StudentManager Pro");
        Router.navigateTo("Login.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
