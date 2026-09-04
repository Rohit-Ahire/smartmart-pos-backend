package in.rohitahire.smartmartpos.service;

import in.rohitahire.smartmartpos.dto.LoginRequest;
import in.rohitahire.smartmartpos.dto.LoginResponse;
import in.rohitahire.smartmartpos.entity.User;
import in.rohitahire.smartmartpos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request){

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid Username"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid Password");
        }

        return new LoginResponse("Login Successful", user.getUsername());
    }
}