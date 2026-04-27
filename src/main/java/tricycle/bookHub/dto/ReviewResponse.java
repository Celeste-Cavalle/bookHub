package tricycle.bookHub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String comment;
    private int rating;
    private Date date;
    private String userFirstName;
    private String userLastName;
    private boolean isOwner;
    private boolean canDelete;
}