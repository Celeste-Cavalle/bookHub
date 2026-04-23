package tricycle.bookHub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tricycle.bookHub.model.Book;
import tricycle.bookHub.service.BookService;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService service;

    @GetMapping("/api/books")
    public List<Book> getAllBooks(){
        return service.getAllBooks();
    }

    @GetMapping("/api/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id){
        Book book = service.getBookById(id);
        if(book != null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    // TODO faire la méthode de recherche textuelle
//    @GetMapping("/api/books/search")
//    public List<Book> getBooksBySearch(){
//        // @pathVariable ?
//        return service.gépafasfaitlaméthode;
//    }

    @PostMapping("/api/books")
    public ResponseEntity<Book> addBook(@RequestBody Book book){
        Book savedBook = service.addBook(book);
        URI location = URI.create("/api/books/" + savedBook.getId());
        return ResponseEntity.created(location).body(savedBook);
    }

    @PutMapping("/api/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book){
        Book updatedBook = service.updateBook(book, id);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("api/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        service.deleteBookById(id);
        return ResponseEntity.noContent().build();

    }


}
