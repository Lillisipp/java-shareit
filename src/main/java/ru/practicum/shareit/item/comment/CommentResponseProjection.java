package ru.practicum.shareit.item.comment;

import java.time.LocalDateTime;

public interface CommentResponseProjection {
    Long getIdComment();

    String getAuthorName();

    LocalDateTime getCreated();

    String getText();
}
