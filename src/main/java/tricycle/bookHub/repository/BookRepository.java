package tricycle.bookHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tricycle.bookHub.model.Book;

public interface BookRepository  extends JpaRepository<Book, Long> {
}
