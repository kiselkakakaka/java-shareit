package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService service;

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto dto) {
        log.info("Create user {}", dto.getEmail());
        return service.create(dto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable @Positive Long id, @RequestBody UserDto dto) {
        log.info("Update user {}", id);
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable @Positive Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public UserDto delete(@PathVariable @Positive Long id) {
        log.info("Delete user {}", id);
        return service.delete(id);
    }
}