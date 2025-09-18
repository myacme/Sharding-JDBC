package com.example.shardingjdbc.service;

import com.example.shardingjdbc.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     * @param order 订单信息
     * @return 创建的订单信息
     */
    Order createOrder(Order order);
    
    /**
     * 根据订单ID查询订单
     * @param orderId 订单ID
     * @return 订单信息
     */
    Order getOrderById(Long orderId);
    
    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> getOrdersByUserId(Long userId);
    
    /**
     * 根据订单号查询订单
     * @param orderNo 订单号
     * @return 订单信息
     */
    Order getOrderByOrderNo(String orderNo);
    
    /**
     * 查询所有订单
     * @return 订单列表
     */
    List<Order> getAllOrders();
    
    /**
     * 更新订单信息
     * @param order 订单信息
     * @return 更新后的订单信息
     */
    Order updateOrder(Order order);
    
    /**
     * 删除订单
     * @param orderId 订单ID
     * @return 是否删除成功
     */
    boolean deleteOrder(Long orderId);
    
    /**
     * 分页查询订单
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 订单列表
     */
    List<Order> getOrdersByPage(int pageNum, int pageSize);
    
    /**
     * 获取订单总数
     * @return 订单总数
     */
    int getOrderCount();
    
    /**
     * 根据用户ID分页查询订单
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 订单列表
     */
    List<Order> getOrdersByUserIdAndPage(Long userId, int pageNum, int pageSize);
    
    /**
     * 根据用户ID获取订单总数
     * @param userId 用户ID
     * @return 订单总数
     */
    int getOrderCountByUserId(Long userId);
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 订单状态
     * @return 是否更新成功
     */
    boolean updateOrderStatus(Long orderId, Integer status);
}
