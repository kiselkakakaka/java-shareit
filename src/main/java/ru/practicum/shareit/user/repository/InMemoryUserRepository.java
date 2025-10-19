package ru.practicum.shareit.user.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0L);

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(seq.incrementAndGet());
        }
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return storage.values().stream()
                .anyMatch(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public boolean existsByEmailForOther(String email, Long excludeId) {
        return storage.values().stream()
                .anyMatch(u -> !u.getId().equals(excludeId)
                        && u.getEmail() != null
                        && u.getEmail().equalsIgnoreCase(email));
    }
}
