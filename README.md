# Sharding-JDBC Demo

这是一个使用Spring Boot、MyBatis、Swagger和Sharding-JDBC的示例程序，演示了数据库分片的基本用法。

## 项目结构

```
sharding-JDBC-demo/
├── src/main/java/com/example/shardingjdbc/
│   ├── ShardingJdbcDemoApplication.java    # 主启动类
│   ├── config/
│   │   └── SwaggerConfig.java              # Swagger配置
│   ├── controller/
│   │   ├── UserController.java             # 用户控制器
│   │   └── OrderController.java            # 订单控制器
│   ├── entity/
│   │   ├── User.java                       # 用户实体
│   │   └── Order.java                      # 订单实体
│   ├── mapper/
│   │   ├── UserMapper.java                 # 用户Mapper接口
│   │   └── OrderMapper.java                # 订单Mapper接口
│   └── service/
│       ├── UserService.java                # 用户服务接口
│       ├── OrderService.java               # 订单服务接口
│       └── impl/
│           ├── UserServiceImpl.java        # 用户服务实现
│           └── OrderServiceImpl.java       # 订单服务实现
├── src/main/resources/
│   ├── application.yml                     # 应用配置
│   ├── mapper/
│   │   ├── UserMapper.xml                  # 用户Mapper XML
│   │   └── OrderMapper.xml                 # 订单Mapper XML
│   └── sql/
│       └── init.sql                        # 数据库初始化脚本
└── pom.xml                                 # Maven配置
```

## 分片规则

### 数据库分片
- **用户表(t_user)**: 按`user_id`取模分片到2个数据库
  - `user_id % 2 == 0` → `ds0` (sharding_db_0)
  - `user_id % 2 == 1` → `ds1` (sharding_db_1)

- **订单表(t_order)**: 按`user_id`取模分片到2个数据库
  - `user_id % 2 == 0` → `ds0` (sharding_db_0)
  - `user_id % 2 == 1` → `ds1` (sharding_db_1)

### 表分片
- **订单表**: 按`order_id`取模分片到2个表
  - `order_id % 2 == 0` → `t_order_0`
  - `order_id % 2 == 1` → `t_order_1`

## 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+ 或 MySQL 8.0+

## 快速开始

### 1. 数据库准备

1. 创建MySQL数据库：
```sql
CREATE DATABASE sharding_db_0;
CREATE DATABASE sharding_db_1;
```

2. 执行初始化脚本：
```bash
mysql -u root -p sharding_db_0 < src/main/resources/sql/init.sql
mysql -u root -p sharding_db_1 < src/main/resources/sql/init.sql
```

### 2. 修改配置

编辑 `src/main/resources/application.yml` 文件，修改数据库连接信息：

```yaml
spring:
  shardingsphere:
    datasource:
      ds0:
        jdbc-url: jdbc:mysql://localhost:3306/sharding_db_0?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
        username: root
        password: 你的密码
      ds1:
        jdbc-url: jdbc:mysql://localhost:3306/sharding_db_1?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
        username: root
        password: 你的密码
```

### 3. 运行应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

### 4. 访问接口

- **Swagger UI**: http://localhost:8080/swagger-ui/
- **API文档**: http://localhost:8080/v2/api-docs

## API接口

### 用户管理
- `POST /api/users` - 创建用户
- `GET /api/users/{userId}` - 根据ID查询用户
- `GET /api/users/username/{username}` - 根据用户名查询用户
- `GET /api/users` - 查询所有用户
- `PUT /api/users/{userId}` - 更新用户
- `DELETE /api/users/{userId}` - 删除用户
- `GET /api/users/page` - 分页查询用户

### 订单管理
- `POST /api/orders` - 创建订单
- `GET /api/orders/{orderId}` - 根据ID查询订单
- `GET /api/orders/user/{userId}` - 根据用户ID查询订单
- `GET /api/orders/orderNo/{orderNo}` - 根据订单号查询订单
- `GET /api/orders` - 查询所有订单
- `PUT /api/orders/{orderId}` - 更新订单
- `DELETE /api/orders/{orderId}` - 删除订单
- `GET /api/orders/page` - 分页查询订单
- `GET /api/orders/user/{userId}/page` - 根据用户ID分页查询订单
- `PUT /api/orders/{orderId}/status` - 更新订单状态

## 测试分片效果

### 1. 创建用户测试分片
```bash
# 创建用户ID为偶数的用户（会分片到ds0）
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"test2","password":"123456","email":"test2@example.com","phone":"13800000002"}'

# 创建用户ID为奇数的用户（会分片到ds1）
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"test1","password":"123456","email":"test1@example.com","phone":"13800000001"}'
```

### 2. 创建订单测试分片
```bash
# 为用户ID为2创建订单（会分片到ds0的某个表）
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":2,"productName":"测试商品","quantity":1,"amount":99.99}'

# 为用户ID为1创建订单（会分片到ds1的某个表）
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"productName":"测试商品2","quantity":2,"amount":199.98}'
```

### 3. 查询测试
```bash
# 查询用户（会自动路由到正确的数据库）
curl http://localhost:8080/api/users/1
curl http://localhost:8080/api/users/2

# 查询订单（会自动路由到正确的数据库和表）
curl http://localhost:8080/api/orders/1
curl http://localhost:8080/api/orders/2
```

## 注意事项

1. **分片键选择**: 确保分片键的选择能够均匀分布数据
2. **跨库查询**: 避免跨库的复杂查询，会影响性能
3. **事务处理**: 跨库事务需要特殊处理
4. **数据一致性**: 分片后需要特别注意数据一致性
5. **监控和日志**: 建议开启SQL日志监控分片效果

## 技术栈

- **Spring Boot 2.7.18** - 应用框架
- **MyBatis 2.3.1** - ORM框架
- **Sharding-JDBC 5.3.2** - 分片中间件
- **MySQL 8.0** - 数据库
- **Swagger 3.0.0** - API文档
- **Lombok** - 代码简化
- **HikariCP** - 连接池

## 许可证

MIT License
