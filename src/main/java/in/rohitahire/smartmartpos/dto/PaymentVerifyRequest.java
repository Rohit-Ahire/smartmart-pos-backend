package in.rohitahire.smartmartpos.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerifyRequest {

    private Long billId;

    @JsonAlias({"razorpay_order_id", "orderId"})
    private String razorpayOrderId;

    @JsonAlias({"razorpay_payment_id", "paymentId"})
    private String razorpayPaymentId;

    @JsonAlias({"razorpay_signature", "signature"})
    private String razorpaySignature;
}