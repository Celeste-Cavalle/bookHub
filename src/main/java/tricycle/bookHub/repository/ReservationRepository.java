package tricycle.bookHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tricycle.bookHub.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // RG : max 5 réservations par user
    long countByUserId(Long userId);

    // RG : un seul user par livre
    boolean existsByBookId(Long bookId);

    // Vérifier si ce user a déjà réservé ce livre
    boolean existsByBookIdAndUserId(Long bookId, Long userId);

    // Récupérer les réservations d'un user
    List<Reservation> findByUserId(Long userId);

    // Supprimer la réservation d'un livre (quand transformée en emprunt)
    void deleteByBookId(Long bookId);

    Optional<Reservation> findByBookId(Long bookId);
}