package in.rohitahire.smartmartpos.config;

import in.rohitahire.smartmartpos.entity.User;
import in.rohitahire.smartmartpos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args){

        User admin = userRepository.findByUsername("admin").orElse(null);

        if (admin == null) {

            admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ADMIN")
                    .build();

            userRepository.save(admin);

            System.out.println("Admin User Created");

        } else if (!passwordEncoder.matches("admin123", admin.getPassword())) {

            admin.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(admin);

            System.out.println("Admin password reset to default");
        }
    }
}