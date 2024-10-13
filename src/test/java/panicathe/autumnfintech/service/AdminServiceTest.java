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

    @Test
    void setTransactionFee_success() {
        // Given
        BigDecimal newFee = BigDecimal.valueOf(200);

        // When
        adminService.setTransactionFee(newFee);

        // Then
        verify(transactionService, times(1)).setFee(newFee);
    }

    @Test
    void setAccountActiveStatus_success() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // When
        adminService.setAccountActiveStatus(1L, false);

        // Then
        assertFalse(testAccount.isActive());
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void setAccountActiveStatus_accountNotFound_throwsException() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(AccountNotFoundException.class, () -> adminService.setAccountActiveStatus(1L, false));
    }

    @Test
    void setAccountTransferLimit_success() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        BigDecimal newLimit = BigDecimal.valueOf(5000);

        // When
        adminService.setAccountTransferLimit(1L, newLimit);

        // Then
        assertEquals(newLimit, testAccount.getTransferLimit());
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void setAccountTransferLimit_accountNotFound_throwsException() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(AccountNotFoundException.class, () -> adminService.setAccountTransferLimit(1L, BigDecimal.valueOf(5000)));
    }
}
