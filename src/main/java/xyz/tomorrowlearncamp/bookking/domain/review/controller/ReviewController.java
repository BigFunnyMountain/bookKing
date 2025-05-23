package xyz.tomorrowlearncamp.bookking.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.request.ReviewRequest;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.request.ReviewUpdateRequest;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.response.ReviewResponse;
import xyz.tomorrowlearncamp.bookking.domain.review.service.ReviewService;
import xyz.tomorrowlearncamp.bookking.common.entity.AuthUser;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/v1/books/{bookId}/reviews")
    public Response<ReviewResponse> saveReview(
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return Response.success(reviewService.saveReview(authUser.getUserId(), bookId, request));
    }

    @GetMapping("/v1/books/{bookId}/reviews")
    public Response<Page<ReviewResponse>> getBookReviews(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Response.success(reviewService.getBookReviews(bookId, page, size));
    }

    @PatchMapping("/v1/books/{bookId}/reviews/{reviewId}")
    public void updateReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        reviewService.updateReview(authUser.getUserId(), bookId, reviewId, request);
    }

    @DeleteMapping("/v1/books/{bookId}/reviews/{reviewId}")
    public void deleteReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        reviewService.deleteReview(authUser.getUserId(), bookId, reviewId);
    }

    @GetMapping("/v1/reviews/my-info")
    public Response<Page<ReviewResponse>> getMyReviews(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Response.success(reviewService.getMyReviews(authUser.getUserId(), page, size));
    }
}
