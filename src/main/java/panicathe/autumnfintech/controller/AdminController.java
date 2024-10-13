package panicathe.autumnfintech.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import panicathe.autumnfintech.dto.ApiResponse;
import panicathe.autumnfintech.service.AdminService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")  // ROLE_ADMIN 권한만 접근 가능
public class AdminController {

    private final AdminService adminService;

    // 1. 송금 수수료 설정 (PUT /admin/transactions/fee)
    @Operation(summary = "Set transaction fee", description = "Sets the fee for all transfers (admin only)")
    @PutMapping("/transactions/fee")
    public ResponseEntity<ApiResponse<String>> setTransactionFee(@RequestParam BigDecimal newFee) {
        adminService.setTransactionFee(newFee);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction fee updated successfully", null));
    }

    // 2. 특정 계좌 활성화/비활성화 (PUT /admin/accounts/{accountId}/status)
    @Operation(summary = "Set account active status", description = "Activates or deactivates a specific account (admin only)")
    @PutMapping("/accounts/{accountId}/status")
    public ResponseEntity<ApiResponse<String>> setAccountActiveStatus(@PathVariable Long accountId,
                                                                      @RequestParam boolean isActive) {
        adminService.setAccountActiveStatus(accountId, isActive);
        String status = isActive ? "activated" : "deactivated";
        return ResponseEntity.ok(new ApiResponse<>(true, "Account " + status + " successfully", null));
    }

    // 3. 특정 계좌 송금 한도 설정 (PUT /admin/accounts/{accountId}/limit)
    @Operation(summary = "Set transfer limit for a specific account", description = "Sets the transfer limit for a specific account (admin only)")
    @PutMapping("/accounts/{accountId}/limit")
    public ResponseEntity<ApiResponse<String>> setAccountTransferLimit(@PathVariable Long accountId,
                                                                       @RequestParam BigDecimal newLimit) {
        adminService.setAccountTransferLimit(accountId, newLimit);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer limit updated for account", null));
    }
}
