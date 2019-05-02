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

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ELEMENT1_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ELEMENT2_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_ITEM_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_KEY_PROPERTY_MAP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_VALUE_PROPERTYY_MAP;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KeyValueElementPresenterTest extends ElementPresenterTest<KeyValueElementView, KeyValueElementView.Presenter> {


    @Mock
    private LIElement propertyFieldsMock;

    @Mock
    private UListElement keyContainerMock;

    @Mock
    private UListElement valueContainerMock;

    @Mock
    private LIElement keyLabelMock;

    @Mock
    private LIElement valueLabelMock;

    @Before
    public void setup() {
        elementView1Mock = mock(KeyValueElementView.class);
        elementView2Mock = mock(KeyValueElementView.class);
        when(elementView1Mock.getKeyContainer()).thenReturn(keyContainerMock);
        when(elementView2Mock.getKeyContainer()).thenReturn(keyContainerMock);
        when(elementView1Mock.getValueContainer()).thenReturn(valueContainerMock);
        when(elementView2Mock.getValueContainer()).thenReturn(valueContainerMock);
        when(elementView1Mock.getItemId()).thenReturn(ELEMENT1_ID);
        when(elementView2Mock.getItemId()).thenReturn(ELEMENT2_ID);
        when(keyLabelMock.getStyle()).thenReturn(styleMock);
        when(valueLabelMock.getStyle()).thenReturn(styleMock);
        when(elementView1Mock.getKeyLabel()).thenReturn(keyLabelMock);
        when(elementView2Mock.getKeyLabel()).thenReturn(keyLabelMock);
        when(elementView1Mock.getValueLabel()).thenReturn(valueLabelMock);
        when(elementView2Mock.getValueLabel()).thenReturn(valueLabelMock);
        super.setup();
        when(viewsProviderMock.getKeyValueElementView()).thenReturn(elementView1Mock);
        when(propertyPresenterMock.getPropertyFields(anyString(), anyString(), anyString())).thenReturn(propertyFieldsMock);
        elementPresenter = spy(new KeyValueElementPresenter() {
            {
                this.viewsProvider = viewsProviderMock;
                this.propertyPresenter = propertyPresenterMock;
                this.elementViewList = elementViewListLocal;
                this.collectionEditorPresenter = collectionPresenterMock;
            }
        });
    }

    @Test
    public void getKeyValueContainer() {
        elementViewListLocal.clear();
        LIElement keyValueContainer = elementPresenter.getKeyValueContainer(TEST_ITEM_ID, TEST_KEY_PROPERTY_MAP, TEST_VALUE_PROPERTYY_MAP);
        verify(elementView1Mock, times(1)).init(elementPresenter);
        verify(elementView1Mock, times(1)).setItemId(TEST_ITEM_ID);
        verify(elementView1Mock, times(1)).getKeyContainer();
        verify(keyContainerMock, times(1)).appendChild(propertyFieldsMock);
        verify(elementView1Mock, times(1)).getValueContainer();
        verify(valueContainerMock, times(1)).appendChild(propertyFieldsMock);
        assertNotNull(keyValueContainer);
        assertTrue(elementViewListLocal.contains(elementView1Mock));
    }

    @Test
    public void onEditItemShown() {
        doReturn(true).when(elementView1Mock).isShown();
        elementPresenter.onEditItem(elementView1Mock);
        verify(elementPresenter, never()).onToggleRowExpansion(eq(elementView1Mock), eq((false)));
        verify(propertyPresenterMock, times(2)).editProperties(anyString());
        verify(styleMock, times(1)).setDisplay(Style.Display.INLINE);
        verify(collectionPresenterMock, times(1)).toggleEditingStatus(eq(true));
    }

    @Test
    public void onEditItemNotShown() {
        doReturn(false).when(elementView1Mock).isShown();
        elementPresenter.onEditItem(elementView1Mock);
        verify(propertyPresenterMock, times(2)).editProperties(anyString());
        verify(styleMock, times(1)).setDisplay(Style.Display.INLINE);
        verify(collectionPresenterMock, times(1)).toggleEditingStatus(eq(true));
    }

    @Test
    public void onStopEditingItem() {
        elementPresenter.onStopEditingItem(elementView1Mock);
        verify(propertyPresenterMock, times(1)).stopEditProperties(eq(ELEMENT1_ID + "#key"));
        verify(propertyPresenterMock, times(1)).stopEditProperties(eq(ELEMENT1_ID + "#value"));
        verify(styleMock, times(1)).setDisplay(Style.Display.NONE);
        verify(collectionPresenterMock, times(1)).toggleEditingStatus(eq(false));
    }

    @Test
    public void onDeleteItem() {
        elementPresenter.onDeleteItem(elementView1Mock);
        verify(propertyPresenterMock, times(1)).deleteProperties(eq(ELEMENT1_ID + "#key"));
        verify(propertyPresenterMock, times(1)).deleteProperties(eq(ELEMENT1_ID + "#value"));
        verify(itemContainerMock, times(1)).removeFromParent();
        assertFalse(elementViewListLocal.contains(elementView1Mock));
        verify(collectionPresenterMock, times(1)).toggleEditingStatus(eq(false));
    }

    @Test
    public void updateItem() {
        elementPresenter.updateItem(elementView1Mock);
        verify(propertyPresenterMock, times(2)).updateProperties(anyString());
        verify(styleMock, times(1)).setDisplay(Style.Display.NONE);
        verify(collectionPresenterMock, times(1)).toggleEditingStatus(eq(false));
    }
}
