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

import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.AbstractCollectionEditorTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractEditingBoxPresenterTest extends AbstractCollectionEditorTest {

    protected EditingBox editingBoxToCloseMock;

    protected EditingBoxPresenter editingBoxPresenter;

    @Mock
    protected LIElement editingBoxMock;

    @Mock
    protected HeadingElement editingBoxTitleMock;

    @Mock
    protected UListElement propertiesContainerMock;

    @Before
    public void setup() {
        super.setup();
        when(editingBoxToCloseMock.getEditingBox()).thenReturn(editingBoxMock);
        when(editingBoxToCloseMock.getEditingBoxTitle()).thenReturn(editingBoxTitleMock);
        when(editingBoxToCloseMock.getPropertiesContainer()).thenReturn(propertiesContainerMock);
    }

    @Test
    public void close() {
        editingBoxPresenter.close(editingBoxToCloseMock);
        verify(editingBoxMock, times(1)).removeFromParent();
        verify(collectionPresenterMock, times(1)).toggleEditingStatus(eq(false));
    }
}