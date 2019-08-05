/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.api.definition.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly.ReadOnly.FALSE;
import static org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly.ReadOnly.NOT_SET;
import static org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly.ReadOnly.TRUE;

@RunWith(MockitoJUnitRunner.class)
public class TextAnnotationTest {

    private TextAnnotation textAnnotation;
    private static final String[] READONLY_FIELDS = {
            "Description",
            "Text",
            "TextFormat"};

    @Before
    public void setup() {
        textAnnotation = new TextAnnotation();
    }

    @Test
    public void testGetReadOnlyNotSet() {

        textAnnotation.setAllowOnlyVisualChange(false);
        checkIfItIsNotSet("something");
    }

    @Test
    public void testGetReadOnlyWithReadOnlyValuesAndAllowOnlyVisualChangesNotSet() {

        textAnnotation.setAllowOnlyVisualChange(false);

        for (final String readonlyField : READONLY_FIELDS) {
            checkIfItIsNotSet(readonlyField);
        }
    }

    private void checkIfItIsNotSet(final String property) {

        final DynamicReadOnly.ReadOnly actual = textAnnotation.getReadOnly(property);

        assertEquals(NOT_SET, actual);
    }

    @Test
    public void testGetReadOnlyWithReadOnlyValues() {

        textAnnotation.setAllowOnlyVisualChange(true);
        for (final String readonlyField : READONLY_FIELDS) {
            checkIfIsReadOnly(readonlyField);
        }
    }

    private void checkIfIsReadOnly(final String property) {

        final DynamicReadOnly.ReadOnly actual = textAnnotation.getReadOnly(property);

        assertEquals(TRUE, actual);
    }

    @Test
    public void testGetReadOnlyWithNotReadOnlyValues() {

        textAnnotation.setAllowOnlyVisualChange(true);

        checkIfItIsNotReadOnly("Font");
        checkIfItIsNotReadOnly("Something");
    }

    private void checkIfItIsNotReadOnly(final String property) {

        final DynamicReadOnly.ReadOnly actual = textAnnotation.getReadOnly(property);

        assertEquals(FALSE, actual);
    }
}