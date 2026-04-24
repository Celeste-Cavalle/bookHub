package tricycle.bookHub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loanDate")
    private Date loanDate;

    @Column(name = "returnDate")
    private Date returnDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status")
    private Statut status;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book books;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
