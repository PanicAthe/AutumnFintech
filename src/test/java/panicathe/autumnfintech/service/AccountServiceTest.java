package panicathe.autumnfintech.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import panicathe.autumnfintech.dto.account.AccountDto;
import panicathe.autumnfintech.dto.account.CreateAccountDto;
import panicathe.autumnfintech.entity.Account;
import panicathe.autumnfintech.entity.User;
import panicathe.autumnfintech.repository.AccountRepository;
import panicathe.autumnfintech.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test user and account
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .build();

        testAccount = Account.builder()
                .id(1L)
                .accountNumber("100000001")
                .balance(BigDecimal.ZERO)
                .transferLimit(BigDecimal.valueOf(1000))
                .user(testUser)
                .isActive(true)  // 기본 활성 상태로 설정
                .build();
    }

    @Test
    void createAccount_success() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        // When
        accountService.createAccount("test@example.com", CreateAccountDto.builder()
                .transferLimit(BigDecimal.valueOf(500)).build());

        // Then
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void createAccount_userNotFound_throwsException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () -> accountService.createAccount("nonexistent@example.com", CreateAccountDto.builder()
                .transferLimit(BigDecimal.valueOf(500)).build()));
    }

    @Test
    void getOwnAccountDetails_success() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testAccount));

        // When
        AccountDto accountDto = accountService.getOwnAccountDetails("test@example.com", 1L);

        // Then
        assertNotNull(accountDto);
        assertEquals("100000001", accountDto.getAccountNumber());
        assertEquals(BigDecimal.ZERO, accountDto.getBalance());
    }

    @Test
    void getOwnAccountDetails_accountNotFound_throwsException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () -> accountService.getOwnAccountDetails("test@example.com", 1L));
    }

    @Test
    void deleteAccount_success() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testAccount));

        // When
        accountService.deleteAccount("test@example.com", 1L);

        // Then
        verify(accountRepository, times(1)).delete(testAccount);
    }

    @Test
    void deleteAccount_withBalance_throwsException() {
        // Given
        testAccount.setBalance(BigDecimal.valueOf(100));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testAccount));

        // Then
        assertThrows(IllegalArgumentException.class, () -> accountService.deleteAccount("test@example.com", 1L));
    }
}
