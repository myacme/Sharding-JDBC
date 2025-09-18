package com.example.shardingjdbc.controller;

import com.example.shardingjdbc.entity.User;
import com.example.shardingjdbc.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器测试
 */
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
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
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }
    
    @Test
    void testCreateUser_Success() throws Exception {
        // Given
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户创建成功"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"));
        
        verify(userService, times(1)).createUser(any(User.class));
    }
    
    @Test
    void testCreateUser_Failure() throws Exception {
        // Given
        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("创建失败"));
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户创建失败：创建失败"));
        
        verify(userService, times(1)).createUser(any(User.class));
    }
    
    @Test
    void testGetUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"));
        
        verify(userService, times(1)).getUserById(1L);
    }
    
    @Test
    void testGetUserById_NotFound() throws Exception {
        // Given
        when(userService.getUserById(999L)).thenReturn(null);
        
        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在"));
        
        verify(userService, times(1)).getUserById(999L);
    }
    
    @Test
    void testGetUserByUsername_Success() throws Exception {
        // Given
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
        
        verify(userService, times(1)).getUserByUsername("testuser");
    }
    
    @Test
    void testGetAllUsers_Success() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.getAllUsers()).thenReturn(users);
        
        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.total").value(1));
        
        verify(userService, times(1)).getAllUsers();
    }
    
    @Test
    void testUpdateUser_Success() throws Exception {
        // Given
        testUser.setEmail("updated@example.com");
        when(userService.updateUser(any(User.class))).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户更新成功"))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"));
        
        verify(userService, times(1)).updateUser(any(User.class));
    }
    
    @Test
    void testUpdateUser_Failure() throws Exception {
        // Given
        when(userService.updateUser(any(User.class))).thenThrow(new RuntimeException("更新失败"));
        
        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户更新失败：更新失败"));
        
        verify(userService, times(1)).updateUser(any(User.class));
    }
    
    @Test
    void testDeleteUser_Success() throws Exception {
        // Given
        when(userService.deleteUser(1L)).thenReturn(true);
        
        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户删除成功"));
        
        verify(userService, times(1)).deleteUser(1L);
    }
    
    @Test
    void testDeleteUser_Failure() throws Exception {
        // Given
        when(userService.deleteUser(999L)).thenReturn(false);
        
        // When & Then
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户删除失败"));
        
        verify(userService, times(1)).deleteUser(999L);
    }
    
    @Test
    void testGetUsersByPage_Success() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.getUsersByPage(1, 10)).thenReturn(users);
        when(userService.getUserCount()).thenReturn(1);
        
        // When & Then
        mockMvc.perform(get("/api/users/page")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
        
        verify(userService, times(1)).getUsersByPage(1, 10);
        verify(userService, times(1)).getUserCount();
    }
    
    @Test
    void testGetUsersByPage_WithDefaultParams() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.getUsersByPage(1, 10)).thenReturn(users);
        when(userService.getUserCount()).thenReturn(1);
        
        // When & Then
        mockMvc.perform(get("/api/users/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(10));
        
        verify(userService, times(1)).getUsersByPage(1, 10);
    }
}
