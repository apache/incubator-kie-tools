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
package org.drools.workbench.screens.scenariosimulation.client.collectioneditor.editingbox;

import com.google.gwt.event.dom.client.ClickEvent;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class AbstractEditingBoxImplTest {

    protected EditingBox.Presenter presenterMock;

    protected EditingBoxImpl editingBoxImpl;

    protected ClickEvent clickEventMock = mock(ClickEvent.class);

    @Test
    public void onSaveItemClickEvent() {
        editingBoxImpl.onSaveItemClickEvent(clickEventMock);
        verify(presenterMock, times(1)).save();
        verify(editingBoxImpl, times(1)).close(clickEventMock);
        verify(clickEventMock, times(2)).stopPropagation();
    }

    @Test
    public void onDiscardItemClickEvent() {
        editingBoxImpl.onDiscardItemClickEvent(clickEventMock);
        verify(editingBoxImpl, times(1)).close(clickEventMock);
        verify(clickEventMock, times(2)).stopPropagation();
    }

    @Test
    public void close() {
        editingBoxImpl.close(clickEventMock);
        verify(presenterMock, times(1)).close(editingBoxImpl);
        verify(clickEventMock, times(1)).stopPropagation();
    }

}