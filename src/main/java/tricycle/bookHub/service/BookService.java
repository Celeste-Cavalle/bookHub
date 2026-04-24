package tricycle.bookHub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tricycle.bookHub.exception.BookNotFoundException;
import tricycle.bookHub.model.Book;
import tricycle.bookHub.model.Etat;
import tricycle.bookHub.model.Statut;
import tricycle.bookHub.repository.BookRepository;
import tricycle.bookHub.repository.LoanRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository repository;
    private final LoanRepository loanRepository;

    public Book addBook(Book book) {
        book.setAvailable(book.getState() == Etat.EMPRUNTABLE);
        return repository.save(book);
    }

    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    public Book getBookById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Ce livre avec cet id: " + id + " n'existe pas"));
    }

    public Book updateBook(Book book, Long id) {
        Book existingBook = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Ce livre avec cet id: " + id + " n'existe pas"));

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setDescription(book.getDescription());
        existingBook.setISBN(book.getISBN());
        existingBook.setCover(book.getCover());
        existingBook.setState(book.getState());
        existingBook.setAvailable(book.getState() == Etat.EMPRUNTABLE);
        existingBook.setCategory(book.getCategory());

        return repository.save(existingBook);
    }

    public void deleteBookById(Long id) {
        boolean hasActiveLoans = loanRepository.existsByBooksIdAndStatusIn(
                id, List.of(Statut.EN_COURS, Statut.RETARD)
        );
        if (hasActiveLoans) {
            throw new IllegalStateException("Impossible de supprimer un livre avec des emprunts actifs");
        }
        loanRepository.deleteByBooksId(id);
        repository.deleteById(id);
    }
}