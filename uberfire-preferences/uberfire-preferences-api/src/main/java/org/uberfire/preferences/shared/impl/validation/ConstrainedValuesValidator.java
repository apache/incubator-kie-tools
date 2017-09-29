/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.uberfire.preferences.shared.impl.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

import org.uberfire.preferences.shared.PropertyValidator;

public class ConstrainedValuesValidator<T> implements PropertyValidator {

    static final String NOT_ALLOWED_VALIDATION_KEY = "PropertyValidator.ConstrainedValuesValidator.NotAllowed";

    private final Supplier<Collection<T>> allowedValuesSupplier;
    private final Function<Object, T> valueParser;

    public ConstrainedValuesValidator(final Supplier<Collection<T>> allowedValuesSupplier,
                                      final Function<Object, T> valueParser) {
        this.allowedValuesSupplier = allowedValuesSupplier;
        this.valueParser = valueParser;
    }

    @Override
    public ValidationResult validate(final Object raw) {
        final T value = valueParser.apply(raw);
        return allowedValuesSupplier.get().stream()
                .anyMatch(v -> v.equals(value)) ?
                new ValidationResult(true,
                                     Collections.emptyList()) :
                new ValidationResult(false,
                                     Collections.singletonList(NOT_ALLOWED_VALIDATION_KEY));
    }
}
