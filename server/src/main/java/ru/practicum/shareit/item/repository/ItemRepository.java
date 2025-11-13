package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("""
            select i from Item i
            where i.available = true
              and (lower(i.name) like lower(concat('%', :text, '%'))
                or lower(i.description) like lower(concat('%', :text, '%')))
            """)
    Page<Item> searchAvailable(String text, Pageable pageable);

    List<Item> findAllByRequest_Id(Long requestId);

    List<Item> findAllByRequest_IdIn(List<Long> requestIds);
}