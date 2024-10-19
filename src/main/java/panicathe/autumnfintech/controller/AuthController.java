package panicathe.autumnfintech.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import panicathe.autumnfintech.dto.ApiResponse;
import panicathe.autumnfintech.dto.user.UserDto;
import panicathe.autumnfintech.dto.user.LoginRequestDto;
import panicathe.autumnfintech.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 1. 회원가입 (POST /auth/users)
    @Operation(summary = "Register a new user", description = "Registers a new user")
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody UserDto userDto) {
        authService.register(userDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", null));
    }

    // 2. 로그인 (POST /auth/tokens)
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    @PostMapping("/tokens")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        String token = authService.login(loginRequestDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", token));
    }

    // 3. 회원탈퇴 (DELETE /auth/users)
    @Operation(summary = "Delete user", description = "Deletes a user account")
    @DeleteMapping("/users")
    public ResponseEntity<ApiResponse<String>> deleteUser(@AuthenticationPrincipal String email) {
        authService.deleteUser(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }
}

