package tricycle.bookHub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tricycle.bookHub.model.Book;
import tricycle.bookHub.service.BookService;

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
    public Book getBookById(@PathVariable Long id){
        return service.getBookById(id);
    }

    // TODO faire la méthode de recherche textuelle
//    @GetMapping("/api/books/search")
//    public List<Book> getBooksBySearch(){
//        // @pathVariable ?
//        return service.gépafasfaitlaméthode;
//    }

    @PostMapping("/api/books")
    public Book addBook(Book book){
        return service.addBook(book);
    }

    @PutMapping("api/books/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book){
        return service.updateBook(book, id);
    }

    @DeleteMapping("api/books/{id}")
    public void deleteBook(@PathVariable Long id){
        service.deleteBookById(id);
    }


}
