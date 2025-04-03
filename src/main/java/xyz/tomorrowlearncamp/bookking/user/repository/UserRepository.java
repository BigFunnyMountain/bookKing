package xyz.tomorrowlearncamp.bookking.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.tomorrowlearncamp.bookking.user.entity.User;

import java.util.Optional;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
