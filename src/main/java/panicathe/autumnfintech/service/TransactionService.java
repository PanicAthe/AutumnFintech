package panicathe.autumnfintech.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import panicathe.autumnfintech.dto.TransferDto;
import panicathe.autumnfintech.dto.transaction.TransactionDetailDto;
import panicathe.autumnfintech.dto.transaction.TransactionListDto;
import panicathe.autumnfintech.entity.Account;
import panicathe.autumnfintech.entity.Transaction;
import panicathe.autumnfintech.entity.enums.TransactionType;
import panicathe.autumnfintech.exception.ErrorMessages;
import panicathe.autumnfintech.repository.AccountRepository;
import panicathe.autumnfintech.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountService accountService; //getAccountByEmailAndId 메소드 사용
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    // 이체 수수료 100원,  ROLE_ADMIN 이 관리하는 API 구현 예정
    private static final BigDecimal FEE = BigDecimal.valueOf(100);  

    // 입금 처리
    @Transactional
    public void deposit(String email, Long accountId, BigDecimal amount) {
        Account account = accountService.getAccountByEmailAndId(email, accountId);
        validateAccountIsActive(account);

        account.setBalance(account.getBalance().add(amount));

        createTransaction(account, account, amount, BigDecimal.ZERO, TransactionType.DEPOSIT);
    }

    // 출금 처리
    @Transactional
    public void withdraw(String email, Long accountId, BigDecimal amount) {
        Account account = accountService.getAccountByEmailAndId(email, accountId);
        validateAccountIsActive(account);
        validateSufficientBalance(account, amount);
        validateTransferLimit(account, amount);

        account.setBalance(account.getBalance().subtract(amount));

        createTransaction(account, account, amount, BigDecimal.ZERO, TransactionType.WITHDRAWAL);
    }

    // 송금 처리
    @Transactional
    public void transfer(String email, Long senderAccountId, TransferDto transferDto) {
        Account senderAccount = accountService.getAccountByEmailAndId(email, senderAccountId);
        validateAccountIsActive(senderAccount);

        Account receiverAccount = accountRepository.findByAccountNumber(transferDto.getReceiverAccountNumber())
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.RECEIVER_ACCOUNT_NOT_FOUND.getMessage()));

        validateAccountIsActive(receiverAccount);
        BigDecimal amount = transferDto.getAmount();
        BigDecimal totalAmount = amount.add(FEE);
        validateSufficientBalance(senderAccount, totalAmount);
        validateTransferLimit(senderAccount, totalAmount);

        senderAccount.setBalance(senderAccount.getBalance().subtract(totalAmount));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));

        createTransaction(senderAccount, receiverAccount, amount, FEE, TransactionType.TRANSFER);
    }

    // 특정 계좌의 거래 내역 조회
    @Transactional(readOnly = true)
    public List<TransactionListDto> getTransactionsByAccount(Long accountId, String email) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND.getMessage()));

        if (!account.getUser().getEmail().equals(email)) {
            throw new IllegalStateException(ErrorMessages.ACCESS_DENIED.getMessage());
        }

        List<Transaction> transactions = transactionRepository.findAllBySenderAccountOrReceiverAccount(account, account);

        return transactions.stream()
                .map(tx -> new TransactionListDto(
                        tx.getId(),
                        tx.getSenderAccount().getAccountNumber(),
                        tx.getReceiverAccount().getAccountNumber(),
                        tx.getAmount(),
                        tx.getType(),
                        tx.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // 거래 ID로 거래 상세 정보 조회
    @Transactional(readOnly = true)
    public TransactionDetailDto getTransactionDetails(Long transactionId, String email) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.TRANSACTION_NOT_FOUND.getMessage()));

        Account senderAccount = transaction.getSenderAccount();
        Account receiverAccount = transaction.getReceiverAccount();

        if (!senderAccount.getUser().getEmail().equals(email) && !receiverAccount.getUser().getEmail().equals(email)) {
            throw new IllegalStateException(ErrorMessages.ACCESS_DENIED.getMessage());
        }

        return new TransactionDetailDto(
                transaction.getId(),
                senderAccount.getAccountNumber(),
                receiverAccount.getAccountNumber(),
                transaction.getAmount(),
                transaction.getFee(),
                transaction.getType(),
                transaction.getCreatedAt(),
                transaction.isCancelled()
        );
    }

    // 헬퍼 메소드: 트랜잭션 생성 메소드
    private void createTransaction(Account sender, Account receiver, BigDecimal amount, BigDecimal fee, TransactionType type) {
        Transaction transaction = Transaction.builder()
                .senderAccount(sender)
                .receiverAccount(receiver)
                .amount(amount)
                .fee(fee)
                .isCancelled(false)
                .type(type)
                .build();
        transactionRepository.save(transaction);
    }

    // 헬퍼 메소드: 계좌 활성화 여부 확인
    private void validateAccountIsActive(Account account) {
        if (!account.isActive()) {
            throw new IllegalStateException(ErrorMessages.ACCOUNT_INACTIVE.getMessage());
        }
    }

    // 헬퍼 메소드: 잔액 충분 여부 확인
    private void validateSufficientBalance(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(ErrorMessages.INSUFFICIENT_BALANCE.getMessage());
        }
    }

    // 헬퍼 메소드: 이체 한도 초과 여부 확인
    private void validateTransferLimit(Account account, BigDecimal amount) {
        if (amount.compareTo(account.getTransferLimit()) > 0) {
            throw new IllegalArgumentException(ErrorMessages.TRANSFER_LIMIT_EXCEEDED.getMessage());
        }
    }
}

