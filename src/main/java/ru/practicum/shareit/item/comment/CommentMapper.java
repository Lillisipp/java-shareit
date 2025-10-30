package ru.practicum.shareit.item.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "id", ignore = true)
    CommentDto toDto(Comment c);

    List<CommentDto> toDto(List<Comment> entities);
}
