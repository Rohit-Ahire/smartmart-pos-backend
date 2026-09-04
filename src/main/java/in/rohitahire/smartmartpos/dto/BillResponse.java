package in.rohitahire.smartmartpos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {

    private Long id;
    private String customerName;
    private BigDecimal totalAmount;
    private String createdAt;
    private String paymentStatus;
}