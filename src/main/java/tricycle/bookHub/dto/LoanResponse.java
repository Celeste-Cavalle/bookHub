package tricycle.bookHub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tricycle.bookHub.model.Statut;

import java.util.Date;

@Getter
@AllArgsConstructor
public class LoanResponse {
    private Long id;
    private String bookTitle;
    private String userEmail;
    private Date loanDate;
    private Date returnDate;
    private Statut status;
}