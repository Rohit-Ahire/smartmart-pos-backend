package in.rohitahire.smartmartpos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {

    private String orderId;
    private int amount;
    private String currency;
    private String key;
}