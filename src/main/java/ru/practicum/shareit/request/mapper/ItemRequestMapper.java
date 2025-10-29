package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface ItemRequestMapper {

    @Mapping(target = "requestorId", source = "requestor")
    ItemRequestDto toDto(ItemRequest r);

    List<ItemRequestDto> toDto(List<ItemRequest> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    @Mapping(target = "requestor", expression = "java(requestor.getId())")
    ItemRequest toEntity(RequestCreateDto dto, User requestor);
}
