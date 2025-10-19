package ru.practicum.shareit.item.repository;

import java.util.List;
import java.util.Optional;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository {
    Item save(Item item);
    Optional<Item> findById(Long id);
    List<Item> findByOwner(Long ownerId);
    List<Item> searchByText(String text);
}