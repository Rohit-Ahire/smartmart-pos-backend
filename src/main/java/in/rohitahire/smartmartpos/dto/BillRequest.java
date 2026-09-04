package in.rohitahire.smartmartpos.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillRequest {

    private String customerName;
    private List<BillItemRequest> items;
}