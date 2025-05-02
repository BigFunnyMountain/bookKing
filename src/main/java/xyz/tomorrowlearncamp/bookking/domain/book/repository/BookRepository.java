package xyz.tomorrowlearncamp.bookking.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT b FROM Book b WHERE b.bookId = :id")
	Book findByIdWithLock(Long id);
}
