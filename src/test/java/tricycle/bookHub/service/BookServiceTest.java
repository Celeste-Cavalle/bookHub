package tricycle.bookHub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tricycle.bookHub.exception.BookNotFoundException;
import tricycle.bookHub.model.Book;
import tricycle.bookHub.model.Category;
import tricycle.bookHub.model.Etat;
import tricycle.bookHub.model.Statut;
import tricycle.bookHub.repository.BookRepository;
import tricycle.bookHub.repository.LoanRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock private BookRepository bookRepository;
    @Mock private LoanRepository loanRepository;

    @InjectMocks private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setDescription("A great book");
        book.setISBN("978-0132350884");
        book.setCover("/uploads/cover.jpg");
        book.setState(Etat.EMPRUNTABLE);
        book.setAvailable(true);
    }

    // ===================== addBook =====================

    @Test
    void addBook_shouldSetAvailableTrue_whenStateIsEmpruntable() {
        book.setState(Etat.EMPRUNTABLE);
        when(bookRepository.save(any())).thenReturn(book);

        Book result = bookService.addBook(book);

        assertThat(result.isAvailable()).isTrue();
        verify(bookRepository).save(book);
    }

    @Test
    void addBook_shouldSetAvailableFalse_whenStateIsNotEmpruntable() {
        book.setState(Etat.EMPRUNTE);
        when(bookRepository.save(any())).thenReturn(book);

        Book result = bookService.addBook(book);

        assertThat(result.isAvailable()).isFalse();
    }

    // ===================== getAllBooks =====================

    @Test
    void getAllBooks_shouldReturnAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> result = bookService.getAllBooks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void getAllBooks_shouldReturnEmptyList_whenNoBooks() {
        when(bookRepository.findAll()).thenReturn(List.of());

        List<Book> result = bookService.getAllBooks();

        assertThat(result).isEmpty();
    }

    // ===================== getBookById =====================

    @Test
    void getBookById_shouldReturnBook_whenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertThat(result.getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void getBookById_shouldThrow_whenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ===================== updateBook =====================

    @Test
    void updateBook_shouldUpdateAllFields() {
        Book updated = new Book();
        updated.setTitle("Refactoring");
        updated.setAuthor("Martin Fowler");
        updated.setDescription("Another great book");
        updated.setISBN("978-0201485677");
        updated.setCover("/uploads/new.jpg");
        updated.setState(Etat.EMPRUNTABLE);

        Category cat = new Category();
        cat.setId(2L);
        updated.setCategory(cat);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Book result = bookService.updateBook(updated, 1L);

        assertThat(result.getTitle()).isEqualTo("Refactoring");
        assertThat(result.getAuthor()).isEqualTo("Martin Fowler");
        assertThat(result.isAvailable()).isTrue();
    }

    @Test
    void updateBook_shouldSetAvailableFalse_whenStateIsNotEmpruntable() {
        Book updated = new Book();
        updated.setTitle("Refactoring");
        updated.setAuthor("Martin Fowler");
        updated.setDescription("Desc");
        updated.setISBN("123");
        updated.setCover("/cover.jpg");
        updated.setState(Etat.INDISPONIBLE);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Book result = bookService.updateBook(updated, 1L);

        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    void updateBook_shouldThrow_whenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(book, 99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ===================== searchBooks =====================

    @Test
    void searchBooks_shouldReturnResults_sortedByTitleByDefault() {
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Agile");
        book2.setAuthor("Someone");

        when(bookRepository.search(any(), any(), any())).thenReturn(new ArrayList<>(List.of(book, book2)));


        List<Book> result = bookService.searchBooks("code", null, null, "title");

        assertThat(result.get(0).getTitle()).isEqualTo("Agile");
        assertThat(result.get(1).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void searchBooks_shouldReturnResults_sortedByAuthor() {
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Agile");
        book2.setAuthor("Alice");

        when(bookRepository.search(any(), any(), any())).thenReturn(new ArrayList<>(List.of(book, book2)));

        List<Book> result = bookService.searchBooks(null, null, null, "author");

        assertThat(result.get(0).getAuthor()).isEqualTo("Alice");
        assertThat(result.get(1).getAuthor()).isEqualTo("Robert C. Martin");
    }

    @Test
    void searchBooks_shouldPassNullQuery_whenQueryIsBlank() {
        when(bookRepository.search(null, null, null)).thenReturn(new ArrayList<>(List.of(book)));

        List<Book> result = bookService.searchBooks("   ", null, null, null);

        verify(bookRepository).search(null, null, null);
        assertThat(result).hasSize(1);
    }

    // ===================== deleteBookById =====================

    @Test
    void deleteBookById_shouldDelete_whenNoActiveLoans() {
        when(loanRepository.existsByBooksIdAndStatusIn(eq(1L), anyList())).thenReturn(false);

        bookService.deleteBookById(1L);

        verify(loanRepository).deleteByBooksId(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBookById_shouldThrow_whenActiveLoansExist() {
        when(loanRepository.existsByBooksIdAndStatusIn(eq(1L), anyList())).thenReturn(true);

        assertThatThrownBy(() -> bookService.deleteBookById(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("emprunts actifs");

        verify(bookRepository, never()).deleteById(any());
    }
}
