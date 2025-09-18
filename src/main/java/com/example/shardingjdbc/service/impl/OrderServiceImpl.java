package com.example.shardingjdbc.service.impl;

import com.example.shardingjdbc.entity.Order;
import com.example.shardingjdbc.mapper.OrderMapper;
import com.example.shardingjdbc.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Override
    public Order createOrder(Order order) {
        log.info("创建订单，用户ID：{}，商品名称：{}", order.getUserId(), order.getProductName());
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        order.setCreateTime(now);
        order.setUpdateTime(now);
        
        // 生成订单号
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            order.setOrderNo(generateOrderNo());
        }
        
        // 设置默认状态为待支付
        if (order.getStatus() == null) {
            order.setStatus(0);
        }
        
        int result = orderMapper.insert(order);
        if (result > 0) {
            log.info("订单创建成功，订单ID：{}，订单号：{}", order.getOrderId(), order.getOrderNo());
            return order;
        } else {
            log.error("订单创建失败");
            throw new RuntimeException("订单创建失败");
        }
    }
    
    @Override
    public Order getOrderById(Long orderId) {
        log.info("根据订单ID查询订单，订单ID：{}", orderId);
        return orderMapper.selectById(orderId);
    }
    
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        log.info("根据用户ID查询订单列表，用户ID：{}", userId);
        return orderMapper.selectByUserId(userId);
    }
    
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        log.info("根据订单号查询订单，订单号：{}", orderNo);
        return orderMapper.selectByOrderNo(orderNo);
    }
    
    @Override
    public List<Order> getAllOrders() {
        log.info("查询所有订单");
        return orderMapper.selectAll();
    }
    
    @Override
    public Order updateOrder(Order order) {
        log.info("更新订单信息，订单ID：{}", order.getOrderId());
        
        // 设置更新时间
        order.setUpdateTime(LocalDateTime.now());
        
        int result = orderMapper.update(order);
        if (result > 0) {
            log.info("订单更新成功，订单ID：{}", order.getOrderId());
            return orderMapper.selectById(order.getOrderId());
        } else {
            log.error("订单更新失败，订单ID：{}", order.getOrderId());
            throw new RuntimeException("订单更新失败");
        }
    }
    
    @Override
    public boolean deleteOrder(Long orderId) {
        log.info("删除订单，订单ID：{}", orderId);
        
        int result = orderMapper.deleteById(orderId);
        if (result > 0) {
            log.info("订单删除成功，订单ID：{}", orderId);
            return true;
        } else {
            log.error("订单删除失败，订单ID：{}", orderId);
            return false;
        }
    }
    
    @Override
    public List<Order> getOrdersByPage(int pageNum, int pageSize) {
        log.info("分页查询订单，页码：{}，每页大小：{}", pageNum, pageSize);
        
        int offset = (pageNum - 1) * pageSize;
        return orderMapper.selectByPage(offset, pageSize);
    }
    
    @Override
    public int getOrderCount() {
        log.info("查询订单总数");
        return orderMapper.countAll();
    }
    
    @Override
    public List<Order> getOrdersByUserIdAndPage(Long userId, int pageNum, int pageSize) {
        log.info("根据用户ID分页查询订单，用户ID：{}，页码：{}，每页大小：{}", userId, pageNum, pageSize);
        
        int offset = (pageNum - 1) * pageSize;
        return orderMapper.selectByUserIdAndPage(userId, offset, pageSize);
    }
    
    @Override
    public int getOrderCountByUserId(Long userId) {
        log.info("根据用户ID查询订单总数，用户ID：{}", userId);
        return orderMapper.countByUserId(userId);
    }
    
    @Override
    public boolean updateOrderStatus(Long orderId, Integer status) {
        log.info("更新订单状态，订单ID：{}，状态：{}", orderId, status);
        
        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        
        int result = orderMapper.update(order);
        if (result > 0) {
            log.info("订单状态更新成功，订单ID：{}，状态：{}", orderId, status);
            return true;
        } else {
            log.error("订单状态更新失败，订单ID：{}，状态：{}", orderId, status);
            return false;
        }
    }
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNo() {
        return "ORDER" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
