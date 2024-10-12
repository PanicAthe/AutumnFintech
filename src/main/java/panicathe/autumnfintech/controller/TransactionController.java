package panicathe.autumnfintech.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import panicathe.autumnfintech.dto.ApiResponse;
import panicathe.autumnfintech.dto.TransferDto;
import panicathe.autumnfintech.dto.transaction.TransactionDetailDto;
import panicathe.autumnfintech.dto.transaction.TransactionListDto;
import panicathe.autumnfintech.service.TransactionService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // 1. 입금 처리
    @Operation(summary = "Deposit money", description = "Deposits money into an account")
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<ApiResponse<String>> deposit(@AuthenticationPrincipal String email,
                                                       @PathVariable Long accountId,
                                                       @RequestParam BigDecimal amount) {
        transactionService.deposit(email, accountId, amount);
        return ResponseEntity.ok(new ApiResponse<>(true, "Amount deposited successfully", null));
    }

    // 2. 출금 처리
    @Operation(summary = "Withdraw money", description = "Withdraws money from the user's account")
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<ApiResponse<String>> withdraw(@AuthenticationPrincipal String email,
                                                        @PathVariable Long accountId,
                                                        @RequestParam BigDecimal amount) {
        transactionService.withdraw(email, accountId, amount);
        return ResponseEntity.ok(new ApiResponse<>(true, "Amount withdrawn successfully", null));
    }

    // 3. 송금 처리
    @Operation(summary = "Transfer money", description = "Transfers money between two accounts")
    @PostMapping("/{accountId}/transfer")
    public ResponseEntity<ApiResponse<String>> transfer(@AuthenticationPrincipal String email,
                                                        @PathVariable Long accountId,
                                                        @Valid @RequestBody TransferDto transferDto) {
        transactionService.transfer(email, accountId, transferDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Amount transferred successfully", null));
    }

    // 4. 특정 계좌의 거래 내역 조회
    @Operation(summary = "Get transactions by account", description = "Fetches all transactions related to a specific account by account ID")
    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<List<TransactionListDto>>> getTransactionsByAccount(
            @AuthenticationPrincipal String email,
            @PathVariable Long accountId) {
        List<TransactionListDto> transactions = transactionService.getTransactionsByAccount(accountId, email);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transactions fetched", transactions));
    }

    // 5. 거래 ID로 거래 상세 정보 조회
    @Operation(summary = "Get transaction details by ID", description = "Fetches detailed information of a specific transaction by transaction ID")
    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionDetailDto>> getTransactionDetails(
            @AuthenticationPrincipal String email,
            @PathVariable Long transactionId) {
        TransactionDetailDto transactionDetails = transactionService.getTransactionDetails(transactionId, email);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction details fetched", transactionDetails));
    }
}
