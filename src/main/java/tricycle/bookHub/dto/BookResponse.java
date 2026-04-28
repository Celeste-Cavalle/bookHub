package tricycle.bookHub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tricycle.bookHub.model.Book;
import tricycle.bookHub.model.Etat;

@Getter
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String description;
    private String ISBN;
    private String cover;
    private boolean isAvailable;
    private Etat state;
    private boolean isReserved;
    private Object category;
}