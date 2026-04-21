package tricycle.bookHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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


// si la première ne fonctionne pas
//    @Query("SELECT b FROM Book b WHERE b.category.id =:categoryId ")
//    List<Book> findByCategory(Long category_id);
}
