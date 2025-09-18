package com.example.shardingjdbc.service;

import com.example.shardingjdbc.entity.User;
import com.example.shardingjdbc.mapper.UserMapper;
import com.example.shardingjdbc.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800000000");
        testUser.setStatus(1);
    }
    
    @Test
    void testCreateUser_Success() {
        // Given
        when(userMapper.insert(any(User.class))).thenReturn(1);
        
        // When
        User result = userService.createUser(testUser);
        
        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(1, result.getStatus());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        
        verify(userMapper, times(1)).insert(any(User.class));
    }
    
    @Test
    void testCreateUser_Failure() {
        // Given
        when(userMapper.insert(any(User.class))).thenReturn(0);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.createUser(testUser);
        });
        
        verify(userMapper, times(1)).insert(any(User.class));
    }
    
    @Test
    void testGetUserById_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        
        // When
        User result = userService.getUserById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("testuser", result.getUsername());
        
        verify(userMapper, times(1)).selectById(1L);
    }
    
    @Test
    void testGetUserById_NotFound() {
        // Given
        when(userMapper.selectById(999L)).thenReturn(null);
        
        // When
        User result = userService.getUserById(999L);
        
        // Then
        assertNull(result);
        verify(userMapper, times(1)).selectById(999L);
    }
    
    @Test
    void testGetUserByUsername_Success() {
        // Given
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        
        // When
        User result = userService.getUserByUsername("testuser");
        
        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        
        verify(userMapper, times(1)).selectByUsername("testuser");
    }
    
    @Test
    void testGetAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(testUser, createAnotherUser());
        when(userMapper.selectAll()).thenReturn(users);
        
        // When
        List<User> result = userService.getAllUsers();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        
        verify(userMapper, times(1)).selectAll();
    }
    
    @Test
    void testUpdateUser_Success() {
        // Given
        testUser.setEmail("updated@example.com");
        when(userMapper.update(any(User.class))).thenReturn(1);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        
        // When
        User result = userService.updateUser(testUser);
        
        // Then
        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
        assertNotNull(result.getUpdateTime());
        
        verify(userMapper, times(1)).update(any(User.class));
        verify(userMapper, times(1)).selectById(1L);
    }
    
    @Test
    void testUpdateUser_Failure() {
        // Given
        when(userMapper.update(any(User.class))).thenReturn(0);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(testUser);
        });
        
        verify(userMapper, times(1)).update(any(User.class));
    }
    
    @Test
    void testDeleteUser_Success() {
        // Given
        when(userMapper.deleteById(1L)).thenReturn(1);
        
        // When
        boolean result = userService.deleteUser(1L);
        
        // Then
        assertTrue(result);
        verify(userMapper, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteUser_Failure() {
        // Given
        when(userMapper.deleteById(999L)).thenReturn(0);
        
        // When
        boolean result = userService.deleteUser(999L);
        
        // Then
        assertFalse(result);
        verify(userMapper, times(1)).deleteById(999L);
    }
    
    @Test
    void testGetUsersByPage_Success() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userMapper.selectByPage(0, 10)).thenReturn(users);
        
        // When
        List<User> result = userService.getUsersByPage(1, 10);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(userMapper, times(1)).selectByPage(0, 10);
    }
    
    @Test
    void testGetUserCount_Success() {
        // Given
        when(userMapper.countAll()).thenReturn(5);
        
        // When
        int result = userService.getUserCount();
        
        // Then
        assertEquals(5, result);
        verify(userMapper, times(1)).countAll();
    }
    
    private User createAnotherUser() {
        User user = new User();
        user.setUserId(2L);
        user.setUsername("anotheruser");
        user.setPassword("password456");
        user.setEmail("another@example.com");
        user.setPhone("13900000000");
        user.setStatus(1);
        return user;
    }
}
