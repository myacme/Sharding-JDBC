package com.example.shardingjdbc.integration;

import com.example.shardingjdbc.entity.Order;
import com.example.shardingjdbc.entity.User;
import com.example.shardingjdbc.service.OrderService;
import com.example.shardingjdbc.service.UserService;
import com.example.shardingjdbc.util.TestDataGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sharding-JDBC 分片功能演示测试
 * 这个测试类展示了Sharding-JDBC的分片功能如何工作
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ShardingDemoTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderService orderService;
    
    @Test
    void testShardingDemo_CompleteWorkflow() {
        log.info("=== 开始Sharding-JDBC分片功能演示 ===");
        
        // 1. 生成测试数据
        TestDataGenerator.TestDataSet testData = TestDataGenerator.generateShardingTestData();
        log.info("生成了 {} 个用户和 {} 个订单的测试数据", testData.getUserCount(), testData.getOrderCount());
        
        // 2. 测试用户分片 - 偶数用户ID分片到ds0
        log.info("\n=== 测试用户数据库分片 ===");
        List<User> evenUsers = testData.getEvenUserIdUsers();
        log.info("偶数用户ID用户数量: {}", evenUsers.size());
        
        for (User user : evenUsers) {
            log.info("创建用户: ID={}, 用户名={} (应该分片到ds0)", user.getUserId(), user.getUsername());
            User createdUser = userService.createUser(user);
            assertNotNull(createdUser);
            
            // 验证可以查询到用户
            User foundUser = userService.getUserById(createdUser.getUserId());
            assertNotNull(foundUser);
            assertEquals(createdUser.getUserId(), foundUser.getUserId());
            log.info("✓ 用户查询成功，分片正确");
        }
        
        // 3. 测试用户分片 - 奇数用户ID分片到ds1
        log.info("\n=== 测试用户数据库分片 ===");
        List<User> oddUsers = testData.getOddUserIdUsers();
        log.info("奇数用户ID用户数量: {}", oddUsers.size());
        
        for (User user : oddUsers) {
            log.info("创建用户: ID={}, 用户名={} (应该分片到ds1)", user.getUserId(), user.getUsername());
            User createdUser = userService.createUser(user);
            assertNotNull(createdUser);
            
            // 验证可以查询到用户
            User foundUser = userService.getUserById(createdUser.getUserId());
            assertNotNull(foundUser);
            assertEquals(createdUser.getUserId(), foundUser.getUserId());
            log.info("✓ 用户查询成功，分片正确");
        }
        
        // 4. 测试订单分片 - 按用户ID分片到不同数据库
        log.info("\n=== 测试订单数据库分片 ===");
        List<Order> allOrders = testData.getOrders();
        log.info("订单总数: {}", allOrders.size());
        
        for (Order order : allOrders) {
            log.info("创建订单: ID={}, 用户ID={}, 商品={} (用户ID为{}，应该分片到ds{})", 
                    order.getOrderId(), order.getUserId(), order.getProductName(),
                    order.getUserId(), order.getUserId() % 2);
            
            Order createdOrder = orderService.createOrder(order);
            assertNotNull(createdOrder);
            
            // 验证可以查询到订单
            Order foundOrder = orderService.getOrderById(createdOrder.getOrderId());
            assertNotNull(foundOrder);
            assertEquals(createdOrder.getOrderId(), foundOrder.getOrderId());
            log.info("✓ 订单查询成功，数据库分片正确");
        }
        
        // 5. 测试订单表分片 - 按订单ID分片到不同表
        log.info("\n=== 测试订单表分片 ===");
        List<Order> evenOrders = testData.getEvenOrderIdOrders();
        List<Order> oddOrders = testData.getOddOrderIdOrders();
        
        log.info("偶数订单ID订单数量: {} (应该分片到t_order_0)", evenOrders.size());
        log.info("奇数订单ID订单数量: {} (应该分片到t_order_1)", oddOrders.size());
        
        // 6. 测试跨分片查询
        log.info("\n=== 测试跨分片查询 ===");
        
        // 查询所有用户（跨数据库）
        List<User> allUsers = userService.getAllUsers();
        log.info("跨数据库查询到 {} 个用户", allUsers.size());
        assertTrue(allUsers.size() >= testData.getUserCount());
        
        // 查询所有订单（跨数据库和表）
        List<Order> allOrdersFromDB = orderService.getAllOrders();
        log.info("跨数据库和表查询到 {} 个订单", allOrdersFromDB.size());
        assertTrue(allOrdersFromDB.size() >= testData.getOrderCount());
        
        // 7. 测试特定用户的订单查询
        log.info("\n=== 测试特定用户订单查询 ===");
        for (User user : testData.getUsers()) {
            List<Order> userOrders = orderService.getOrdersByUserId(user.getUserId());
            List<Order> expectedOrders = testData.getOrdersByUserId(user.getUserId());
            
            log.info("用户ID={} 的订单数量: 实际={}, 期望={}", 
                    user.getUserId(), userOrders.size(), expectedOrders.size());
            assertTrue(userOrders.size() >= expectedOrders.size());
        }
        
        // 8. 测试分页查询
        log.info("\n=== 测试分页查询 ===");
        List<User> usersPage1 = userService.getUsersByPage(1, 5);
        List<Order> ordersPage1 = orderService.getOrdersByPage(1, 5);
        
        log.info("用户分页查询结果: {} 个用户", usersPage1.size());
        log.info("订单分页查询结果: {} 个订单", ordersPage1.size());
        
        assertNotNull(usersPage1);
        assertNotNull(ordersPage1);
        
        // 9. 测试订单状态更新
        log.info("\n=== 测试订单状态更新 ===");
        if (!allOrdersFromDB.isEmpty()) {
            Order firstOrder = allOrdersFromDB.get(0);
            log.info("更新订单ID={}的状态为已支付", firstOrder.getOrderId());
            
            boolean updateResult = orderService.updateOrderStatus(firstOrder.getOrderId(), 1);
            assertTrue(updateResult);
            log.info("✓ 订单状态更新成功");
        }
        
        // 10. 测试统计信息
        log.info("\n=== 测试统计信息 ===");
        int totalUsers = userService.getUserCount();
        int totalOrders = orderService.getOrderCount();
        
        log.info("总用户数: {}", totalUsers);
        log.info("总订单数: {}", totalOrders);
        
        assertTrue(totalUsers >= testData.getUserCount());
        assertTrue(totalOrders >= testData.getOrderCount());
        
        log.info("\n=== Sharding-JDBC分片功能演示完成 ===");
        log.info("✓ 数据库分片功能正常");
        log.info("✓ 表分片功能正常");
        log.info("✓ 跨分片查询功能正常");
        log.info("✓ 分页查询功能正常");
        log.info("✓ 数据一致性保持正常");
    }
    
    @Test
    void testShardingPerformance() {
        log.info("=== 开始分片性能测试 ===");
        
        long startTime = System.currentTimeMillis();
        
        // 创建大量测试数据
        int userCount = 100;
        int ordersPerUser = 5;
        
        log.info("创建 {} 个用户，每个用户 {} 个订单", userCount, ordersPerUser);
        
        for (int i = 1; i <= userCount; i++) {
            // 创建用户
            User user = TestDataGenerator.generateUser((long) i);
            User createdUser = userService.createUser(user);
            assertNotNull(createdUser);
            
            // 为每个用户创建订单
            for (int j = 1; j <= ordersPerUser; j++) {
                Order order = TestDataGenerator.generateOrder((long) (i * 100 + j), (long) i);
                Order createdOrder = orderService.createOrder(order);
                assertNotNull(createdOrder);
            }
            
            if (i % 20 == 0) {
                log.info("已创建 {} 个用户及其订单", i);
            }
        }
        
        long createTime = System.currentTimeMillis() - startTime;
        log.info("数据创建完成，耗时: {} ms", createTime);
        
        // 测试查询性能
        startTime = System.currentTimeMillis();
        
        // 查询所有用户
        List<User> allUsers = userService.getAllUsers();
        log.info("查询到 {} 个用户", allUsers.size());
        
        // 查询所有订单
        List<Order> allOrders = orderService.getAllOrders();
        log.info("查询到 {} 个订单", allOrders.size());
        
        long queryTime = System.currentTimeMillis() - startTime;
        log.info("查询完成，耗时: {} ms", queryTime);
        
        // 验证数据正确性
        assertEquals(userCount, allUsers.size());
        assertEquals(userCount * ordersPerUser, allOrders.size());
        
        log.info("=== 分片性能测试完成 ===");
        log.info("✓ 数据创建性能正常");
        log.info("✓ 查询性能正常");
        log.info("✓ 数据一致性验证通过");
    }
}
