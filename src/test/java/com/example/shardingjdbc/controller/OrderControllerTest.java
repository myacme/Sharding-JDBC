package com.example.shardingjdbc.controller;

import com.example.shardingjdbc.entity.Order;
import com.example.shardingjdbc.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单控制器测试
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private OrderService orderService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
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
        testOrder.setCreateTime(LocalDateTime.now());
        testOrder.setUpdateTime(LocalDateTime.now());
    }
    
    @Test
    void testCreateOrder_Success() throws Exception {
        // Given
        when(orderService.createOrder(any(Order.class))).thenReturn(testOrder);
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("订单创建成功"))
                .andExpect(jsonPath("$.data.orderId").value(1))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.productName").value("iPhone 15"));
        
        verify(orderService, times(1)).createOrder(any(Order.class));
    }
    
    @Test
    void testCreateOrder_Failure() throws Exception {
        // Given
        when(orderService.createOrder(any(Order.class))).thenThrow(new RuntimeException("创建失败"));
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("订单创建失败：创建失败"));
        
        verify(orderService, times(1)).createOrder(any(Order.class));
    }
    
    @Test
    void testGetOrderById_Success() throws Exception {
        // Given
        when(orderService.getOrderById(1L)).thenReturn(testOrder);
        
        // When & Then
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.orderId").value(1))
                .andExpect(jsonPath("$.data.orderNo").value("ORDER20231201001"));
        
        verify(orderService, times(1)).getOrderById(1L);
    }
    
    @Test
    void testGetOrderById_NotFound() throws Exception {
        // Given
        when(orderService.getOrderById(999L)).thenReturn(null);
        
        // When & Then
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("订单不存在"));
        
        verify(orderService, times(1)).getOrderById(999L);
    }
    
    @Test
    void testGetOrdersByUserId_Success() throws Exception {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrdersByUserId(1L)).thenReturn(orders);
        
        // When & Then
        mockMvc.perform(get("/api/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.total").value(1));
        
        verify(orderService, times(1)).getOrdersByUserId(1L);
    }
    
    @Test
    void testGetOrderByOrderNo_Success() throws Exception {
        // Given
        when(orderService.getOrderByOrderNo("ORDER20231201001")).thenReturn(testOrder);
        
        // When & Then
        mockMvc.perform(get("/api/orders/orderNo/ORDER20231201001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.orderNo").value("ORDER20231201001"));
        
        verify(orderService, times(1)).getOrderByOrderNo("ORDER20231201001");
    }
    
    @Test
    void testGetAllOrders_Success() throws Exception {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getAllOrders()).thenReturn(orders);
        
        // When & Then
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.total").value(1));
        
        verify(orderService, times(1)).getAllOrders();
    }
    
    @Test
    void testUpdateOrder_Success() throws Exception {
        // Given
        testOrder.setProductName("iPhone 15 Pro");
        when(orderService.updateOrder(any(Order.class))).thenReturn(testOrder);
        
        // When & Then
        mockMvc.perform(put("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("订单更新成功"))
                .andExpect(jsonPath("$.data.productName").value("iPhone 15 Pro"));
        
        verify(orderService, times(1)).updateOrder(any(Order.class));
    }
    
    @Test
    void testUpdateOrder_Failure() throws Exception {
        // Given
        when(orderService.updateOrder(any(Order.class))).thenThrow(new RuntimeException("更新失败"));
        
        // When & Then
        mockMvc.perform(put("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("订单更新失败：更新失败"));
        
        verify(orderService, times(1)).updateOrder(any(Order.class));
    }
    
    @Test
    void testDeleteOrder_Success() throws Exception {
        // Given
        when(orderService.deleteOrder(1L)).thenReturn(true);
        
        // When & Then
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("订单删除成功"));
        
        verify(orderService, times(1)).deleteOrder(1L);
    }
    
    @Test
    void testDeleteOrder_Failure() throws Exception {
        // Given
        when(orderService.deleteOrder(999L)).thenReturn(false);
        
        // When & Then
        mockMvc.perform(delete("/api/orders/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("订单删除失败"));
        
        verify(orderService, times(1)).deleteOrder(999L);
    }
    
    @Test
    void testGetOrdersByPage_Success() throws Exception {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrdersByPage(1, 10)).thenReturn(orders);
        when(orderService.getOrderCount()).thenReturn(1);
        
        // When & Then
        mockMvc.perform(get("/api/orders/page")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
        
        verify(orderService, times(1)).getOrdersByPage(1, 10);
        verify(orderService, times(1)).getOrderCount();
    }
    
    @Test
    void testGetOrdersByUserIdAndPage_Success() throws Exception {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrdersByUserIdAndPage(1L, 1, 10)).thenReturn(orders);
        when(orderService.getOrderCountByUserId(1L)).thenReturn(1);
        
        // When & Then
        mockMvc.perform(get("/api/orders/user/1/page")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.total").value(1));
        
        verify(orderService, times(1)).getOrdersByUserIdAndPage(1L, 1, 10);
        verify(orderService, times(1)).getOrderCountByUserId(1L);
    }
    
    @Test
    void testUpdateOrderStatus_Success() throws Exception {
        // Given
        when(orderService.updateOrderStatus(1L, 1)).thenReturn(true);
        
        // When & Then
        mockMvc.perform(put("/api/orders/1/status")
                .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("订单状态更新成功"));
        
        verify(orderService, times(1)).updateOrderStatus(1L, 1);
    }
    
    @Test
    void testUpdateOrderStatus_Failure() throws Exception {
        // Given
        when(orderService.updateOrderStatus(999L, 1)).thenReturn(false);
        
        // When & Then
        mockMvc.perform(put("/api/orders/999/status")
                .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("订单状态更新失败"));
        
        verify(orderService, times(1)).updateOrderStatus(999L, 1);
    }
}
