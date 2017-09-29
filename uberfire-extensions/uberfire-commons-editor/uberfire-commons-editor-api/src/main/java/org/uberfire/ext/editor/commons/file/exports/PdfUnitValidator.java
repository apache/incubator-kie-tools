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

package org.uberfire.ext.editor.commons.file.exports;

import org.uberfire.preferences.shared.PropertyValidator;
import org.uberfire.preferences.shared.impl.validation.EnumValuePropertyValidator;
import org.uberfire.preferences.shared.impl.validation.ValidationResult;

public class PdfUnitValidator implements PropertyValidator {

    private static final PdfExportPreferences.Unit[] VALUES = PdfExportPreferences.Unit.values();

    private final EnumValuePropertyValidator<PdfExportPreferences.Unit> validator;

    public PdfUnitValidator() {
        this.validator = new EnumValuePropertyValidator<>(VALUES);
    }

    @Override
    public ValidationResult validate(Object value) {
        return validator.validate(value);
    }
}
