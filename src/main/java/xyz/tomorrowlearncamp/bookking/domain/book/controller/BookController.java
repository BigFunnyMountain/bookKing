package xyz.tomorrowlearncamp.bookking.domain.book.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.AddBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.SearchBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookStockRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.BookResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.service.BookService;
import xyz.tomorrowlearncamp.bookking.domain.book.service.SearchBookService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookController {
    private final BookService bookService;
    private final SearchBookService searchBookService;

    //ToDo : 국립도서관API 호출 Post
    @PostMapping("/v1/books/search")
    public ResponseEntity<String> searchBooks(@RequestBody SearchBookRequestDto requestDto){
            String result = searchBookService.searchBooks(requestDto);
            return ResponseEntity.ok(result);
    }

    //ToDo : 권한 체크 필요
    @PostMapping("/v1/books")
    public ResponseEntity<Long> addBook(@RequestBody @Valid AddBookRequestDto requestDto) {
        return ResponseEntity.ok(bookService.addBook(requestDto));
    }

    //ToDo : 권한 체크 필요
    @PatchMapping("/v1/books/{bookId}")
    public ResponseEntity<Void> updateBook(
        @PathVariable("bookId") Long bookId,
        @RequestBody @Valid UpdateBookRequestDto requestDto
    ){
        bookService.updateBook(bookId, requestDto);
        return ResponseEntity.ok().build();
    }

    //ToDo : 권한 체크 필요
    @PatchMapping("/v1/books/{bookId}/stock")
    public ResponseEntity<Void> updateBookStock(
        @PathVariable("bookId") Long bookId,
        @RequestBody @Valid UpdateBookStockRequestDto requestDto
    ){
        bookService.updateBookStock(bookId, requestDto);
        return ResponseEntity.ok().build();
    }

        //ToDo : 권한 체크 필요
    @DeleteMapping("/v1/books/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable("bookId") Long bookId){
        bookService.deleteBook(bookId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/v1/books")
    public ResponseEntity<List<BookResponseDto>> getAllBooks(){
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/v1/books/{bookId}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable("bookId") Long bookId){
        return ResponseEntity.ok(bookService.getBookById(bookId));
    }
}
