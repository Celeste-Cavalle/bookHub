package tricycle.bookHub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;
import java.util.List;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "loanDate")
    private Date loanDate;

    @NotBlank
    @Column(name = "returnDate")
    private Date returnDate;

    @NotBlank
    @Column(name = "statut")
    private Statut statut;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private List<Book> books;

}
