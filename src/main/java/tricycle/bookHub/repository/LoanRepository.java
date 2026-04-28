package tricycle.bookHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tricycle.bookHub.model.Loan;
import tricycle.bookHub.model.Statut;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // RG-LOAN-01 : compter les emprunts actifs d'un user
    long countByUserIdAndStatus(Long userId, Statut status);

    // RG-LOAN-03 : vérifier si le user a un emprunt en retard
    boolean existsByUserIdAndStatus(Long userId, Statut status);

    // RG-LOAN-04 : vérifier si le même livre est déjà emprunté EN_COURS par ce user
    boolean existsByUserIdAndBooksIdAndStatus(Long userId, Long bookId, Statut status);

    List<Loan> findByUserId(Long userId);

    List<Loan> findByStatusAndReturnDateBefore(Statut status, Date date);

    boolean existsByBooksIdAndStatusIn(Long bookId, List<Statut> statuts);

    void deleteByBooksId(Long bookId);

    List<Loan> findByBooksIdAndStatusIn(Long bookId, List<Statut> statuts);

    boolean existsByBooksIdAndUserIdAndStatus(Long bookId, Long userId, Statut status);
}