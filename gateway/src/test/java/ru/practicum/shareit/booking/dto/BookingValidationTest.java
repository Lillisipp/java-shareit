package ru.practicum.shareit.booking.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    void bookItemRequestDto_WithValidData_ShouldPassValidation() {
        BookItemRequestDto dto = new BookItemRequestDto(
                1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}