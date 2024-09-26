package panicathe.autumnfintech.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import panicathe.autumnfintech.dto.user.LoginRequestDto;
import panicathe.autumnfintech.dto.user.UserDto;
import panicathe.autumnfintech.entity.User;

import panicathe.autumnfintech.jwt.JwtProvider;
import panicathe.autumnfintech.repository.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    @Transactional
    public void register(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        if(userRepository.existsByUsername(userDto.getUsername()))
            throw new IllegalArgumentException("Username already exists.");

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .isActive(true)
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }

    @Transactional
    public String login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwtProvider.create(user.getEmail(), user.getRole());
    }

    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));


//        if (userRepository.countAccountsById(user.getId()) > 0) {
//            throw new IllegalArgumentException("Cannot delete user with active accounts.");
//        }  Account 기능 생성후 수정.

        user.setActive(false); // Soft delete
        userRepository.save(user);
    }
}
