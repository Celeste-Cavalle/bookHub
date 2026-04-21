package tricycle.bookHub.service;

import org.springframework.stereotype.Service;
import tricycle.bookHub.exception.BookNotFoundException;
import tricycle.bookHub.model.Book;
import tricycle.bookHub.repository.BookRepository;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public Book addBook(Book book){
       return repository.save(book);
    }

    public List<Book> getAllBooks(){
       return repository.findAll();
    }

    public Optional<Book> getBookById(long id){
        return repository.findById(id);
    }

    public Book updateBook(Book book, Long id){
        if(repository.findById(id).isEmpty()) {
            throw new BookNotFoundException("Ce livre avec cet id: "+ id + " n'existe pas");
        }
        return repository.save(book);
    }

    public void deleteBookById(Book book, long id){
        if(repository.findById(id).isEmpty()) {
            throw new BookNotFoundException("Ce livre avec cet id: "+ id + " n'existe pas");
        }
        repository.delete(book);
    }

 }
