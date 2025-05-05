package xyz.tomorrowlearncamp.bookking.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;

import java.util.Optional;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    @Query("select u from User u where u.email = :email and u.deletedAt is not null")
    boolean existsByEmailAndDeletedFalse(String email);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);


    // 삭제 여부와는 상관없이 조회하는거 (관리자)
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailIncludingDeleted(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdIncludingDeleted(@Param("id") Long id);

}