package tricycle.bookHub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tricycle.bookHub.model.Loan;
import tricycle.bookHub.model.User;
import tricycle.bookHub.service.LoanService;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class LoanController {

    private final LoanService service;

    @PostMapping("/api/loans")
    public ResponseEntity<Loan> addLoan(@RequestBody Loan loan){
        Loan savedLoan = service.addLoan(loan);
        URI location = URI.create("/api/loans/" + savedLoan.getId());

        return ResponseEntity.created(location).body(savedLoan);
    }

    @GetMapping("/api/loans/my")
    public ResponseEntity<List<Loan>> getLoansById(@AuthenticationPrincipal User user){
        if (user == null){
            return ResponseEntity.status(401).build();
        }

        Long userId = user.getId();
        List<Loan> loans = service.getLoanByUserID(userId);
        return ResponseEntity.ok(loans);
    }

    //ROLE bibliothécaire
    @GetMapping("/api/loans")
    public ResponseEntity<List<Loan>> getAllLoans(){
        List<Loan> loans = service.getAllLoans();
        return ResponseEntity.ok(loans);
    }

    //ROLE bibliothécaire
    //pour passer de en cours à retourner
    @PutMapping("/api/loans/{id}/return")
    public ResponseEntity<Loan> returnLoan(@PathVariable Long id){
        Loan updatedLoan = service.markLoanAsReturned(id);
       return ResponseEntity.ok(updatedLoan);
    }
}
