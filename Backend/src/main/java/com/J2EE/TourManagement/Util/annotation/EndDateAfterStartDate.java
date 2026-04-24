package com.J2EE.TourManagement.Util.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EndDateAfterStartDate {
    String message() default "End date phải sau start date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}