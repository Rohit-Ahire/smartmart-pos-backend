package in.rohitahire.smartmartpos.dto;

import lombok.Data;

@Data
public class PaymentOrderResponse {

    private String orderId;
    private Integer amount;
    private String currency;
    private String key;
}