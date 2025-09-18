package com.example.shardingjdbc.controller;

import com.example.shardingjdbc.entity.Order;
import com.example.shardingjdbc.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping
    @Operation(summary = "创建订单", description = "创建新的订单")
    public ResponseEntity<Map<String, Object>> createOrder(
            @Parameter(description = "订单信息") @RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订单创建成功");
            result.put("data", createdOrder);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("创建订单失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "订单创建失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @GetMapping("/{orderId}")
    @Operation(summary = "根据订单ID查询订单", description = "通过订单ID获取订单详细信息")
    public ResponseEntity<Map<String, Object>> getOrderById(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            Map<String, Object> result = new HashMap<>();
            if (order != null) {
                result.put("success", true);
                result.put("message", "查询成功");
                result.put("data", order);
            } else {
                result.put("success", false);
                result.put("message", "订单不存在");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询订单失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID查询订单列表", description = "获取指定用户的所有订单")
    public ResponseEntity<Map<String, Object>> getOrdersByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", orders);
            result.put("total", orders.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询用户订单失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @GetMapping("/orderNo/{orderNo}")
    @Operation(summary = "根据订单号查询订单", description = "通过订单号获取订单详细信息")
    public ResponseEntity<Map<String, Object>> getOrderByOrderNo(
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        try {
            Order order = orderService.getOrderByOrderNo(orderNo);
            Map<String, Object> result = new HashMap<>();
            if (order != null) {
                result.put("success", true);
                result.put("message", "查询成功");
                result.put("data", order);
            } else {
                result.put("success", false);
                result.put("message", "订单不存在");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询订单失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @GetMapping
    @Operation(summary = "查询所有订单", description = "获取所有订单列表")
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", orders);
            result.put("total", orders.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询所有订单失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @PutMapping("/{orderId}")
    @Operation(summary = "更新订单信息", description = "修改订单的详细信息")
    public ResponseEntity<Map<String, Object>> updateOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "订单信息") @RequestBody Order order) {
        try {
            order.setOrderId(orderId);
            Order updatedOrder = orderService.updateOrder(order);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订单更新成功");
            result.put("data", updatedOrder);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("更新订单失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "订单更新失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @DeleteMapping("/{orderId}")
    @Operation(summary = "删除订单", description = "根据订单ID删除订单")
    public ResponseEntity<Map<String, Object>> deleteOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        try {
            boolean success = orderService.deleteOrder(orderId);
            Map<String, Object> result = new HashMap<>();
            if (success) {
                result.put("success", true);
                result.put("message", "订单删除成功");
            } else {
                result.put("success", false);
                result.put("message", "订单删除失败");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("删除订单失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "订单删除失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @GetMapping("/page")
    @Operation(summary = "分页查询订单", description = "分页获取订单列表")
    public ResponseEntity<Map<String, Object>> getOrdersByPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<Order> orders = orderService.getOrdersByPage(pageNum, pageSize);
            int total = orderService.getOrderCount();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", orders);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            result.put("total", total);
            result.put("totalPages", (total + pageSize - 1) / pageSize);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("分页查询订单失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @GetMapping("/user/{userId}/page")
    @Operation(summary = "根据用户ID分页查询订单", description = "分页获取指定用户的订单列表")
    public ResponseEntity<Map<String, Object>> getOrdersByUserIdAndPage(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<Order> orders = orderService.getOrdersByUserIdAndPage(userId, pageNum, pageSize);
            int total = orderService.getOrderCountByUserId(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", orders);
            result.put("userId", userId);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            result.put("total", total);
            result.put("totalPages", (total + pageSize - 1) / pageSize);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("分页查询用户订单失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @PutMapping("/{orderId}/status")
    @Operation(summary = "更新订单状态", description = "修改订单的状态")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "订单状态") @RequestParam Integer status) {
        try {
            boolean success = orderService.updateOrderStatus(orderId, status);
            Map<String, Object> result = new HashMap<>();
            if (success) {
                result.put("success", true);
                result.put("message", "订单状态更新成功");
            } else {
                result.put("success", false);
                result.put("message", "订单状态更新失败");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("更新订单状态失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "订单状态更新失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
