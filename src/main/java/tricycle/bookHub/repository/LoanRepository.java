package tricycle.bookHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tricycle.bookHub.model.Loan;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserId(Long userId);
}
