package tricycle.bookHub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tricycle.bookHub.dto.ReviewRequest;
import tricycle.bookHub.dto.ReviewResponse;
import tricycle.bookHub.model.Role;
import tricycle.bookHub.model.User;
import tricycle.bookHub.service.ReviewService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books/{bookId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getByBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User user) {
        Long userId  = user != null ? user.getId() : null;
        boolean isAdmin = user != null && user.getRole() == Role.ADMIN;
        return ResponseEntity.ok(service.getByBook(bookId, userId, isAdmin));
    }

    @GetMapping("/average")
    public ResponseEntity<Map<String, Double>> getAverage(@PathVariable Long bookId) {
        return ResponseEntity.ok(Map.of("average", service.getAverageRating(bookId) != null
                ? service.getAverageRating(bookId) : 0.0));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(bookId, user.getId(), request));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(service.update(reviewId, user.getId(), request));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User user) {
        boolean isAdmin = user.getRole() == Role.ADMIN;
        service.delete(reviewId, user.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }
}