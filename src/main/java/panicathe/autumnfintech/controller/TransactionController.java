package panicathe.autumnfintech.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import panicathe.autumnfintech.dto.*;
import panicathe.autumnfintech.dto.transaction.TranactionDto;
import panicathe.autumnfintech.dto.transaction.TransactionAmountRequest;
import panicathe.autumnfintech.dto.transaction.TransactionDetailDto;
import panicathe.autumnfintech.dto.transaction.TransactionListDto;
import panicathe.autumnfintech.service.TransactionService;


@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // 1. 입금 처리 (POST /transactions/{accountId}/deposit)
    @Operation(summary = "Deposit money", description = "Deposits money into an account")
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<ApiResponse<String>> deposit(@AuthenticationPrincipal String email,
                                                       @PathVariable Long accountId,
                                                       @RequestBody TransactionAmountRequest amountRequest) {
        transactionService.deposit(email, accountId, amountRequest.getAmount());
        return ResponseEntity.ok(new ApiResponse<>(true, "Amount deposited successfully", null));
    }

    // 2. 출금 처리 (POST /transactions/{accountId}/withdraw)
    @Operation(summary = "Withdraw money", description = "Withdraws money from the user's account")
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<ApiResponse<String>> withdraw(@AuthenticationPrincipal String email,
                                                        @PathVariable Long accountId,
                                                        @RequestBody TransactionAmountRequest amountRequest) {
        transactionService.withdraw(email, accountId, amountRequest.getAmount());
        return ResponseEntity.ok(new ApiResponse<>(true, "Amount withdrawn successfully", null));
    }

    // 3. 송금 처리 (POST /transactions/{accountId}/transfer)
    @Operation(summary = "Transfer money", description = "Transfers money between two accounts")
    @PostMapping("/{accountId}/transfer")
    public ResponseEntity<ApiResponse<String>> transfer(@AuthenticationPrincipal String email,
                                                        @PathVariable Long accountId,
                                                        @Valid @RequestBody TranactionDto tranactionDto) {
        transactionService.transfer(email, accountId, tranactionDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Amount transferred successfully", null));
    }

    // 4. 송금 이력 조회 (GET /transactions)
    @Operation(summary = "Get transfer history", description = "Fetches the authenticated user's transfer history with pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionListDto>>> getTransferHistory(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionListDto> transfers = transactionService.getTransferHistory(email, page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer history fetched", transfers));
    }

    // 5. 거래 ID로 거래 상세 정보 조회 (GET /transactions/{transactionId})
    @Operation(summary = "Get transaction details", description = "Fetches detailed information of a specific transaction by transaction ID")
    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionDetailDto>> getTransactionDetails(
            @AuthenticationPrincipal String email,
            @PathVariable Long transactionId) {
        TransactionDetailDto transactionDetails = transactionService.getTransactionDetails(transactionId, email);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction details fetched", transactionDetails));
    }

    // 6. 송금 취소 (POST /transactions/{transactionId}/cancel)
    @Operation(summary = "Cancel transfer", description = "Cancels a transfer if done within the past hour")
    @PostMapping("/{transactionId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelTransfer(@AuthenticationPrincipal String email,
                                                              @PathVariable Long transactionId) {
        transactionService.cancelTransfer(email, transactionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer cancelled successfully", null));
    }
}
