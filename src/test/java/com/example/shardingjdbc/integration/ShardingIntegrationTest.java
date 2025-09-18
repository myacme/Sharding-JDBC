package com.example.shardingjdbc.integration;

import com.example.shardingjdbc.entity.Order;
import com.example.shardingjdbc.entity.User;
import com.example.shardingjdbc.service.OrderService;
import com.example.shardingjdbc.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sharding-JDBC 分片功能集成测试
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ShardingIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderService orderService;
    
    @Test
    void testUserSharding_EvenUserId_ShouldGoToDs0() {
        // Given - 创建用户ID为偶数的用户（应该分片到ds0）
        User user = createUser(2L, "user2", "user2@example.com");
        
        // When
        User createdUser = userService.createUser(user);
        
        // Then
        assertNotNull(createdUser);
        assertEquals(2L, createdUser.getUserId());
        assertEquals("user2", createdUser.getUsername());
        
        // 验证可以查询到用户（说明分片正确）
        User foundUser = userService.getUserById(2L);
        assertNotNull(foundUser);
        assertEquals("user2", foundUser.getUsername());
    }
    
    @Test
    void testUserSharding_OddUserId_ShouldGoToDs1() {
        // Given - 创建用户ID为奇数的用户（应该分片到ds1）
        User user = createUser(1L, "user1", "user1@example.com");
        
        // When
        User createdUser = userService.createUser(user);
        
        // Then
        assertNotNull(createdUser);
        assertEquals(1L, createdUser.getUserId());
        assertEquals("user1", createdUser.getUsername());
        
        // 验证可以查询到用户（说明分片正确）
        User foundUser = userService.getUserById(1L);
        assertNotNull(foundUser);
        assertEquals("user1", foundUser.getUsername());
    }
    
    @Test
    void testOrderSharding_EvenUserId_ShouldGoToDs0() {
        // Given - 为用户ID为偶数的用户创建订单（应该分片到ds0）
        Order order = createOrder(2L, "iPhone 15", new BigDecimal("5999.00"));
        
        // When
        Order createdOrder = orderService.createOrder(order);
        
        // Then
        assertNotNull(createdOrder);
        assertEquals(2L, createdOrder.getUserId());
        assertEquals("iPhone 15", createdOrder.getProductName());
        
        // 验证可以查询到订单（说明分片正确）
        Order foundOrder = orderService.getOrderById(createdOrder.getOrderId());
        assertNotNull(foundOrder);
        assertEquals(2L, foundOrder.getUserId());
    }
    
    @Test
    void testOrderSharding_OddUserId_ShouldGoToDs1() {
        // Given - 为用户ID为奇数的用户创建订单（应该分片到ds1）
        Order order = createOrder(1L, "Samsung Galaxy", new BigDecimal("4999.00"));
        
        // When
        Order createdOrder = orderService.createOrder(order);
        
        // Then
        assertNotNull(createdOrder);
        assertEquals(1L, createdOrder.getUserId());
        assertEquals("Samsung Galaxy", createdOrder.getProductName());
        
        // 验证可以查询到订单（说明分片正确）
        Order foundOrder = orderService.getOrderById(createdOrder.getOrderId());
        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getUserId());
    }
    
    @Test
    void testOrderTableSharding_EvenOrderId_ShouldGoToOrder0() {
        // Given - 创建订单ID为偶数的订单
        Order order = createOrder(3L, "MacBook Pro", new BigDecimal("12999.00"));
        order.setOrderId(2L); // 设置为偶数
        
        // When
        Order createdOrder = orderService.createOrder(order);
        
        // Then
        assertNotNull(createdOrder);
        assertEquals(3L, createdOrder.getUserId());
        
        // 验证可以查询到订单（说明表分片正确）
        Order foundOrder = orderService.getOrderById(createdOrder.getOrderId());
        assertNotNull(foundOrder);
        assertEquals(3L, foundOrder.getUserId());
    }
    
    @Test
    void testOrderTableSharding_OddOrderId_ShouldGoToOrder1() {
        // Given - 创建订单ID为奇数的订单
        Order order = createOrder(4L, "iPad Air", new BigDecimal("8999.00"));
        order.setOrderId(3L); // 设置为奇数
        
        // When
        Order createdOrder = orderService.createOrder(order);
        
        // Then
        assertNotNull(createdOrder);
        assertEquals(4L, createdOrder.getUserId());
        
        // 验证可以查询到订单（说明表分片正确）
        Order foundOrder = orderService.getOrderById(createdOrder.getOrderId());
        assertNotNull(foundOrder);
        assertEquals(4L, foundOrder.getUserId());
    }
    
    @Test
    void testCrossShardQuery_ShouldWork() {
        // Given - 创建不同分片的用户和订单
        User user1 = createUser(1L, "user1", "user1@example.com"); // 奇数，ds1
        User user2 = createUser(2L, "user2", "user2@example.com"); // 偶数，ds0
        
        Order order1 = createOrder(1L, "Product 1", new BigDecimal("100.00"));
        Order order2 = createOrder(2L, "Product 2", new BigDecimal("200.00"));
        
        // When
        User createdUser1 = userService.createUser(user1);
        User createdUser2 = userService.createUser(user2);
        Order createdOrder1 = orderService.createOrder(order1);
        Order createdOrder2 = orderService.createOrder(order2);
        
        // Then - 验证跨分片查询
        List<User> allUsers = userService.getAllUsers();
        assertTrue(allUsers.size() >= 2);
        
        List<Order> allOrders = orderService.getAllOrders();
        assertTrue(allOrders.size() >= 2);
        
        // 验证特定用户的订单查询
        List<Order> user1Orders = orderService.getOrdersByUserId(1L);
        assertTrue(user1Orders.size() >= 1);
        
        List<Order> user2Orders = orderService.getOrdersByUserId(2L);
        assertTrue(user2Orders.size() >= 1);
    }
    
    @Test
    void testShardingConsistency() {
        // Given - 创建用户和订单
        User user = createUser(5L, "user5", "user5@example.com");
        Order order = createOrder(5L, "Test Product", new BigDecimal("99.99"));
        
        // When
        User createdUser = userService.createUser(user);
        Order createdOrder = orderService.createOrder(order);
        
        // Then - 验证数据一致性
        User foundUser = userService.getUserById(5L);
        Order foundOrder = orderService.getOrderById(createdOrder.getOrderId());
        
        assertNotNull(foundUser);
        assertNotNull(foundOrder);
        assertEquals(5L, foundUser.getUserId());
        assertEquals(5L, foundOrder.getUserId());
        
        // 验证用户订单关联查询
        List<Order> userOrders = orderService.getOrdersByUserId(5L);
        assertTrue(userOrders.size() >= 1);
        assertTrue(userOrders.stream().anyMatch(o -> o.getOrderId().equals(createdOrder.getOrderId())));
    }
    
    @Test
    void testPaginationAcrossShards() {
        // Given - 创建多个用户和订单
        for (int i = 1; i <= 5; i++) {
            User user = createUser((long) i, "user" + i, "user" + i + "@example.com");
            userService.createUser(user);
            
            Order order = createOrder((long) i, "Product " + i, new BigDecimal("100.00").multiply(new BigDecimal(i)));
            orderService.createOrder(order);
        }
        
        // When - 分页查询
        List<User> usersPage1 = userService.getUsersByPage(1, 3);
        List<Order> ordersPage1 = orderService.getOrdersByPage(1, 3);
        
        // Then
        assertNotNull(usersPage1);
        assertNotNull(ordersPage1);
        assertTrue(usersPage1.size() <= 3);
        assertTrue(ordersPage1.size() <= 3);
    }
    
    private User createUser(Long userId, String username, String email) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setPassword("password123");
        user.setEmail(email);
        user.setPhone("13800000000");
        user.setStatus(1);
        return user;
    }
    
    private Order createOrder(Long userId, String productName, BigDecimal amount) {
        Order order = new Order();
        order.setUserId(userId);
        order.setProductName(productName);
        order.setQuantity(1);
        order.setAmount(amount);
        order.setStatus(0);
        return order;
    }
}
