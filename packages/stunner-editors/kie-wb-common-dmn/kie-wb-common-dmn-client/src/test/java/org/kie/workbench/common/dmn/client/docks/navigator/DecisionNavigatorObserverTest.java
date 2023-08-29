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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.Arrays;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorObserverTest {

    @Mock
    private DecisionNavigatorPresenter presenter;

    @Mock
    private DecisionNavigatorTreePresenter treePresenter;

    @Mock
    private Canvas canvas;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private ClientSession clientSession;

    @Mock
    private HasName hasName;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private Element<?> element;

    private String uuid;

    private DecisionNavigatorObserver observer;

    @Before
    public void setup() {
        when(presenter.getTreePresenter()).thenReturn(treePresenter);
        observer = spy(new DecisionNavigatorObserver());
        uuid = "uuid";
    }

    @Test
    public void testOnCanvasClearWhenPresenterIsPresent() {
        observer.init(presenter);
        observer.onCanvasClear(new CanvasClearEvent(canvas));
        verify(presenter).refresh();
    }

    @Test
    public void testOnCanvasElementAdded() {
        observer.init(presenter);
        observer.onCanvasElementAdded(new CanvasElementAddedEvent(canvasHandler, element));
        verify(presenter).refresh();
    }

    @Test
    public void testOnCanvasElementUpdated() {
        observer.init(presenter);
        observer.onCanvasElementUpdated(new CanvasElementUpdatedEvent(canvasHandler, element));
        verify(presenter).refresh();
    }

    @Test
    public void testOnCanvasElementRemoved() {
        observer.init(presenter);
        observer.onCanvasElementRemoved(new CanvasElementRemovedEvent(canvasHandler, element));
        verify(presenter).refresh();
    }

    @Test
    public void testOnNestedElementSelected() {
        observer.init(presenter);
        observer.onNestedElementSelected(new EditExpressionEvent(clientSession, uuid, hasExpression, Optional.of(hasName), false));
        verify(presenter).refresh();
    }

    @Test
    public void testOnNestedElementAdded() {
        observer.init(presenter);
        observer.onNestedElementAdded(new ExpressionEditorChanged(uuid));
        verify(presenter).refresh();
    }

    @Test
    public void testOnDMNDiagramSelected() {
        observer.init(presenter);
        observer.onDMNDiagramSelected(new DMNDiagramSelected(null));
        verify(presenter).refresh();
    }

    @Test
    public void testSelectItemWhenExpressionIsNotNull() {

        final EditExpressionEvent event = makeEditExpressionEvent();
        final Expression expression = mock(Expression.class);
        final Id id = mock(Id.class);

        when(hasExpression.getExpression()).thenReturn(expression);
        when(expression.getId()).thenReturn(id);
        when(id.getValue()).thenReturn(uuid);

        observer.init(presenter);
        observer.selectItem(event.getHasExpression());

        verify(treePresenter).selectItem(uuid);
    }

    @Test
    public void testSelectItemWhenExpressionIsNull() {

        final EditExpressionEvent event = makeEditExpressionEvent();

        when(hasExpression.getExpression()).thenReturn(null);

        observer.init(presenter);
        observer.selectItem(event.getHasExpression());

        verify(treePresenter, never()).selectItem(Mockito.<String>any());
    }

    @Test
    public void testSetActiveParent() {

        final EditExpressionEvent event = makeEditExpressionEvent();

        observer.init(presenter);
        observer.setActiveParent(event);

        verify(treePresenter).setActiveParentUUID(uuid);
    }

    @Test
    public void testGetActiveParent() {

        final DecisionNavigatorItem expectedItem = makeItem(uuid);

        when(treePresenter.getActiveParent()).thenReturn(expectedItem);
        observer.init(presenter);

        final DecisionNavigatorItem actualItem = observer.getActiveParent().get();

        assertEquals(expectedItem, actualItem);
    }

    private DecisionNavigatorItem makeItem(final String uuid,
                                           final DecisionNavigatorItem... items) {
        final DecisionNavigatorItem item = new DecisionNavigatorItemBuilder().withUUID(uuid).build();
        item.getChildren().addAll(Arrays.asList(items));
        return item;
    }

    private EditExpressionEvent makeEditExpressionEvent() {
        final Optional<HasName> optionalName = Optional.of(hasName);
        return new EditExpressionEvent(clientSession, uuid, hasExpression, optionalName, false);
    }
}
