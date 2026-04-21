package tricycle.bookHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tricycle.bookHub.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
