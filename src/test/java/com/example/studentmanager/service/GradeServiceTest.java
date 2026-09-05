package com.example.studentmanager.service;

import com.example.studentmanager.dao.AttendanceDao;
import com.example.studentmanager.dao.SubjectDao;
import com.example.studentmanager.dao.TaskDao;
import com.example.studentmanager.model.AttendanceRecord;
import com.example.studentmanager.model.Subject;
import com.example.studentmanager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GradeServiceTest {

    @Mock
    private SubjectDao subjectDao;
    @Mock
    private TaskDao taskDao;
    @Mock
    private AttendanceDao attendanceDao;

    private GradeService gradeService;

    @BeforeEach
    public void setUp() {
        gradeService = new GradeService(subjectDao, taskDao, attendanceDao);
    }

    @Test
    public void testGetOverallAverage_EmptySubjects() {
        when(subjectDao.getSubjectsForUser("testUser")).thenReturn(Collections.emptyList());
        assertEquals(0.0, gradeService.getOverallAverage("testUser"), 0.01);
    }

    @Test
    public void testGetOverallAverage_WithSubjects() {
        Subject s1 = new Subject(1, "testUser", "Math", 85, 100, 80);
        Subject s2 = new Subject(2, "testUser", "Physics", 50, 200, 50); // 25%
        when(subjectDao.getSubjectsForUser("testUser")).thenReturn(Arrays.asList(s1, s2));
        
        // Math = 85%, Physics = 25% -> average = 55%
        assertEquals(55.0, gradeService.getOverallAverage("testUser"), 0.01);
    }

    @Test
    public void testGetTasksDueTodayCount() {
        LocalDate today = LocalDate.now();
        Task t1 = new Task(1, "testUser", "Task1", "Cat", today, "10:00", "High", 0, "TODO");
        Task t2 = new Task(2, "testUser", "Task2", "Cat", today, "10:00", "High", 0, "DONE");
        Task t3 = new Task(3, "testUser", "Task3", "Cat", today.plusDays(1), "10:00", "High", 0, "TODO");

        when(taskDao.getTasksForUser("testUser")).thenReturn(Arrays.asList(t1, t2, t3));

        long count = gradeService.getTasksDueTodayCount("testUser");
        assertEquals(1, count); // Only t1 is due today and not DONE
    }

    @Test
    public void testGetAttendancePercentage() {
        AttendanceRecord r1 = new AttendanceRecord(1, "testUser", "Math", true, LocalDate.now());
        AttendanceRecord r2 = new AttendanceRecord(2, "testUser", "Physics", false, LocalDate.now());
        AttendanceRecord r3 = new AttendanceRecord(3, "testUser", "History", true, LocalDate.now());
        
        when(attendanceDao.getAttendanceForUser("testUser")).thenReturn(Arrays.asList(r1, r2, r3));
        
        // 2 out of 3 = 66.66%
        assertEquals(66.66, gradeService.getAttendancePercentage("testUser"), 0.01);
    }
}
