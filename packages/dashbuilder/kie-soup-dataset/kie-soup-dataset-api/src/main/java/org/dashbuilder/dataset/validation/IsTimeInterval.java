package org.dashbuilder.dataset.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

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
