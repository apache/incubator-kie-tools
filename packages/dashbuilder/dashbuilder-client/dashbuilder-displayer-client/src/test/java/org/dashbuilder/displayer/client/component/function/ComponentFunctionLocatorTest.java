/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.component.function;

import java.util.Collections;
import java.util.Optional;

import org.dashbuilder.displayer.external.ExternalComponentFunction;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComponentFunctionLocatorTest {

    private final String F1_NAME = "f1";
    private ExternalComponentFunction f1;
    private ComponentFunctionLocator locator;

    @Before
    public void prepare() {
        locator = new ComponentFunctionLocator();
        f1 = mock(ExternalComponentFunction.class);
        when(f1.getName()).thenReturn(F1_NAME);
        locator.functions = Collections.singletonList(f1);
    }

    @Test
    public void testFindFunctionByName() {
        Optional<ExternalComponentFunction> result = locator.findFunctionByName(F1_NAME);
        assertEquals(f1, result.get());
    }

    @Test
    public void testFindFunctionByNameNotFound() {
        Optional<ExternalComponentFunction> result = locator.findFunctionByName("not found");
        assertFalse(result.isPresent());
    }

}