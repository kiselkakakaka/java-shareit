package ru.practicum.shareit.user;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public UserDto create(@RequestBody /*@Valid*/ UserDto dto) {
        return service.create(dto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long id, @RequestBody UserDto patch) {
        return service.update(id, patch);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
