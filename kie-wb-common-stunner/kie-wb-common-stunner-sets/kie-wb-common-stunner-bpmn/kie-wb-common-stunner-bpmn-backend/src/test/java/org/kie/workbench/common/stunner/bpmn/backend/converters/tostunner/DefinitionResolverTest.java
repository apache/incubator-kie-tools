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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner;

import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefinitionResolverTest {

    @Test
    public void testObtainResolutionFactor() {
        double factor = DefinitionResolver.obtainResolutionFactor();
        assertEquals(DefinitionResolver.DEFAULT_RESOLUTION, factor, 0d);
        System.setProperty(DefinitionResolver.BPMN_DIAGRAM_RESOLUTION_PROPERTY, "0.25");
        factor = DefinitionResolver.obtainResolutionFactor();
        assertEquals(0.25d, factor, 0d);
        System.clearProperty(DefinitionResolver.BPMN_DIAGRAM_RESOLUTION_PROPERTY);
    }

    @Test
    public void testCalculateResolutionFactor() {
        BPMNDiagram diagram = mock(BPMNDiagram.class);
        when(diagram.getResolution()).thenReturn(0f);
        double factor = DefinitionResolver.calculateResolutionFactor(diagram);
        assertEquals(1d, factor, 0d);
        when(diagram.getResolution()).thenReturn(250f);
        factor = DefinitionResolver.calculateResolutionFactor(diagram);
        assertEquals(0.45d, factor, 0d);
    }
}
