package ru.practicum.shareit.item.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;

    public ItemServiceImpl(ItemRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        ensureUserExists(ownerId);
        validateNewItem(dto);
        ItemRequest request = null;
        Item item = ItemMapper.fromDto(dto, ownerId, request);
        Item saved = repository.save(item);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patch) {
        ensureUserExists(ownerId);
        Item item = repository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь не найдена: " + itemId));
        if (!item.getOwner().equals(ownerId)) {
            throw new ForbiddenException("Редактировать вещь может только владелец");
        }
        if (patch.getName() != null) {
            item.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            item.setDescription(patch.getDescription());
        }
        if (patch.getAvailable() != null) {
            item.setAvailable(patch.getAvailable());
        }
        repository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(Long requesterId, Long itemId) {
        return repository.findById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        ensureUserExists(ownerId);
        return repository.findByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return repository.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void ensureUserExists(Long userId) {
        userService.get(userId);
    }

    private void validateNewItem(ItemDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestException("name обязателен");
        }
        if (dto.getDescription() == null) {
            throw new BadRequestException("description обязателен");
        }
        if (dto.getAvailable() == null) {
            throw new BadRequestException("available обязателен");
        }
    }
}
