package tricycle.bookHub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tricycle.bookHub.dto.LoanRequest;
import tricycle.bookHub.dto.LoanResponse;
import tricycle.bookHub.model.Loan;
import tricycle.bookHub.model.User;
import tricycle.bookHub.service.LoanService;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(request));
    }

    @PutMapping("/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponse> returnLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnLoan(id));
    }

    @GetMapping("/active/book/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponse> getActiveLoanByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(loanService.getActiveLoanByBook(bookId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Loan>> getMyLoans(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(loanService.getMyLoans(user.getId()));
    }

}