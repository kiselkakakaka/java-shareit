package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookItemRequestDto> {
    @Override
    public boolean isValid(BookItemRequestDto v, ConstraintValidatorContext c) {
        if (v == null || v.getStart() == null || v.getEnd() == null) return true; // дадут @NotNull
        return v.getStart().isBefore(v.getEnd());
    }
}
