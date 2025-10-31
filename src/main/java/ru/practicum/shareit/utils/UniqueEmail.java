package ru.practicum.shareit.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.hibernate.validator.internal.constraintvalidators.hv.UniqueElementsValidator;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UniqueElementsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    String message() default "Email уже используется";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
