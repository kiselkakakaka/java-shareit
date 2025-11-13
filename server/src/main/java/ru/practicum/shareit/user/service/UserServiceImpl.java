package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    @Override
    @Transactional
    public UserDto create(UserDto dto) {
        if (dto.getEmail() != null && repo.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Пользователь с email=" + dto.getEmail() + " уже существует");
        }
        User u = UserMapper.toModel(dto);
        return UserMapper.toDto(repo.save(u));
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto dto) {
        User u = repo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));

        if (dto.getName() != null) {
            u.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            if (repo.existsByEmailAndIdNot(dto.getEmail(), id)) {
                throw new ConflictException("Пользователь с email=" + dto.getEmail() + " уже существует");
            }
            u.setEmail(dto.getEmail());
        }
        return UserMapper.toDto(repo.save(u));
    }

    @Override
    public UserDto get(Long id) {
        return repo.findById(id).map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Override
    public List<UserDto> getAll() {
        return repo.findAll().stream().map(UserMapper::toDto).toList();
    }

    @Override
    @Transactional
    public UserDto delete(Long id) {
        User u = repo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
        repo.delete(u);
        return UserMapper.toDto(u);
    }
}