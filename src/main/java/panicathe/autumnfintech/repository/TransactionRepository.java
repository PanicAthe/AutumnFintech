package panicathe.autumnfintech.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import panicathe.autumnfintech.entity.Account;
import panicathe.autumnfintech.entity.Transaction;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllBySenderAccount(Account account, PageRequest pageRequest);
}
