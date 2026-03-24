package portfolio_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import portfolio_service.model.Account;
import portfolio_service.service.AccountService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // GET /api/v1/accounts
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    // GET /api/v1/accounts/1
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    // POST /api/v1/accounts
    @PostMapping
    public ResponseEntity<Account> createAccount(
            @RequestBody Account account) {
        Account created = accountService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}