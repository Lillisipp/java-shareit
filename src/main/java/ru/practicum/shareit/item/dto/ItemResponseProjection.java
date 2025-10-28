package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.CommentResponseProjection;

import java.util.List;

public interface ItemResponseProjection {
    Long getId();

    String getName();

    String getDescription();

    Boolean getAvailable();

    @Value("#{target.owner.getDisplayName()}")
    Long getOwnerId();

    @JsonProperty("requestId")
    Long getRequest();

    default BookingDto getLastBooking() {
        return null;
    }

    default BookingDto getNextBooking() {
        return null;
    }

    List<CommentResponseProjection> getComments();
}
