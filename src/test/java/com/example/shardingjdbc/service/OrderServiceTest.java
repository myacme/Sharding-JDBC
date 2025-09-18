package com.example.shardingjdbc.service;

import com.example.shardingjdbc.entity.Order;
import com.example.shardingjdbc.mapper.OrderMapper;
import com.example.shardingjdbc.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderMapper orderMapper;
    
    @InjectMocks
    private OrderServiceImpl orderService;
    
    private Order testOrder;
    
    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setOrderId(1L);
        testOrder.setUserId(1L);
        testOrder.setOrderNo("ORDER20231201001");
        testOrder.setProductName("iPhone 15");
        testOrder.setQuantity(1);
        testOrder.setAmount(new BigDecimal("5999.00"));
        testOrder.setStatus(0);
    }
    
    @Test
    void testCreateOrder_Success() {
        // Given
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        
        // When
        Order result = orderService.createOrder(testOrder);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("iPhone 15", result.getProductName());
        assertEquals(0, result.getStatus());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        assertNotNull(result.getOrderNo());
        
        verify(orderMapper, times(1)).insert(any(Order.class));
    }
    
    @Test
    void testCreateOrder_WithCustomOrderNo() {
        // Given
        testOrder.setOrderNo("CUSTOM_ORDER_001");
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        
        // When
        Order result = orderService.createOrder(testOrder);
        
        // Then
        assertNotNull(result);
        assertEquals("CUSTOM_ORDER_001", result.getOrderNo());
        
        verify(orderMapper, times(1)).insert(any(Order.class));
    }
    
    @Test
    void testCreateOrder_Failure() {
        // Given
        when(orderMapper.insert(any(Order.class))).thenReturn(0);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(testOrder);
        });
        
        verify(orderMapper, times(1)).insert(any(Order.class));
    }
    
    @Test
    void testGetOrderById_Success() {
        // Given
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        
        // When
        Order result = orderService.getOrderById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals("ORDER20231201001", result.getOrderNo());
        
        verify(orderMapper, times(1)).selectById(1L);
    }
    
    @Test
    void testGetOrderById_NotFound() {
        // Given
        when(orderMapper.selectById(999L)).thenReturn(null);
        
        // When
        Order result = orderService.getOrderById(999L);
        
        // Then
        assertNull(result);
        verify(orderMapper, times(1)).selectById(999L);
    }
    
    @Test
    void testGetOrdersByUserId_Success() {
        // Given
        List<Order> orders = Arrays.asList(testOrder, createAnotherOrder());
        when(orderMapper.selectByUserId(1L)).thenReturn(orders);
        
        // When
        List<Order> result = orderService.getOrdersByUserId(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getUserId());
        
        verify(orderMapper, times(1)).selectByUserId(1L);
    }
    
    @Test
    void testGetOrderByOrderNo_Success() {
        // Given
        when(orderMapper.selectByOrderNo("ORDER20231201001")).thenReturn(testOrder);
        
        // When
        Order result = orderService.getOrderByOrderNo("ORDER20231201001");
        
        // Then
        assertNotNull(result);
        assertEquals("ORDER20231201001", result.getOrderNo());
        
        verify(orderMapper, times(1)).selectByOrderNo("ORDER20231201001");
    }
    
    @Test
    void testGetAllOrders_Success() {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderMapper.selectAll()).thenReturn(orders);
        
        // When
        List<Order> result = orderService.getAllOrders();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(orderMapper, times(1)).selectAll();
    }
    
    @Test
    void testUpdateOrder_Success() {
        // Given
        testOrder.setProductName("iPhone 15 Pro");
        when(orderMapper.update(any(Order.class))).thenReturn(1);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        
        // When
        Order result = orderService.updateOrder(testOrder);
        
        // Then
        assertNotNull(result);
        assertEquals("iPhone 15 Pro", result.getProductName());
        assertNotNull(result.getUpdateTime());
        
        verify(orderMapper, times(1)).update(any(Order.class));
        verify(orderMapper, times(1)).selectById(1L);
    }
    
    @Test
    void testUpdateOrder_Failure() {
        // Given
        when(orderMapper.update(any(Order.class))).thenReturn(0);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            orderService.updateOrder(testOrder);
        });
        
        verify(orderMapper, times(1)).update(any(Order.class));
    }
    
    @Test
    void testDeleteOrder_Success() {
        // Given
        when(orderMapper.deleteById(1L)).thenReturn(1);
        
        // When
        boolean result = orderService.deleteOrder(1L);
        
        // Then
        assertTrue(result);
        verify(orderMapper, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteOrder_Failure() {
        // Given
        when(orderMapper.deleteById(999L)).thenReturn(0);
        
        // When
        boolean result = orderService.deleteOrder(999L);
        
        // Then
        assertFalse(result);
        verify(orderMapper, times(1)).deleteById(999L);
    }
    
    @Test
    void testGetOrdersByPage_Success() {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderMapper.selectByPage(0, 10)).thenReturn(orders);
        when(orderMapper.countAll()).thenReturn(1);
        
        // When
        List<Order> result = orderService.getOrdersByPage(1, 10);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(orderMapper, times(1)).selectByPage(0, 10);
    }
    
    @Test
    void testGetOrderCount_Success() {
        // Given
        when(orderMapper.countAll()).thenReturn(5);
        
        // When
        int result = orderService.getOrderCount();
        
        // Then
        assertEquals(5, result);
        verify(orderMapper, times(1)).countAll();
    }
    
    @Test
    void testGetOrdersByUserIdAndPage_Success() {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderMapper.selectByUserIdAndPage(1L, 0, 10)).thenReturn(orders);
        when(orderMapper.countByUserId(1L)).thenReturn(1);
        
        // When
        List<Order> result = orderService.getOrdersByUserIdAndPage(1L, 1, 10);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(orderMapper, times(1)).selectByUserIdAndPage(1L, 0, 10);
    }
    
    @Test
    void testGetOrderCountByUserId_Success() {
        // Given
        when(orderMapper.countByUserId(1L)).thenReturn(3);
        
        // When
        int result = orderService.getOrderCountByUserId(1L);
        
        // Then
        assertEquals(3, result);
        verify(orderMapper, times(1)).countByUserId(1L);
    }
    
    @Test
    void testUpdateOrderStatus_Success() {
        // Given
        when(orderMapper.update(any(Order.class))).thenReturn(1);
        
        // When
        boolean result = orderService.updateOrderStatus(1L, 1);
        
        // Then
        assertTrue(result);
        verify(orderMapper, times(1)).update(any(Order.class));
    }
    
    @Test
    void testUpdateOrderStatus_Failure() {
        // Given
        when(orderMapper.update(any(Order.class))).thenReturn(0);
        
        // When
        boolean result = orderService.updateOrderStatus(1L, 1);
        
        // Then
        assertFalse(result);
        verify(orderMapper, times(1)).update(any(Order.class));
    }
    
    private Order createAnotherOrder() {
        Order order = new Order();
        order.setOrderId(2L);
        order.setUserId(1L);
        order.setOrderNo("ORDER20231201002");
        order.setProductName("MacBook Pro");
        order.setQuantity(1);
        order.setAmount(new BigDecimal("12999.00"));
        order.setStatus(1);
        return order;
    }
}
