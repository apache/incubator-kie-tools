/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.decision;

import java.util.Arrays;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.decision.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
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

        verify(presenter).removeAllElements();
        verify(presenter).refreshTreeView();
    }

    @Test
    public void testOnCanvasClearWhenPresenterIsNotPresent() {
        observer.onCanvasClear(new CanvasClearEvent(canvas));

        verify(presenter, never()).removeAllElements();
        verify(presenter, never()).refreshTreeView();
    }

    @Test
    public void testOnCanvasElementAddedWhenPresenterIsPresent() {
        observer.init(presenter);

        observer.onCanvasElementAdded(new CanvasElementAddedEvent(canvasHandler, element));

        verify(presenter).addOrUpdateElement(element);
    }

    @Test
    public void testOnCanvasElementAddedWhenPresenterIsNotPresent() {
        observer.onCanvasElementAdded(new CanvasElementAddedEvent(canvasHandler, element));

        verify(presenter, never()).addOrUpdateElement(any());
    }

    @Test
    public void testOnCanvasElementUpdatedWhenPresenterIsPresent() {
        observer.init(presenter);

        observer.onCanvasElementUpdated(new CanvasElementUpdatedEvent(canvasHandler, element));

        verify(presenter).addOrUpdateElement(element);
    }

    @Test
    public void testOnCanvasElementUpdatedWhenPresenterIsNotPresent() {
        observer.onCanvasElementUpdated(new CanvasElementUpdatedEvent(canvasHandler, element));

        verify(presenter, never()).addOrUpdateElement(any());
    }

    @Test
    public void testOnCanvasElementRemovedWhenPresenterIsPresent() {
        observer.init(presenter);

        observer.onCanvasElementRemoved(new CanvasElementRemovedEvent(canvasHandler, element));

        verify(presenter).removeElement(element);
    }

    @Test
    public void testOnCanvasElementRemovedWhenPresenterIsNotPresent() {
        observer.onCanvasElementRemoved(new CanvasElementRemovedEvent(canvasHandler, element));

        verify(presenter, never()).removeElement(any());
    }

    @Test
    public void testOnNestedElementSelected() {
        final EditExpressionEvent event = makeEditExpressionEvent();

        doNothing().when(observer).selectItem(any());
        doNothing().when(observer).setActiveParent(any());

        observer.onNestedElementSelected(event);

        verify(observer).selectItem(event);
        verify(observer).setActiveParent(event);
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
        observer.selectItem(event);

        verify(treePresenter).selectItem(uuid);
    }

    @Test
    public void testSelectItemWhenExpressionIsNull() {

        final EditExpressionEvent event = makeEditExpressionEvent();

        when(hasExpression.getExpression()).thenReturn(null);

        observer.init(presenter);
        observer.selectItem(event);

        verify(treePresenter, never()).selectItem(anyString());
    }

    @Test
    public void testSetActiveParent() {

        final EditExpressionEvent event = makeEditExpressionEvent();

        observer.init(presenter);
        observer.setActiveParent(event);

        verify(treePresenter).setActiveParentUUID(uuid);
    }

    @Test
    public void testOnNestedElementAdded() {

        final String uuid1 = "123";
        final String uuid2 = "456";
        final String uuid3 = "789";
        final DecisionNavigatorItem child1 = makeItem(uuid2);
        final DecisionNavigatorItem child2 = makeItem(uuid3);
        final DecisionNavigatorItem item = makeItem(uuid1, child1, child2);
        final Graph graph = mock(Graph.class);
        final Node node = mock(Node.class);

        when(presenter.getGraph()).thenReturn(Optional.of(graph));
        when(graph.getNode(uuid1)).thenReturn(node);
        doReturn(item).when(observer).getActiveParent();

        observer.init(presenter);
        observer.onNestedElementAdded(new ExpressionEditorChanged());

        verify(presenter).updateElement(node);
        verify(treePresenter).selectItem(uuid2);
        verify(treePresenter).selectItem(uuid3);
    }

    @Test
    public void testOnNestedElementLostFocus() {

        observer.init(presenter);
        observer.onNestedElementLostFocus(new CanvasFocusedShapeEvent(canvasHandler, uuid));

        verify(treePresenter).deselectItem();
    }

    @Test
    public void testGetActiveParent() {

        final DecisionNavigatorItem expectedItem = makeItem(uuid);

        when(treePresenter.getActiveParent()).thenReturn(expectedItem);
        observer.init(presenter);

        final DecisionNavigatorItem actualItem = observer.getActiveParent();

        assertEquals(expectedItem, actualItem);
    }

    private DecisionNavigatorItem makeItem(final String uuid,
                                           final DecisionNavigatorItem... items) {
        final DecisionNavigatorItem item = new DecisionNavigatorItem(uuid);
        item.getChildren().addAll(Arrays.asList(items));
        return item;
    }

    private EditExpressionEvent makeEditExpressionEvent() {
        final Optional<HasName> optionalName = Optional.of(hasName);
        return new EditExpressionEvent(clientSession, uuid, hasExpression, optionalName);
    }
}
