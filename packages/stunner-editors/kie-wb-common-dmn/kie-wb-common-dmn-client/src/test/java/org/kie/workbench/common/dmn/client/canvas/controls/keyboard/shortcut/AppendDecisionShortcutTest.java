/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.canvas.controls.keyboard.shortcut;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.client.canvas.controls.actions.DMNCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.mockito.Mock;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.CONTROL;
import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.D;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AppendDecisionShortcutTest {

    @Mock
    private ToolboxDomainLookups toolboxDomainLookups;

    @Mock
    private DefinitionsCacheRegistry definitionsCacheRegistry;

    @Mock
    private DMNCreateNodeAction generalCreateNodeAction;

    private AppendDecisionShortcut appendDecisionShortcut;

    @Before
    public void setup() {
        appendDecisionShortcut = spy(new AppendDecisionShortcut(toolboxDomainLookups,
                                                                definitionsCacheRegistry,
                                                                generalCreateNodeAction));
    }

    @Test
    public void testMatchesPressedKeys() {

        assertTrue(appendDecisionShortcut.matchesPressedKeys(D));
        verify(appendDecisionShortcut).getKeyCombination();
        assertFalse(appendDecisionShortcut.matchesPressedKeys(CONTROL, D));
    }

    @Test
    public void testMatchesSelectedElement_WhenIsDecision() {

        final Element decisionElement = createDecision();

        assertTrue(appendDecisionShortcut.matchesSelectedElement(decisionElement));
    }

    @Test
    public void testMatchesSelectedElement_WhenIsInput() {

        final Element inputElement = createInput();

        assertTrue(appendDecisionShortcut.matchesSelectedElement(inputElement));
    }

    @Test
    public void testMatchesSelectedElement_WhenIsNotDecisionOrInput() {

        final Element genericElement = createGenericElement();

        assertFalse(appendDecisionShortcut.matchesSelectedElement(genericElement));
    }

    @Test
    public void testCanAppendNodeOfDefinition_WhenIsDecision() {

        final Decision decision = mock(Decision.class);

        assertTrue(appendDecisionShortcut.canAppendNodeOfDefinition(decision));
    }

    @Test
    public void testCanAppendNodeOfDefinition_WhenIsNotDecision() {

        final Object obj = mock(Object.class);

        assertFalse(appendDecisionShortcut.canAppendNodeOfDefinition(obj));
    }

    @Test
    public void testGetKeyCombination() {

        final KeyboardEvent.Key[] expected = new KeyboardEvent.Key[]{D};

        final KeyboardEvent.Key[] actual = appendDecisionShortcut.getKeyCombination();

        assertArrayEquals(expected, actual);
    }

    private Element createDecision() {
        return createElementOf(mock(Decision.class));
    }

    private Element createInput() {
        return createElementOf(mock(InputData.class));
    }

    private Element createGenericElement() {
        return createElementOf(mock(Object.class));
    }

    private Element createElementOf(final Object content) {

        final Element element = mock(Element.class);
        final Definition definition = mock(Definition.class);
        when(definition.getDefinition()).thenReturn(content);
        when(element.getContent()).thenReturn(definition);

        return element;
    }
}
