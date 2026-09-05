package in.rohitahire.smartmartpos.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private long billCount;
    private java.math.BigDecimal totalSpent;
    private String lastVisit;
}