package tricycle.bookHub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tricycle.bookHub.dto.ReviewRequest;
import tricycle.bookHub.dto.ReviewResponse;
import tricycle.bookHub.model.*;
import tricycle.bookHub.repository.*;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository   bookRepository;
    private final UserRepository   userRepository;
    private final LoanRepository   loanRepository;

    public List<ReviewResponse> getByBook(Long bookId, Long currentUserId, boolean isAdmin) {
        return reviewRepository.findByBookId(bookId).stream()
                .map(r -> {
                    boolean isOwner   = currentUserId != null && r.getUser().getId().equals(currentUserId);
                    boolean canDelete = isOwner || isAdmin;
                    return new ReviewResponse(
                            r.getId(),
                            r.getComment(),
                            r.getRating(),
                            r.getDate(),
                            r.getUser().getFirstName(),
                            r.getUser().getLastName(),
                            isOwner,
                            canDelete
                    );
                })
                .toList();
    }

    public Double getAverageRating(Long bookId) {
        return reviewRepository.findAverageRatingByBookId(bookId);
    }

    @Transactional
    public ReviewResponse create(Long bookId, Long userId, ReviewRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Livre introuvable"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        boolean hasFinishedLoan = loanRepository.existsByBooksIdAndUserIdAndStatus(
                bookId, userId, Statut.TERMINE
        );
        if (!hasFinishedLoan) {
            throw new IllegalStateException("Vous devez avoir emprunté et rendu ce livre pour le noter");
        }

        if (reviewRepository.findByBookIdAndUserId(bookId, userId).isPresent()) {
            throw new IllegalStateException("Vous avez déjà noté ce livre");
        }

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setComment(request.getComment());
        review.setRating(request.getRating());
        review.setDate(new Date());

        Review saved = reviewRepository.save(review);

        return new ReviewResponse(
                saved.getId(),
                saved.getComment(),
                saved.getRating(),
                saved.getDate(),
                user.getFirstName(),
                user.getLastName(),
                true,
                true
        );
    }

    @Transactional
    public ReviewResponse update(Long reviewId, Long userId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Avis introuvable"));

        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Vous ne pouvez pas modifier l'avis d'un autre utilisateur");
        }

        review.setComment(request.getComment());
        review.setRating(request.getRating());
        review.setDate(new Date());

        Review saved = reviewRepository.save(review);

        return new ReviewResponse(
                saved.getId(),
                saved.getComment(),
                saved.getRating(),
                saved.getDate(),
                saved.getUser().getFirstName(),
                saved.getUser().getLastName(),
                true,
                true
        );
    }

    @Transactional
    public void delete(Long reviewId, Long userId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Avis introuvable"));

        if (!isAdmin && !review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Vous ne pouvez pas supprimer l'avis d'un autre utilisateur");
        }

        reviewRepository.delete(review);
    }
}