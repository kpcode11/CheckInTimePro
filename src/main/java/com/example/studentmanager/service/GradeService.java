package com.example.studentmanager.service;

import com.example.studentmanager.dao.AttendanceDao;
import com.example.studentmanager.dao.SubjectDao;
import com.example.studentmanager.dao.TaskDao;
import com.example.studentmanager.model.AttendanceRecord;
import com.example.studentmanager.model.Subject;
import com.example.studentmanager.model.Task;

import java.time.LocalDate;
import java.util.List;

public class GradeService {
    private final SubjectDao subjectDao = new SubjectDao();
    private final TaskDao taskDao = new TaskDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();

    public List<Subject> getSubjects(String username) {
        return subjectDao.getSubjectsForUser(username);
    }

    public List<Task> getTasks(String username) {
        return taskDao.getTasksForUser(username);
    }

    public double getOverallAverage(String username) {
        List<Subject> subjects = getSubjects(username);
        if (subjects.isEmpty()) return 0.0;
        
        double totalPercentage = 0;
        for (Subject s : subjects) {
            if (s.marksTotal() > 0) {
                totalPercentage += ((double) s.marksObtained() / s.marksTotal()) * 100;
            }
        }
        return totalPercentage / subjects.size();
    }

    public long getTasksDueTodayCount(String username) {
        List<Task> tasks = getTasks(username);
        LocalDate today = LocalDate.now();
        return tasks.stream()
            .filter(t -> today.equals(t.taskDate()) && !"DONE".equalsIgnoreCase(t.status()))
            .count();
    }
    
    public List<Task> getUpcomingTasks(String username, int limit) {
        List<Task> tasks = getTasks(username);
        LocalDate today = LocalDate.now();
        return tasks.stream()
            .filter(t -> t.taskDate() != null && !t.taskDate().isBefore(today) && !"DONE".equalsIgnoreCase(t.status()))
            .sorted((t1, t2) -> t1.taskDate().compareTo(t2.taskDate()))
            .limit(limit)
            .toList();
    }

    public double getAttendancePercentage(String username) {
        List<AttendanceRecord> records = attendanceDao.getAttendanceForUser(username);
        if (records.isEmpty()) return 0.0;
        
        long attended = records.stream().filter(AttendanceRecord::attended).count();
        return ((double) attended / records.size()) * 100;
    }
}
