package in.rohitahire.smartmartpos.controller;

import in.rohitahire.smartmartpos.dto.PaymentOrderResponse;
import in.rohitahire.smartmartpos.dto.PaymentVerifyRequest;
import in.rohitahire.smartmartpos.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Long> request) {
        try {
            Long billId = request.get("billId");

            if (billId == null) {
                return ResponseEntity.badRequest().body("billId is required");
            }

            PaymentOrderResponse response = paymentService.createOrder(billId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerifyRequest request) {
        try {
            boolean success = paymentService.verifyPayment(request);

            Map<String, String> response = new HashMap<>();

            if (success) {
                response.put("status", "success");
                return ResponseEntity.ok(response);
            }

            response.put("status", "failed");
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.status(500).body(error);
        }
    }
}