package panicathe.autumnfintech.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import panicathe.autumnfintech.entity.Account;
import panicathe.autumnfintech.exception.custom.AccountNotFoundException;
import panicathe.autumnfintech.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

class AdminServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AdminService adminService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testAccount = Account.builder()
                .id(1L)
                .accountNumber("100000001")
                .isActive(true)
                .transferLimit(BigDecimal.valueOf(1000))
                .build();
    }

    // 1. 송금 수수료 설정 테스트
    @Test
    void setTransactionFee_success() {
        // Given
        BigDecimal newFee = BigDecimal.valueOf(200);

        // When
        adminService.setTransactionFee(newFee);

        // Then
        verify(transactionService, times(1)).setFee(newFee);
    }

    // 2. 계좌 활성화/비활성화 테스트
    @Test
    void setAccountActiveStatus_success() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // When
        adminService.setAccountActiveStatus(1L, false);

        // Then
        assertFalse(testAccount.isActive());
    }

    @Test
    void setAccountActiveStatus_accountNotFound_throwsException() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(AccountNotFoundException.class, () -> adminService.setAccountActiveStatus(1L, false));
    }

    @Test
    void setAccountActiveStatus_alreadyActive_doesNothing() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // When
        adminService.setAccountActiveStatus(1L, true);  // Already active

        // Then
        verify(accountRepository, never()).save(testAccount);  // Should not save because it's already active
        assertTrue(testAccount.isActive());
    }

    @Test
    void setAccountActiveStatus_alreadyInactive_doesNothing() {
        // Given
        testAccount.setActive(false);  // Set inactive
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // When
        adminService.setAccountActiveStatus(1L, false);  // Already inactive

        // Then
        verify(accountRepository, never()).save(testAccount);  // Should not save because it's already inactive
        assertFalse(testAccount.isActive());
    }

    // 3. 계좌 송금 한도 설정 테스트
    @Test
    void setAccountTransferLimit_success() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        BigDecimal newLimit = BigDecimal.valueOf(5000);

        // When
        adminService.setAccountTransferLimit(1L, newLimit);

        // Then
        assertEquals(newLimit, testAccount.getTransferLimit());
    }

    @Test
    void setAccountTransferLimit_accountNotFound_throwsException() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(AccountNotFoundException.class, () -> adminService.setAccountTransferLimit(1L, BigDecimal.valueOf(5000)));
    }

}
