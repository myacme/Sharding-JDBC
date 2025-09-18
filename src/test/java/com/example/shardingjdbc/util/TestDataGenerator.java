package com.example.shardingjdbc.util;

import com.example.shardingjdbc.entity.Order;
import com.example.shardingjdbc.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 测试数据生成工具
 */
public class TestDataGenerator {
    
    private static final Random random = new Random();
    private static final String[] PRODUCT_NAMES = {
        "iPhone 15", "iPhone 15 Pro", "MacBook Pro", "MacBook Air", "iPad Air", "iPad Pro",
        "Samsung Galaxy S24", "Samsung Galaxy Note", "Dell XPS", "Dell Inspiron",
        "Surface Pro", "Surface Laptop", "ThinkPad X1", "ThinkPad T14",
        "华为 Mate 60", "华为 P60", "小米 14", "小米 13", "OPPO Find X7", "vivo X100"
    };
    
    private static final String[] FIRST_NAMES = {
        "张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴",
        "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗"
    };
    
    private static final String[] LAST_NAMES = {
        "伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军",
        "洋", "勇", "艳", "杰", "娟", "涛", "明", "超", "秀兰", "霞"
    };
    
    /**
     * 生成测试用户
     */
    public static User generateUser(Long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(generateUsername());
        user.setPassword("password123");
        user.setEmail(generateEmail());
        user.setPhone(generatePhone());
        user.setStatus(random.nextInt(2)); // 0或1
        user.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(365)));
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }
    
    /**
     * 生成测试订单
     */
    public static Order generateOrder(Long orderId, Long userId) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setProductName(PRODUCT_NAMES[random.nextInt(PRODUCT_NAMES.length)]);
        order.setQuantity(random.nextInt(5) + 1); // 1-5
        order.setAmount(generateAmount());
        order.setStatus(random.nextInt(5)); // 0-4
        order.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(30)));
        order.setUpdateTime(LocalDateTime.now());
        order.setRemark("测试订单备注 - " + UUID.randomUUID().toString().substring(0, 8));
        return order;
    }
    
    /**
     * 生成多个测试用户
     */
    public static List<User> generateUsers(int count, Long startUserId) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(generateUser(startUserId + i));
        }
        return users;
    }
    
    /**
     * 生成多个测试订单
     */
    public static List<Order> generateOrders(int count, Long startOrderId, Long userId) {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            orders.add(generateOrder(startOrderId + i, userId));
        }
        return orders;
    }
    
    /**
     * 生成分片测试数据
     * 创建用户ID为奇数和偶数的用户，以及对应的订单
     */
    public static TestDataSet generateShardingTestData() {
        TestDataSet dataSet = new TestDataSet();
        
        // 生成偶数用户ID的用户（分片到ds0）
        for (int i = 0; i < 5; i++) {
            Long userId = (long) (i * 2 + 2); // 2, 4, 6, 8, 10
            User user = generateUser(userId);
            dataSet.addUser(user);
            
            // 为每个用户生成1-3个订单
            int orderCount = random.nextInt(3) + 1;
            for (int j = 0; j < orderCount; j++) {
                Long orderId = (long) (i * 10 + j * 2 + 2); // 偶数订单ID
                Order order = generateOrder(orderId, userId);
                dataSet.addOrder(order);
            }
        }
        
        // 生成奇数用户ID的用户（分片到ds1）
        for (int i = 0; i < 5; i++) {
            Long userId = (long) (i * 2 + 1); // 1, 3, 5, 7, 9
            User user = generateUser(userId);
            dataSet.addUser(user);
            
            // 为每个用户生成1-3个订单
            int orderCount = random.nextInt(3) + 1;
            for (int j = 0; j < orderCount; j++) {
                Long orderId = (long) (i * 10 + j * 2 + 1); // 奇数订单ID
                Order order = generateOrder(orderId, userId);
                dataSet.addOrder(order);
            }
        }
        
        return dataSet;
    }
    
    /**
     * 生成用户名
     */
    private static String generateUsername() {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return firstName + lastName + random.nextInt(9999);
    }
    
    /**
     * 生成邮箱
     */
    private static String generateEmail() {
        String[] domains = {"gmail.com", "qq.com", "163.com", "126.com", "sina.com", "hotmail.com"};
        String domain = domains[random.nextInt(domains.length)];
        return "user" + random.nextInt(999999) + "@" + domain;
    }
    
    /**
     * 生成手机号
     */
    private static String generatePhone() {
        String[] prefixes = {"138", "139", "150", "151", "152", "188", "189"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        return prefix + String.format("%08d", random.nextInt(100000000));
    }
    
    /**
     * 生成订单号
     */
    private static String generateOrderNo() {
        return "ORDER" + System.currentTimeMillis() + String.format("%03d", random.nextInt(1000));
    }
    
    /**
     * 生成订单金额
     */
    private static BigDecimal generateAmount() {
        double amount = random.nextDouble() * 10000 + 100; // 100-10100
        return BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 测试数据集
     */
    public static class TestDataSet {
        private List<User> users = new ArrayList<>();
        private List<Order> orders = new ArrayList<>();
        
        public void addUser(User user) {
            users.add(user);
        }
        
        public void addOrder(Order order) {
            orders.add(order);
        }
        
        public List<User> getUsers() {
            return users;
        }
        
        public List<Order> getOrders() {
            return orders;
        }
        
        public int getUserCount() {
            return users.size();
        }
        
        public int getOrderCount() {
            return orders.size();
        }
        
        public List<User> getEvenUserIdUsers() {
            return users.stream()
                    .filter(user -> user.getUserId() % 2 == 0)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        public List<User> getOddUserIdUsers() {
            return users.stream()
                    .filter(user -> user.getUserId() % 2 == 1)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        public List<Order> getOrdersByUserId(Long userId) {
            return orders.stream()
                    .filter(order -> order.getUserId().equals(userId))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        public List<Order> getEvenOrderIdOrders() {
            return orders.stream()
                    .filter(order -> order.getOrderId() % 2 == 0)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        public List<Order> getOddOrderIdOrders() {
            return orders.stream()
                    .filter(order -> order.getOrderId() % 2 == 1)
                    .collect(java.util.stream.Collectors.toList());
        }
    }
}
