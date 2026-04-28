package tricycle.bookHub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tricycle.bookHub.dto.ReservationResponse;
import tricycle.bookHub.model.*;
import tricycle.bookHub.repository.BookRepository;
import tricycle.bookHub.repository.ReservationRepository;
import tricycle.bookHub.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private BookRepository bookRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ReservationService reservationService;

    private Book book;
    private User user;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setCover("/uploads/cover.jpg");
        book.setState(Etat.EMPRUNTE);
        book.setAvailable(false);

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
    }

    // ===================== create =====================

    @Test
    void create_shouldCreateReservation_whenAllConditionsAreMet() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reservationRepository.existsByBookId(1L)).thenReturn(false);
        when(reservationRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(false);
        when(reservationRepository.countByUserId(1L)).thenReturn(0L);
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        ReservationResponse response = reservationService.create(1L, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getBookTitle()).isEqualTo("Clean Code");
        assertThat(response.getUserId()).isEqualTo(1L);
    }

    @Test
    void create_shouldThrow_whenBookAlreadyReservedByOther() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reservationRepository.existsByBookId(1L)).thenReturn(true);

        assertThatThrownBy(() -> reservationService.create(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà réservé par un autre");
    }

    @Test
    void create_shouldThrow_whenUserAlreadyReservedThisBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reservationRepository.existsByBookId(1L)).thenReturn(false);
        when(reservationRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> reservationService.create(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà réservé ce livre");
    }

    @Test
    void create_shouldThrow_whenUserHasMaxReservations() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reservationRepository.existsByBookId(1L)).thenReturn(false);
        when(reservationRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(false);
        when(reservationRepository.countByUserId(1L)).thenReturn(5L);

        assertThatThrownBy(() -> reservationService.create(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("maximum de 5 réservations");
    }

    @Test
    void create_shouldThrow_whenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.create(99L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Livre introuvable");
    }

    // ===================== cancel =====================

    @Test
    void cancel_shouldDeleteReservation_whenOwnerCancels() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setBook(book);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        reservationService.cancel(1L, 1L);

        verify(reservationRepository).delete(reservation);
    }

    @Test
    void cancel_shouldThrow_whenUserIsNotOwner() {
        User otherUser = new User();
        otherUser.setId(2L);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(otherUser);
        reservation.setBook(book);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancel(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("autre utilisateur");
    }

    @Test
    void cancel_shouldThrow_whenReservationNotFound() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.cancel(99L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Réservation introuvable");
    }

    // ===================== getActiveByBook =====================

    @Test
    void getActiveByBook_shouldReturnReservation_whenExists() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(new Date());

        when(reservationRepository.findByBookId(1L)).thenReturn(Optional.of(reservation));

        ReservationResponse response = reservationService.getActiveByBook(1L);

        assertThat(response).isNotNull();
        assertThat(response.getBookTitle()).isEqualTo("Clean Code");
    }

    @Test
    void getActiveByBook_shouldThrow_whenNoReservation() {
        when(reservationRepository.findByBookId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getActiveByBook(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Aucune réservation active");
    }

    // ===================== getMyReservations =====================

    @Test
    void getMyReservations_shouldReturnList_whenUserHasReservations() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(new Date());

        when(reservationRepository.findByUserId(1L)).thenReturn(List.of(reservation));

        List<ReservationResponse> result = reservationService.getMyReservations(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookTitle()).isEqualTo("Clean Code");
    }

    @Test
    void getMyReservations_shouldReturnEmptyList_whenNoReservations() {
        when(reservationRepository.findByUserId(1L)).thenReturn(List.of());

        List<ReservationResponse> result = reservationService.getMyReservations(1L);

        assertThat(result).isEmpty();
    }
}
