package panicathe.autumnfintech.dto.account;

// 계좌 활성화/비활성화를 위한 DTO
public class AccountStatusRequest {
    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

