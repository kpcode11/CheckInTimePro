package com.example.studentmanager.service;

import javafx.application.Platform;
import org.controlsfx.control.Notifications;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderService {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                // In a real app, this polls the DB for exact task times.
                // Notifications.create().title("Task Reminder").text("You have a task due soon!").showInformation();
            });
        }, 1, 60, TimeUnit.MINUTES);
    }
    
    public void stop() {
        scheduler.shutdown();
    }
}
