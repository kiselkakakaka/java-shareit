package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where i.owner.id = :ownerId order by i.id asc")
    List<Item> findByOwner(@Param("ownerId") Long ownerId);

    @Query("""
        select i from Item i
        where i.available = true
          and ( lower(i.name) like lower(concat('%', :text, '%'))
             or lower(i.description) like lower(concat('%', :text, '%')) )
        order by i.id asc
    """)
    List<Item> searchByText(@Param("text") String text);
}