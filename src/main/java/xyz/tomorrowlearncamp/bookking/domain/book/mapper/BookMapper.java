package xyz.tomorrowlearncamp.bookking.domain.book.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.AddBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.BookDto;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.BookSource;

@Mapper(componentModel = "spring")
public interface BookMapper {
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateBookFromDto(UpdateBookRequestDto dto, @MappingTarget Book book);

	default Book toEntity(AddBookRequestDto dto) {
	        return Book.builder()
	                .isbn(dto.getIsbn())
	                .title(dto.getTitle())
	                .subject(dto.getSubject())
	                .author(dto.getAuthor())
	                .publisher(dto.getPublisher())
	                .bookIntroductionUrl(dto.getBookIntroductionUrl())
	                .prePrice(dto.getPrePrice())
	                .page(dto.getPage())
	                .titleUrl(dto.getTitleUrl())
	                .publicationDate(dto.getPublicationDate().toString())
	                .stock(dto.getStock())
	                .source(BookSource.DIRECT)
	                .build();
	    }

	@Mapping(source = "publishPredate", target = "publicationDate")
	@Mapping(source = "eaIsbn", target = "isbn")
	@Mapping(target = "source", constant = "API")
	Book toEntity(BookDto dto);
}

