package org.dashbuilder.dataset.validation;

import org.dashbuilder.dataset.date.TimeAmount;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * <p>JSR303 annotation implementation for <code>org.dashbuilder.common.shared.validation.IsTimeInterval</code>.</p>
 */
public class IsTimeIntervalImpl implements ConstraintValidator<IsTimeInterval, String> {

    @Override
    public void initialize(IsTimeInterval constraintAnnotation) {
        // Do nothing.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && value.trim().length() > 0) {
            try {
                TimeAmount.parse(value);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }

}
