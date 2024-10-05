package panicathe.autumnfintech.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import panicathe.autumnfintech.dto.ApiResponse;
import panicathe.autumnfintech.dto.user.UserDto;
import panicathe.autumnfintech.dto.user.LoginRequestDto;
import panicathe.autumnfintech.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Registers a new user and returns a JWT token")
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody UserDto userDto) {
        authService.register(userDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", null));
    }

    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    @PostMapping("/tokens")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        String token = authService.login(loginRequestDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", token));
    }

    @Operation(summary = "Delete user", description = "Deletes a user account")
    @DeleteMapping("/users/delete")
    public ResponseEntity<ApiResponse<String>> deleteUser(@AuthenticationPrincipal String email) {
        authService.deleteUser(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }

}
