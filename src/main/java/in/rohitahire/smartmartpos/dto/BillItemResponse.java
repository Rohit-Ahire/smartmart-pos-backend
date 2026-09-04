package in.rohitahire.smartmartpos.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillItemResponse {

    private String productName;
    private Integer quantity;
    private BigDecimal price;
}