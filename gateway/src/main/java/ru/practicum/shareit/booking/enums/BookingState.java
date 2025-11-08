package ru.practicum.shareit.booking.enums;

/**
 * ALL — все брони,
 * CURRENT — текущие бронирование,
 * PAST — завершенные бронирования,
 * FUTURE — будующие бронирование,
 * WAITING — жидающие подтверждения,
 * REJECTED — отклонённые.
 */

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;


    public static BookingState parse(String status) {
        if (status == null) return BookingState.ALL;
        String normalized = status.trim().toUpperCase();
        try {
            return BookingState.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + status);
        }
    }
}
