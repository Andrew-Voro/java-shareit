package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@AllArgsConstructor
@Data
public class UserDto {
    private Long id;
    private String name;
    @NonNull
    private String email;
}
