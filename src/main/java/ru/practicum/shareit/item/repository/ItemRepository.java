package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
              select i
              from Item i
              where i.available = true
                and (
                  upper(i.name) like upper(concat('%', :text, '%'))
                  or upper(i.description) like upper(concat('%', :text, '%'))
                )
            """)
    List<Item> search(String text);

    @EntityGraph(attributePaths = {"owner"})
    Page<Item> findByOwner_Id(Long ownerId, Pageable pageable);
}
