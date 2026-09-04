package in.rohitahire.smartmartpos.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillItemRequest {

    private Long productId;
    private Integer quantity;
}