package ru.practicum.shareit.item.comment;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByItem_IdOrderByCreatedAsc(Long itemId);

    List<Comment> findByItem_IdInOrderByCreatedDesc(List<Long> itemIds);

}
