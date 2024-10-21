package panicathe.autumnfintech.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import panicathe.autumnfintech.dto.account.AccountDto;
import panicathe.autumnfintech.dto.account.CreateAccountDto;
import panicathe.autumnfintech.entity.Account;
import panicathe.autumnfintech.entity.User;
import panicathe.autumnfintech.exception.custom.*;
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
                .isActive(true)
                .build();

        // Mock common repository behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testAccount));
    }

    // 1. 계좌 생성 테스트
    @Test
    void createAccount_success() {
        // Given
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
        assertThrows(UserNotFoundException.class, () -> accountService.createAccount("nonexistent@example.com", CreateAccountDto.builder()
                .transferLimit(BigDecimal.valueOf(500)).build()));
    }

    // 2. 계좌 상세 조회 테스트
    @Test
    void getOwnAccountDetails_success() {
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
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        // Then
        assertThrows(AccountNotFoundException.class, () -> accountService.getOwnAccountDetails("test@example.com", 1L));
    }

    // 3. 계좌 삭제 테스트
    @Test
    void deleteAccount_success() {
        // When
        accountService.deleteAccount("test@example.com", 1L);

        // Then
        verify(accountRepository, times(1)).delete(testAccount);
    }

    @Test
    void deleteAccount_withBalance_throwsException() {
        // Given
        testAccount.setBalance(BigDecimal.valueOf(100));

        // Then
        assertThrows(InsufficientBalanceException.class, () -> accountService.deleteAccount("test@example.com", 1L));
    }

    @Test
    void deleteAccount_inactiveAccount_throwsException() {
        // Given
        testAccount.setActive(false);

        // Then
        assertThrows(AccountInactiveException.class, () -> accountService.deleteAccount("test@example.com", 1L));
    }

    // 4. 송금 한도 설정 테스트
    @Test
    void setTransferLimit_success() {
        // When
        accountService.setTransferLimit("test@example.com", 1L, BigDecimal.valueOf(2000));

        // Then
        verify(accountRepository, times(1)).save(testAccount);
        assertEquals(BigDecimal.valueOf(2000), testAccount.getTransferLimit());
    }

    @Test
    void setTransferLimit_accountNotFound_throwsException() {
        // Given
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        // Then
        assertThrows(AccountNotFoundException.class, () -> accountService.setTransferLimit("test@example.com", 1L, BigDecimal.valueOf(2000)));
    }

}
