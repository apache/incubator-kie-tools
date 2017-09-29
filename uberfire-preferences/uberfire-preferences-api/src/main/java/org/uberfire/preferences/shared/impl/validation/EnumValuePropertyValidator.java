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

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.uberfire.preferences.shared.PropertyValidator;

public class EnumValuePropertyValidator<T extends Enum<?>> implements PropertyValidator {

    private final ConstrainedValuesValidator<String> validator;

    @SuppressWarnings("unchecked")
    public EnumValuePropertyValidator(final T[] values) {
        this.validator = new ConstrainedValuesValidator<>(() -> toCollection(values),
                                                          EnumValuePropertyValidator::parseString);
    }

    @Override
    public ValidationResult validate(final Object value) {
        return validator.validate(value);
    }

    public static <T extends Enum<?>> String format(final T value) {
        return value.name().toUpperCase();
    }

    public static String parseString(final Object value) {
        return value.toString().toUpperCase();
    }

    private static <T extends Enum<?>> Collection<String> toCollection(final T[] values) {
        return Arrays.stream(values)
                .map(EnumValuePropertyValidator::format)
                .collect(Collectors.toList());
    }
}
