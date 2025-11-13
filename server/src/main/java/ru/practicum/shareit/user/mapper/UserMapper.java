package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public final class UserMapper {
    private UserMapper() {}

    public static User toModel(UserDto dto) {
        if (dto == null) return null;
        User u = new User();
        u.setId(dto.getId());
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        return u;
    }

    public static UserDto toDto(User u) {
        if (u == null) return null;
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        return dto;
    }
}