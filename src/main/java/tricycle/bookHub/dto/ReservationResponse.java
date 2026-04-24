package tricycle.bookHub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private String bookTitle;
    private String bookCover;
    private Date reservationDate;
}