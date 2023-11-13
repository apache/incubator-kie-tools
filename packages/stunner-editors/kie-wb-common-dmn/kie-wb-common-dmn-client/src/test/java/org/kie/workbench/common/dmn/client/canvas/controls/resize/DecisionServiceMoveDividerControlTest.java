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
package org.kie.workbench.common.dmn.client.canvas.controls.resize;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceDividerLineY;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.shape.view.decisionservice.DecisionServiceSVGShapeView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.client.canvas.controls.resize.DecisionServiceMoveDividerControl.DIVIDER_Y_PROPERTY_ID;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DecisionServiceMoveDividerControlTest {

    private static final String ELEMENT_UUID = "uuid";

    private static final double DIVIDER_Y = 25.0;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Shape shape;

    @Mock
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private Element element;

    @Mock
    private Definition definition;

    @Captor
    private ArgumentCaptor<DragHandler> dragHandlerCaptor;

    private DecisionServiceMoveDividerControl control;

    @Before
    public void setup() {
        when(commandManagerProvider.getCommandManager()).thenReturn(commandManager);
        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(element.getContent()).thenReturn(definition);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getShape(ELEMENT_UUID)).thenReturn(shape);

        this.control = spy(new DecisionServiceMoveDividerControl(canvasCommandFactory));
        this.control.setCommandManagerProvider(commandManagerProvider);
        this.control.init(canvasHandler);
    }

    @Test
    public void testCommandManager() {
        assertThat(control.getCommandManager()).isEqualTo(commandManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterDecisionServiceElement() {
        final DecisionService decisionService = mock(DecisionService.class);
        final DecisionServiceSVGShapeView decisionServiceShapeView = mock(DecisionServiceSVGShapeView.class);

        when(definition.getDefinition()).thenReturn(decisionService);
        when(shape.getShapeView()).thenReturn(decisionServiceShapeView);

        control.register(element);

        verify(control).registerHandler(Mockito.<String>any(), Mockito.<ViewHandler>any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterDecisionServiceElementDragEnd() {
        final DefinitionManager definitionManager = mock(DefinitionManager.class);
        final AdapterManager adapterManager = mock(AdapterManager.class);
        final AdapterRegistry adapterRegistry = mock(AdapterRegistry.class);
        final PropertyAdapter<Object, Object> propertyAdapter = mock(PropertyAdapter.class);
        final DefinitionAdapter<Object> definitionAdapter = mock(DefinitionAdapter.class);
        final DecisionServiceDividerLineY dividerLineY = new DecisionServiceDividerLineY();
        final Optional dividerYProperty = Optional.of(dividerLineY);
        final UpdateElementPropertyCommand updateElementPropertyCommand = mock(UpdateElementPropertyCommand.class);

        final DecisionService decisionService = mock(DecisionService.class);
        final DecisionServiceSVGShapeView decisionServiceShapeView = mock(DecisionServiceSVGShapeView.class);
        final DragEvent dragEvent = mock(DragEvent.class);

        when(canvasHandler.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionAdapter(Mockito.<Class>any())).thenReturn(definitionAdapter);
        when(definitionAdapter.getProperty(decisionService, DIVIDER_Y_PROPERTY_ID)).thenReturn(dividerYProperty);
        when(propertyAdapter.getId(dividerLineY)).thenReturn(DIVIDER_Y_PROPERTY_ID);
        when(canvasCommandFactory.updatePropertyValue(eq(element), eq(DIVIDER_Y_PROPERTY_ID), Mockito.<Object>any())).thenReturn(updateElementPropertyCommand);

        when(definition.getDefinition()).thenReturn(decisionService);
        when(shape.getShapeView()).thenReturn(decisionServiceShapeView);

        control.register(element);

        verify(decisionServiceShapeView).addDividerDragHandler(dragHandlerCaptor.capture());

        when(decisionServiceShapeView.getDividerLineY()).thenReturn(DIVIDER_Y);
        final DragHandler dragHandler = dragHandlerCaptor.getValue();
        dragHandler.end(dragEvent);

        verify(canvasCommandFactory).updatePropertyValue(eq(element), eq(DIVIDER_Y_PROPERTY_ID), eq(DIVIDER_Y));
        verify(commandManager).execute(eq(canvasHandler), eq(updateElementPropertyCommand));
    }

    @Test
    public void testRegisterNonDecisionServiceElement() {
        final Decision decision = mock(Decision.class);
        when(definition.getDefinition()).thenReturn(decision);

        control.register(element);

        verify(control, never()).registerHandler(Mockito.<String>any(), Mockito.<ViewHandler>any());
    }
}
