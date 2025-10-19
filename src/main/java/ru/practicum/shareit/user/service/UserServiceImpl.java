package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDto create(UserDto dto) {
        validateEmail(dto.getEmail());
        if (repository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email уже занят: " + dto.getEmail());
        }
        User user = UserMapper.fromDto(dto);
        User saved = repository.save(user);
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto update(Long id, UserDto patch) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));

        if (patch.getEmail() != null) {
            validateEmail(patch.getEmail());
            if (repository.existsByEmailForOther(patch.getEmail(), id)) {
                throw new ConflictException("Email уже используется другим пользователем: " + patch.getEmail());
            }
            user.setEmail(patch.getEmail());
        }
        if (patch.getName() != null) {
            user.setName(patch.getName());
        }
        repository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto get(Long id) {
        return repository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new BadRequestException("Некорректный email");
        }
    }
}