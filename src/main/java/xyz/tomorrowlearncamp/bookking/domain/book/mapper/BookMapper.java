package xyz.tomorrowlearncamp.bookking.domain.book.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.AddBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateBookFromDto(UpdateBookRequestDto dto, @MappingTarget Book book);

	Book toEntity(AddBookRequestDto dto);
}

