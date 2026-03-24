package portfolio_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio_service.model.Account;
import portfolio_service.repository.AccountRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public Account createAccount(Account account) {
        if (accountRepository.existsByAccountNumber(
                account.getAccountNumber())) {
            throw new RuntimeException("Account number already exists: "
                    + account.getAccountNumber());
        }
        Account saved = accountRepository.save(account);
        log.info("Account created: {}", saved.getAccountNumber());
        return saved;
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Account not found: " + id));
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Account not found: " + accountNumber));
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}
