package tricycle.bookHub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tricycle.bookHub.dto.ReviewRequest;
import tricycle.bookHub.dto.ReviewResponse;
import tricycle.bookHub.model.*;
import tricycle.bookHub.repository.BookRepository;
import tricycle.bookHub.repository.LoanRepository;
import tricycle.bookHub.repository.ReviewRepository;
import tricycle.bookHub.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private BookRepository bookRepository;
    @Mock private UserRepository userRepository;
    @Mock private LoanRepository loanRepository;

    @InjectMocks private ReviewService reviewService;

    private Book book;
    private User user;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        reviewRequest = new ReviewRequest();
        reviewRequest.setComment("Super livre !");
        reviewRequest.setRating(5);
    }

    // ===================== getByBook =====================

    @Test
    void getByBook_shouldReturnReviews_withOwnerFlagForCurrentUser() {
        Review review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setBook(book);
        review.setComment("Super !");
        review.setRating(5);
        review.setDate(new Date());

        when(reviewRepository.findByBookId(1L)).thenReturn(List.of(review));

        List<ReviewResponse> result = reviewService.getByBook(1L, 1L, false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isOwner()).isTrue();
        assertThat(result.get(0).isCanDelete()).isTrue();
    }

    @Test
    void getByBook_shouldReturnReviews_withoutOwnerFlag_forOtherUser() {
        Review review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setBook(book);
        review.setComment("Super !");
        review.setRating(5);
        review.setDate(new Date());

        when(reviewRepository.findByBookId(1L)).thenReturn(List.of(review));

        List<ReviewResponse> result = reviewService.getByBook(1L, 2L, false);

        assertThat(result.get(0).isOwner()).isFalse();
        assertThat(result.get(0).isCanDelete()).isFalse();
    }

    @Test
    void getByBook_shouldAllowAdminToDelete_evenIfNotOwner() {
        Review review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setBook(book);
        review.setComment("Nul");
        review.setRating(1);
        review.setDate(new Date());

        when(reviewRepository.findByBookId(1L)).thenReturn(List.of(review));

        List<ReviewResponse> result = reviewService.getByBook(1L, 99L, true);

        assertThat(result.get(0).isOwner()).isFalse();
        assertThat(result.get(0).isCanDelete()).isTrue();
    }

    // ===================== getAverageRating =====================

    @Test
    void getAverageRating_shouldReturnAverage() {
        when(reviewRepository.findAverageRatingByBookId(1L)).thenReturn(4.2);

        Double avg = reviewService.getAverageRating(1L);

        assertThat(avg).isEqualTo(4.2);
    }

    @Test
    void getAverageRating_shouldReturnNull_whenNoReviews() {
        when(reviewRepository.findAverageRatingByBookId(1L)).thenReturn(null);

        Double avg = reviewService.getAverageRating(1L);

        assertThat(avg).isNull();
    }

    // ===================== create =====================

    @Test
    void create_shouldCreateReview_whenUserHasFinishedLoan() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByBooksIdAndUserIdAndStatus(1L, 1L, Statut.TERMINE)).thenReturn(true);
        when(reviewRepository.findByBookIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        when(reviewRepository.save(any())).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        ReviewResponse response = reviewService.create(1L, 1L, reviewRequest);

        assertThat(response).isNotNull();
        assertThat(response.getComment()).isEqualTo("Super livre !");
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.isOwner()).isTrue();
    }

    @Test
    void create_shouldThrow_whenUserHasNoFinishedLoan() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByBooksIdAndUserIdAndStatus(1L, 1L, Statut.TERMINE)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.create(1L, 1L, reviewRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("emprunté et rendu");
    }

    @Test
    void create_shouldThrow_whenUserAlreadyReviewedBook() {
        Review existing = new Review();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByBooksIdAndUserIdAndStatus(1L, 1L, Statut.TERMINE)).thenReturn(true);
        when(reviewRepository.findByBookIdAndUserId(1L, 1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> reviewService.create(1L, 1L, reviewRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà noté");
    }

    @Test
    void create_shouldThrow_whenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.create(99L, 1L, reviewRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Livre introuvable");
    }

    // ===================== update =====================

    @Test
    void update_shouldUpdateReview_whenOwner() {
        Review review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setBook(book);
        review.setComment("Pas terrible");
        review.setRating(2);
        review.setDate(new Date());

        ReviewRequest updateRequest = new ReviewRequest();
        updateRequest.setComment("Finalement super !");
        updateRequest.setRating(5);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenReturn(review);

        ReviewResponse response = reviewService.update(1L, 1L, updateRequest);

        assertThat(response.getComment()).isEqualTo("Finalement super !");
        assertThat(response.getRating()).isEqualTo(5);
    }

    @Test
    void update_shouldThrow_whenUserIsNotOwner() {
        Review review = new Review();
        review.setId(1L);
        review.setUser(user);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.update(1L, 2L, reviewRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("modifier l'avis d'un autre");
    }

    @Test
    void update_shouldThrow_whenReviewNotFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.update(99L, 1L, reviewRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Avis introuvable");
    }

    // ===================== delete =====================

    @Test
    void delete_shouldDelete_whenOwner() {
        Review review = new Review();
        review.setId(1L);
        review.setUser(user);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.delete(1L, 1L, false);

        verify(reviewRepository).delete(review);
    }

    @Test
    void delete_shouldDelete_whenAdmin() {
        User otherUser = new User();
        otherUser.setId(2L);

        Review review = new Review();
        review.setId(1L);
        review.setUser(otherUser);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.delete(1L, 1L, true);

        verify(reviewRepository).delete(review);
    }

    @Test
    void delete_shouldThrow_whenNotOwnerAndNotAdmin() {
        User otherUser = new User();
        otherUser.setId(2L);

        Review review = new Review();
        review.setId(1L);
        review.setUser(otherUser);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.delete(1L, 1L, false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("supprimer l'avis d'un autre");
    }

    @Test
    void delete_shouldThrow_whenReviewNotFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.delete(99L, 1L, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Avis introuvable");
    }
}
