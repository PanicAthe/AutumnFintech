package panicathe.autumnfintech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import panicathe.autumnfintech.entity.Account;
import panicathe.autumnfintech.entity.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllBySenderAccountOrReceiverAccount(Account account, Account account1);
}
