//package ru.practicum.shareit.user.dto;
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
//class UserValidationTest {
//
//    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
//
//    @Test
//    void userCreateDto_WithBlankName_ShouldFailValidation() {
//        UserCreateDto dto = new UserCreateDto("", "john@example.com");
//
//        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
//
//        assertFalse(violations.isEmpty());
//        assertTrue(violations.stream()
//                .anyMatch(v -> v.getMessage().contains("Name is required")));
//    }
//
//    @Test
//    void userCreateDto_WithBlankEmail_ShouldFailValidation() {
//        UserCreateDto dto = new UserCreateDto("John Doe", "");
//
//        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
//
//        assertFalse(violations.isEmpty());
//        assertTrue(violations.stream()
//                .anyMatch(v -> v.getMessage().contains("Email is required")));
//    }
//
//    @Test
//    void userCreateDto_WithInvalidEmail_ShouldFailValidation() {
//        UserCreateDto dto = new UserCreateDto("John Doe", "invalid-email");
//
//        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
//
//        assertFalse(violations.isEmpty());
//        assertTrue(violations.stream()
//                .anyMatch(v -> v.getMessage().contains("Invalid email format")));
//    }
//
//    @Test
//    void userCreateDto_WithValidData_ShouldPassValidation() {
//        UserCreateDto dto = new UserCreateDto("John Doe", "john@example.com");
//
//        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
//
//        assertTrue(violations.isEmpty());
//    }
//
//    @Test
//    void userUpdateDto_WithInvalidEmail_ShouldFailValidation() {
//        UserUpdateDto dto = new UserUpdateDto("John Doe", "invalid-email");
//
//        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);
//
//        assertFalse(violations.isEmpty());
//        assertTrue(violations.stream()
//                .anyMatch(v -> v.getMessage().contains("Invalid email format")));
//    }
//
//    @Test
//    void userUpdateDto_WithValidEmail_ShouldPassValidation() {
//        UserUpdateDto dto = new UserUpdateDto("John Doe", "john@example.com");
//
//        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);
//
//        assertTrue(violations.isEmpty());
//    }
//
//    @Test
//    void userUpdateDto_WithNullFields_ShouldPassValidation() {
//        UserUpdateDto dto = new UserUpdateDto(null, null);
//
//        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);
//
//        assertTrue(violations.isEmpty());
//    }
//}