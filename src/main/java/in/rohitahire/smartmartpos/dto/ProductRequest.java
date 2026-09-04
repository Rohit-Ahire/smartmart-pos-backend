package in.rohitahire.smartmartpos.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private String name;
    private String category;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;
}