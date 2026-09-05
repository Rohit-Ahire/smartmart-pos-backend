package in.rohitahire.smartmartpos.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import in.rohitahire.smartmartpos.dto.PaymentOrderResponse;
import in.rohitahire.smartmartpos.dto.PaymentVerifyRequest;
import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.entity.BillItem;
import in.rohitahire.smartmartpos.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final BillRepository billRepository;

    @Transactional
    public PaymentOrderResponse createOrder(Long billId) throws Exception {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        int amountPaise = bill.getTotalAmount()
                .movePointRight(2)
                .intValueExact();

        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();
        options.put("amount", amountPaise);
        options.put("currency", "INR");
        options.put("receipt", "receipt_" + billId);

        Order order = client.orders.create(options);

        bill.setRazorpayOrderId(order.get("id").toString());
        billRepository.save(bill);

        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setOrderId(order.get("id").toString());
        response.setAmount(amountPaise);
        response.setCurrency("INR");
        response.setKey(keyId);

        return response;
    }

    @Transactional
    public boolean verifyPayment(PaymentVerifyRequest request) throws Exception {

        String data = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

        String generated = bytesToHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));

        if (!generated.equals(request.getRazorpaySignature())) {
            return false;
        }

        Bill bill = billRepository.findById(request.getBillId())
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        bill.setPaymentStatus("PAID");
        bill.setRazorpayPaymentId(request.getRazorpayPaymentId());

        if (bill.getRazorpayOrderId() == null) {
            bill.setRazorpayOrderId(request.getRazorpayOrderId());
        }

        billRepository.save(bill);

        return true;
    }

    @Transactional
    public void markAsPaid(Long billId) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        bill.setPaymentStatus("PAID");
        billRepository.save(bill);
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}