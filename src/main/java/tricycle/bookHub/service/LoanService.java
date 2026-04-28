package tricycle.bookHub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tricycle.bookHub.dto.LoanRequest;
import tricycle.bookHub.dto.LoanResponse;
import tricycle.bookHub.model.*;
import tricycle.bookHub.repository.BookRepository;
import tricycle.bookHub.repository.LoanRepository;
import tricycle.bookHub.repository.ReservationRepository;
import tricycle.bookHub.repository.UserRepository;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final int MAX_LOANS     = 3;
    private static final int LOAN_DURATION = 14;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public LoanResponse createLoan(LoanRequest request) {

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Livre introuvable"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        // RG-LOAN-03 : bloqué si retard
        if (loanRepository.existsByUserIdAndStatus(user.getId(), Statut.RETARD)) {
            throw new IllegalStateException("RG-LOAN-03 : emprunt bloqué, l'utilisateur a un retard en cours");
        }

        // RG-LOAN-01 : max 3 emprunts actifs
        if (loanRepository.countByUserIdAndStatus(user.getId(), Statut.EN_COURS) >= MAX_LOANS) {
            throw new IllegalStateException("RG-LOAN-01 : l'utilisateur a atteint le maximum de 3 emprunts simultanés");
        }

        // Livre disponible
        if (!book.isAvailable() || book.getState() != Etat.EMPRUNTABLE) {
            throw new IllegalStateException("Ce livre n'est pas disponible à l'emprunt");
        }

        // RG-LOAN-04 : même livre déjà en cours
        if (loanRepository.existsByUserIdAndBooksIdAndStatus(user.getId(), book.getId(), Statut.EN_COURS)) {
            throw new IllegalStateException("RG-LOAN-04 : ce livre est déjà emprunté par cet utilisateur");
        }

        // Création de l'emprunt
        Date loanDate   = new Date();
        Date returnDate = addDays(loanDate, LOAN_DURATION);

        Loan loan = new Loan();
        loan.setBooks(book);
        loan.setUser(user);
        loan.setLoanDate(loanDate);
        loan.setReturnDate(returnDate);
        loan.setStatus(Statut.EN_COURS);

        // Mise à jour du livre
        book.setState(Etat.EMPRUNTE);
        book.setAvailable(false);
        bookRepository.save(book);

        reservationRepository.deleteByBookId(book.getId());

        Loan saved = loanRepository.save(loan);

        return new LoanResponse(
                saved.getId(),
                book.getTitle(),
                user.getEmail(),
                saved.getLoanDate(),
                saved.getReturnDate(),
                saved.getStatus()
        );
    }

    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    @Transactional
    public LoanResponse returnLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Emprunt introuvable"));

        if (loan.getStatus() == Statut.TERMINE) {
            throw new IllegalStateException("Cet emprunt est déjà terminé");
        }

        loan.setStatus(Statut.TERMINE);
        loan.setReturnDate(new Date());

        Book book = loan.getBooks();
        boolean hasReservation = reservationRepository.existsByBookId(book.getId());

        if (hasReservation) {
            book.setState(Etat.RESERVE);
            book.setAvailable(false);
        } else {
            book.setState(Etat.EMPRUNTABLE);
            book.setAvailable(true);
        }
        bookRepository.save(book);

        loanRepository.save(loan);

        return new LoanResponse(
                loan.getId(),
                book.getTitle(),
                loan.getUser().getEmail(),
                loan.getLoanDate(),
                loan.getReturnDate(),
                loan.getStatus()
        );
    }

    public LoanResponse getActiveLoanByBook(Long bookId) {
        List<Loan> loans = loanRepository.findByBooksIdAndStatusIn(
                bookId, List.of(Statut.EN_COURS, Statut.RETARD)
        );

        if (loans.isEmpty()) {
            throw new IllegalArgumentException("Aucun emprunt actif pour ce livre");
        }

        Loan loan = loans.stream()
                .max(Comparator.comparing(Loan::getLoanDate))
                .orElseThrow();

        return new LoanResponse(
                loan.getId(),
                loan.getBooks().getTitle(),
                loan.getUser().getEmail(),
                loan.getLoanDate(),
                loan.getReturnDate(),
                loan.getStatus()
        );
    }

    public List<Loan> getMyLoans(Long userId){
        return loanRepository.findByUserId(userId);
    }
}