package xyz.tomorrowlearncamp.bookking.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
