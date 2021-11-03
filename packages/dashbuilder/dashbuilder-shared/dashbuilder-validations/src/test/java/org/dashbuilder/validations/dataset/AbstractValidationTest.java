package org.dashbuilder.validations.dataset;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;

import static org.mockito.Mockito.*;

public class AbstractValidationTest {

    protected Validator validator;

    public void setup() {
        validator = spy(new ValidatorMock());
    }

    // Mockito is not able to mock javax.validation.Validator, so let's create an empty implementation and spy it.
    class ValidatorMock implements Validator {

        @Override
        public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
            return null;
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
            return null;
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
            return null;
        }

        @Override
        public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> type) {
            return null;
        }
    }
}
