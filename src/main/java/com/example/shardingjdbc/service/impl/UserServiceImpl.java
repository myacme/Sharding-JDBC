package com.example.shardingjdbc.service.impl;

import com.example.shardingjdbc.entity.User;
import com.example.shardingjdbc.mapper.UserMapper;
import com.example.shardingjdbc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User createUser(User user) {
        log.info("创建用户，用户名：{}", user.getUsername());
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        
        // 设置默认状态为启用
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        
        int result = userMapper.insert(user);
        if (result > 0) {
            log.info("用户创建成功，用户ID：{}", user.getUserId());
            return user;
        } else {
            log.error("用户创建失败");
            throw new RuntimeException("用户创建失败");
        }
    }
    
    @Override
    public User getUserById(Long userId) {
        log.info("根据用户ID查询用户，用户ID：{}", userId);
        return userMapper.selectById(userId);
    }
    
    @Override
    public User getUserByUsername(String username) {
        log.info("根据用户名查询用户，用户名：{}", username);
        return userMapper.selectByUsername(username);
    }
    
    @Override
    public List<User> getAllUsers() {
        log.info("查询所有用户");
        return userMapper.selectAll();
    }
    
    @Override
    public User updateUser(User user) {
        log.info("更新用户信息，用户ID：{}", user.getUserId());
        
        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        
        int result = userMapper.update(user);
        if (result > 0) {
            log.info("用户更新成功，用户ID：{}", user.getUserId());
            return userMapper.selectById(user.getUserId());
        } else {
            log.error("用户更新失败，用户ID：{}", user.getUserId());
            throw new RuntimeException("用户更新失败");
        }
    }
    
    @Override
    public boolean deleteUser(Long userId) {
        log.info("删除用户，用户ID：{}", userId);
        
        int result = userMapper.deleteById(userId);
        if (result > 0) {
            log.info("用户删除成功，用户ID：{}", userId);
            return true;
        } else {
            log.error("用户删除失败，用户ID：{}", userId);
            return false;
        }
    }
    
    @Override
    public List<User> getUsersByPage(int pageNum, int pageSize) {
        log.info("分页查询用户，页码：{}，每页大小：{}", pageNum, pageSize);
        
        int offset = (pageNum - 1) * pageSize;
        return userMapper.selectByPage(offset, pageSize);
    }
    
    @Override
    public int getUserCount() {
        log.info("查询用户总数");
        return userMapper.countAll();
    }
}
