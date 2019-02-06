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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ItemElementPresenterTest extends ElementPresenterTest<ItemElementView, ItemElementView.Presenter> {

    private static final String TEST_ITEM_ID = "TEST-ITEM-ID";

    private Map<String, String> testPropertiesMap = Collections.singletonMap("TEST-KEY", "TEST-VALUE");


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
            }
        });
    }

    @Test
    public void getItemContainer() {
        elementViewListLocal.clear();
        LIElement itemContainer = elementPresenter.getItemContainer(TEST_ITEM_ID, testPropertiesMap);
        verify(elementView1Mock, times(1)).init(elementPresenter);
        verify(elementView1Mock, times(1)).setItemId(TEST_ITEM_ID);
        verify(elementView1Mock, times(1)).getItemContainer();
        verify(elementView1Mock, times(1)).getSaveChange();
        verify(innerItemContainerMock, times(1)).insertBefore(propertyFieldsMock, saveChangeMock);
        assertNotNull(itemContainer);
        assertTrue(elementViewListLocal.contains(elementView1Mock));
    }

    @Test
    public void onEditItem() {
        elementPresenter.onEditItem(elementView1Mock);
        verify(propertyPresenterMock, times(1)).editProperties(anyString());
        verify(styleMock, times(1)).setVisibility(Style.Visibility.VISIBLE);
    }

    @Test
    public void onStopEditingItem() {
        elementPresenter.onStopEditingItem(elementView1Mock);
        verify(propertyPresenterMock, times(1)).stopEditProperties(eq(ELEMENT1_ID));
        verify(styleMock, times(1)).setVisibility(eq(Style.Visibility.HIDDEN));
    }

    @Test
    public void onDeleteItem() {
        elementPresenter.onDeleteItem(elementView1Mock);
        verify(propertyPresenterMock, times(1)).deleteProperties(eq(ELEMENT1_ID));
        verify(itemContainerMock, times(1)).removeFromParent();
        assertFalse(elementViewListLocal.contains(elementView1Mock));
    }

    @Test
    public void updateItem() {
        elementPresenter.updateItem(elementView1Mock);
        verify(propertyPresenterMock, times(1)).updateProperties(anyString());
        verify(styleMock, times(1)).setVisibility(Style.Visibility.HIDDEN);
    }


    @Test
    public void getItemsProperties() {
        List<Map<String, String>> itemsProperties = elementPresenter.getItemsProperties();
        assertNotNull(itemsProperties);
    }
}
