# Sharding-JDBC 测试说明

## 测试结构

```
src/test/java/com/example/shardingjdbc/
├── controller/                    # Controller层测试
│   ├── UserControllerTest.java   # 用户控制器测试
│   └── OrderControllerTest.java  # 订单控制器测试
├── service/                      # Service层测试
│   ├── UserServiceTest.java     # 用户服务单元测试
│   └── OrderServiceTest.java    # 订单服务单元测试
├── integration/                  # 集成测试
│   ├── ShardingIntegrationTest.java  # 分片功能集成测试
│   └── ShardingDemoTest.java    # 分片功能演示测试
└── util/                        # 测试工具
    └── TestDataGenerator.java   # 测试数据生成器
```

## 测试类型

### 1. 单元测试 (Unit Tests)
- **UserServiceTest**: 测试用户服务的所有方法
- **OrderServiceTest**: 测试订单服务的所有方法
- **UserControllerTest**: 测试用户控制器的REST API
- **OrderControllerTest**: 测试订单控制器的REST API

### 2. 集成测试 (Integration Tests)
- **ShardingIntegrationTest**: 测试Sharding-JDBC分片功能
- **ShardingDemoTest**: 演示分片功能的完整工作流程

### 3. 测试工具
- **TestDataGenerator**: 生成测试数据，支持分片测试

## 运行测试

### 运行所有测试
```bash
mvn test
```

### 运行特定测试类
```bash
# 运行单元测试
mvn test -Dtest=UserServiceTest
mvn test -Dtest=OrderServiceTest

# 运行集成测试
mvn test -Dtest=ShardingIntegrationTest
mvn test -Dtest=ShardingDemoTest

# 运行Controller测试
mvn test -Dtest=UserControllerTest
mvn test -Dtest=OrderControllerTest
```

### 运行特定测试方法
```bash
mvn test -Dtest=UserServiceTest#testCreateUser_Success
mvn test -Dtest=ShardingIntegrationTest#testUserSharding_EvenUserId_ShouldGoToDs0
```

## 测试环境准备

### 1. 数据库准备
```sql
-- 创建测试数据库
CREATE DATABASE sharding_db_0_test;
CREATE DATABASE sharding_db_1_test;

-- 执行初始化脚本（使用测试数据库）
mysql -u root -p sharding_db_0_test < src/main/resources/sql/init.sql
mysql -u root -p sharding_db_1_test < src/main/resources/sql/init.sql
```

### 2. 修改测试配置
编辑 `src/test/resources/application-test.yml` 文件，修改数据库连接信息：

```yaml
spring:
  shardingsphere:
    datasource:
      ds0:
        jdbc-url: jdbc:mysql://localhost:3306/sharding_db_0_test?...
        username: root
        password: 你的密码
      ds1:
        jdbc-url: jdbc:mysql://localhost:3306/sharding_db_1_test?...
        username: root
        password: 你的密码
```

## 测试内容说明

### 分片功能测试

#### 1. 用户分片测试
- **偶数用户ID** → 分片到 `ds0` (sharding_db_0_test)
- **奇数用户ID** → 分片到 `ds1` (sharding_db_1_test)

#### 2. 订单分片测试
- **数据库分片**: 按 `user_id` 分片
  - 偶数 `user_id` → `ds0`
  - 奇数 `user_id` → `ds1`
- **表分片**: 按 `order_id` 分片
  - 偶数 `order_id` → `t_order_0`
  - 奇数 `order_id` → `t_order_1`

#### 3. 跨分片查询测试
- 查询所有用户（跨数据库）
- 查询所有订单（跨数据库和表）
- 特定用户的订单查询
- 分页查询

### 性能测试
- 大量数据创建测试
- 查询性能测试
- 分片路由性能测试

## 测试数据

### TestDataGenerator 功能
- 生成随机用户数据
- 生成随机订单数据
- 支持分片测试数据生成
- 提供分片验证工具方法

### 测试数据特点
- 用户ID按奇偶性分布，便于测试分片
- 订单ID按奇偶性分布，便于测试表分片
- 包含中文姓名、邮箱、手机号等真实数据
- 订单包含多种商品类型和价格范围

## 测试验证点

### 1. 分片正确性
- 数据按分片规则正确分布
- 查询能正确路由到对应分片
- 跨分片查询结果正确

### 2. 数据一致性
- 创建的数据能正确查询
- 更新操作正确执行
- 删除操作正确执行

### 3. 性能表现
- 分片不影响查询性能
- 大量数据操作正常
- 分页查询性能良好

### 4. API功能
- REST API响应正确
- 错误处理正常
- 参数验证正确

## 注意事项

1. **测试数据库**: 使用独立的测试数据库，避免影响生产数据
2. **事务回滚**: 集成测试使用 `@Transactional` 自动回滚
3. **测试隔离**: 每个测试方法独立运行，互不影响
4. **数据清理**: 测试完成后自动清理数据
5. **日志输出**: 测试过程中会输出详细的SQL日志，便于调试

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 验证数据库连接配置
   - 确认数据库用户权限

2. **分片规则不生效**
   - 检查Sharding-JDBC配置
   - 验证分片键设置
   - 查看SQL日志输出

3. **测试数据冲突**
   - 清理测试数据库
   - 重新运行测试
   - 检查测试隔离设置

4. **性能测试超时**
   - 调整测试数据量
   - 检查数据库性能
   - 优化测试配置
