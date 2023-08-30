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

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SessionPreviewCanvasHandlerProxyTest {

    @Mock
    private BaseCanvasHandler wrapped;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private GraphUtils graphUtils;

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private TextPropertyProviderFactory textPropertyProviderFactory;

    private SessionPreviewCanvasHandlerProxy proxy;

    @Before
    public void setup() {
        proxy = new SessionPreviewCanvasHandlerProxy(wrapped,
                                                     definitionManager,
                                                     graphUtils,
                                                     shapeManager,
                                                     textPropertyProviderFactory);
    }

    @Test
    public void checkGetGraphExecutionContextIsNull() {
        assertNull(proxy.getGraphExecutionContext());
    }

    @Test
    public void checkBuildGraphIndexExecutesCallback() {
        final Command loadCallback = mock(Command.class);
        proxy.buildGraphIndex(loadCallback);

        verify(loadCallback).execute();
    }

    @Test
    public void checkGetRuleManagerDelegatesToWrapped() {
        proxy.getRuleManager();

        verify(wrapped).getRuleManager();
    }

    @Test
    public void checkDestroyGraphIndexExecutesCallback() {
        final Command loadCallback = mock(Command.class);
        proxy.destroyGraphIndex(loadCallback);

        verify(loadCallback).execute();
    }

    @Test
    public void checkGetGraphIndexDelegatesToWrapped() {
        proxy.getGraphIndex();

        verify(wrapped).getGraphIndex();
    }

    @Test
    public void checkGetDefinitionManagerDelegatesToWrapped() {
        proxy.getDefinitionManager();

        verify(wrapped).getDefinitionManager();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRegisterDelegatesToWrapped() {
        final Shape shape = mock(Shape.class);
        final Element candidate = mock(Element.class);
        final boolean fireEvents = true;
        proxy.register(shape,
                       candidate,
                       fireEvents);

        verify(wrapped).register(eq(shape),
                                 eq(candidate),
                                 eq(fireEvents));

        final String shapeSetId = "ShapeSetId";
        proxy.register(shapeSetId,
                       candidate);

        verify(wrapped).register(eq(shapeSetId),
                                 eq(candidate));

        final ShapeFactory factory = mock(ShapeFactory.class);
        proxy.register(factory,
                       candidate,
                       fireEvents);

        verify(wrapped).register(eq(factory),
                                 eq(candidate),
                                 eq(fireEvents));
    }

    @Test
    public void checkDeregisterDelegatesToWrapped() {
        final Element candidate = mock(Element.class);
        proxy.deregister(candidate);

        verify(wrapped).deregister(eq(candidate));

        final Shape shape = mock(Shape.class);
        final boolean fireEvents = true;
        proxy.deregister(shape,
                         candidate,
                         fireEvents);

        verify(wrapped).deregister(eq(shape),
                                   eq(candidate),
                                   eq(fireEvents));

        proxy.deregister(candidate,
                         fireEvents);

        verify(wrapped).deregister(eq(candidate),
                                   eq(fireEvents));
    }

    @Test
    public void checkAddChildDelegatesToWrapped() {
        final Element parent = mock(Element.class);
        final Element child = mock(Element.class);
        proxy.addChild(parent,
                       child);

        verify(wrapped).addChild(eq(parent),
                                 eq(child));
    }

    @Test
    public void checkAddChildWithIndexDelegatesToWrapped() {
        final Element parent = mock(Element.class);
        final Element child = mock(Element.class);
        final int index = 0;
        proxy.addChild(parent,
                       child,
                       index);

        verify(wrapped).addChild(eq(parent),
                                 eq(child),
                                 eq(index));
    }

    @Test
    public void checkRemoveChildDelegatesToWrapped() {
        final Element parent = mock(Element.class);
        final Element child = mock(Element.class);
        proxy.removeChild(parent,
                          child);

        verify(wrapped).removeChild(eq(parent),
                                    eq(child));
    }

    @Test
    public void checkGetElementAtDelegatesToWrapped() {
        final double x = 0;
        final double y = 1;
        proxy.getElementAt(x,
                           y);

        verify(wrapped).getElementAt(eq(x),
                                     eq(y));
    }

    @Test
    public void checkDockDelegatesToWrapped() {
        final Element parent = mock(Element.class);
        final Element child = mock(Element.class);
        proxy.dock(parent,
                   child);

        verify(wrapped).dock(eq(parent),
                             eq(child));
    }

    @Test
    public void checkUndockDelegatesToWrapped() {
        final Element parent = mock(Element.class);
        final Element child = mock(Element.class);
        proxy.undock(parent,
                     child);

        verify(wrapped).undock(eq(parent),
                               eq(child));
    }

    @Test
    public void checkClearDelegatesToWrapped() {
        proxy.clear();

        verify(wrapped).clear();
    }

    @Test
    public void checkDoClearDelegatesToWrapped() {
        proxy.doClear();

        verify(wrapped).doClear();
    }

    @Test
    public void checkDestroyDelegatesToWrapped() {
        proxy.destroy();

        verify(wrapped).destroy();
    }

    @Test
    public void checkDoDestroyDelegatesToWrapped() {
        proxy.doDestroy();

        verify(wrapped).doDestroy();
    }

    @Test
    public void checkApplyElementMutationDelegatesToWrapped() {
        final Shape shape = mock(Shape.class);
        final Element candidate = mock(Element.class);
        final boolean applyPosition = true;
        final boolean applyProperties = false;
        final MutationContext mutationContext = mock(MutationContext.class);
        proxy.applyElementMutation(shape,
                                   candidate,
                                   applyPosition,
                                   applyProperties,
                                   mutationContext);

        verify(wrapped).applyElementMutation(eq(shape),
                                             eq(candidate),
                                             eq(applyPosition),
                                             eq(applyProperties),
                                             eq(mutationContext));

        proxy.applyElementMutation(candidate,
                                   mutationContext);

        verify(wrapped).applyElementMutation(eq(candidate),
                                             eq(mutationContext));

        proxy.applyElementMutation(candidate,
                                   applyPosition,
                                   applyProperties,
                                   mutationContext);

        verify(wrapped).applyElementMutation(eq(candidate),
                                             eq(applyPosition),
                                             eq(applyProperties),
                                             eq(mutationContext));
    }

    @Test
    public void checkUpdateElementPositionDelegatesToWrapped() {
        final Element candidate = mock(Element.class);
        final MutationContext mutationContext = mock(MutationContext.class);
        proxy.updateElementPosition(candidate,
                                    mutationContext);

        verify(wrapped).updateElementPosition(eq(candidate),
                                              eq(mutationContext));
    }

    @Test
    public void updateElementPropertiesDelegatesToWrapped() {
        final Element candidate = mock(Element.class);
        final MutationContext mutationContext = mock(MutationContext.class);
        proxy.updateElementProperties(candidate,
                                      mutationContext);

        verify(wrapped).updateElementProperties(eq(candidate),
                                                eq(mutationContext));
    }

    @Test
    public void checkGetShapeFactoryDelegatesToWrapped() {
        final String shapeSetId = "ShapeSetId";
        proxy.getShapeFactory(shapeSetId);

        verify(wrapped).getShapeFactory(eq(shapeSetId));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkHandleDelegatesToWrapped() {
        final AbstractCanvas canvas = mock(AbstractCanvas.class);
        proxy.handle(canvas);

        verify(wrapped).handle(eq(canvas));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkDrawDelegatesToWrapped() {
        final Diagram diagram = mock(Diagram.class);
        final ParameterizedCommand loadCallback = mock(ParameterizedCommand.class);
        proxy.draw(diagram,
                   loadCallback);

        verify(wrapped).draw(eq(diagram),
                             eq(loadCallback));
    }

    @Test
    public void checkGetDiagramDelegatesToWrapped() {
        proxy.getDiagram();

        verify(wrapped).getDiagram();
    }

    @Test
    public void checkGetCanvasDelegatesToWrapped() {
        proxy.getCanvas();

        verify(wrapped).getCanvas();
    }

    @Test
    public void checkAddRegistrationListenerDelegatesToWrapped() {
        final CanvasElementListener instance = mock(CanvasElementListener.class);
        proxy.addRegistrationListener(instance);

        verify(wrapped).addRegistrationListener(eq(instance));
    }

    @Test
    public void checkRemoveRegistrationListenerDelegatesToWrapped() {
        final CanvasElementListener instance = mock(CanvasElementListener.class);
        proxy.removeRegistrationListener(instance);

        verify(wrapped).removeRegistrationListener(eq(instance));
    }

    @Test
    public void checkClearRegistrationListenersDelegatesToWrapped() {
        proxy.clearRegistrationListeners();

        verify(wrapped).clearRegistrationListeners();
    }

    @Test
    public void checkNotifyCanvasElementRemovedDelegatesToWrapped() {
        final Element candidate = mock(Element.class);
        proxy.notifyCanvasElementRemoved(candidate);

        verify(wrapped).notifyCanvasElementRemoved(eq(candidate));
    }

    @Test
    public void checkNotifyCanvasElementAddedDelegatesToWrapped() {
        final Element candidate = mock(Element.class);
        proxy.notifyCanvasElementAdded(candidate);

        verify(wrapped).notifyCanvasElementAdded(eq(candidate));
    }

    @Test
    public void checkNotifyCanvasElementUpdatedDelegatesToWrapped() {
        final Element candidate = mock(Element.class);
        proxy.notifyCanvasElementUpdated(candidate);

        verify(wrapped).notifyCanvasElementUpdated(eq(candidate));
    }

    @Test
    public void checkNotifyCanvasClearDelegatesToWrapped() {
        proxy.notifyCanvasClear();

        verify(wrapped).notifyCanvasClear();
    }

    @Test
    public void checkClearCanvasDelegatesToWrapped() {
        proxy.clearCanvas();

        verify(wrapped).clearCanvas();
    }

    @Test
    public void checkGetAbstractCanvasDelegatesToWrapped() {
        proxy.getAbstractCanvas();

        verify(wrapped).getAbstractCanvas();
    }

    @Test
    public void checkIsCanvasRootDelegatesToWrapped() {
        final Element parent = mock(Element.class);
        proxy.isCanvasRoot(parent);

        verify(wrapped).isCanvasRoot(eq(parent));
    }

    @Test
    public void checkGetUuidDelegatesToWrapped() {
        proxy.getUuid();

        verify(wrapped).getUuid();
    }
}
