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

package org.kie.workbench.common.stunner.forms.backend.gen;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormGenerationModelProvidersTest {

    @Mock
    private FormGenerationModelProvider provider1;

    @Mock
    private FormGenerationModelProvider provider2;

    @Mock
    private Diagram diagram1;

    @Mock
    private Diagram diagram2;

    private FormGenerationModelProviders tested;

    @Before
    public void init() {
        when(provider1.accepts(eq(diagram1))).thenReturn(true);
        when(provider1.accepts(eq(diagram2))).thenReturn(false);
        when(provider2.accepts(eq(diagram1))).thenReturn(false);
        when(provider2.accepts(eq(diagram2))).thenReturn(true);
        Collection<FormGenerationModelProvider<?>> providers = Arrays.asList(provider1, provider2);
        tested = new FormGenerationModelProviders(providers);
    }

    @Test
    public void testGetModelProviders() {
        assertEquals(provider1, tested.getModelProvider(diagram1));
        assertEquals(provider2, tested.getModelProvider(diagram2));
    }
}
