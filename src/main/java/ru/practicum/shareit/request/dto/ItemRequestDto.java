package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;

public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestorId;
    private LocalDateTime created;

    public ItemRequestDto() {
    }

    public ItemRequestDto(Long id, String description, Long requestorId, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestorId = requestorId;
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Long getRequestorId() {
        return requestorId;
    }

    public LocalDateTime getCreated() {
        return created;
    }
}