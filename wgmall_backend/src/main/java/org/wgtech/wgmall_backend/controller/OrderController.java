package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.CreateOrderRequest;
import org.wgtech.wgmall_backend.dto.RefundRequest;
import org.wgtech.wgmall_backend.entity.Order;
import org.wgtech.wgmall_backend.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "包含订单查询、创建、退款等操作")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "根据用户ID查询订单列表（所有人）", description = "返回该用户所有的下单记录")
    @GetMapping("/user/{userId}")
    public List<Order> getUserOrders(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    @Operation(summary = "用户提交退款申请（用户）", description = "设置订单为退款中状态，并记录退款理由")
    @PostMapping("/{orderId}/refund")
    public String refundOrder(
            @Parameter(description = "订单ID", required = true)
            @PathVariable Long orderId,
            @RequestBody RefundRequest request) {
        orderService.requestRefund(orderId, request.getReason());
        return "退款申请已提交";
    }

    @Operation(summary = "创建新订单，用户点击购买按钮后（用户）", description = "前端提交下单请求，包含用户ID、商品ID、数量、总金额")
    @PostMapping("/create")
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(
                request.getUserId(),
                request.getProductId(),
                request.getQuantity(),
                request.getTotalAmount()
        );
    }
}
