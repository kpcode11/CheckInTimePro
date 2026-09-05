package com.example.studentmanager;

import atlantafx.base.theme.Dracula;
import com.example.studentmanager.util.Router;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());

        // Set window icon
        try {
            var iconStream = MainApp.class.getResourceAsStream("/com/example/studentmanager/icon.jpg");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception ignored) {}

        Router.init(stage);
        stage.setTitle("StudentManager Pro");
        Router.navigateTo("Login.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
