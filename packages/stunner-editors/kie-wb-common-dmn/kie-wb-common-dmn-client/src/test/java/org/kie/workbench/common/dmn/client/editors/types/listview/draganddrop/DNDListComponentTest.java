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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent.View;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent.DEFAULT_INDENTATION_SIZE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent.DEFAULT_ITEM_HEIGHT;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DATA_X_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DATA_Y_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.HIDDEN_Y_POSITION;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DNDListComponentTest {

    @Mock
    private View view;

    @Mock
    private DataTypeList dataTypeList;

    private DNDListComponent dndListComponent;

    @Before
    public void setup() {
        dndListComponent = spy(new DNDListComponent(view, dataTypeList));
    }

    @Test
    public void testInit() {
        dndListComponent.init();
        verify(view).init(dndListComponent);
    }

    @Test
    public void testRefreshItemsPosition() {
        dndListComponent.refreshItemsPosition();
        verify(view).refreshItemsPosition();
    }

    @Test
    public void testRefreshItemsCSSAndHTMLPosition() {
        dndListComponent.refreshItemsCSSAndHTMLPosition();
        verify(view).consolidateHierarchicalLevel(false);
        verify(view).refreshItemsPosition();
    }

    @Test
    public void testRegisterNewItem() {

        final HTMLElement htmlElement = mock(HTMLElement.class);
        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.registerItem(htmlElement)).thenReturn(expectedElement);

        final HTMLElement actualElement = dndListComponent.registerNewItem(htmlElement);

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = dndListComponent.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testClear() {
        dndListComponent.clear();
        verify(view).clear();
    }

    @Test
    public void testConsolidateYPosition() {
        dndListComponent.consolidateYPosition();
        verify(view).consolidateYPosition();
    }

    @Test
    public void testGetItemHeight() {
        assertEquals(DEFAULT_ITEM_HEIGHT, dndListComponent.getItemHeight());
    }

    @Test
    public void testGetIndentationSize() {
        assertEquals(DEFAULT_INDENTATION_SIZE, dndListComponent.getIndentationSize());
    }

    @Test
    public void testSetPositionX() {

        final Element element = mock(Element.class);
        final int positionX = 1;

        dndListComponent.setPositionX(element, positionX);

        verify(element).setAttribute(DATA_X_POSITION, positionX);
    }

    @Test
    public void testSetPositionY() {

        final Element element = mock(Element.class);
        final int positionY = 1;

        dndListComponent.setPositionY(element, positionY);

        verify(element).setAttribute(DATA_Y_POSITION, positionY);
    }

    @Test
    public void testGetPositionY() {

        final Element element = mock(Element.class);
        final int expectedPositionY = 1;

        when(element.getAttribute(DATA_Y_POSITION)).thenReturn("1");

        final int actualPositionY = dndListComponent.getPositionY(element);

        assertEquals(expectedPositionY, actualPositionY);
    }

    @Test
    public void testOnDropItem() {

        final HTMLElement current = mock(HTMLElement.class);
        final HTMLElement hover = mock(HTMLElement.class);

        dndListComponent.setOnDropItem((c, h) -> {
            assertEquals(current, c);
            assertEquals(hover, h);
        });

        dndListComponent.executeOnDropItemCallback(current, hover);
    }

    @Test
    public void testGetPreviousElement() {

        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);

        when(view.getPreviousElement(element0)).thenReturn(Optional.of(element1));
        when(view.getPreviousElement(element1)).thenReturn(Optional.of(element2));

        final Optional<Element> actual = dndListComponent.getPreviousElement(element0, element -> element == element2);

        assertTrue(actual.isPresent());
        assertEquals(element2, actual.get());
    }

    @Test
    public void testGetPreviousElementWhenReferenceIsNull() {
        assertFalse(dndListComponent.getPreviousElement(null, null).isPresent());
    }

    @Test
    public void testRefreshDragAreaSize() {
        dndListComponent.refreshDragAreaSize();
        verify(view).refreshDragAreaSize();
    }

    @Test
    public void testSetInitialPositionY() {

        final HTMLElement dragAndDropElement = mock(HTMLElement.class);
        final String parentPosition = "4";
        final HTMLElement child0 = mock(HTMLElement.class);
        final HTMLElement child1 = mock(HTMLElement.class);
        final HTMLElement child2 = mock(HTMLElement.class);
        final List<HTMLElement> children = asList(child0, child1, child2);

        when(dragAndDropElement.getAttribute(DATA_Y_POSITION)).thenReturn(parentPosition);

        dndListComponent.setInitialPositionY(dragAndDropElement, children);

        verify(child0).setAttribute(DATA_Y_POSITION, 4.001);
        verify(child1).setAttribute(DATA_Y_POSITION, 4.002);
        verify(child2).setAttribute(DATA_Y_POSITION, 4.003);
    }

    @Test
    public void testSetInitialHiddenPositionY() {

        final HTMLElement element = mock(HTMLElement.class);
        dndListComponent.setInitialHiddenPositionY(element);

        verify(element).setAttribute(DATA_Y_POSITION, HIDDEN_Y_POSITION);
    }

    @Test
    public void testHighlightLevel() {
        final HTMLElement htmlElement = mock(HTMLElement.class);
        dndListComponent.highlightLevel(htmlElement);
        verify(dataTypeList).highlightLevel(htmlElement);
    }
}
