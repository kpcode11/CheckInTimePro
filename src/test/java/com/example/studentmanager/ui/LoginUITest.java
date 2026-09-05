package com.example.studentmanager.ui;

import com.example.studentmanager.MainApp;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
public class LoginUITest {

    @Start
    private void start(Stage stage) throws Exception {
        MainApp app = new MainApp();
        app.start(stage);
    }

    @Test
    void testLoginScreenAppears(FxRobot robot) {
        // Just verify the login button is present
        assertThat(robot.lookup("Login").queryButton()).hasText("Login");
    }
}
