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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import org.eclipse.bpmn2.AdHocOrdering;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdHocSubProcessPropertyReaderTest {

    private static final String SCRIPT = "SCRIPT";

    @Mock
    private AdHocSubProcess process;

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private DefinitionResolver definitionResolver;

    private AdHocSubProcessPropertyReader propertyReader;

    @Before
    public void setUp() {
        propertyReader = new AdHocSubProcessPropertyReader(process, diagram, definitionResolver);
    }

    @Test
    public void testGetAdHocCompletionConditionWithFormalExpression() {
        for (Scripts.LANGUAGE language : Scripts.LANGUAGE.values()) {
            testGetAdHocCompletionConditionWithFormalExpression(new ScriptTypeValue(language.language(), SCRIPT), language.format(), SCRIPT);
        }
    }

    @Test
    public void testGetAdHocCompletionConditionWithoutFormalExpression() {
        when(process.getCompletionCondition()).thenReturn(null);
        assertEquals(new ScriptTypeValue(Scripts.LANGUAGE.MVEL.language(), "autocomplete"), propertyReader.getAdHocCompletionCondition());
    }

    private void testGetAdHocCompletionConditionWithFormalExpression(ScriptTypeValue expectedValue, String currentLanguage, String currentBody) {
        FormalExpression formalExpression = mock(FormalExpression.class);
        when(formalExpression.getLanguage()).thenReturn(currentLanguage);
        when(formalExpression.getBody()).thenReturn(currentBody);
        when(process.getCompletionCondition()).thenReturn(formalExpression);
        assertEquals(expectedValue, propertyReader.getAdHocCompletionCondition());
    }

    @Test
    public void testGetAdHocOrderingSequential() {
        testGetAdHocOrdering("Sequential", AdHocOrdering.SEQUENTIAL);
    }

    @Test
    public void testGetAdHocOrderingParallel() {
        testGetAdHocOrdering("Parallel", AdHocOrdering.PARALLEL);
    }

    private void testGetAdHocOrdering(String expectedValue, AdHocOrdering currentOrdering) {
        when(process.getOrdering()).thenReturn(currentOrdering);
        assertEquals(expectedValue, propertyReader.getAdHocOrdering());
    }
}
