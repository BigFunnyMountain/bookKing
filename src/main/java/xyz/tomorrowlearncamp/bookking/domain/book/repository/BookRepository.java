package xyz.tomorrowlearncamp.bookking.domain.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

	@Query("select b from Book b "
		+ "where lower(b.title) like lower(concat('%', :keyword, '%')) "
		+ "or lower(cast(b.author as string)) like lower(concat('%', :keyword, '%')) "
		+ "or lower(b.publisher) like lower(concat('%', :keyword, '%')) "
		+ "or lower(b.subject) like lower(concat('%', :keyword, '%'))"
		+ "order by b.bookId asc")
	Page<Book> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
