package com.J2EE.TourManagement.Controller;

import com.J2EE.TourManagement.Model.PaymentOrder;
import com.J2EE.TourManagement.Service.PaymentOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/v1/payment-orders")
public class PaymentOrderController {


    private final PaymentOrderService paymentOrderService;


    public PaymentOrderController(PaymentOrderService paymentOrderService) {
        this.paymentOrderService = paymentOrderService;
    }


    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody PaymentOrderRequest request) {
        PaymentOrder order = paymentOrderService.createPendingOrder(request.userId, request.amount);
        return ResponseEntity.ok(order);
    }


    @GetMapping("/{orderCode}")
    public ResponseEntity<?> getOrder(@PathVariable String orderCode) {
        return paymentOrderService.getOrderByCode(orderCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/{orderCode}/success")
    public ResponseEntity<?> markSuccess(@PathVariable String orderCode) {
        paymentOrderService.markOrderSuccess(orderCode);
        return ResponseEntity.ok("PAYMENT_SUCCESS");
    }


    @PostMapping("/{orderCode}/cancel")
    public ResponseEntity<?> markCanceled(@PathVariable String orderCode) {
        paymentOrderService.markOrderCanceled(orderCode);
        return ResponseEntity.ok("PAYMENT_CANCELED");
    }
}
