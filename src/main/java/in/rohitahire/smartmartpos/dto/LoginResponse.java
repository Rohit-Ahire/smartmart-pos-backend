package in.rohitahire.smartmartpos.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String username;
}