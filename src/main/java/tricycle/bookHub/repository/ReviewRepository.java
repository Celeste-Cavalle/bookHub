package tricycle.bookHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tricycle.bookHub.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
