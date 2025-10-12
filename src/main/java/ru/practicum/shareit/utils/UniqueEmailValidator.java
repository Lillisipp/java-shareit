//package ru.practicum.shareit.utils;
//
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import ru.practicum.shareit.user.dto.UserDto;
//
//@Component
//public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
//
//    @Autowired
//    private UserDto userDto;
//    @Override
//    public void initialize(UniqueEmail constraintAnnotation) {
//    }
//
//    @Override
//    public boolean isValid(String email, ConstraintValidatorContext context) {
//        if (email == null) {return true;}
//        return !userDto.existsByEmail(email);
//    }
//
//
//}
