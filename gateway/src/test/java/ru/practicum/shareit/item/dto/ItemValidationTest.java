//package ru.practicum.shareit.item.dto;
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
//class ItemValidationTest {
//
//    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
//
//    @Test
//    void itemCreateDto_WithBlankName_ShouldFailValidation() {
//        ItemCreateDto dto = new ItemCreateDto("", "Description", true, null);
//
//        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
//
//        assertFalse(violations.isEmpty());
//        assertTrue(violations.stream()
//                .anyMatch(v -> v.getMessage().contains("Item name is required")));
//    }
//
//    @Test
//    void itemCreateDto_WithBlankDescription_ShouldFailValidation() {
//        ItemCreateDto dto = new ItemCreateDto("Name", "", true, null);
//
//        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
//
//        assertFalse(violations.isEmpty());
//        assertTrue(violations.stream()
//                .anyMatch(v -> v.getMessage().contains("Item description is required")));
//    }
//
//    @Test
//    void itemCreateDto_WithNullAvailable_ShouldFailValidation() {
//        ItemCreateDto dto = new ItemCreateDto("Name", "Description", null, null);
//
//        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
//
//        assertFalse(violations.isEmpty());
//        assertTrue(violations.stream()
//                .anyMatch(v -> v.getMessage().contains("Available status is required")));
//    }
//
//    @Test
//    void itemCreateDto_WithValidData_ShouldPassValidation() {
//        ItemCreateDto dto = new ItemCreateDto("Drill", "Powerful drill", true, null);
//
//        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
//
//        assertTrue(violations.isEmpty());
//    }
//
//    @Test
//    void commentCreateDto_WithBlankText_ShouldFailValidation() {
//        CommentCreateDto dto = new CommentCreateDto("");
//
//        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);
//
//        assertFalse(violations.isEmpty());
//        assertTrue(violations.stream()
//                .anyMatch(v -> v.getMessage().contains("Comment text is required")));
//    }
//
//    @Test
//    void commentCreateDto_WithValidData_ShouldPassValidation() {
//        CommentCreateDto dto = new CommentCreateDto("Great item!");
//
//        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);
//
//        assertTrue(violations.isEmpty());
//    }
//}