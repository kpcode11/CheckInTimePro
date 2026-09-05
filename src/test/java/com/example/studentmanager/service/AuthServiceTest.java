package com.example.studentmanager.service;

import com.example.studentmanager.dao.UserDao;
import com.example.studentmanager.model.User;
import com.example.studentmanager.util.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserDao userDao;

    private AuthService authService;

    @BeforeEach
    public void setUp() {
        authService = new AuthService(userDao);
    }

    @Test
    public void testRegister_UserExists() {
        when(userDao.findByUsername("existingUser")).thenReturn(Optional.of(new User(1, "existingUser", "hash", Timestamp.valueOf(LocalDateTime.now()))));
        
        boolean result = authService.register("existingUser", "password");
        
        assertFalse(result);
        verify(userDao, never()).insertUser(anyString(), anyString());
    }

    @Test
    public void testRegister_NewUser() {
        when(userDao.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userDao.insertUser(eq("newUser"), anyString())).thenReturn(true);
        
        boolean result = authService.register("newUser", "password");
        
        assertTrue(result);
        verify(userDao, times(1)).insertUser(eq("newUser"), anyString());
    }

    @Test
    public void testLogin_UnknownUser() {
        when(userDao.findByUsername("unknown")).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(Exception.class, () -> authService.login("unknown", "pass"));
        assertEquals("Unknown user.", exception.getMessage());
    }

    @Test
    public void testLogin_WrongPassword() {
        String hash = PasswordHasher.hashPassword("correctPass");
        User user = new User(1, "testUser", hash, Timestamp.valueOf(LocalDateTime.now()));
        when(userDao.findByUsername("testUser")).thenReturn(Optional.of(user));
        
        Exception exception = assertThrows(Exception.class, () -> authService.login("testUser", "wrongPass"));
        assertEquals("Wrong password.", exception.getMessage());
    }

    @Test
    public void testLogin_Success() throws Exception {
        String hash = PasswordHasher.hashPassword("correctPass");
        User user = new User(1, "testUser", hash, Timestamp.valueOf(LocalDateTime.now()));
        when(userDao.findByUsername("testUser")).thenReturn(Optional.of(user));
        
        User result = authService.login("testUser", "correctPass");
        
        assertNotNull(result);
        assertEquals("testUser", result.username());
    }
}
