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

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.events.CloseCompositeEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SaveEditorEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CollectionViewImplTest extends AbstractCollectionEditorTest {

    private CollectionViewImpl collectionEditorViewImpl;

    @Mock
    protected DivElement collectionEditorModalBodyMock;

    @Before
    public void setup() {
        when(collectionEditorModalBodyMock.getStyle()).thenReturn(styleMock);
        this.collectionEditorViewImpl = spy(new CollectionViewImpl() {
            {
                this.presenter = collectionPresenterMock;
                this.collectionEditorModalBody = collectionEditorModalBodyMock;
            }
        });
    }

    @Test
    public void setValue() {
        String testValue = "TEST-JSON";
        collectionEditorViewImpl.setValue(testValue);
        verify(collectionPresenterMock, times(1)).setValue(eq(testValue));
    }

    @Test
    public void onCloseCollectionEditorButtonClick() {
        collectionEditorViewImpl.onCloseCollectionEditorButtonClick(clickEventMock);
        verify(collectionEditorViewImpl, times(1)).fireEvent(isA(CloseCompositeEvent.class));
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onCancelButtonClick() {
        collectionEditorViewImpl.onCancelButtonClick(clickEventMock);
        verify(collectionEditorViewImpl, times(1)).close();
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onRemoveButtonClick() {
        collectionEditorViewImpl.onRemoveButtonClick(clickEventMock);
        verify(collectionPresenterMock, times(1)).remove();
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onSaveButtonClick() {
        collectionEditorViewImpl.onSaveButtonClick(clickEventMock);
        verify(collectionPresenterMock, times(1)).save();
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onAddItemButton() {
        collectionEditorViewImpl.onAddItemButton(clickEventMock);
        verify(collectionPresenterMock, times(1)).showEditingBox();
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onFaAngleRightClick() {
        doReturn(true).when(collectionEditorViewImpl).isShown();
        collectionEditorViewImpl.onFaAngleRightClick(clickEventMock);
        verify(collectionPresenterMock, times(1)).onToggleRowExpansion(eq(true));
        verify(clickEventMock, times(1)).stopPropagation();
        reset(collectionPresenterMock);
        reset(clickEventMock);
        doReturn(false).when(collectionEditorViewImpl).isShown();
        collectionEditorViewImpl.onFaAngleRightClick(clickEventMock);
        verify(collectionPresenterMock, times(1)).onToggleRowExpansion(eq(false));
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void toggleRowExpansion() {
        doReturn(true).when(collectionEditorViewImpl).isShown();
        collectionEditorViewImpl.toggleRowExpansion();
        verify(collectionEditorViewImpl, times(1)).toggleRowExpansion(false);
        reset(collectionEditorViewImpl);
        doReturn(false).when(collectionEditorViewImpl).isShown();
        collectionEditorViewImpl.toggleRowExpansion();
        verify(collectionEditorViewImpl, times(1)).toggleRowExpansion(true);
    }

    @Test
    public void updateValue() {
        collectionEditorViewImpl.updateValue("TEST_VALUE");
        verify(collectionEditorViewImpl, times(1)).fireEvent(isA(SaveEditorEvent.class));
    }

    @Test
    public void close() {
        collectionEditorViewImpl.close();
        verify(collectionEditorViewImpl, times(1)).fireEvent(isA(CloseCompositeEvent.class));
    }

    @Test
    public void setFixedHeight() {
        double value = 23.0;
        Style.Unit unit = Style.Unit.PX;
        collectionEditorViewImpl.setFixedHeight(value, unit);
        verify(styleMock, times(1)).setHeight(eq(value), eq(unit));
    }
}
