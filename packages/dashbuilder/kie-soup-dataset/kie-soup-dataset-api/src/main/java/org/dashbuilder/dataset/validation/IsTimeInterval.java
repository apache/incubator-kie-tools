package org.dashbuilder.dataset.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;

/**
 * <p>JSR303 annotation that checks if the property value is a valid time interval.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.CONSTRUCTOR,ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy= IsTimeIntervalImpl.class)
public @interface IsTimeInterval {
    String message() default "";

    Class[] groups() default {};

    Class[] payload() default {};

}
