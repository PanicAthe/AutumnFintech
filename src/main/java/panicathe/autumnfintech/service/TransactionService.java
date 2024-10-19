package panicathe.autumnfintech.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import panicathe.autumnfintech.dto.TransferDto;
import panicathe.autumnfintech.dto.transaction.TransactionDetailDto;
import panicathe.autumnfintech.dto.transaction.TransactionListDto;
import panicathe.autumnfintech.entity.Account;
import panicathe.autumnfintech.entity.Transaction;
import panicathe.autumnfintech.entity.User;
import panicathe.autumnfintech.entity.enums.TransactionType;
import panicathe.autumnfintech.exception.custom.*;
import panicathe.autumnfintech.repository.AccountRepository;
import panicathe.autumnfintech.repository.TransactionRepository;
import panicathe.autumnfintech.repository.UserRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // 이체 수수료 100원, ROLE_ADMIN 이 관리
    private static BigDecimal FEE = BigDecimal.valueOf(100);

    // 수수료 설정 메서드
    public void setFee(BigDecimal newFee) {
        FEE = newFee;
    }

    // 1. 입금 처리
    @Transactional
    public void deposit(String email, Long accountId, BigDecimal amount) {
        Account account = accountService.getAccountByEmailAndId(email, accountId);
        validateAccountIsActive(account);
        account.setBalance(account.getBalance().add(amount));
        createTransaction(account, account, amount, BigDecimal.ZERO, TransactionType.DEPOSIT);
    }

    // 2. 출금 처리
    @Transactional
    public void withdraw(String email, Long accountId, BigDecimal amount) {
        Account account = accountService.getAccountByEmailAndId(email, accountId);
        validateAccountIsActive(account);
        validateSufficientBalance(account, amount);
        validateTransferLimit(account, amount);
        account.setBalance(account.getBalance().subtract(amount));
        createTransaction(account, account, amount, BigDecimal.ZERO, TransactionType.WITHDRAWAL);
    }

    // 3. 송금 처리
    @Transactional
    public void transfer(String email, Long senderAccountId, TransferDto transferDto) {
        Account senderAccount = accountService.getAccountByEmailAndId(email, senderAccountId);
        validateAccountIsActive(senderAccount);

        Account receiverAccount = accountRepository.findByAccountNumber(transferDto.getReceiverAccountNumber())
                .orElseThrow(ReceiverAccountNotFoundException::new);
        validateAccountIsActive(receiverAccount);

        BigDecimal amount = transferDto.getAmount();
        BigDecimal totalAmount = amount.add(FEE);
        validateSufficientBalance(senderAccount, totalAmount);
        validateTransferLimit(senderAccount, totalAmount);

        senderAccount.setBalance(senderAccount.getBalance().subtract(totalAmount));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));

        createTransaction(senderAccount, receiverAccount, amount, FEE, TransactionType.TRANSFER);
    }

    // 4. 송금 이력 조회 (페이징 지원)
    @Transactional(readOnly = true)
    public Page<TransactionListDto> getTransferHistory(String email, int page, int size) {
        Long userId = accountService.getUserIdByEmail(email);
        Account account = accountService.getAccountByEmailAndId(email, userId);
        PageRequest pageRequest = PageRequest.of(page, size);

        return transactionRepository.findAllBySenderAccount(account, pageRequest)
                .map(tx -> new TransactionListDto(
                        tx.getId(),
                        tx.getSenderAccount().getAccountNumber(),
                        tx.getReceiverAccount().getAccountNumber(),
                        tx.getAmount(),
                        tx.getType(),
                        tx.getCreatedAt()
                ));
    }

    // 5. 거래 ID로 거래 상세 정보 조회
    @Transactional(readOnly = true)
    public TransactionDetailDto getTransactionDetails(Long transactionId, String email) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(TransactionNotFoundException::new);

        Account senderAccount = transaction.getSenderAccount();
        Account receiverAccount = transaction.getReceiverAccount();

        if (!senderAccount.getUser().getEmail().equals(email) && !receiverAccount.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException();
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

    // 6. 송금 취소
    @Transactional
    public void cancelTransfer(String email, Long transactionId) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(TransactionNotFoundException::new);

        Account senderAccount = transaction.getSenderAccount();
        Account receiverAccount = transaction.getReceiverAccount();

        validateTransactionCancellation(user, senderAccount, transaction);
        processRefund(senderAccount, receiverAccount, transaction);

        transactionRepository.save(transaction);
    }

    // 헬퍼 메소드: 이체 취소 가능 여부 확인
    private void validateTransactionCancellation(User user, Account senderAccount, Transaction transaction) {
        validateAccountIsActive(senderAccount);

        if (!senderAccount.getUser().equals(user)) {
            throw new TransactionNotFoundException();
        }

        if (!transaction.canBeCancelled()) {
            throw new TransactionCannotBeCancelledException();
        }
    }

    // 헬퍼 메소드: 환불 처리 로직
    private void processRefund(Account senderAccount, Account receiverAccount, Transaction transaction) {
        senderAccount.setBalance(senderAccount.getBalance().add(transaction.getAmount().add(transaction.getFee())));
        receiverAccount.setBalance(receiverAccount.getBalance().subtract(transaction.getAmount()));
        transaction.cancelTransaction();
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
            throw new AccountInactiveException();
        }
    }

    // 헬퍼 메소드: 잔액 충분 여부 확인
    private void validateSufficientBalance(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
    }

    // 헬퍼 메소드: 이체 한도 초과 여부 확인
    private void validateTransferLimit(Account account, BigDecimal amount) {
        if (amount.compareTo(account.getTransferLimit()) > 0) {
            throw new TransferLimitExceededException();
        }
    }
}
