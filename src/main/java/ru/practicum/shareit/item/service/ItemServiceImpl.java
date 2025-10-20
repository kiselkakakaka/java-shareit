package ru.practicum.shareit.item.service;

import java.util.Collections;
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
import ru.practicum.shareit.user.repository.UserRepository;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(final ItemRepository itemRepository,
                           final UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto create(final Long ownerId, final ItemDto dto) {
        requireUserExists(ownerId);

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestException("name обязателен");
        }
        if (dto.getDescription() == null) {
            throw new BadRequestException("description обязателен");
        }
        if (dto.getAvailable() == null) {
            throw new BadRequestException("available обязателен");
        }

        Item item = ItemMapper.fromDto(dto, ownerId, null);
        Item saved = itemRepository.save(item);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(final Long ownerId, final Long itemId, final ItemDto patch) {
        requireUserExists(ownerId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: id=" + itemId));

        if (!ownerId.equals(item.getOwner())) {
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

        Item saved = itemRepository.save(item);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto getById(final Long requesterId, final Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: id=" + itemId));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getOwnerItems(final Long ownerId) {
        requireUserExists(ownerId);
        return itemRepository.findByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(final String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void requireUserExists(final Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: id=" + userId);
        }
    }
}