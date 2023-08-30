/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.validation.client.dynamic;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.inject.Alternative;
import javax.validation.Constraint;
import javax.validation.ConstraintViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates values with for any {@link Constraint Constraints} that were available at compile-time.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
@SuppressWarnings("unchecked")
@Alternative
public class DynamicValidator {

  private static final Logger logger = LoggerFactory.getLogger(DynamicValidator.class);

  private final Map<DynamicValidatorKey, GeneratedDynamicValidator<?>> validators = new HashMap<>();

  /**
   * Used by generated code to add {@link GeneratedDynamicValidator GeneratedDynamicValidators}. Allows overriding of
   * validators (logged at warning level).
   */
  public void addValidator(final String constraint, final String valueType, final GeneratedDynamicValidator<?> validator) {
    final DynamicValidatorKey key = new DynamicValidatorKey(constraint, valueType);
    if (validators.containsKey(key)) {
      logger.warn("Validator for " + key + " is being overridden.\n\tPrevious: "
              + validators.get(key).getClass().getSimpleName() + "\n\tNew: " + validator.getClass().getSimpleName());
    }
    validators.put(key, validator);
  }

  /**
   * @param constraint
   *          The {@link Constraint} annotation type.
   * @param parameters
   *          A map of parameter values for the given {@link Constraint}. Keys are names of the parameters in the
   *          {@link Constraint} annotation source.
   * @param value
   *          The value being validated.
   * @return An empty set if the given value is valid, or a set with one or more {@link ConstraintViolation
   *         ConstraintViolations} if the given value is invalid.
   * @throws IllegalArgumentException
   *           If a validator can not be found for the given {@link Constraint} and value type.
   */
  public <T> Set<ConstraintViolation<T>> validate(final Class<? extends Annotation> constraint,
          final Map<String, Object> parameters, final T value) {
    return validate(constraint.getName(), parameters, value);
  }

  /**
   * @param constraint
   *          The fully-qualified class name of a {@link Constraint}.
   * @param parameters
   *          A map of parameter values for the given {@link Constraint}. Keys are names of the parameters in the
   *          {@link Constraint} annotation source.
   * @param value
   *          The value being validated.
   * @return An empty set if the given value is valid, or a set with one or more {@link ConstraintViolation
   *         ConstraintViolations} if the given value is invalid.
   * @throws IllegalArgumentException
   *           If a validator can not be found for the given {@link Constraint} and value type.
   */
  public <T> Set<ConstraintViolation<T>> validate(final String constraint, final Map<String, Object> parameters, final T value) {
    final GeneratedDynamicValidator<T> dynamicValidator = getValidatorOrThrow(constraint, value);
    return dynamicValidator.validate(parameters, value);
  }

  private <T> GeneratedDynamicValidator<T> getValidatorOrThrow(final String constraint, final T value) {
    final DynamicValidatorKey originalKey = new DynamicValidatorKey(constraint, getValueClassName(value));
    DynamicValidatorKey key = originalKey;
    GeneratedDynamicValidator<T> dynamicValidator = (GeneratedDynamicValidator<T>) validators.get(key);

    while (dynamicValidator == null && key.hasAlias()) {
      key = key.getAlias().get();
      dynamicValidator = (GeneratedDynamicValidator<T>) validators.get(key);
    }

    if (dynamicValidator == null) {
      throw new IllegalArgumentException("There is no validator for " + originalKey);
    }
    else {      
      return dynamicValidator;
    }
  }

  private <T> String getValueClassName(final T value) {
    return Optional
            .ofNullable(value)
            .map(v -> v.getClass().getName())
            .orElseGet(() -> Object.class.getName());
  }
}
