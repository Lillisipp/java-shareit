package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.dto.BookingShortDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ItemOwnerDto{
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private BookingShortDto lastBooking;
        private BookingShortDto nextBooking;
}
