package tricycle.bookHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tricycle.bookHub.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
