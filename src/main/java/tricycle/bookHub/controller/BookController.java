package tricycle.bookHub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tricycle.bookHub.model.Book;
import tricycle.bookHub.repository.ReservationRepository;
import tricycle.bookHub.service.BookService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService service;
    private final ReservationRepository reservationRepository;

    @GetMapping("/api/books")
    public List<Book> getAllBooks(){
        return service.getAllBooks();
    }

    @GetMapping("/api/books/{id}")
    public ResponseEntity<Map<String, Object>> getBookById(@PathVariable Long id) {
        Book book = service.getBookById(id);
        boolean isReserved = reservationRepository.existsByBookId(id);

        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("id", book.getId());
        response.put("title", book.getTitle());
        response.put("author", book.getAuthor());
        response.put("description", book.getDescription());
        response.put("ISBN", book.getISBN());
        response.put("cover", book.getCover());
        response.put("isAvailable", book.isAvailable());
        response.put("state", book.getState());
        response.put("isReserved", isReserved);
        response.put("category", book.getCategory());

        return ResponseEntity.ok(response);
    }

    // TODO faire la méthode de recherche textuelle
    @GetMapping("/api/books/search")
    public List<Book> searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false, defaultValue = "title") String sort
    ) {
        return service.searchBooks(query, categoryId, available, sort);
    }

    @PostMapping("/api/books")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> addBook(@RequestBody Book book){
        Book savedBook = service.addBook(book);
        URI location = URI.create("/api/books/" + savedBook.getId());
        return ResponseEntity.created(location).body(savedBook);
    }

    @PutMapping("/api/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book){
        Book updatedBook = service.updateBook(book, id);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("api/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        service.deleteBookById(id);
        return ResponseEntity.noContent().build();

    }


}
