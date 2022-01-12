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
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public abstract class AbstractCollectionEditorTest {

    @Mock
    protected ViewsProvider viewsProviderMock;

    @Mock
    protected CollectionPresenter collectionPresenterMock;

    @Mock
    protected PropertyPresenter propertyPresenterMock;

    @Mock
    protected LIElement saveChangeMock;

    @Mock
    protected Style styleMock;

    @Mock
    protected LIElement itemContainerMock;

    @Mock
    protected UListElement innerItemContainerMock;

    @Mock
    protected SpanElement faAngleRightMock;

    @Mock
    protected LIElement itemSeparatorMock;

    @Mock
    protected SpanElement itemSeparatorTextMock;

    @Mock
    protected LIElement editingPropertyFieldsMock;

    @Mock
    protected ClickEvent clickEventMock;

    protected void setup() {
        when(saveChangeMock.getStyle()).thenReturn(styleMock);
        when(propertyPresenterMock.getEditingPropertyFields(anyString(), anyString(), anyString())).thenReturn(editingPropertyFieldsMock);
    }

}
