package ru.practicum.shareit.user.service;

import java.util.List;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto create(UserDto dto);

    UserDto update(Long id, UserDto patch);

    UserDto get(Long id);

    List<UserDto> getAll();

    void delete(Long id);
}
