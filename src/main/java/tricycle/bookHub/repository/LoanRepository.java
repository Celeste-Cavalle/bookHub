package tricycle.bookHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tricycle.bookHub.model.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
