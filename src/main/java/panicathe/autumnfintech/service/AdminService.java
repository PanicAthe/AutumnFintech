package panicathe.autumnfintech.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import panicathe.autumnfintech.entity.Account;
import panicathe.autumnfintech.repository.AccountRepository;
import panicathe.autumnfintech.exception.custom.*;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    // 송금 수수료 설정
    @Transactional
    public void setTransactionFee(BigDecimal newFee) {
        transactionService.setFee(newFee);  // 트랜잭션 서비스에서 수수료 업데이트
    }

    // 특정 계좌 활성화/비활성화
    @Transactional
    public void setAccountActiveStatus(Long accountId, boolean isActive) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        account.setActive(isActive);  // 계좌 활성화/비활성화 상태 변경
    }

    // 특정 계좌 송금 한도 설정
    @Transactional
    public void setAccountTransferLimit(Long accountId, BigDecimal newLimit) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        account.setTransferLimit(newLimit);  // 계좌 송금 한도 설정
    }
}
