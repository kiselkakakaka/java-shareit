package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @RequestBody ItemDto dto) {
        return itemService.create(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto dto) {
        return itemService.update(ownerId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                           @PathVariable Long itemId) {
        return itemService.getById(requesterId, itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                       @RequestParam(name = "from", defaultValue = "0") int from,
                                       @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemService.getOwnerItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text,
                                @RequestParam(name = "from", defaultValue = "0") int from,
                                @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody CommentDto body) {
        return itemService.addComment(userId, itemId, body.getText());
    }
}
