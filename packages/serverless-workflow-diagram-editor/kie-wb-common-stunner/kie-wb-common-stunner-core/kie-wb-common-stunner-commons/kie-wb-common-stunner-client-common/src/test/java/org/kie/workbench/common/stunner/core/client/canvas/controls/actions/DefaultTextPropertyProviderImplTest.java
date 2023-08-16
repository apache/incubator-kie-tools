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

package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultTextPropertyProviderImplTest {

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private Element<? extends Definition> element;

    @Mock
    private Definition content;

    @Mock
    private Definition definition;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private CanvasCommand<AbstractCanvasHandler> command;

    private TextPropertyProvider provider;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.provider = new DefaultTextPropertyProviderImpl(definitionUtils,
                                                            canvasCommandFactory);
        when(element.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);
        when(definitionUtils.getNameIdentifier(eq(definition))).thenReturn("name");
        when(canvasCommandFactory.updatePropertyValue(eq(element),
                                                      anyString(),
                                                      anyString())).thenReturn(command);
    }

    @Test
    public void checkPriorityEnsuresCatchAllOperation() {
        assertEquals(TextPropertyProviderFactory.CATCH_ALL_PRIORITY,
                     provider.getPriority());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkSupportsAllElements() {
        assertTrue(provider.supports(element));
    }

    @Test
    public void checkReadGetsNameFromPropertyMetaData() {
        provider.getText(element);

        verify(definitionUtils).getName(eq(definition));
    }

    @Test
    public void checkWriteUsesCommandToUpdateNamePropertyMetaData() {
        provider.setText(canvasHandler,
                         commandManager,
                         element,
                         "text");

        verify(canvasCommandFactory).updatePropertyValue(eq(element),
                                                         eq("name"),
                                                         eq("text"));
        verify(commandManager).execute(eq(canvasHandler),
                                       eq(command));
    }
}
