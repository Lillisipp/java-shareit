//package ru.practicum.shareit.request.dto;
//
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validation;
//import jakarta.validation.Validator;
//import org.junit.jupiter.api.Test;
//
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ItemRequestValidationTest {
//
//    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
//
//    @Test
//    void itemRequestCreateDto_WithBlankDescription_ShouldFailValidation() {
//        ItemRequestCreateDto dto = new ItemRequestCreateDto("");
//
//        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);
//
//        assertFalse(violations.isEmpty());
//        assertTrue(violations.stream()
//                .anyMatch(v -> v.getMessage().contains("Request description is required")));
//    }
//
//    @Test
//    void itemRequestCreateDto_WithValidData_ShouldPassValidation() {
//        ItemRequestCreateDto dto = new ItemRequestCreateDto("Need a drill");
//
//        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);
//
//        assertTrue(violations.isEmpty());
//    }
//}