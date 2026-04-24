package tricycle.bookHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tricycle.bookHub.model.Book;

import java.util.List;


@Repository
public interface BookRepository  extends JpaRepository<Book, Long> {

    //recherche textuelle on a dit !!! => pas ca donc
    List<Book> findByAuthor(String author);

    //recherche textuelle on a dit !!!
    List<Book> findByISBN(String ISBN);

    List<Book> findByCategoryId(Long id);

    List<Book> findByAvailable(Boolean isAvailable);

    @Query("""
    SELECT b FROM Book b
    WHERE (:query IS NULL OR
           LOWER(b.title)  LIKE LOWER(CONCAT('%', :query, '%')) OR
           LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR
           LOWER(b.ISBN)   LIKE LOWER(CONCAT('%', :query, '%')))
    AND   (:categoryId IS NULL OR b.category.id = :categoryId)
    AND   (:available  IS NULL OR b.isAvailable = :available)
""")
    List<Book> search(
            @Param("query")      String query,
            @Param("categoryId") Long categoryId,
            @Param("available")  Boolean available
    );
}
