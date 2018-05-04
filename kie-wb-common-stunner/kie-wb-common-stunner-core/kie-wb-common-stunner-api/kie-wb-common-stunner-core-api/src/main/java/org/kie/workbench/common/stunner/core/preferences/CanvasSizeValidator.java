/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 */

package org.kie.workbench.common.stunner.core.preferences;

import java.util.Collections;
import java.util.List;

import org.uberfire.preferences.shared.PropertyValidator;
import org.uberfire.preferences.shared.impl.validation.ValidationResult;

public abstract class CanvasSizeValidator implements PropertyValidator<String> {

    private static final List<String> MESSAGE_BUNDLE_KEYS = Collections.singletonList("PropertyValidator.CanvasSizeValidator.InvalidOutOfRange");

    int maxValue;

    int minValue;

    public CanvasSizeValidator(int minValue,
                               int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public ValidationResult validate(String value) {
        int intValue;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return new ValidationResult(false,
                                        messagesBundleKeys());
        }
        if (intValue < minValue || intValue > maxValue) {
            return new ValidationResult(false,
                                        messagesBundleKeys());
        } else {
            return new ValidationResult(true,
                                        Collections.emptyList());
        }
    }

    protected List<String> messagesBundleKeys() {
        return MESSAGE_BUNDLE_KEYS;
    }
}
