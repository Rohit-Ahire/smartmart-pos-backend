package in.rohitahire.smartmartpos.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import in.rohitahire.smartmartpos.dto.PaymentOrderResponse;
import in.rohitahire.smartmartpos.dto.PaymentVerifyRequest;
import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.entity.Payment;
import in.rohitahire.smartmartpos.repository.BillRepository;
import in.rohitahire.smartmartpos.repository.PaymentRepository;
import in.rohitahire.smartmartpos.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public PaymentOrderResponse createOrder(Long billId) throws Exception {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

        int amount = bill.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .intValue();

        JSONObject options = new JSONObject();
        options.put("amount", amount);
        options.put("currency", "INR");
        options.put("receipt", "bill_" + bill.getId());

        Order order = razorpay.orders.create(options);

        Payment payment = paymentRepository.findByBill(bill)
                .orElse(new Payment());

        payment.setBill(bill);
        payment.setAmount(bill.getTotalAmount().doubleValue());
        payment.setRazorpayOrderId(order.get("id"));
        payment.setStatus("CREATED");

        paymentRepository.save(payment);

        return PaymentOrderResponse.builder()
                .orderId(order.get("id"))
                .amount(amount)
                .currency("INR")
                .key(keyId)
                .build();
    }

    @Override
    @Transactional
    public boolean verifyPayment(PaymentVerifyRequest request) throws Exception {

        String payload = request.getRazorpayOrderId()
                + "|"
                + request.getRazorpayPaymentId();

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                keySecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        mac.init(secretKeySpec);

        String generatedSignature = bytesToHex(
                mac.doFinal(payload.getBytes(StandardCharsets.UTF_8))
        );

        if (!generatedSignature.equals(request.getRazorpaySignature())) {
            return false;
        }

        Payment payment = paymentRepository
                .findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        Bill bill = payment.getBill();
        bill.setPaymentStatus("SUCCESS");
        billRepository.save(bill);

        return true;
    }

    private String bytesToHex(byte[] bytes) {

        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}