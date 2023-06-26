package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ItemRequestDto toRequestDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor().getId(),
                request.getCreated()
        );
    }

    public static ItemRequest toDtoRequest(ItemRequestDto requestDto, User requestor) {
        return new ItemRequest(
                requestDto.getId(),
                requestDto.getDescription(),
                requestor,
                requestDto.getCreated()
        );
    }
}
