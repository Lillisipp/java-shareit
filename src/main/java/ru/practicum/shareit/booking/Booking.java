package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_booking")
    private Long id;

    @Column(name="start_time")
    private LocalDateTime start;

    @Column(name="end_time")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name="id_item")
    private Item item;

    @ManyToOne
    @JoinColumn(name="id_booker")
    private User booker;

//    @ManyToOne
//    @JoinColumn(name = "id_owner")
//    private User owner;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

}
