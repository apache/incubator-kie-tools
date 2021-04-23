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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.dom.client.ButtonElement;
import org.junit.Test;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ELEMENT1_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ELEMENT2_ID;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class ElementPresenterTest<E extends ElementView, T extends ElementView.Presenter<E>> extends AbstractCollectionEditorTest {



    protected T elementPresenter;

    /**
     * <code>List</code> of currently present <code>ElementView</code>s
     */
    protected List<E> elementViewListLocal = new ArrayList<>();

    protected E elementView1Mock;
    protected E elementView2Mock;

    @Mock
    protected ButtonElement editItemButtonMock;
    @Mock
    protected ButtonElement deleteItemButtonMock;

    protected void setup() {
        super.setup();
        when(elementView1Mock.getEditItemButton()).thenReturn(editItemButtonMock);
        when(elementView2Mock.getEditItemButton()).thenReturn(editItemButtonMock);
        when(elementView1Mock.getDeleteItemButton()).thenReturn(deleteItemButtonMock);
        when(elementView2Mock.getDeleteItemButton()).thenReturn(deleteItemButtonMock);
        when(elementView1Mock.getItemId()).thenReturn(ELEMENT1_ID);
        when(elementView2Mock.getItemId()).thenReturn(ELEMENT2_ID);
        when(elementView1Mock.getItemSeparator()).thenReturn(itemSeparatorMock);
        when(elementView1Mock.getItemSeparator()).thenReturn(itemSeparatorMock);
        when(elementView1Mock.getFaAngleRight()).thenReturn(faAngleRightMock);
        when(elementView2Mock.getFaAngleRight()).thenReturn(faAngleRightMock);
        when(elementView1Mock.getItemContainer()).thenReturn(itemContainerMock);
        when(elementView2Mock.getItemContainer()).thenReturn(itemContainerMock);
        when(elementView1Mock.getInnerItemContainer()).thenReturn(innerItemContainerMock);
        when(elementView2Mock.getInnerItemContainer()).thenReturn(innerItemContainerMock);
        when(elementView1Mock.getSaveChange()).thenReturn(saveChangeMock);
        when(elementView2Mock.getSaveChange()).thenReturn(saveChangeMock);
        elementViewListLocal.add(elementView1Mock);
        elementViewListLocal.add(elementView2Mock);
        for (String el : Arrays.asList(ELEMENT1_ID, ELEMENT2_ID)) {
            when(propertyPresenterMock.getSimpleProperties(eq(el + "#KEY"))).thenReturn(new HashMap<>());
            when(propertyPresenterMock.getSimpleProperties(eq(el + "#MULTIPART_VALUE"))).thenReturn(new HashMap<>());
        }
    }

    @Test
    public void onToggleRowExpansionTrue() {
        commonOnToggleRowExpansion(true);
    }

    @Test
    public void onToggleRowExpansionFalse() {
        commonOnToggleRowExpansion(false);
    }

    @Test
    public void updateCommonToggleStatusNotIsShown() {
        commonUpdateCommonToggleStatus(false, true);
        commonUpdateCommonToggleStatus(false, false);
    }

    @Test
    public void updateCommonToggleStatusIsShown() {
        commonUpdateCommonToggleStatus(true, true);
        commonUpdateCommonToggleStatus(true, false);
    }

    @Test
    public void remove() {
        elementPresenter.remove();
        elementViewListLocal.forEach(elementViewMock -> {
            verify(elementPresenter, times(1)).onDeleteItem(eq(elementViewMock));
        });
    }

    @Test
    public void toggleEditItemButtonStatusToDisableTrue() {
        elementPresenter.toggleEditingStatus(true);
        elementViewListLocal.forEach(elementViewMock -> {
            verify(elementViewMock, times(1)).getEditItemButton();
            verify(elementViewMock, times(1)).getDeleteItemButton();
        });
        verify(editItemButtonMock, times(2)).setDisabled(true);
        verify(deleteItemButtonMock, times(2)).setDisabled(true);
    }

    @Test
    public void toggleEditItemButtonStatusToDisableFalse() {
        elementPresenter.toggleEditingStatus(false);
        elementViewListLocal.forEach(elementViewMock -> {
            verify(elementViewMock, times(1)).getEditItemButton();
            verify(elementViewMock, times(1)).getDeleteItemButton();
        });
        verify(editItemButtonMock, times(2)).setDisabled(false);
        verify(deleteItemButtonMock, times(2)).setDisabled(false);
    }

    private void commonUpdateCommonToggleStatus(boolean isShown, boolean allEquals) {
        if (allEquals) {
            elementViewListLocal.forEach(elementViewMock -> doReturn(!isShown).when(elementViewMock).isShown());
        } else {
            doReturn(!isShown).when(elementView1Mock).isShown();
            doReturn(isShown).when(elementView2Mock).isShown();
        }
        elementPresenter.updateCommonToggleStatus(isShown);
        if (allEquals) {
            verify(collectionPresenterMock, times(1)).updateRowExpansionStatus(eq(isShown));
        } else {
            verify(collectionPresenterMock, never()).updateRowExpansionStatus(eq(isShown));
        }
        reset(collectionPresenterMock);
    }

    private void commonOnToggleRowExpansion(boolean isShown) {
        elementPresenter.onToggleRowExpansion(isShown);
        elementViewListLocal.forEach(elementViewMock -> {
            verify(elementPresenter, times(1)).onToggleRowExpansion(eq(elementViewMock), eq(isShown));
        });
    }
}