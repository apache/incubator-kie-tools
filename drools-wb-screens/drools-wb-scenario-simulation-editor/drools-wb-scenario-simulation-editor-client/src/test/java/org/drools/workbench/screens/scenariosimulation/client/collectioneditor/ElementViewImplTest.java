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

import org.junit.Test;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.FA_ANGLE_DOWN;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.FA_ANGLE_RIGHT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class ElementViewImplTest<E extends ElementView, T extends ElementView.Presenter<E>>  extends AbstractCollectionEditorTest {

    protected T elementPresenterMock;

    protected E elementView;


    protected void setup() {
        super.setup();

    
    }


    @Test
    public void onFaAngleRightClick() {
        when(faAngleRightMock.getClassName()).thenReturn(FA_ANGLE_RIGHT);
        elementView.onFaAngleRightClick(clickEventMock);
        verify(elementPresenterMock, times(1)).onToggleRowExpansion(eq(elementView), eq(false));
        verify(clickEventMock, times(1)).stopPropagation();
        reset(clickEventMock);
        reset(elementPresenterMock);
        when(faAngleRightMock.getClassName()).thenReturn(FA_ANGLE_DOWN);
        elementView.onFaAngleRightClick(clickEventMock);
        verify(elementPresenterMock, times(1)).onToggleRowExpansion(eq(elementView), eq(true));
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onEditItemButtonClick() {
        elementView.onEditItemButtonClick(clickEventMock);
        verify(elementPresenterMock, times(1)).onEditItem(eq(elementView));
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onDeleteItemButtonClick() {
        elementView.onDeleteItemButtonClick(clickEventMock);
        verify(elementPresenterMock, times(1)).onDeleteItem(eq(elementView));
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onSaveChangeButtonClick() {
        elementView.onSaveChangeButtonClick(clickEventMock);
        verify(elementPresenterMock, times(1)).updateItem(eq(elementView));
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onCancelChangeButton() {
        elementView.onCancelChangeButton(clickEventMock);
        verify(elementPresenterMock, times(1)).onStopEditingItem(eq(elementView));
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void isShown() {
        when(faAngleRightMock.getClassName()).thenReturn(FA_ANGLE_DOWN);
        assertTrue(elementView.isShown());
        when(faAngleRightMock.getClassName()).thenReturn(FA_ANGLE_RIGHT);
        assertFalse(elementView.isShown());
    }

    @Test
    public void toggleRowExpansion() {
        commonToggleRowExpansion(true);
        commonToggleRowExpansion(false);
    }

    private void commonToggleRowExpansion(boolean toExpand) {
        String classToAdd = toExpand ? FA_ANGLE_DOWN : FA_ANGLE_RIGHT;
        String classToRemove= toExpand ? FA_ANGLE_RIGHT : FA_ANGLE_DOWN;
        elementView.toggleRowExpansion(toExpand);
        verify(faAngleRightMock, times(1)).addClassName(eq(classToAdd));
        verify(faAngleRightMock, times(1)).removeClassName(eq(classToRemove));
        reset(faAngleRightMock);
    }

}