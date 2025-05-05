package xyz.tomorrowlearncamp.bookking.domain.review.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import xyz.tomorrowlearncamp.bookking.domain.review.entity.Review;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.ReviewState;

import java.util.List;
import java.util.Optional;

import static xyz.tomorrowlearncamp.bookking.domain.review.entity.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByUserAndBookAndState(Long userId, Long bookId, ReviewState state) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(review)
                .where(
                        review.userId.eq(userId),
                        review.bookId.eq(bookId),
                        review.reviewState.eq(state)
                )
                .fetchFirst();
        return fetchOne != null;
    }

    @Override
    public Page<Review> findByBookIdAndState(Long bookId, ReviewState state, Pageable pageable) {
        List<Review> content = queryFactory
                .selectFrom(review)
                .where(
                        review.bookId.eq(bookId),
                        review.reviewState.eq(state)
                )
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(review.count())
                .from(review)
                .where(
                        review.bookId.eq(bookId),
                        review.reviewState.eq(state)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, count != null ? count : 0);
    }

    @Override
    public Page<Review> findByUserIdAndState(Long userId, ReviewState state, Pageable pageable) {
        List<Review> content = queryFactory
                .selectFrom(review)
                .where(
                        review.userId.eq(userId),
                        review.reviewState.eq(state)
                )
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(review.count())
                .from(review)
                .where(
                        review.userId.eq(userId),
                        review.reviewState.eq(state)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, count != null ? count : 0);
    }

    @Override
    public Optional<Review> findByIdAndUserIdAndBookIdAndState(Long id, Long userId, Long bookId, ReviewState state) {
        Review result = queryFactory
                .selectFrom(review)
                .where(
                        review.id.eq(id),
                        review.userId.eq(userId),
                        review.bookId.eq(bookId),
                        review.reviewState.eq(state)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<Review> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable) {
        List<Review> content = queryFactory
                .selectFrom(review)
                .where(
                        review.userId.eq(userId),
                        review.deletedAt.isNull()
                )
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(review.count())
                .from(review)
                .where(
                        review.userId.eq(userId),
                        review.deletedAt.isNull()
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, count != null ? count : 0);
    }

    @Override
    public Optional<Review> findByIdAndDeletedAtIsNull(Long reviewId) {
        Review result = queryFactory
                .selectFrom(review)
                .where(
                        review.id.eq(reviewId),
                        review.deletedAt.isNull()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
