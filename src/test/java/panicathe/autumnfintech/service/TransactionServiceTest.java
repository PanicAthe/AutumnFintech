package panicathe.autumnfintech.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import panicathe.autumnfintech.dto.transaction.TranactionDto;
import panicathe.autumnfintech.entity.Account;
import panicathe.autumnfintech.entity.Transaction;
import panicathe.autumnfintech.entity.enums.TransactionType;
import panicathe.autumnfintech.exception.custom.*;
import panicathe.autumnfintech.repository.AccountRepository;
import panicathe.autumnfintech.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;

class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    private Account testAccount;
    private Account receiverAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testAccount = Account.builder()
                .id(1L)
                .accountNumber("100000001")
                .balance(BigDecimal.ZERO)
                .transferLimit(BigDecimal.valueOf(1000))
                .isActive(true)
                .build();

        receiverAccount = Account.builder()
                .id(2L)
                .accountNumber("100000002")
                .balance(BigDecimal.ZERO)
                .isActive(true)
                .build();
    }

    // 1. 입금 테스트
    @Test
    void deposit_success() {
        when(accountService.getAccountByEmailAndId("test@example.com", 1L)).thenReturn(testAccount);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        transactionService.deposit("test@example.com", 1L, BigDecimal.valueOf(100));

        assertEquals(BigDecimal.valueOf(100), testAccount.getBalance());
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals(TransactionType.DEPOSIT, savedTransaction.getType());
    }

    @Test
    void deposit_inactiveAccount_throwsException() {
        testAccount.setActive(false);
        when(accountService.getAccountByEmailAndId("test@example.com", 1L)).thenReturn(testAccount);

        assertThrows(AccountInactiveException.class, () -> transactionService.deposit("test@example.com", 1L, BigDecimal.valueOf(100)));
    }

    // 2. 출금 테스트
    @Test
    void withdraw_success() {
        testAccount.setBalance(BigDecimal.valueOf(200));
        when(accountService.getAccountByEmailAndId("test@example.com", 1L)).thenReturn(testAccount);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        transactionService.withdraw("test@example.com", 1L, BigDecimal.valueOf(100));

        assertEquals(BigDecimal.valueOf(100), testAccount.getBalance());
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals(TransactionType.WITHDRAWAL, savedTransaction.getType());
    }

    @Test
    void withdraw_inactiveAccount_throwsException() {
        testAccount.setActive(false);
        when(accountService.getAccountByEmailAndId("test@example.com", 1L)).thenReturn(testAccount);

        assertThrows(AccountInactiveException.class, () -> transactionService.withdraw("test@example.com", 1L, BigDecimal.valueOf(100)));
    }

    @Test
    void withdraw_insufficientBalance_throwsException() {
        when(accountService.getAccountByEmailAndId("test@example.com", 1L)).thenReturn(testAccount);

        assertThrows(InsufficientBalanceException.class, () -> transactionService.withdraw("test@example.com", 1L, BigDecimal.valueOf(100)));
    }

    // 3. 송금 테스트
    @Test
    void transfer_success() {
        testAccount.setBalance(BigDecimal.valueOf(500));
        TranactionDto tranactionDto = new TranactionDto("100000002", BigDecimal.valueOf(200));

        when(accountService.getAccountByEmailAndId("test@example.com", 1L)).thenReturn(testAccount);
        when(accountRepository.findByAccountNumber("100000002")).thenReturn(Optional.of(receiverAccount));

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        transactionService.transfer("test@example.com", 1L, tranactionDto);

        assertEquals(BigDecimal.valueOf(200), testAccount.getBalance());
        assertEquals(BigDecimal.valueOf(200), receiverAccount.getBalance());
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals(TransactionType.TRANSFER, savedTransaction.getType());
        assertEquals(BigDecimal.valueOf(100), savedTransaction.getFee());
    }

    @Test
    void transfer_inactiveReceiverAccount_throwsException() {
        receiverAccount.setActive(false);
        TranactionDto tranactionDto = new TranactionDto("100000002", BigDecimal.valueOf(200));

        when(accountService.getAccountByEmailAndId("test@example.com", 1L)).thenReturn(testAccount);
        when(accountRepository.findByAccountNumber("100000002")).thenReturn(Optional.of(receiverAccount));

        assertThrows(AccountInactiveException.class, () -> transactionService.transfer("test@example.com", 1L, tranactionDto));
    }

    @Test
    void transfer_insufficientBalance_throwsException() {
        testAccount.setBalance(BigDecimal.valueOf(100));
        TranactionDto tranactionDto = new TranactionDto("100000002", BigDecimal.valueOf(200));

        when(accountService.getAccountByEmailAndId("test@example.com", 1L)).thenReturn(testAccount);
        when(accountRepository.findByAccountNumber("100000002")).thenReturn(Optional.of(receiverAccount));

        assertThrows(InsufficientBalanceException.class, () -> transactionService.transfer("test@example.com", 1L, tranactionDto));
    }

    @Test
    void transfer_amountExceedsTransferLimit_throwsException() {
        testAccount.setBalance(BigDecimal.valueOf(1000));
        testAccount.setTransferLimit(BigDecimal.valueOf(300));
        TranactionDto tranactionDto = new TranactionDto("100000002", BigDecimal.valueOf(400));

        when(accountService.getAccountByEmailAndId("test@example.com", 1L)).thenReturn(testAccount);
        when(accountRepository.findByAccountNumber("100000002")).thenReturn(Optional.of(receiverAccount));

        assertThrows(TransferLimitExceededException.class, () -> transactionService.transfer("test@example.com", 1L, tranactionDto));
    }
}
