package tricycle.bookHub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tricycle.bookHub.exception.BookNotFoundException;
import tricycle.bookHub.model.Book;
import tricycle.bookHub.repository.BookRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository repository;

    public Book addBook(Book book){
       return repository.save(book);
    }

    public List<Book> getAllBooks(){
       return repository.findAll();
    }

    public Book getBookById(long id){
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Ce livre avec cet id: "+ id + " n'existe pas"));
    }

    public Book updateBook(Book book, Long id){
        Book existingBook = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Ce livre avec cet id: "+ id + " n'existe pas"));

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setDescription(book.getDescription());
        existingBook.setISBN(book.getISBN());
        existingBook.setCover(book.getCover());
        existingBook.setAvailable(book.isAvailable());
        existingBook.setState(book.getState());
        existingBook.setCategory(book.getCategory());

        return repository.save(existingBook);
    }

    public void deleteBookById(long id){
        Book existingBook = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Ce livre avec cet id: "+ id + " n'existe pas"));

        repository.delete(existingBook);
    }

 }
