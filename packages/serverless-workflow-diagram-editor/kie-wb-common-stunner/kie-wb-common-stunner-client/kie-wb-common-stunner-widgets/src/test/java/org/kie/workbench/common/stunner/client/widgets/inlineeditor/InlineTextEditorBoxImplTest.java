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


package org.kie.workbench.common.stunner.client.widgets.inlineeditor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.DefaultTextPropertyProviderImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InlineTextEditorBoxImplTest {

    public static final String NAME = "name";
    public static final String MODIFIED_NAME = "modified_name";
    public static final String ID = "id";
    public static final double BOX_WIDTH = 50d;
    public static final double BOX_HEIGHT = 50d;

    protected InlineTextEditorBoxImpl presenter;

    @Mock
    private DefinitionUtils definitionUtils;
    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    @Mock
    private InlineTextEditorBoxViewImpl view;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private Command closeCallback;
    @Mock
    private RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandProvider;
    @Mock
    private TextPropertyProviderFactory textPropertyProviderFactory;
    @Mock
    private CanvasCommandManager canvasCommandManager;
    @Mock
    private Element element;
    @Mock
    private Definition definition;
    @Mock
    private TextPropertyProvider textPropertyProvider;
    private Object objectDefinition = new Object();

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        this.textPropertyProvider = new DefaultTextPropertyProviderImpl(definitionUtils,
                                                                        canvasCommandFactory);

        when(element.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(objectDefinition);
        when(definitionUtils.getName(objectDefinition)).thenReturn(NAME);
        when(definitionUtils.getNameIdentifier(objectDefinition)).thenReturn(ID);
        when(commandProvider.getCommandManager()).thenReturn(canvasCommandManager);
        when(canvasHandler.getTextPropertyProviderFactory()).thenReturn(textPropertyProviderFactory);
        when(textPropertyProviderFactory.getProvider(any(Element.class))).thenReturn(textPropertyProvider);

        presenter = new InlineTextEditorBoxImpl(view);
        presenter.setup();
        verify(view).init(presenter);
        presenter.initialize(canvasHandler,
                             closeCallback);
        presenter.setCommandManagerProvider(commandProvider);
        presenter.getElement();

        verify(view).getElement();

        presenter.show(element, BOX_WIDTH, BOX_HEIGHT);

        verify(view).show(NAME, BOX_WIDTH, BOX_HEIGHT);
    }

    @Test
    public void testOnChangeName() {
        presenter.onChangeName(MODIFIED_NAME);
        assertEquals(MODIFIED_NAME, presenter.getNameValue());
    }

    @Test
    public void testOnSave() {
        presenter.onChangeName(MODIFIED_NAME);
        verifyNameNotSaved();
        presenter.onSave();
        verifyNameSaved();
    }

    @Test
    public void testFlush() {
        presenter.onChangeName(MODIFIED_NAME);
        presenter.flush();
        verifyNameFlushed();
    }

    @Test
    public void testFlushValueNull() {
        presenter.onChangeName(null);
        presenter.flush();

        assertEquals(null,
                     presenter.getNameValue());
        verify(definitionUtils, never()).getNameIdentifier(objectDefinition);
        verify(canvasCommandFactory, never()).updatePropertyValue(element,
                                                                  ID,
                                                                  MODIFIED_NAME);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnClose() {
        presenter.onChangeName(MODIFIED_NAME);

        assertEquals(MODIFIED_NAME,
                     presenter.getNameValue());

        presenter.onClose();

        assertEquals(null,
                     presenter.getNameValue());
        verify(definitionUtils,
               never()).getNameIdentifier(objectDefinition);
        verify(canvasCommandFactory,
               never()).updatePropertyValue(element,
                                            ID,
                                            MODIFIED_NAME);
        verify(commandProvider,
               never()).getCommandManager();
        verify(canvasCommandManager,
               never()).execute(any(),
                                any());
        verify(view).hide();
        verify(closeCallback).execute();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetFontFamily() {
        presenter.setFontFamily("Open Sans");
        verify(view).setFontFamily("Open Sans");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetFontSize() {
        presenter.setFontSize(10d);
        verify(view).setFontSize(10d);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetMultiline() {
        presenter.setMultiline(true);
        verify(view).setMultiline(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetPlaceholder() {
        presenter.setPlaceholder("Name");
        verify(view).setPlaceholder("Name");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetTextBoxInternalAlignment() {
        presenter.setTextBoxInternalAlignment("MIDDLE");
        verify(view).setTextBoxInternalAlignment("MIDDLE");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        presenter.destroy();

        assertNull(presenter.canvasHandler);
        assertNull(presenter.textPropertyProviderFactory);
        assertNull(presenter.commandManagerProvider);
        assertNull(presenter.closeCallback);
        assertNull(presenter.element);
        assertNull(presenter.value);
    }

    @SuppressWarnings("unchecked")
    protected void verifyNameNotSaved() {
        assertEquals(MODIFIED_NAME,
                     presenter.getNameValue());
        verify(definitionUtils,
               never()).getNameIdentifier(objectDefinition);
        verify(canvasCommandFactory,
               never()).updatePropertyValue(element,
                                            ID,
                                            MODIFIED_NAME);
        verify(commandProvider,
               never()).getCommandManager();
        verify(canvasCommandManager,
               never()).execute(any(),
                                any());
        verify(view,
               never()).hide();
        verify(closeCallback,
               never()).execute();
    }

    protected void verifyNameSaved() {
        verifyNameFlushed();
        verify(view).hide();
        verify(closeCallback).execute();
    }

    @SuppressWarnings("unchecked")
    protected void verifyNameFlushed() {
        assertEquals(MODIFIED_NAME,
                     presenter.getNameValue());
        verify(definitionUtils).getNameIdentifier(objectDefinition);
        verify(canvasCommandFactory).updatePropertyValue(element,
                                                         ID,
                                                         MODIFIED_NAME);
        verify(commandProvider).getCommandManager();
        verify(canvasCommandManager).execute(any(),
                                             any());
    }
}
