package xyz.tomorrowlearncamp.bookking.domain.book.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.AddBookRequest;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookRequest;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.BookDto;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.BookSource;

@Mapper(componentModel = "spring")
public interface BookMapper {
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateBookFromDto(UpdateBookRequest dto, @MappingTarget Book book);

	default Book toEntity(AddBookRequest dto) {
	        return Book.builder()
	                .title(dto.getTitle())
	                .subject(dto.getSubject())
	                .author(dto.getAuthor())
	                .publisher(dto.getPublisher())
	                .bookIntroductionUrl(dto.getBookIntroductionUrl())
	                .prePrice(dto.getPrePrice())
	                .publicationDate(dto.getPublicationDate())
	                .stock(0L)
	                .source(BookSource.DIRECT)
	                .build();
	    }

	@Mapping(source = "publishPredate", target = "publicationDate")
	@Mapping(target = "source", constant = "API")
	Book toEntity(BookDto dto);
}

