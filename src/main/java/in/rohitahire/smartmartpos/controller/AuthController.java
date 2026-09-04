package in.rohitahire.smartmartpos.controller;

import in.rohitahire.smartmartpos.dto.LoginRequest;
import in.rohitahire.smartmartpos.dto.LoginResponse;
import in.rohitahire.smartmartpos.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }
}