package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto dto) {
        return userClient.create(dto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId,
                                         @RequestBody Map<String, Object> patch) {
        return userClient.update(userId, patch);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable long userId) {
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> all() {
        return userClient.getAll();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        return userClient.delete(userId);
    }

    public record UserDto(
            Long id,
            @NotBlank String name,
            @NotBlank @Email String email
    ) {}
}
