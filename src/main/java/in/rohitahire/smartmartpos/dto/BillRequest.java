package in.rohitahire.smartmartpos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillRequest {

    private String customerName;

    // CASH or ONLINE
    private String paymentMethod;

    private List<ProductRequest> items;
}