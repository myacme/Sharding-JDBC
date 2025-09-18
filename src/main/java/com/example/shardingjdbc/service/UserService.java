package com.example.shardingjdbc.service;

import com.example.shardingjdbc.entity.User;

import java.util.List;

/**
 * 用户服务接口
 * @author MyAcme
 */
public interface UserService {
    
    /**
     * 创建用户
     * @param user 用户信息
     * @return 创建的用户信息
     */
    User createUser(User user);
    
    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Long userId);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User updateUser(User user);
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long userId);
    
    /**
     * 分页查询用户
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 用户列表
     */
    List<User> getUsersByPage(int pageNum, int pageSize);
    
    /**
     * 获取用户总数
     * @return 用户总数
     */
    int getUserCount();
}
