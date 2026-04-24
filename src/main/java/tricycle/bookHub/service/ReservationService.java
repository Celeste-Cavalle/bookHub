package tricycle.bookHub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tricycle.bookHub.dto.ReservationResponse;
import tricycle.bookHub.model.*;
import tricycle.bookHub.repository.BookRepository;
import tricycle.bookHub.repository.ReservationRepository;
import tricycle.bookHub.repository.UserRepository;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final int MAX_RESERVATIONS = 5;

    private final ReservationRepository reservationRepository;
    private final BookRepository        bookRepository;
    private final UserRepository        userRepository;

    @Transactional
    public ReservationResponse create(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Livre introuvable"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        // Le livre doit être indisponible pour être réservable
        if (book.getState() == Etat.EMPRUNTABLE) {
            throw new IllegalStateException("Ce livre est disponible, empruntez-le directement !");
        }

        // Un seul user par livre
        if (reservationRepository.existsByBookId(bookId)) {
            throw new IllegalStateException("Ce livre est déjà réservé par un autre utilisateur");
        }

        // Ce user a déjà réservé ce livre
        if (reservationRepository.existsByBookIdAndUserId(bookId, userId)) {
            throw new IllegalStateException("Vous avez déjà réservé ce livre");
        }

        // Max 5 réservations
        if (reservationRepository.countByUserId(userId) >= MAX_RESERVATIONS) {
            throw new IllegalStateException("Vous avez atteint le maximum de 5 réservations simultanées");
        }

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setUser(user);
        reservation.setReservationDate(new Date());

        book.setState(Etat.RESERVE);
        book.setAvailable(false);
        bookRepository.save(book);

        Reservation saved = reservationRepository.save(reservation);

        return new ReservationResponse(
                saved.getId(),
                book.getTitle(),
                book.getCover(),
                saved.getReservationDate()
        );
    }

    @Transactional
    public void cancel(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));

        // Vérifier que c'est bien la réservation de ce user
        if (!reservation.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Vous ne pouvez pas annuler la réservation d'un autre utilisateur");
        }

        Book book = reservation.getBook();
        book.setState(Etat.EMPRUNTABLE);
        book.setAvailable(true);
        bookRepository.save(book);

        reservationRepository.delete(reservation);
    }

    public List<ReservationResponse> getMyReservations(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(r -> new ReservationResponse(
                        r.getId(),
                        r.getBook().getTitle(),
                        r.getBook().getCover(),
                        r.getReservationDate()
                ))
                .toList();
    }
}