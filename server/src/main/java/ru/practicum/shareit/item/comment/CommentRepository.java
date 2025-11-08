package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByItem_IdOrderByCreatedAsc(Long itemId);

    @Query("""
                select c
                from Comment c
                  join fetch c.item i
                  join fetch c.author a
                where i.id in :ids
                order by i.id asc, c.created asc
            """)
    List<Comment> findByItemIdInOrderByCreatedAsc(@Param("ids") List<Long> ids);
}
