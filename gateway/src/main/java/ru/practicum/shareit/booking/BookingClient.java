package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Component
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplate restTemplate) {
        super(restTemplate, serverUrl + API_PREFIX);
    }

    public ResponseEntity<Object> bookItem(Long userId, BookingShortDto request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> approve(Long ownerId, Long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, ownerId, null);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookings(Long userId, String state, Integer from, Integer size) {
        Map<String, Object> params = Map.of("state", state, "from", from, "size", size);
        String path = UriComponentsBuilder.fromPath("")
                .query("state={state}&from={from}&size={size}")
                .build(false).toString();
        return get(path, userId, params);
    }

    public ResponseEntity<Object> getOwnerBookings(Long ownerId, String state, Integer from, Integer size) {
        Map<String, Object> params = Map.of("state", state, "from", from, "size", size);
        String path = UriComponentsBuilder.fromPath("/owner")
                .query("state={state}&from={from}&size={size}")
                .build(false).toString();
        return get(path, ownerId, params);
    }
}