package panicathe.autumnfintech.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import panicathe.autumnfintech.dto.account.AccountDto;
import panicathe.autumnfintech.dto.account.CreateAccountDto;
import panicathe.autumnfintech.dto.account.UserAccountInfoDto;
import panicathe.autumnfintech.entity.Account;
import panicathe.autumnfintech.entity.User;
import panicathe.autumnfintech.exception.ErrorMessages;
import panicathe.autumnfintech.repository.AccountRepository;
import panicathe.autumnfintech.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    // 헬퍼 메소드 - 이메일을 통해 사용자 조회
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.USER_NOT_FOUND.getMessage()));
    }

    // 계좌 생성
    @Transactional
    public void createAccount(String email, CreateAccountDto createAccountDto) {
        User user = getUserByEmail(email); // 중복 제거
        String accountNumber = generateUniqueAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .user(user)
                .balance(BigDecimal.ZERO)
                .transferLimit(createAccountDto.getTransferLimit())
                .isActive(true)  // 생성 시 활성화 상태로 설정
                .build();

        accountRepository.save(account);
    }

    // 계좌 삭제 (잔액이 0이어야 가능)
    @Transactional
    public void deleteAccount(String email, Long accountId) {
        Account account = getAccountByEmailAndId(email, accountId);
        if (!account.isActive()) {
            throw new IllegalStateException(ErrorMessages.ACCOUNT_INACTIVE.getMessage());
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException(ErrorMessages.ACCOUNT_BALANCE_NON_ZERO.getMessage());
        }
        accountRepository.delete(account);
    }

    // 계좌 송금/출금 한도 설정
    @Transactional
    public void setTransferLimit(String email, Long accountId, BigDecimal newLimit) {
        Account account = getAccountByEmailAndId(email, accountId);
        account.setTransferLimit(newLimit);
        // save 호출 불필요
    }

    // 본인의 계좌 상세 정보를 ID로 조회
    @Transactional(readOnly = true)
    public AccountDto getOwnAccountDetails(String email, Long accountId) {
        Account account = getAccountByEmailAndId(email, accountId);
        return new AccountDto(account.getId(), account.getAccountNumber(), account.getUser().getUsername(), account.getBalance(), account.isActive());
    }

    // 타인의 계좌 소유자 이름 조회 (계좌번호 기반)
    @Transactional(readOnly = true)
    public UserAccountInfoDto getAccountInfoByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND.getMessage()));
        if (!account.isActive()) {
            throw new IllegalStateException(ErrorMessages.ACCOUNT_INACTIVE.getMessage());
        }
        return new UserAccountInfoDto(account.getAccountNumber(), account.getUser().getUsername());
    }

    // 유저의 본인 계좌들 조회
    @Transactional(readOnly = true)
    public List<AccountDto> getUserAccounts(String email) {
        User user = getUserByEmail(email); // 중복 제거

        return accountRepository.findAllByUser(user).stream()
                .map(account -> new AccountDto(account.getId(), account.getAccountNumber(),
                        user.getUsername(), account.getBalance(), account.isActive()))
                .collect(Collectors.toList());
    }

    //  헬퍼 메소드 : 이메일과 계좌 ID로 계좌 조회
    Account getAccountByEmailAndId(String email, Long accountId) {
        User user = getUserByEmail(email); // 중복 제거
        return accountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND.getMessage()));
    }

    //  헬퍼 메소드 : 유니크한 계좌번호 생성
    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "1000" + String.format("%08d", (int) (Math.random() * 100000000));
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());
        return accountNumber;
    }

}