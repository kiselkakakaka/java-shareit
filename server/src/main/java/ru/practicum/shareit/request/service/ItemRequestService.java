package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestOutDto create(Long userId, ItemRequestCreateDto dto);

    List<ItemRequestOutDto> getOwn(Long userId);

    List<ItemRequestOutDto> getAll(Long userId, int from, int size);

    ItemRequestOutDto getById(Long userId, Long requestId);
}
