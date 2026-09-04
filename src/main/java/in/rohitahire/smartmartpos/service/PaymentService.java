package in.rohitahire.smartmartpos.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import in.rohitahire.smartmartpos.dto.PaymentOrderResponse;
import in.rohitahire.smartmartpos.dto.PaymentVerifyRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class PaymentService {

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    public PaymentOrderResponse createOrder(Long amount) throws Exception {

        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        int amountPaise = Math.toIntExact(amount * 100);

        JSONObject options = new JSONObject();
        options.put("amount", amountPaise);
        options.put("currency", "INR");
        options.put("receipt", "receipt_" + System.currentTimeMillis());

        Order order = client.orders.create(options);

        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setOrderId(order.get("id").toString());
        response.setAmount(amountPaise);
        response.setCurrency("INR");
        response.setKey(keyId);

        return response;
    }

    public boolean verifyPayment(PaymentVerifyRequest request) throws Exception {

        String data = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

        String generated = bytesToHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));

        return generated.equals(request.getRazorpaySignature());
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}