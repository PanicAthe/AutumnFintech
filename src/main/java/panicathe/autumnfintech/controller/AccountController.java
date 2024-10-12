package panicathe.autumnfintech.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import panicathe.autumnfintech.dto.account.AccountDto;
import panicathe.autumnfintech.dto.ApiResponse;
import panicathe.autumnfintech.dto.account.CreateAccountDto;
import panicathe.autumnfintech.dto.account.UserAccountInfoDto;
import panicathe.autumnfintech.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // 1. 계좌 생성
    @Operation(summary = "Create a new account", description = "Creates a new account for the authenticated user")
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createAccount(@AuthenticationPrincipal String email,
                                                             @Valid @RequestBody CreateAccountDto createAccountDto) {
        accountService.createAccount(email, createAccountDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account created successfully", null));
    }

    // 2. 본인의 계좌 상세 정보 조회 (ID 기반)
    @Operation(summary = "Get account details (own account)", description = "Gets details of the authenticated user's account by account ID")
    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountDto>> getAccountDetails(@AuthenticationPrincipal String email,
                                                                     @PathVariable Long accountId) {
        AccountDto accountDto = accountService.getOwnAccountDetails(email, accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account details fetched", accountDto));
    }

    // 3. 타인의 계좌 소유자 이름 조회 (계좌번호 기반)
    @Operation(summary = "Get account owner name (other account)", description = "Gets the owner name of an account by account number")
    @GetMapping("/info/{accountNumber}")
    public ResponseEntity<ApiResponse<UserAccountInfoDto>> getAccountOwnerName(@PathVariable String accountNumber) {
        UserAccountInfoDto userAccountInfo = accountService.getAccountInfoByAccountNumber(accountNumber);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account owner fetched", userAccountInfo));
    }

    // 4. 본인의 모든 계좌 목록 조회
    @Operation(summary = "Get user accounts", description = "Fetches all accounts of the authenticated user")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<AccountDto>>> getUserAccounts(@AuthenticationPrincipal String email) {
        List<AccountDto> accounts = accountService.getUserAccounts(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "User accounts fetched successfully", accounts));
    }

    // 5. 계좌 삭제 (잔액이 0이어야 가능)
    @Operation(summary = "Delete an account", description = "Deletes an account if the balance is zero")
    @DeleteMapping("/{accountId}")
    public ResponseEntity<ApiResponse<String>> deleteAccount(@AuthenticationPrincipal String email,
                                                             @PathVariable Long accountId) {
        accountService.deleteAccount(email, accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account deleted successfully", null));
    }

    // 6. 계좌 송금/출금 한도 설정
    @Operation(summary = "Set transfer limit", description = "Sets the transfer limit for a specific account")
    @PutMapping("/{accountId}/limit")
    public ResponseEntity<ApiResponse<String>> setTransferLimit(@AuthenticationPrincipal String email,
                                                                @PathVariable Long accountId,
                                                                @RequestParam @NotNull BigDecimal newLimit) {
        accountService.setTransferLimit(email, accountId, newLimit);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer limit updated successfully", null));
    }
}