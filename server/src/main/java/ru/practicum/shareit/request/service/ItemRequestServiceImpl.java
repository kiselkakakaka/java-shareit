package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepo;
    private final UserRepository userRepo;
    private final ItemRepository itemRepo;

    private static PageRequest page(int from, int size) {
        return PageRequest.of(from / size, size);
    }

    @Override
    @Transactional
    public ItemRequestOutDto create(Long userId, ItemRequestCreateDto dto) {
        User requester = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        ItemRequest request = ItemRequestMapper.toEntity(dto, requester);
        ItemRequest saved = requestRepo.save(request);

        List<Item> items = itemRepo.findAllByRequest_Id(saved.getId());
        return ItemRequestMapper.toOutDto(saved, items);
    }

    @Override
    public List<ItemRequestOutDto> getOwn(Long userId) {
        checkUser(userId);

        List<ItemRequest> requests =
                requestRepo.findAllByRequesterIdOrderByCreatedDesc(userId);

        Map<Long, List<Item>> itemsByRequest = loadItemsForRequests(requests);

        return requests.stream()
                .map(r -> ItemRequestMapper.toOutDto(
                        r,
                        itemsByRequest.getOrDefault(r.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestOutDto> getAll(Long userId, int from, int size) {
        checkUser(userId);

        List<ItemRequest> requests =
                requestRepo.findByRequesterIdNotOrderByCreatedDesc(userId, page(from, size))
                        .getContent();

        Map<Long, List<Item>> itemsByRequest = loadItemsForRequests(requests);

        return requests.stream()
                .map(r -> ItemRequestMapper.toOutDto(
                        r,
                        itemsByRequest.getOrDefault(r.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestOutDto getById(Long userId, Long requestId) {
        checkUser(userId);

        ItemRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request not found: " + requestId));

        List<Item> items = itemRepo.findAllByRequest_Id(requestId);
        return ItemRequestMapper.toOutDto(req, items);
    }


    private void checkUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
    }

    private Map<Long, List<Item>> loadItemsForRequests(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemRepo.findAllByRequest_IdIn(requestIds);

        return items.stream()
                .filter(i -> i.getRequest() != null)
                .collect(Collectors.groupingBy(i -> i.getRequest().getId()));
    }
}