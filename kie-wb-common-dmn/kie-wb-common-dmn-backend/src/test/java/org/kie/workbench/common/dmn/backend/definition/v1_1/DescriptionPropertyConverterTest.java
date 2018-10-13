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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.property.dmn.Description;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DescriptionPropertyConverterTest {

    private static final String DESCRIPTION = "description";

    @Test
    public void testWBFromDMNWithNullValue() {
        assertEquals("", DescriptionPropertyConverter.wbFromDMN(null).getValue());
    }

    @Test
    public void testWBFromDMNWithNonNullValue() {
        assertEquals(DESCRIPTION, DescriptionPropertyConverter.wbFromDMN(DESCRIPTION).getValue());
    }

    @Test
    public void testDMNFromWBWithNull() {
        assertNull(DescriptionPropertyConverter.dmnFromWB(null));
    }

    @Test
    public void testDMNFromWBWithNullValue() {
        assertNull(DescriptionPropertyConverter.dmnFromWB(new Description(null)));
    }

    @Test
    public void testDMNFromWBWithEmptyValue() {
        assertNull(DescriptionPropertyConverter.dmnFromWB(new Description("")));
    }

    @Test
    public void testDMNFromWBWithNonNullValue() {
        assertEquals(DESCRIPTION, DescriptionPropertyConverter.dmnFromWB(new Description(DESCRIPTION)));
    }
}
