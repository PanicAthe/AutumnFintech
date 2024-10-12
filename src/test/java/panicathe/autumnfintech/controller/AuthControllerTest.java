package panicathe.autumnfintech.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import panicathe.autumnfintech.dto.user.LoginRequestDto;
import panicathe.autumnfintech.dto.user.UserDto;
import panicathe.autumnfintech.exception.GlobalExceptionHandler;
import panicathe.autumnfintech.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "wrongpassword");

        // When
        when(authService.login(any(LoginRequestDto.class))).thenThrow(new IllegalArgumentException("Invalid credentials"));

        // Then
        mockMvc.perform(post("/auth/tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void testRegisterUserEmailExists() throws Exception {
        // Given
        UserDto userDto = UserDto.builder()
                .username("testUser")
                .email("existing@example.com")
                .password("password123")
                .build();

        // When
        doThrow(new IllegalArgumentException("Email already exists.")).when(authService).register(any(UserDto.class));

        // Then
        mockMvc.perform(post("/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already exists."));
    }
}
