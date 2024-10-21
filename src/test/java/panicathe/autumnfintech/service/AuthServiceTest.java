package panicathe.autumnfintech.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import panicathe.autumnfintech.dto.user.LoginRequestDto;
import panicathe.autumnfintech.dto.user.UserDto;
import panicathe.autumnfintech.entity.User;
import panicathe.autumnfintech.exception.custom.*;
import panicathe.autumnfintech.jwt.JwtProvider;
import panicathe.autumnfintech.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .password("encoded_password")
                .isActive(true)
                .role("ROLE_USER")
                .build();
    }

    // 1. 회원가입 성공 테스트
    @Test
    void register_success() {
        // Given
        UserDto userDto = UserDto.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded_password");

        // When
        authService.register(userDto);

        // Then
        verify(userRepository, times(1)).save(any(User.class));

        // 추가: 패스워드가 인코딩되었는지 확인
        verify(passwordEncoder, times(1)).encode("password");
    }

    @Test
    void register_emailAlreadyExists_throwsException() {
        // Given
        UserDto userDto = UserDto.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Then
        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(userDto));
    }

    // 2. 로그인 성공 테스트
    @Test
    void login_success() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "password");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", testUser.getPassword())).thenReturn(true);
        when(jwtProvider.create(testUser.getEmail(), testUser.getRole())).thenReturn("jwt_token");

        // When
        String token = authService.login(loginRequestDto);

        // Then
        assertEquals("jwt_token", token);

        // 추가: JWT 토큰 생성이 호출되었는지 확인
        verify(jwtProvider, times(1)).create(testUser.getEmail(), testUser.getRole());
    }

    @Test
    void login_invalidCredentials_throwsException() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "wrong_password");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong_password", testUser.getPassword())).thenReturn(false);

        // Then
        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequestDto));
    }

    // 3. 유저 삭제 성공 테스트
    @Test
    void deleteUser_success() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.countAccountsById(1L)).thenReturn(0);

        // When
        authService.deleteUser("test@example.com");

        // Then
        assertFalse(testUser.isActive());
        assertThrows(UserNotFoundException.class, () -> authService.deleteUser("nonexistent@example.com"));
    }

    @Test
    void deleteUser_withActiveAccounts_throwsException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.countAccountsById(1L)).thenReturn(1);  // 활성 계좌가 있을 때

        // Then
        assertThrows(UserHasActiveAccountsException.class, () -> authService.deleteUser("test@example.com"));
    }

    @Test
    void deleteUser_userNotFound_throwsException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> authService.deleteUser("nonexistent@example.com"));
    }
}
