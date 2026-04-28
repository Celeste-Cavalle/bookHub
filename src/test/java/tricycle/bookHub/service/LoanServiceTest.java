package tricycle.bookHub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tricycle.bookHub.dto.LoanRequest;
import tricycle.bookHub.dto.LoanResponse;
import tricycle.bookHub.model.*;
import tricycle.bookHub.repository.BookRepository;
import tricycle.bookHub.repository.LoanRepository;
import tricycle.bookHub.repository.ReservationRepository;
import tricycle.bookHub.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock private LoanRepository loanRepository;
    @Mock private BookRepository bookRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReservationRepository reservationRepository;

    @InjectMocks private LoanService loanService;

    private Book book;
    private User user;
    private LoanRequest request;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setState(Etat.EMPRUNTABLE);
        book.setAvailable(true);

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        request = new LoanRequest();
        request.setBookId(1L);
        request.setUserId(1L);
    }

    // ===================== createLoan =====================

    @Test
    void createLoan_shouldCreateLoan_whenAllConditionsAreMet() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserIdAndStatus(1L, Statut.RETARD)).thenReturn(false);
        when(loanRepository.countByUserIdAndStatus(1L, Statut.EN_COURS)).thenReturn(0L);
        when(loanRepository.existsByUserIdAndBooksIdAndStatus(1L, 1L, Statut.EN_COURS)).thenReturn(false);
        when(loanRepository.save(any())).thenAnswer(inv -> {
            Loan l = inv.getArgument(0);
            l.setId(1L);
            return l;
        });

        LoanResponse response = loanService.createLoan(request);

        assertThat(response).isNotNull();
        assertThat(response.getBookTitle()).isEqualTo("Clean Code");
        assertThat(response.getUserEmail()).isEqualTo("test@test.com");
        assertThat(response.getStatus()).isEqualTo(Statut.EN_COURS);
        verify(bookRepository).save(book);
        verify(reservationRepository).deleteByBookId(1L);
    }

    @Test
    void createLoan_shouldThrow_whenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        request.setBookId(99L);

        assertThatThrownBy(() -> loanService.createLoan(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Livre introuvable");
    }

    @Test
    void createLoan_shouldThrow_whenUserNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        request.setUserId(99L);

        assertThatThrownBy(() -> loanService.createLoan(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Utilisateur introuvable");
    }

    @Test
    void createLoan_shouldThrow_whenUserHasOverdueLoan_RG_LOAN_03() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserIdAndStatus(1L, Statut.RETARD)).thenReturn(true);

        assertThatThrownBy(() -> loanService.createLoan(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RG-LOAN-03");
    }

    @Test
    void createLoan_shouldThrow_whenUserHasMaxActiveLoans_RG_LOAN_01() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserIdAndStatus(1L, Statut.RETARD)).thenReturn(false);
        when(loanRepository.countByUserIdAndStatus(1L, Statut.EN_COURS)).thenReturn(3L);

        assertThatThrownBy(() -> loanService.createLoan(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RG-LOAN-01");
    }

    @Test
    void createLoan_shouldThrow_whenBookNotAvailable() {
        book.setAvailable(false);
        book.setState(Etat.EMPRUNTE);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserIdAndStatus(1L, Statut.RETARD)).thenReturn(false);
        when(loanRepository.countByUserIdAndStatus(1L, Statut.EN_COURS)).thenReturn(0L);

        assertThatThrownBy(() -> loanService.createLoan(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pas disponible");
    }

    @Test
    void createLoan_shouldThrow_whenUserAlreadyHasSameBook_RG_LOAN_04() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserIdAndStatus(1L, Statut.RETARD)).thenReturn(false);
        when(loanRepository.countByUserIdAndStatus(1L, Statut.EN_COURS)).thenReturn(0L);
        when(loanRepository.existsByUserIdAndBooksIdAndStatus(1L, 1L, Statut.EN_COURS)).thenReturn(true);

        assertThatThrownBy(() -> loanService.createLoan(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RG-LOAN-04");
    }

    @Test
    void createLoan_shouldSetBookStateToReserved_whenReservationExistsAfterReturn() {
        // Ce test couvre le cas returnLoan avec réservation existante
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBooks(book);
        loan.setUser(user);
        loan.setLoanDate(new Date());
        loan.setReturnDate(new Date());
        loan.setStatus(Statut.EN_COURS);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(reservationRepository.existsByBookId(1L)).thenReturn(true);

        LoanResponse response = loanService.returnLoan(1L);

        assertThat(book.getState()).isEqualTo(Etat.RESERVE);
        assertThat(book.isAvailable()).isFalse();
    }

    // ===================== returnLoan =====================

    @Test
    void returnLoan_shouldMarkLoanAsFinished_whenLoanIsActive() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBooks(book);
        loan.setUser(user);
        loan.setLoanDate(new Date());
        loan.setReturnDate(new Date());
        loan.setStatus(Statut.EN_COURS);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(reservationRepository.existsByBookId(1L)).thenReturn(false);

        LoanResponse response = loanService.returnLoan(1L);

        assertThat(response.getStatus()).isEqualTo(Statut.TERMINE);
        assertThat(book.getState()).isEqualTo(Etat.EMPRUNTABLE);
        assertThat(book.isAvailable()).isTrue();
    }

    @Test
    void returnLoan_shouldThrow_whenLoanNotFound() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.returnLoan(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Emprunt introuvable");
    }

    @Test
    void returnLoan_shouldThrow_whenLoanAlreadyFinished() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBooks(book);
        loan.setUser(user);
        loan.setStatus(Statut.TERMINE);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.returnLoan(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà terminé");
    }

    // ===================== getActiveLoanByBook =====================

    @Test
    void getActiveLoanByBook_shouldReturnLoan_whenActiveLoanExists() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBooks(book);
        loan.setUser(user);
        loan.setLoanDate(new Date());
        loan.setReturnDate(new Date());
        loan.setStatus(Statut.EN_COURS);

        when(loanRepository.findByBooksIdAndStatusIn(eq(1L), anyList()))
                .thenReturn(Optional.of(loan));

        LoanResponse response = loanService.getActiveLoanByBook(1L);

        assertThat(response).isNotNull();
        assertThat(response.getBookTitle()).isEqualTo("Clean Code");
    }

    @Test
    void getActiveLoanByBook_shouldThrow_whenNoActiveLoan() {
        when(loanRepository.findByBooksIdAndStatusIn(eq(1L), anyList()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.getActiveLoanByBook(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Aucun emprunt actif");
    }

    // ===================== getMyLoans =====================

    @Test
    void getMyLoans_shouldReturnUserLoans() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUser(user);
        loan.setBooks(book);
        loan.setStatus(Statut.EN_COURS);

        when(loanRepository.findByUserId(1L)).thenReturn(List.of(loan));

        var loans = loanService.getMyLoans(1L);

        assertThat(loans).hasSize(1);
    }

    @Test
    void getMyLoans_shouldReturnEmptyList_whenNoLoans() {
        when(loanRepository.findByUserId(1L)).thenReturn(List.of());

        var loans = loanService.getMyLoans(1L);

        assertThat(loans).isEmpty();
    }
}
