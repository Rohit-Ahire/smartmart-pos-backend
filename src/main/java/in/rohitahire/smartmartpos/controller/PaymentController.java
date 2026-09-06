package in.rohitahire.smartmartpos.controller;

import in.rohitahire.smartmartpos.dto.PaymentOrderResponse;
import in.rohitahire.smartmartpos.dto.PaymentVerifyRequest;
import in.rohitahire.smartmartpos.service.PaymentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://*.vercel.app"
})
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @RequestBody CreateOrderRequest request) throws Exception {

        if (request.getBillId() == null) {
            throw new RuntimeException("billId is required");
        }

        return ResponseEntity.ok(
                paymentService.createOrder(request.getBillId())
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @RequestBody PaymentVerifyRequest request) throws Exception {

        boolean verified = paymentService.verifyPayment(request);

        if (verified) {
            return ResponseEntity.ok("Payment verified successfully");
        }

        return ResponseEntity.badRequest().body("Invalid payment signature");
    }

    @Data
    public static class CreateOrderRequest {
        private Long billId;
    }
}