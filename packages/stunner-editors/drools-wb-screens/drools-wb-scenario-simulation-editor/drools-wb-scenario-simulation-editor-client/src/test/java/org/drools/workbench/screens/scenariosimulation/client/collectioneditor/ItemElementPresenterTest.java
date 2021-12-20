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
package org.drools.workbench.screens.scenariosimulation.client.collectioneditor;

import java.util.Map;

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ELEMENT1_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.EXPANDABLE_PROPERTIES;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.EXPANDABLE_PROPERTIES_VALUES;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ITEM_ID_EXPANDABLE_PROPERTIES_MAP_LOCAL;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_ITEM_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_PROPERTIES_MAP;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ItemElementPresenterTest extends ElementPresenterTest<ItemElementView, ItemElementView.Presenter> {


    @Mock
    private LIElement propertyFieldsMock;

    @Before
    public void setup() {
        elementView1Mock = mock(ItemElementView.class);
        elementView2Mock = mock(ItemElementView.class);
        super.setup();
        when(viewsProviderMock.getListEditorElementView()).thenReturn(elementView1Mock);
        when(propertyPresenterMock.getPropertyFields(anyString(), anyString(), anyString())).thenReturn(propertyFieldsMock);
        elementPresenter = spy(new ItemElementPresenter() {
            {
                this.viewsProvider = viewsProviderMock;
                this.propertyPresenter = propertyPresenterMock;
                this.elementViewList = elementViewListLocal;
                this.collectionEditorPresenter = collectionPresenterMock;
                this.itemIdExpandablePropertiesMap = ITEM_ID_EXPANDABLE_PROPERTIES_MAP_LOCAL;
            }
        });
        ITEM_ID_EXPANDABLE_PROPERTIES_MAP_LOCAL.put(elementView1Mock.getItemId(), EXPANDABLE_PROPERTIES);
        ITEM_ID_EXPANDABLE_PROPERTIES_MAP_LOCAL.put(elementView2Mock.getItemId(), EXPANDABLE_PROPERTIES);
    }

    @Test
    public void getItemContainer() {
        elementViewListLocal.clear();
        ITEM_ID_EXPANDABLE_PROPERTIES_MAP_LOCAL.clear();
        LIElement itemContainer = elementPresenter.getItemContainer(TEST_ITEM_ID, TEST_PROPERTIES_MAP, EXPANDABLE_PROPERTIES_VALUES);
        verify(elementView1Mock, times(1 + EXPANDABLE_PROPERTIES_VALUES.size())).init(elementPresenter); // Add invocation inside EXPANDABLE_PROPERTIES_VALUES loop
        verify(elementView1Mock, times(1)).setItemId(TEST_ITEM_ID);
        verify(elementView1Mock, times(1 + EXPANDABLE_PROPERTIES_VALUES.size())).getItemContainer(); // Add invocation inside EXPANDABLE_PROPERTIES_VALUES loop
        verify(elementView1Mock, times(3)).getSaveChange(); // Three times because invoked also inside EXPANDABLE_PROPERTIES_VALUES loop
        TEST_PROPERTIES_MAP.forEach((propertyName, propertyValue) -> {
            verify(propertyPresenterMock, times(1))
                    .getPropertyFields(eq(TEST_ITEM_ID), eq(propertyName), eq(propertyValue));
            verify(innerItemContainerMock, times(1)).insertBefore(propertyFieldsMock, saveChangeMock);
            reset(innerItemContainerMock);
        });
        assertTrue(ITEM_ID_EXPANDABLE_PROPERTIES_MAP_LOCAL.containsKey(TEST_ITEM_ID));
        EXPANDABLE_PROPERTIES_VALUES.forEach((nestedPropertyName, nestedPropertiesValues) -> {
            assertTrue(ITEM_ID_EXPANDABLE_PROPERTIES_MAP_LOCAL.get(TEST_ITEM_ID).contains(nestedPropertyName));
            verify((ItemElementPresenter) elementPresenter, times(1)).addExpandableItemElementView(eq(elementView1Mock), eq(nestedPropertiesValues), eq(nestedPropertyName));
        });
        assertNotNull(itemContainer);
        assertTrue(elementViewListLocal.contains(elementView1Mock));
    }

    @Test
    public void onEditItemShown() {
        doReturn(true).when(elementView1Mock).isShown();
        elementPresenter.onEditItem(elementView1Mock);
        verify(elementPresenter, never()).onToggleRowExpansion(eq(elementView1Mock), eq((false)));
        verify(propertyPresenterMock, times(1)).editProperties(eq(elementView1Mock.getItemId()));
        for (String expandableProperty : EXPANDABLE_PROPERTIES) {
            verify(propertyPresenterMock, times(1)).editProperties(eq(ELEMENT1_ID + "." + expandableProperty));
        }
        verify(styleMock, times(1)).setDisplay(Style.Display.INLINE);
        verify(collectionPresenterMock, times(1)).toggleEditingStatus(eq(true));
    }

    @Test
    public void onEditItemNotShown() {
        doReturn(false).when(elementView1Mock).isShown();
        elementPresenter.onEditItem(elementView1Mock);
        verify(elementPresenter, times(1)).onToggleRowExpansion(eq(elementView1Mock), eq((false)));
        verify(propertyPresenterMock, times(1)).editProperties(eq(elementView1Mock.getItemId()));
        for (String expandableProperty : EXPANDABLE_PROPERTIES) {
            verify(propertyPresenterMock, times(1)).editProperties(eq(ELEMENT1_ID + "." + expandableProperty));
        }
        verify(styleMock, times(1)).setDisplay(Style.Display.INLINE);
        verify(collectionPresenterMock, times(1)).toggleEditingStatus(eq(true));
    }

    @Test
    public void onStopEditingItem() {
        elementPresenter.onStopEditingItem(elementView1Mock);
        verify(elementPresenter, never()).onToggleRowExpansion(eq(elementView1Mock), eq((false)));
        verify(propertyPresenterMock, times(1)).stopEditProperties(eq(ELEMENT1_ID));
        verify(styleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(collectionPresenterMock, times(1)).toggleEditingStatus(eq(false));
    }

    @Test
    public void onDeleteItem() {
        elementPresenter.onDeleteItem(elementView1Mock);
        verify(propertyPresenterMock, times(1)).deleteProperties(eq(ELEMENT1_ID));
        verify(itemContainerMock, times(1)).removeFromParent();
        assertFalse(elementViewListLocal.contains(elementView1Mock));
        verify(collectionPresenterMock, times(1)).toggleEditingStatus(eq(false));
    }

    @Test
    public void updateItem() {
        elementPresenter.updateItem(elementView1Mock);
        verify(propertyPresenterMock, times(1)).updateProperties(eq(elementView1Mock.getItemId()));
        for (String expandableProperty : EXPANDABLE_PROPERTIES) {
            verify(propertyPresenterMock, times(1)).updateProperties(eq(ELEMENT1_ID + "." + expandableProperty));
        }
        verify(styleMock, times(1)).setDisplay(Style.Display.NONE);
        verify(collectionPresenterMock, times(1)).toggleEditingStatus(eq(false));
    }

    @Test
    public void getItemsProperties() {
        Map<String, Map<String, String>> itemsProperties = elementPresenter.getSimpleItemsProperties();
        assertNotNull(itemsProperties);
    }
}
