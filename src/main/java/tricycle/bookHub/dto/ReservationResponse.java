package tricycle.bookHub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tricycle.bookHub.model.Etat;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private String bookTitle;
    private String bookCover;
    private Date reservationDate;
    private Etat bookState;
    private Long userId;
}