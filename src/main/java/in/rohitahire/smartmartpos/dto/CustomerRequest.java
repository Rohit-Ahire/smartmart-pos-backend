package in.rohitahire.smartmartpos.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    private String name;
    private String phone;
    private String email;
}