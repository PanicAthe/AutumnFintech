    package panicathe.autumnfintech.controller;

    import io.swagger.v3.oas.annotations.Operation;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;
    import panicathe.autumnfintech.dto.*;
    import panicathe.autumnfintech.dto.account.AccountLimitRequest;
    import panicathe.autumnfintech.dto.account.AccountStatusRequest;
    import panicathe.autumnfintech.dto.transaction.TransactionFeeRequest;
    import panicathe.autumnfintech.service.AdminService;

    @RestController
    @RequestMapping("/admin")
    @RequiredArgsConstructor
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // ROLE_ADMIN 권한만 접근 가능
    public class AdminController {

        private final AdminService adminService;

        // 1. 송금 수수료 설정 (PUT /admin/transactions/fee)
        @Operation(summary = "Set transaction fee", description = "Sets the fee for all transfers (admin only)")
        @PutMapping("/transactions/fee")
        public ResponseEntity<ApiResponse<String>> setTransactionFee(@RequestBody TransactionFeeRequest feeRequest) {
            adminService.setTransactionFee(feeRequest.getNewFee());
            return ResponseEntity.ok(new ApiResponse<>(true, "Transaction fee updated successfully", null));
        }

        // 2. 특정 계좌 활성화/비활성화 (PUT /admin/accounts/{accountId}/status)
        @Operation(summary = "Set account active status", description = "Activates or deactivates a specific account (admin only)")
        @PutMapping("/accounts/{accountId}/status")
        public ResponseEntity<ApiResponse<String>> setAccountActiveStatus(@PathVariable Long accountId,
                                                                          @RequestBody AccountStatusRequest statusRequest) {
            adminService.setAccountActiveStatus(accountId, statusRequest.isActive());
            String status = statusRequest.isActive() ? "activated" : "deactivated";
            return ResponseEntity.ok(new ApiResponse<>(true, "Account " + status + " successfully", null));
        }

        // 3. 특정 계좌 송금 한도 설정 (PUT /admin/accounts/{accountId}/limit)
        @Operation(summary = "Set transfer limit for a specific account", description = "Sets the transfer limit for a specific account (admin only)")
        @PutMapping("/accounts/{accountId}/limit")
        public ResponseEntity<ApiResponse<String>> setAccountTransferLimit(@PathVariable Long accountId,
                                                                           @RequestBody AccountLimitRequest limitRequest) {
            adminService.setAccountTransferLimit(accountId, limitRequest.getNewLimit());
            return ResponseEntity.ok(new ApiResponse<>(true, "Transfer limit updated for account", null));
        }
    }


