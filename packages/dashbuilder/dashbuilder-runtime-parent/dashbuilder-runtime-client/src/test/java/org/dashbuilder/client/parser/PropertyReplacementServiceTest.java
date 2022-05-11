/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.parser;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertyReplacementServiceTest {

    private String TEXT_WITH_PROPERTY = "Property: ${prop}";

    PropertyReplacementService replaceService;

    @Before
    public void setup() {
        replaceService = new PropertyReplacementService();
    }

    @Test
    public void testPropertiesReplacement() {
        var result = replaceService.replace(TEXT_WITH_PROPERTY, Collections.singletonMap("prop", "value"));
        assertEquals("Property: value", result);
    }

    @Test
    public void testPropertiesReplacementWithoutProperty() {
        assertEquals(TEXT_WITH_PROPERTY, TEXT_WITH_PROPERTY);
    }

}
