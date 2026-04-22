package tricycle.bookHub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tricycle.bookHub.exception.LoanNotFoundException;
import tricycle.bookHub.model.Loan;
import tricycle.bookHub.model.Statut;
import tricycle.bookHub.repository.LoanRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LoanService {

    private final LoanRepository repository;

    public List<Loan> getAllLoans(){ return repository.findAll();}

    public Loan addLoan(Loan loan){
        return repository.save(loan);
    }

    public List<Loan> getLoanByUserID(Long userId){
        return repository.findByUserId(userId);
    }

    public Loan getLoanById(Long id){
        return repository.findById(id).
                orElseThrow(() -> new LoanNotFoundException("Cet emprunt avec cet id: "+ id + " n'existe pas"));
    }

    public Loan updateLoan(Loan loan, Long id){
        Loan existingLoan = repository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Cet emprunt avec cet id: "+ id + " n'existe pas"));

        existingLoan.setLoanDate(loan.getLoanDate());
        existingLoan.setReturnDate(loan.getReturnDate());
        existingLoan.setStatus(loan.getStatus());
        existingLoan.setBooks(loan.getBooks());
        existingLoan.setUser(loan.getUser());

        return repository.save(existingLoan);
    }

    public Loan markLoanAsReturned(Long id){
        Loan existingLoan = repository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Cet emprunt avec cet id: "+ id + " n'existe pas"));

        existingLoan.setStatus(Statut.TERMINE);

        return repository.save(existingLoan);
    }

    public void deleteLoan(Long id){
        repository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Cet emprunt avec cet id: "+ id + " n'existe pas"));
    }
}
