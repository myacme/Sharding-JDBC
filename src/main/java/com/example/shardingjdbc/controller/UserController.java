package com.example.shardingjdbc.controller;

import com.example.shardingjdbc.entity.User;
import com.example.shardingjdbc.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新的用户")
    public ResponseEntity<Map<String, Object>> createUser(
            @Parameter(description = "用户信息") @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "用户创建成功");
            result.put("data", createdUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("创建用户失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户创建失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "根据用户ID查询用户", description = "通过用户ID获取用户详细信息")
    public ResponseEntity<Map<String, Object>> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            Map<String, Object> result = new HashMap<>();
            if (user != null) {
                result.put("success", true);
                result.put("message", "查询成功");
                result.put("data", user);
            } else {
                result.put("success", false);
                result.put("message", "用户不存在");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询用户失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名查询用户", description = "通过用户名获取用户详细信息")
    public ResponseEntity<Map<String, Object>> getUserByUsername(
            @Parameter(description = "用户名") @PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            Map<String, Object> result = new HashMap<>();
            if (user != null) {
                result.put("success", true);
                result.put("message", "查询成功");
                result.put("data", user);
            } else {
                result.put("success", false);
                result.put("message", "用户不存在");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询用户失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @GetMapping
    @Operation(summary = "查询所有用户", description = "获取所有用户列表")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", users);
            result.put("total", users.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询所有用户失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @PutMapping("/{userId}")
    @Operation(summary = "更新用户信息", description = "修改用户的详细信息")
    public ResponseEntity<Map<String, Object>> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "用户信息") @RequestBody User user) {
        try {
            user.setUserId(userId);
            User updatedUser = userService.updateUser(user);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "用户更新成功");
            result.put("data", updatedUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("更新用户失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户更新失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            boolean success = userService.deleteUser(userId);
            Map<String, Object> result = new HashMap<>();
            if (success) {
                result.put("success", true);
                result.put("message", "用户删除成功");
            } else {
                result.put("success", false);
                result.put("message", "用户删除失败");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("删除用户失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户删除失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @GetMapping("/page")
    @Operation(summary = "分页查询用户", description = "分页获取用户列表")
    public ResponseEntity<Map<String, Object>> getUsersByPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<User> users = userService.getUsersByPage(pageNum, pageSize);
            int total = userService.getUserCount();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", users);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            result.put("total", total);
            result.put("totalPages", (total + pageSize - 1) / pageSize);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("分页查询用户失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
