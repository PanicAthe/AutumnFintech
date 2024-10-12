package panicathe.autumnfintech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import panicathe.autumnfintech.entity.Account;
import panicathe.autumnfintech.entity.User;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByIdAndUser(Long accountId, User user);

    List<Account> findAllByUser(User user);
}
