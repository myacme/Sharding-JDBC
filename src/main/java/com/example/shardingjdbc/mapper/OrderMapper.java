package com.example.shardingjdbc.mapper;

import com.example.shardingjdbc.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {
    
    /**
     * 插入订单
     * @param order 订单信息
     * @return 影响行数
     */
    int insert(Order order);
    
    /**
     * 根据订单ID查询订单
     * @param orderId 订单ID
     * @return 订单信息
     */
    Order selectById(@Param("orderId") Long orderId);
    
    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据订单号查询订单
     * @param orderNo 订单号
     * @return 订单信息
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 查询所有订单
     * @return 订单列表
     */
    List<Order> selectAll();
    
    /**
     * 更新订单信息
     * @param order 订单信息
     * @return 影响行数
     */
    int update(Order order);
    
    /**
     * 根据订单ID删除订单
     * @param orderId 订单ID
     * @return 影响行数
     */
    int deleteById(@Param("orderId") Long orderId);
    
    /**
     * 分页查询订单
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 订单列表
     */
    List<Order> selectByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询订单总数
     * @return 订单总数
     */
    int countAll();
    
    /**
     * 根据用户ID分页查询订单
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 订单列表
     */
    List<Order> selectByUserIdAndPage(@Param("userId") Long userId, 
                                     @Param("offset") int offset, 
                                     @Param("limit") int limit);
    
    /**
     * 根据用户ID查询订单总数
     * @param userId 用户ID
     * @return 订单总数
     */
    int countByUserId(@Param("userId") Long userId);
}
