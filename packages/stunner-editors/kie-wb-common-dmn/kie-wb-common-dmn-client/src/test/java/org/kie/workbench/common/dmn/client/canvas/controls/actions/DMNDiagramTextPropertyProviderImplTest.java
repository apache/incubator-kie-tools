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
package org.kie.workbench.common.dmn.client.canvas.controls.actions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.NameHolder;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DMNDiagramTextPropertyProviderImplTest {

    private static final String NAME_FIELD = "name";

    private static final String NAME_VALUE = "text";

    private static final String NAME_VALUE_WITH_WHITESPACE = "   " + NAME_VALUE + "   ";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private Element<Definition> element;

    @Mock
    private Definition content;

    @Mock
    private DMNDiagram definition;

    @Mock
    private Definitions definitions;

    @Mock
    private Name name;

    @Mock
    private NameHolder nameHolder;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private CanvasCommand<AbstractCanvasHandler> command;

    @Captor
    private ArgumentCaptor<Name> nameArgumentCaptor;

    private TextPropertyProvider provider;

    @Before
    public void setup() {
        this.provider = new DMNDiagramTextPropertyProviderImpl(canvasCommandFactory, definitionUtils);
        when(element.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);
        when(definition.getDefinitions()).thenReturn(definitions);
        when(definitions.getName()).thenReturn(name);
        when(definitions.getNameHolder()).thenReturn(nameHolder);
        when(nameHolder.getValue()).thenReturn(name);
        when(definitionUtils.getNameIdentifier(eq(definitions))).thenReturn(NAME_FIELD);
        when(canvasCommandFactory.updatePropertyValue(eq(element),
                                                      Mockito.<String>any(),
                                                      Mockito.<String>any())).thenReturn(command);
    }

    @Test
    public void checkPriorityLessThanCatchAll() {
        assertTrue(provider.getPriority() < TextPropertyProviderFactory.CATCH_ALL_PRIORITY);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkSupportsDMNDiagram() {
        assertTrue(provider.supports(element));

        final Element other = mock(Element.class);
        final Definition otherContent = mock(Definition.class);
        final TextAnnotation otherDefinition = mock(TextAnnotation.class);
        when(other.getContent()).thenReturn(otherContent);
        when(otherContent.getDefinition()).thenReturn(otherDefinition);

        assertFalse(provider.supports(other));
    }

    @Test
    public void checkReadGetsTextFromNameProperty() {
        provider.getText(element);

        verify(definitions).getNameHolder();
        verify(nameHolder).getValue();
    }

    @Test
    public void checkWriteUsesCommandToUpdateTextProperty() {
        provider.setText(canvasHandler, commandManager, element, NAME_VALUE);

        verify(canvasCommandFactory).updatePropertyValue(eq(element), eq(NAME_FIELD), nameArgumentCaptor.capture());
        assertEquals(NAME_VALUE, nameArgumentCaptor.getValue().getValue());
        verify(commandManager).execute(eq(canvasHandler), eq(command));
    }

    @Test
    public void checkWriteUsesCommandToUpdateTextPropertyWithWhitespace() {
        provider.setText(canvasHandler, commandManager, element, NAME_VALUE_WITH_WHITESPACE);

        verify(canvasCommandFactory).updatePropertyValue(eq(element), eq(NAME_FIELD), nameArgumentCaptor.capture());
        assertEquals(NAME_VALUE, nameArgumentCaptor.getValue().getValue());
        verify(commandManager).execute(eq(canvasHandler), eq(command));
    }
}
