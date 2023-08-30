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
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TextAnnotationTextPropertyProviderImplTest {

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
    private TextAnnotation definition;

    @Mock
    private Text text;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private CanvasCommand<AbstractCanvasHandler> command;

    @Captor
    private ArgumentCaptor<Object> nameArgumentCaptor;

    private TextPropertyProvider provider;

    @Before
    public void setup() {
        this.provider = spy(new TextAnnotationTextPropertyProviderImpl(canvasCommandFactory, definitionUtils));
        when(element.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);
        when(definition.getText()).thenReturn(text);
        when(definitionUtils.getNameIdentifier(definition)).thenReturn(NAME_FIELD);
        when(canvasCommandFactory.updatePropertyValue(eq(element),
                                                      Mockito.any(),
                                                      Mockito.any())).thenReturn(command);
    }

    @Test
    public void checkPriorityLessThanCatchAll() {
        assertTrue(provider.getPriority() < TextPropertyProviderFactory.CATCH_ALL_PRIORITY);
    }

    @Test
    public void checkSupportsTextAnnotationElements() {
        assertTrue(provider.supports(element));

        final Element other = mock(Element.class);
        final Definition otherContent = mock(Definition.class);
        final InputData otherDefinition = mock(InputData.class);
        when(other.getContent()).thenReturn(otherContent);
        when(otherContent.getDefinition()).thenReturn(otherDefinition);

        assertFalse(provider.supports(other));
    }

    @Test
    public void checkReadGetsTextFromTextProperty() {
        provider.getText(element);

        verify(text).getValue();
    }

    @Test
    public void checkWriteUsesCommandToUpdateTextProperty() {
        provider.setText(canvasHandler, commandManager, element, NAME_VALUE);

        verify(canvasCommandFactory).updatePropertyValue(element, NAME_FIELD, NAME_VALUE);
        verify(commandManager).execute(canvasHandler, command);
    }

    @Test
    public void checkNodeUpdateWithSameAssignedName() {
        when(provider.getText(element)).thenReturn(NAME_VALUE);

        provider.setText(canvasHandler, commandManager, element, NAME_VALUE);

        verify(canvasCommandFactory, never()).updatePropertyValue(eq(element), eq(NAME_FIELD), nameArgumentCaptor.capture());
        verify(commandManager, never()).execute(canvasHandler, command);
    }

    @Test
    public void checkNodeUpdateWithDifferentAssignedName() {
        when(provider.getText(element)).thenReturn(NAME_VALUE_WITH_WHITESPACE);

        provider.setText(canvasHandler, commandManager, element, NAME_VALUE);

        verify(canvasCommandFactory, times(1)).updatePropertyValue(eq(element), eq(NAME_FIELD), nameArgumentCaptor.capture());
        assertEquals(NAME_VALUE, nameArgumentCaptor.getValue().toString());
        verify(commandManager, times(1)).execute(canvasHandler, command);
    }
}
