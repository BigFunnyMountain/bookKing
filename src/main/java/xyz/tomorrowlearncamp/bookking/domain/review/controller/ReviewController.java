package xyz.tomorrowlearncamp.bookking.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.request.ReviewRequest;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.request.ReviewUpdateRequest;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.response.ReviewCreateResponse;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.response.ReviewResponse;
import xyz.tomorrowlearncamp.bookking.domain.review.service.ReviewService;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AuthUser;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/v1/books/{bookId}/reviews")
    public ResponseEntity<ReviewCreateResponse> saveReview(
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(reviewService.saveReview(authUser.getUserId(), bookId, request));
    }

    @GetMapping("/v1/books/{bookId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getBookReviews(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(reviewService.getBookReviews(bookId, page, size));
    }

    @PatchMapping("/v1/books/{bookId}/reviews/{reviewId}")
    public ResponseEntity<String> updateReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        reviewService.updateReview(authUser.getUserId(), bookId, reviewId, request);
        return ResponseEntity.ok("리뷰가 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/v1/books/{bookId}/reviews/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        reviewService.deleteReview(authUser.getUserId(), bookId, reviewId);
        return ResponseEntity.ok("리뷰가 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/v1/reviews/myinfo")
    public ResponseEntity<Page<ReviewResponse>> getMyReviews(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(reviewService.getMyReviews(authUser.getUserId(), page, size));
    }
}
