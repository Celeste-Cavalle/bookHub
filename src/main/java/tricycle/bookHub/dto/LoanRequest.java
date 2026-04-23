package tricycle.bookHub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class LoanRequest {

    @NotNull
    private Long bookId;

    @NotNull
    private Long userId;
}