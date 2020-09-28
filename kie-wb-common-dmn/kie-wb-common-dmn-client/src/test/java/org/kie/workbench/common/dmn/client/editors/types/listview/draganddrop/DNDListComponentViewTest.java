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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DOMRect;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import elemental2.dom.NodeList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DATA_X_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DATA_Y_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DRAGGABLE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DRAGGING;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.GRIP;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.HOVER;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DNDListComponentViewTest {

    @Mock
    private HTMLDivElement dragArea;

    @Mock
    private DNDListComponent presenter;

    private DNDListComponentView view;

    @Before
    public void setup() {
        view = spy(new DNDListComponentView(dragArea));
        view.init(presenter);
    }

    @Test
    public void testInit() {
        // init is called by @Before
        verify(view).setupDragAreaHandlers();
    }

    @Test
    public void testRegisterItem() {

        final HTMLElement expectedItem = mock(HTMLElement.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);

        doReturn(expectedItem).when(view).createItem(htmlElement);
        doReturn(2).when(view).getMaxPositionY();

        final HTMLElement actualItem = view.registerItem(htmlElement);

        verify(actualItem).setAttribute(DATA_Y_POSITION, 3);
        verify(actualItem).setAttribute(DATA_X_POSITION, 0);
        verify(dragArea).appendChild(actualItem);
        assertEquals(expectedItem, actualItem);
    }

    @Test
    public void testGetMaxPositionY() {

        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);

        when(element0.getAttribute(DATA_Y_POSITION)).thenReturn("2");
        when(element1.getAttribute(DATA_Y_POSITION)).thenReturn("4");
        when(element2.getAttribute(DATA_Y_POSITION)).thenReturn("8");

        mockDragAreaWithChildren(element0, element1, element2);

        final int expectedMaxPosition = 8;
        final int actualMaxPosition = view.getMaxPositionY();

        assertEquals(expectedMaxPosition, actualMaxPosition);
    }

    @Test
    public void testRefreshItemsPosition() {

        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);

        element0.style = mock(CSSStyleDeclaration.class);
        element1.style = mock(CSSStyleDeclaration.class);
        element2.style = mock(CSSStyleDeclaration.class);

        dragArea.style = mock(CSSStyleDeclaration.class);

        when(presenter.getItemHeight()).thenReturn(50);
        when(presenter.getIndentationSize()).thenReturn(75);

        when(element0.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(element1.getAttribute(DATA_Y_POSITION)).thenReturn("1");
        when(element2.getAttribute(DATA_Y_POSITION)).thenReturn("2");

        when(element0.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(element1.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(element2.getAttribute(DATA_X_POSITION)).thenReturn("1");

        mockDragAreaWithChildren(element0, element1, element2);

        view.refreshItemsPosition();

        verify(element0.style).setProperty("top", "0px");
        verify(element1.style).setProperty("top", "50px");
        verify(element2.style).setProperty("top", "100px");

        verify(element0.style).setProperty("padding-left", "0px");
        verify(element1.style).setProperty("padding-left", "75px");
        verify(element2.style).setProperty("padding-left", "75px");

        verify(element0.style).setProperty("width", "calc(100% - 0px)");
        verify(element1.style).setProperty("width", "calc(100% - 0px)");
        verify(element2.style).setProperty("width", "calc(100% - 0px)");

        verify(dragArea.style).setProperty("height", "151px");
    }

    @Test
    public void testRefreshItemsHTML() {

        final HTMLElement element = mock(HTMLElement.class);
        dragArea.firstChild = element;

        mockDragAreaWithChildren(element);
        when(dragArea.removeChild(element)).then(a -> {
            dragArea.firstChild = null;
            return element;
        });

        view.refreshItemsHTML();

        verify(dragArea).removeChild(element);
        verify(dragArea).appendChild(element);
    }

    @Test
    public void testConsolidateHierarchicalLevelWhenIsDraggedByUser() {

        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);

        element0.style = mock(CSSStyleDeclaration.class);
        element1.style = mock(CSSStyleDeclaration.class);
        element2.style = mock(CSSStyleDeclaration.class);

        dragArea.style = mock(CSSStyleDeclaration.class);

        when(element0.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(element1.getAttribute(DATA_Y_POSITION)).thenReturn("2");
        when(element2.getAttribute(DATA_Y_POSITION)).thenReturn("1");

        when(element0.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(element1.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(element2.getAttribute(DATA_X_POSITION)).thenReturn("3");

        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"0\"]")).thenReturn(element0);
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"2\"]")).thenReturn(element1);
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"1\"]")).thenReturn(element2);

        mockDragAreaWithChildren(element0, element1, element2);

        view.consolidateHierarchicalLevel(true);

        verify(element0).setAttribute(DATA_Y_POSITION, 0);
        verify(element2, times(2)).setAttribute(DATA_Y_POSITION, 1);
        verify(element1).setAttribute(DATA_Y_POSITION, 2);

        verify(element0).setAttribute(DATA_X_POSITION, 0);
        verify(element2).setAttribute(DATA_X_POSITION, 1);
        verify(element1, never()).setAttribute(anyString(), anyString());
    }

    @Test
    public void testConsolidateHierarchicalLevelWhenIsNotDraggedByUser() {

        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);

        element0.style = mock(CSSStyleDeclaration.class);
        element1.style = mock(CSSStyleDeclaration.class);
        element2.style = mock(CSSStyleDeclaration.class);

        dragArea.style = mock(CSSStyleDeclaration.class);

        when(element0.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(element1.getAttribute(DATA_Y_POSITION)).thenReturn("2");
        when(element2.getAttribute(DATA_Y_POSITION)).thenReturn("1");

        when(element0.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(element1.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(element2.getAttribute(DATA_X_POSITION)).thenReturn("3");

        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"0\"]")).thenReturn(element0);
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"2\"]")).thenReturn(element1);
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"1\"]")).thenReturn(element2);

        mockDragAreaWithChildren(element0, element1, element2);

        view.consolidateHierarchicalLevel(false);

        verify(element0).setAttribute(DATA_Y_POSITION, 0);
        verify(element2, times(2)).setAttribute(DATA_Y_POSITION, 1);
        verify(element1).setAttribute(DATA_Y_POSITION, 2);

        verify(element0, never()).setAttribute(DATA_X_POSITION, 0);
        verify(element2).setAttribute(DATA_X_POSITION, 1);
        verify(element1, never()).setAttribute(anyString(), anyString());
    }

    @Test
    public void testConsolidateHierarchicalLevelWhenListHasOneInvalidItem() {

        final HTMLElement element = mock(HTMLElement.class);

        element.style = mock(CSSStyleDeclaration.class);
        dragArea.style = mock(CSSStyleDeclaration.class);

        when(element.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(element.getAttribute(DATA_X_POSITION)).thenReturn("9");

        mockDragAreaWithChildren(element);

        view.consolidateHierarchicalLevel(true);

        verify(element).setAttribute(DATA_X_POSITION, 0);
    }

    @Test
    public void testConsolidateHierarchicalLevelWhenListHasOneValidItem() {

        final HTMLElement element = mock(HTMLElement.class);

        element.style = mock(CSSStyleDeclaration.class);
        dragArea.style = mock(CSSStyleDeclaration.class);

        when(element.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(element.getAttribute(DATA_X_POSITION)).thenReturn("1");

        mockDragAreaWithChildren(element);

        view.consolidateHierarchicalLevel(true);

        verify(element, never()).setAttribute(anyString(), anyString());
    }

    @Test
    public void testConsolidateYPosition() {

        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);

        element0.style = mock(CSSStyleDeclaration.class);
        element1.style = mock(CSSStyleDeclaration.class);

        dragArea.style = mock(CSSStyleDeclaration.class);

        when(element0.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(element1.getAttribute(DATA_Y_POSITION)).thenReturn("2");

        mockDragAreaWithChildren(element0, element1);

        view.consolidateYPosition();

        verify(element0).setAttribute(DATA_Y_POSITION, 0);
        verify(element1).setAttribute(DATA_Y_POSITION, 1);
    }

    @Test
    public void testClear() {

        final HTMLElement element = mock(HTMLElement.class);
        dragArea.firstChild = element;

        mockDragAreaWithChildren(element);
        when(dragArea.removeChild(element)).then(a -> {
            dragArea.firstChild = null;
            return element;
        });

        view.clear();

        verify(dragArea).removeChild(element);
    }

    @Test
    public void testGetPreviousElementWhenElementIsFound() {

        final HTMLElement current = mock(HTMLElement.class);
        final HTMLElement expectedPrevious = mock(HTMLElement.class);

        when(expectedPrevious.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(current.getAttribute(DATA_Y_POSITION)).thenReturn("1");
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"0\"]")).thenReturn(expectedPrevious);

        final Optional<HTMLElement> actualPrevious = view.getPreviousElement(current);

        assertTrue(actualPrevious.isPresent());
        assertEquals(expectedPrevious, actualPrevious.get());
    }

    @Test
    public void testGetPreviousElementWhenElementIsNotFound() {

        final HTMLElement current = mock(HTMLElement.class);

        when(current.getAttribute(DATA_Y_POSITION)).thenReturn("1");
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"0\"]")).thenReturn(null);

        final Optional<HTMLElement> actualPrevious = view.getPreviousElement(current);

        assertFalse(actualPrevious.isPresent());
    }

    @Test
    public void testCreateItem() {

        final HTMLDocument document = mock(HTMLDocument.class);
        final HTMLElement expectedItem = mock(HTMLElement.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);
        final HTMLElement grip = mock(HTMLElement.class);
        final HTMLElement i0 = mock(HTMLElement.class);
        final HTMLElement i1 = mock(HTMLElement.class);

        DNDListDOMHelper.Factory.DOCUMENT = document;
        expectedItem.classList = mock(DOMTokenList.class);
        grip.classList = mock(DOMTokenList.class);
        i0.classList = mock(DOMTokenList.class);
        i1.classList = mock(DOMTokenList.class);
        when(document.createElement("div")).thenReturn(expectedItem, grip);
        when(document.createElement("i")).thenReturn(i0, i1);

        final HTMLElement actualItem = view.createItem(htmlElement);

        verify(actualItem).appendChild(grip);
        verify(actualItem).appendChild(htmlElement);
        verify(actualItem.classList).add(DRAGGABLE);

        assertEquals(expectedItem, actualItem);
    }

    @Test
    public void testSetupDragAreaHandlers() {

        final Event event = mock(Event.class);

        doNothing().when(view).onStartDrag(any());
        doNothing().when(view).onDrag(any());
        doNothing().when(view).onDrop();

        dragArea.onmousedown.onInvoke(event);
        dragArea.onmousemove.onInvoke(event);
        dragArea.onmouseup.onInvoke(event);
        dragArea.onmouseout.onInvoke(event);

        final InOrder inOrder = Mockito.inOrder(view);

        inOrder.verify(view).onStartDrag(event);
        inOrder.verify(view).onDrag(event);
        inOrder.verify(view, times(2)).onDrop();
    }

    @Test
    public void testOnStartDragWhenTargetIsGrip() {

        final Event event = mock(Event.class);
        final HTMLElement parent = mock(HTMLElement.class);
        final HTMLElement target = mock(HTMLElement.class);

        event.target = target;
        target.parentNode = parent;
        target.classList = mock(DOMTokenList.class);

        when(target.classList.contains(GRIP)).thenReturn(true);
        doNothing().when(view).holdDraggingElement(any());

        view.onStartDrag(event);

        verify(view).holdDraggingElement(parent);
    }

    @Test
    public void testOnStartDragWhenTargetIsNotGrip() {

        final Event event = mock(Event.class);
        final HTMLElement parent = mock(HTMLElement.class);
        final HTMLElement target = mock(HTMLElement.class);

        event.target = target;
        target.parentNode = parent;
        target.classList = mock(DOMTokenList.class);

        when(target.classList.contains(GRIP)).thenReturn(false);

        view.onStartDrag(event);

        verify(view, never()).holdDraggingElement(parent);
    }

    @Test
    public void testOnDragWhenUserIsDragging() {

        final Event event = mock(Event.class);

        doReturn(false).when(view).isNotDragging();
        doNothing().when(view).updateDraggingElementY(event);
        doNothing().when(view).updateDraggingElementX(event);
        doNothing().when(view).updateHoverElement();
        doNothing().when(view).updateDependentsPosition();

        view.onDrag(event);

        verify(view).updateDraggingElementY(event);
        verify(view).updateDraggingElementX(event);
        verify(view).updateHoverElement();
        verify(view).updateDependentsPosition();
    }

    @Test
    public void testOnDragWhenUserIsNotDragging() {

        final Event event = mock(Event.class);

        doReturn(true).when(view).isNotDragging();

        view.onDrag(event);

        verify(view, never()).updateDraggingElementY(event);
        verify(view, never()).updateDraggingElementX(event);
        verify(view, never()).updateHoverElement();
        verify(view, never()).updateDependentsPosition();
    }

    @Test
    public void testOnDropWhenUserIsDragging() {

        doReturn(false).when(view).isNotDragging();
        doNothing().when(view).updateDraggingElementsPosition();
        doNothing().when(view).executeOnDropItemCallback();
        doNothing().when(view).releaseDraggingElement();
        doNothing().when(view).consolidateHierarchicalLevel(true);
        doNothing().when(view).refreshItemsPosition();
        doNothing().when(view).refreshItemsHTML();
        doNothing().when(view).clearHover();

        view.onDrop();

        verify(view).updateDraggingElementsPosition();
        verify(view).executeOnDropItemCallback();
        verify(view).releaseDraggingElement();
        verify(view).consolidateHierarchicalLevel(true);
        verify(view).refreshItemsPosition();
        verify(view).refreshItemsHTML();
        verify(view).clearHover();
    }

    @Test
    public void testOnDropWhenUserIsNotDragging() {

        doReturn(true).when(view).isNotDragging();

        view.onDrop();

        verify(view, never()).updateDraggingElementsPosition();
        verify(view, never()).executeOnDropItemCallback();
        verify(view, never()).releaseDraggingElement();
        verify(view, never()).consolidateHierarchicalLevel(true);
        verify(view, never()).refreshItemsPosition();
        verify(view, never()).refreshItemsHTML();
        verify(view, never()).clearHover();
    }

    @Test
    public void testUpdateDraggingElementsPositionWhenPreviousElementHasChildren() {

        final HTMLElement draggingElement = mock(HTMLElement.class);
        final HTMLElement previousElement = mock(HTMLElement.class);
        final HTMLElement getDependentElement0 = mock(HTMLElement.class);
        final HTMLElement getDependentElement1 = mock(HTMLElement.class);
        final HTMLElement getDependentElement2 = mock(HTMLElement.class);
        final List<HTMLElement> dependentElements = asList(getDependentElement0, getDependentElement1, getDependentElement2);

        draggingElement.style = mock(CSSStyleDeclaration.class);
        getDependentElement0.style = mock(CSSStyleDeclaration.class);
        getDependentElement1.style = mock(CSSStyleDeclaration.class);
        getDependentElement2.style = mock(CSSStyleDeclaration.class);

        doReturn(draggingElement).when(view).getDragging();
        doReturn(Optional.of(previousElement)).when(view).getPreviousElement(draggingElement);
        doReturn(dependentElements).when(view).getDependentElements();
        doReturn(true).when(view).hasChildren(previousElement);

        when(presenter.getIndentationSize()).thenReturn(50);
        when(draggingElement.style.getPropertyValue("padding-left")).thenReturn("100px");
        when(getDependentElement0.style.getPropertyValue("padding-left")).thenReturn("150px");
        when(getDependentElement1.style.getPropertyValue("padding-left")).thenReturn("200px");
        when(getDependentElement2.style.getPropertyValue("padding-left")).thenReturn("250px");

        view.updateDraggingElementsPosition();

        verify(draggingElement).setAttribute(DATA_X_POSITION, 3);
        verify(getDependentElement0).setAttribute(DATA_X_POSITION, 4);
        verify(getDependentElement1).setAttribute(DATA_X_POSITION, 5);
        verify(getDependentElement2).setAttribute(DATA_X_POSITION, 6);
    }

    @Test
    public void testUpdateDraggingElementsPositionWhenPreviousElementDoesNotHaveChildren() {

        final HTMLElement draggingElement = mock(HTMLElement.class);
        final HTMLElement previousElement = mock(HTMLElement.class);
        final HTMLElement dependentElement0 = mock(HTMLElement.class);
        final HTMLElement dependentElement1 = mock(HTMLElement.class);
        final HTMLElement dependentElement2 = mock(HTMLElement.class);
        final List<HTMLElement> dependentElements = asList(dependentElement0, dependentElement1, dependentElement2);

        draggingElement.style = mock(CSSStyleDeclaration.class);
        dependentElement0.style = mock(CSSStyleDeclaration.class);
        dependentElement1.style = mock(CSSStyleDeclaration.class);
        dependentElement2.style = mock(CSSStyleDeclaration.class);

        doReturn(draggingElement).when(view).getDragging();
        doReturn(Optional.of(previousElement)).when(view).getPreviousElement(draggingElement);
        doReturn(dependentElements).when(view).getDependentElements();
        doReturn(false).when(view).hasChildren(previousElement);

        when(presenter.getIndentationSize()).thenReturn(50);
        when(draggingElement.style.getPropertyValue("padding-left")).thenReturn("100px");
        when(dependentElement0.style.getPropertyValue("padding-left")).thenReturn("150px");
        when(dependentElement1.style.getPropertyValue("padding-left")).thenReturn("200px");
        when(dependentElement2.style.getPropertyValue("padding-left")).thenReturn("250px");

        view.updateDraggingElementsPosition();

        verify(draggingElement).setAttribute(DATA_X_POSITION, 2);
        verify(dependentElement0).setAttribute(DATA_X_POSITION, 3);
        verify(dependentElement1).setAttribute(DATA_X_POSITION, 4);
        verify(dependentElement2).setAttribute(DATA_X_POSITION, 5);
    }

    @Test
    public void testExecuteOnDropItemCallbackWhenHoverElementIsPresent() {

        final HTMLElement hoverElement = mock(HTMLElement.class);
        final HTMLElement draggingElement = mock(HTMLElement.class);
        final HTMLElement dependentElement0 = mock(HTMLElement.class);
        final HTMLElement dependentElement1 = mock(HTMLElement.class);
        final HTMLElement dependentElement2 = mock(HTMLElement.class);
        final List<HTMLElement> dependentElements = asList(dependentElement0, dependentElement1, dependentElement2);
        final List<HTMLElement> expectedChildren = asList(dependentElement0, dependentElement1, dependentElement2, draggingElement);

        doReturn(draggingElement).when(view).getDragging();
        doReturn(dependentElements).when(view).getDependentElements();
        doNothing().when(view).fixChildrenPosition(anyInt(), anyInt(), anyListOf(HTMLElement.class));
        when(dragArea.querySelector(".kie-dnd-hover")).thenReturn(hoverElement);
        when(hoverElement.getAttribute(DATA_X_POSITION)).thenReturn("3");
        when(draggingElement.getAttribute(DATA_X_POSITION)).thenReturn("4");

        view.executeOnDropItemCallback();

        verify(draggingElement).setAttribute(DATA_X_POSITION, 4);
        verify(view).fixChildrenPosition(4, 0, expectedChildren);
        verify(presenter).executeOnDropItemCallback(draggingElement, hoverElement);
    }

    @Test
    public void testExecuteOnDropItemCallbackWhenHoverElementIsNotPresent() {

        final HTMLElement draggingElement = mock(HTMLElement.class);

        doReturn(draggingElement).when(view).getDragging();
        when(dragArea.querySelector(".kie-dnd-hover")).thenReturn(null);

        view.executeOnDropItemCallback();

        verifyNoMoreInteractions(draggingElement);
        verify(view, never()).fixChildrenPosition(anyInt(), anyInt(), anyListOf(HTMLElement.class));
        verify(presenter).executeOnDropItemCallback(draggingElement, null);
    }

    @Test
    public void testFixChildrenPosition() {

        final HTMLElement dependentElement0 = mock(HTMLElement.class);
        final HTMLElement dependentElement1 = mock(HTMLElement.class);
        final HTMLElement dependentElement2 = mock(HTMLElement.class);
        final int minimalXPosition = 4;
        final int numberOfExtraLevels = 6;
        final List<HTMLElement> children = asList(dependentElement0, dependentElement1, dependentElement2);

        when(dependentElement0.getAttribute(DATA_X_POSITION)).thenReturn("8");
        when(dependentElement1.getAttribute(DATA_X_POSITION)).thenReturn("2");
        when(dependentElement2.getAttribute(DATA_X_POSITION)).thenReturn("5");

        view.fixChildrenPosition(minimalXPosition, numberOfExtraLevels, children);

        verify(dependentElement0).setAttribute(DATA_X_POSITION, 4);
        verify(dependentElement1).setAttribute(DATA_X_POSITION, 4);
    }

    @Test
    public void testUpdateHoverElementWhenPositionIsInNotTheRange() {

        final HTMLElement draggingElement = mock(HTMLElement.class);

        draggingElement.offsetTop = 75;

        doNothing().when(view).hover(anyInt());
        doReturn(draggingElement).when(view).getDragging();
        when(presenter.getItemHeight()).thenReturn(50);
        when(draggingElement.getAttribute(DATA_Y_POSITION)).thenReturn("1");

        view.updateHoverElement();

        verify(view).hover(2);
    }

    @Test
    public void testUpdateHoverElementWhenPositionIsInTheRange() {

        final HTMLElement draggingElement = mock(HTMLElement.class);

        draggingElement.offsetTop = 45;

        doNothing().when(view).hover(anyInt());
        doReturn(draggingElement).when(view).getDragging();
        when(presenter.getItemHeight()).thenReturn(50);
        when(draggingElement.getAttribute(DATA_Y_POSITION)).thenReturn("1");

        view.updateHoverElement();

        verify(view, never()).hover(anyInt());
    }

    @Test
    public void testHoverWhenHoverElementIsBeingDragged() {

        final HTMLElement hoverElement = mock(HTMLElement.class);

        hoverElement.classList = mock(DOMTokenList.class);
        when(hoverElement.classList.contains(DRAGGING)).thenReturn(true);
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"42\"]")).thenReturn(hoverElement);

        doNothing().when(view).clearHover();

        view.hover(42);

        verify(view).clearHover();
        verify(hoverElement.classList, never()).add(HOVER);
        verify(presenter, never()).highlightLevel(hoverElement);
    }

    @Test
    public void testHoverWhenHoverElementIsNotBeingDragged() {

        final HTMLElement hoverElement = mock(HTMLElement.class);

        hoverElement.classList = mock(DOMTokenList.class);
        when(hoverElement.classList.contains(DRAGGING)).thenReturn(false);
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"42\"]")).thenReturn(hoverElement);

        doNothing().when(view).clearHover();

        view.hover(42);

        verify(view).clearHover();
        verify(hoverElement.classList).add(HOVER);
        verify(presenter).highlightLevel(hoverElement);
    }

    @Test
    public void testClearHover() {

        final HTMLElement hoverElement = mock(HTMLElement.class);

        hoverElement.classList = mock(DOMTokenList.class);
        when(dragArea.querySelector(".kie-dnd-hover")).thenReturn(hoverElement);

        view.clearHover();

        verify(hoverElement.classList).remove(HOVER);
    }

    @Test
    public void testUpdateDependentsPosition() {

        final HTMLElement draggingElement = mock(HTMLElement.class);
        final HTMLElement dependentElement0 = mock(HTMLElement.class);
        final HTMLElement dependentElement1 = mock(HTMLElement.class);
        final HTMLElement dependentElement2 = mock(HTMLElement.class);
        final List<HTMLElement> dependentElements = asList(dependentElement0, dependentElement1, dependentElement2);

        draggingElement.style = mock(CSSStyleDeclaration.class);

        dependentElement0.style = mock(CSSStyleDeclaration.class);
        dependentElement1.style = mock(CSSStyleDeclaration.class);
        dependentElement2.style = mock(CSSStyleDeclaration.class);

        when(presenter.getItemHeight()).thenReturn(50);
        when(presenter.getIndentationSize()).thenReturn(50);

        when(draggingElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(dependentElement0.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(dependentElement1.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(dependentElement2.getAttribute(DATA_X_POSITION)).thenReturn("2");

        when(draggingElement.style.getPropertyValue("top")).thenReturn("50px");
        when(draggingElement.style.getPropertyValue("width")).thenReturn("calc(100% - 100px)");

        doReturn(draggingElement).when(view).getDragging();
        doReturn(dependentElements).when(view).getDependentElements();

        view.updateDependentsPosition();

        verify(dependentElement0.style).setProperty("top", "100px");
        verify(dependentElement1.style).setProperty("top", "150px");
        verify(dependentElement2.style).setProperty("top", "200px");
        verify(dependentElement0.style).setProperty("width", "calc(100% - 100px)");
        verify(dependentElement1.style).setProperty("width", "calc(100% - 100px)");
        verify(dependentElement2.style).setProperty("width", "calc(100% - 100px)");
    }

    @Test
    public void testHasChildrenWhenElementHasChildren() {

        final HTMLElement currentElement = mock(HTMLElement.class);
        final HTMLElement draggingElement = mock(HTMLElement.class);
        final HTMLElement nextElement = mock(HTMLElement.class);

        doReturn(draggingElement).when(view).getDragging();

        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"1\"]")).thenReturn(draggingElement);
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"2\"]")).thenReturn(nextElement);

        when(currentElement.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(draggingElement.getAttribute(DATA_Y_POSITION)).thenReturn("1");
        when(nextElement.getAttribute(DATA_Y_POSITION)).thenReturn("2");

        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(draggingElement.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(nextElement.getAttribute(DATA_X_POSITION)).thenReturn("1");

        assertTrue(view.hasChildren(currentElement));
    }

    @Test
    public void testHasChildrenWhenElementDoesNotHaveChildren() {

        final HTMLElement currentElement = mock(HTMLElement.class);
        final HTMLElement nextElement = mock(HTMLElement.class);

        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"1\"]")).thenReturn(nextElement);

        when(currentElement.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(nextElement.getAttribute(DATA_Y_POSITION)).thenReturn("1");

        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(nextElement.getAttribute(DATA_X_POSITION)).thenReturn("0");

        assertFalse(view.hasChildren(currentElement));
    }

    @Test
    public void testHasChildrenWhenNextElementIsNull() {

        final HTMLElement currentElement = mock(HTMLElement.class);

        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"1\"]")).thenReturn(null);

        when(currentElement.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("0");

        assertFalse(view.hasChildren(currentElement));
    }

    @Test
    public void testUpdateDraggingElementYWhenDraggingYPositionIsLessThanMin() {

        final Event event = mock(Event.class);
        final HTMLElement draggingElement = mock(HTMLElement.class);
        final HTMLElement siblingElement = mock(HTMLElement.class);
        final HTMLElement getDependentElement0 = mock(HTMLElement.class);
        final HTMLElement getDependentElement1 = mock(HTMLElement.class);
        final HTMLElement getDependentElement2 = mock(HTMLElement.class);
        final List<HTMLElement> dependentElements = asList(getDependentElement0, getDependentElement1, getDependentElement2);
        final int newDraggingYPosition = 10;

        draggingElement.offsetTop = 130;
        draggingElement.style = mock(CSSStyleDeclaration.class);

        when(draggingElement.getAttribute(DATA_Y_POSITION)).thenReturn("1");
        when(presenter.getItemHeight()).thenReturn(50);
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"6\"]")).thenReturn(siblingElement);

        doNothing().when(view).clearHover();
        doNothing().when(view).refreshItemsPosition();
        doReturn(draggingElement).when(view).getDragging();
        doReturn(newDraggingYPosition).when(view).getNewDraggingYPosition(event);
        doReturn(dependentElements).when(view).getDependentElements();

        view.updateDraggingElementY(event);

        // update dragging element y
        verify(siblingElement).setAttribute(DATA_Y_POSITION, 1);
        verify(draggingElement).setAttribute(DATA_Y_POSITION, 3);
        verify(getDependentElement0).setAttribute(DATA_Y_POSITION, 4);
        verify(getDependentElement1).setAttribute(DATA_Y_POSITION, 5);
        verify(getDependentElement2).setAttribute(DATA_Y_POSITION, 6);
        verify(view).clearHover();
        verify(view).refreshItemsPosition();

        // set CSS top
        verify(draggingElement.style).setProperty("top", "10px");
    }

    @Test
    public void testUpdateDraggingElementYWhenDraggingYPositionIsGreaterThanMin() {

        final Event event = mock(Event.class);
        final HTMLElement draggingElement = mock(HTMLElement.class);
        final HTMLElement siblingElement = mock(HTMLElement.class);
        final HTMLElement getDependentElement0 = mock(HTMLElement.class);
        final HTMLElement getDependentElement1 = mock(HTMLElement.class);
        final HTMLElement getDependentElement2 = mock(HTMLElement.class);
        final List<HTMLElement> dependentElements = asList(getDependentElement0, getDependentElement1, getDependentElement2);
        final int newDraggingYPosition = 10;

        draggingElement.offsetTop = 30;
        draggingElement.style = mock(CSSStyleDeclaration.class);

        when(draggingElement.getAttribute(DATA_Y_POSITION)).thenReturn("2");
        when(presenter.getItemHeight()).thenReturn(50);
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"1\"]")).thenReturn(siblingElement);

        doNothing().when(view).clearHover();
        doNothing().when(view).refreshItemsPosition();
        doReturn(draggingElement).when(view).getDragging();
        doReturn(newDraggingYPosition).when(view).getNewDraggingYPosition(event);
        doReturn(dependentElements).when(view).getDependentElements();

        view.updateDraggingElementY(event);

        // update dragging element y
        verify(siblingElement).setAttribute(DATA_Y_POSITION, 5);
        verify(draggingElement).setAttribute(DATA_Y_POSITION, 1);
        verify(getDependentElement0).setAttribute(DATA_Y_POSITION, 2);
        verify(getDependentElement1).setAttribute(DATA_Y_POSITION, 3);
        verify(getDependentElement2).setAttribute(DATA_Y_POSITION, 4);
        verify(view).clearHover();
        verify(view).refreshItemsPosition();

        // set CSS top
        verify(draggingElement.style).setProperty("top", "10px");
    }

    @Test
    public void testUpdateDraggingElementX() {

        final MouseEvent event = mock(MouseEvent.class);
        final HTMLElement draggingElement = mock(HTMLElement.class);
        final DOMRect rect = mock(DOMRect.class);

        event.x = 100;
        rect.left = 25;
        draggingElement.style = mock(CSSStyleDeclaration.class);
        dragArea.offsetWidth = 300;

        when(presenter.getIndentationSize()).thenReturn(50);
        when(dragArea.getBoundingClientRect()).thenReturn(rect);
        doReturn(draggingElement).when(view).getDragging();

        view.updateDraggingElementX(event);

        verify(draggingElement.style).setProperty("width", "calc(100% - 65px)");
    }

    @Test
    public void testUpdateDraggingElementXWhenNewDraggingXPositionIsGreaterThanMax() {

        final MouseEvent event = mock(MouseEvent.class);
        final HTMLElement draggingElement = mock(HTMLElement.class);
        final DOMRect rect = mock(DOMRect.class);

        event.x = 1000;
        rect.left = 25;
        draggingElement.style = mock(CSSStyleDeclaration.class);
        dragArea.offsetWidth = 300;

        when(presenter.getIndentationSize()).thenReturn(50);
        when(dragArea.getBoundingClientRect()).thenReturn(rect);
        doReturn(draggingElement).when(view).getDragging();

        view.updateDraggingElementX(event);

        verify(draggingElement.style).setProperty("width", "calc(100% - 250px)");
    }

    @Test
    public void testUpdateDraggingElementXWhenNewDraggingXPositionIsLessThanZero() {

        final MouseEvent event = mock(MouseEvent.class);
        final HTMLElement draggingElement = mock(HTMLElement.class);
        final DOMRect rect = mock(DOMRect.class);

        event.x = -1000;
        rect.left = 25;
        draggingElement.style = mock(CSSStyleDeclaration.class);
        dragArea.offsetWidth = 300;

        when(presenter.getIndentationSize()).thenReturn(50);
        when(dragArea.getBoundingClientRect()).thenReturn(rect);
        doReturn(draggingElement).when(view).getDragging();

        view.updateDraggingElementX(event);

        verify(draggingElement.style).setProperty("width", "calc(100% - 0px)");
    }

    @Test
    public void testGetNewDraggingYPosition() {

        final MouseEvent event = mock(MouseEvent.class);
        final HTMLElement draggingElement = mock(HTMLElement.class);
        final DOMRect rect = mock(DOMRect.class);

        event.y = 100;
        rect.top = 25;
        draggingElement.style = mock(CSSStyleDeclaration.class);
        dragArea.offsetHeight = 300;

        when(presenter.getItemHeight()).thenReturn(50);
        when(dragArea.getBoundingClientRect()).thenReturn(rect);
        doReturn(draggingElement).when(view).getDragging();

        final int actualYPosition = view.getNewDraggingYPosition(event);
        final int expectedYPosition = 50;

        assertEquals(expectedYPosition, actualYPosition);
    }

    @Test
    public void testGetNewDraggingYPositionWhenNewYPositionIsGreaterThanMax() {

        final MouseEvent event = mock(MouseEvent.class);
        final HTMLElement draggingElement = mock(HTMLElement.class);
        final DOMRect rect = mock(DOMRect.class);

        event.y = 1000;
        rect.top = 25;
        draggingElement.style = mock(CSSStyleDeclaration.class);
        dragArea.offsetHeight = 300;

        when(presenter.getItemHeight()).thenReturn(50);
        when(dragArea.getBoundingClientRect()).thenReturn(rect);
        doReturn(draggingElement).when(view).getDragging();

        final int actualYPosition = view.getNewDraggingYPosition(event);
        final int expectedYPosition = 325;

        assertEquals(expectedYPosition, actualYPosition);
    }

    @Test
    public void testIsNotDraggingWhenItsDragging() {
        doReturn(mock(HTMLElement.class)).when(view).getDragging();
        assertFalse(view.isNotDragging());
    }

    @Test
    public void testIsNotDraggingWhenItsNotDragging() {
        doReturn(null).when(view).getDragging();
        assertTrue(view.isNotDragging());
    }

    @Test
    public void testHoldDraggingElement() {

        final HTMLElement expectedDragging = mock(HTMLElement.class);
        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);
        final List<HTMLElement> expectedDependentElements = asList(element0, element1, element2);

        expectedDragging.classList = mock(DOMTokenList.class);
        element0.classList = mock(DOMTokenList.class);
        element1.classList = mock(DOMTokenList.class);
        element2.classList = mock(DOMTokenList.class);

        doReturn(expectedDependentElements).when(view).getDependentElements(expectedDragging);

        view.holdDraggingElement(expectedDragging);

        final HTMLElement actualDragging = view.getDragging();
        final List<HTMLElement> actualDependentElements = view.getDependentElements();

        expectedDragging.classList.add(DRAGGING);
        element0.classList.add(DRAGGING);
        element1.classList.add(DRAGGING);
        element2.classList.add(DRAGGING);
        assertEquals(expectedDragging, actualDragging);
        assertEquals(expectedDependentElements, actualDependentElements);
    }

    @Test
    public void testReleaseDraggingElement() {

        final HTMLElement dragging = mock(HTMLElement.class);
        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);
        final List<HTMLElement> dependentElements = asList(element0, element1, element2);

        dragging.classList = mock(DOMTokenList.class);
        element0.classList = mock(DOMTokenList.class);
        element1.classList = mock(DOMTokenList.class);
        element2.classList = mock(DOMTokenList.class);

        doReturn(dependentElements).when(view).getDependentElements(dragging);
        view.holdDraggingElement(dragging);

        view.releaseDraggingElement();

        final HTMLElement actualDragging = view.getDragging();
        final List<HTMLElement> actualDependentElements = view.getDependentElements();

        dragging.classList.remove(DRAGGING);
        element0.classList.remove(DRAGGING);
        element1.classList.remove(DRAGGING);
        element2.classList.remove(DRAGGING);
        assertNull(actualDragging);
        assertEquals(emptyList(), actualDependentElements);
    }

    private void mockDragAreaWithChildren(final HTMLElement... children) {

        final NodeList<Element> nodeList = spy(new NodeList<>());

        nodeList.length = children.length;
        for (int i = 0; i < children.length; i++) {
            doReturn(children[i]).when(nodeList).getAt(i);
        }

        when(dragArea.querySelectorAll(any())).thenReturn(nodeList);
    }
}
