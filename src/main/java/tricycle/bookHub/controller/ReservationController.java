package tricycle.bookHub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tricycle.bookHub.dto.ReservationResponse;
import tricycle.bookHub.model.User;
import tricycle.bookHub.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService service;

    @PostMapping("/book/{bookId}")
    public ResponseEntity<ReservationResponse> create(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(bookId, user.getId()));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal User user) {
        service.cancel(reservationId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMy(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getMyReservations(user.getId()));
    }
}