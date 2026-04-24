package tricycle.bookHub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title")
    private String title;

    @NotBlank
    @Column(name = "author")
    private String author;

    @NotBlank
    @Column(name = "description")
    private String description;

    @NotBlank
    @Column(name = "ISBN")
    private String ISBN;

    @NotBlank
    @Column(name = "cover")
    private String cover;

    @Column(name = "available")
    private boolean isAvailable;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    @NotNull
    private Etat state;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
