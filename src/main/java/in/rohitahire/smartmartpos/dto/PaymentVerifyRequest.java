package in.rohitahire.smartmartpos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentVerifyRequest {

    private Long billId;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}