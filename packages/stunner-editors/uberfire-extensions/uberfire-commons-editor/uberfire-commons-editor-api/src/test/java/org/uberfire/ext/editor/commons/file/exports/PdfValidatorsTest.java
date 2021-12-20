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
 */

package org.uberfire.ext.editor.commons.file.exports;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PdfValidatorsTest {

    private static final PdfOrientationValidator ORIENTATION_VALIDATOR = new PdfOrientationValidator();
    private static final PdfFormatValidator FORMAT_VALIDATOR = new PdfFormatValidator();
    private static final PdfUnitValidator UNIT_VALIDATOR = new PdfUnitValidator();

    @Test
    public void testOrientation() {
        assertTrue(ORIENTATION_VALIDATOR.validate(PdfExportPreferences.Orientation.PORTRAIT.name()).isValid());
        assertTrue(ORIENTATION_VALIDATOR.validate(PdfExportPreferences.Orientation.LANDSCAPE.name()).isValid());
        assertFalse(ORIENTATION_VALIDATOR.validate("someOrientation").isValid());
    }

    @Test
    public void testFormat() {
        assertTrue(FORMAT_VALIDATOR.validate(PdfExportPreferences.Format.A0.name()).isValid());
        assertTrue(FORMAT_VALIDATOR.validate(PdfExportPreferences.Format.A10.name()).isValid());
        assertTrue(FORMAT_VALIDATOR.validate(PdfExportPreferences.Format.B0.name()).isValid());
        assertTrue(FORMAT_VALIDATOR.validate(PdfExportPreferences.Format.B10.name()).isValid());
        assertTrue(FORMAT_VALIDATOR.validate(PdfExportPreferences.Format.C0.name()).isValid());
        assertTrue(FORMAT_VALIDATOR.validate(PdfExportPreferences.Format.C10.name()).isValid());
        assertFalse(FORMAT_VALIDATOR.validate("A11").isValid());
    }

    @Test
    public void testUnit() {
        assertTrue(UNIT_VALIDATOR.validate(PdfExportPreferences.Unit.MM.name()).isValid());
        assertTrue(UNIT_VALIDATOR.validate(PdfExportPreferences.Unit.CM.name()).isValid());
        assertTrue(UNIT_VALIDATOR.validate(PdfExportPreferences.Unit.IN.name()).isValid());
        assertTrue(UNIT_VALIDATOR.validate(PdfExportPreferences.Unit.PT.name()).isValid());
        assertFalse(UNIT_VALIDATOR.validate("xs").isValid());
    }
}
