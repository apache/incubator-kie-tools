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

import com.google.gwt.dom.client.UListElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LOWER_CASE_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_KEY;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_KEY_PROPERTY_MAP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_PROPERTYNAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_VALUE_PROPERTYY_MAP;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KeyValueEditingBoxPresenterTest extends AbstractEditingBoxPresenterTest {



    @Mock
    private KeyValueEditingBox keyValueEditingBoxMock;

    @Mock
    private UListElement keyContainerMock;

    @Mock
    private UListElement valueContainerMock;

    @Before
    public void setup() {
        editingBoxToCloseMock = keyValueEditingBoxMock;
        when(keyValueEditingBoxMock.getKeyContainer()).thenReturn(keyContainerMock);
        when(keyValueEditingBoxMock.getValueContainer()).thenReturn(valueContainerMock);
        when(keyValueEditingBoxMock.getPropertiesContainer()).thenReturn(propertiesContainerMock);
        when(viewsProviderMock.getKeyValueEditingBox()).thenReturn(keyValueEditingBoxMock);
        editingBoxPresenter = spy(new KeyValueEditingBoxPresenter() {
            {
                this.viewsProvider = viewsProviderMock;
                this.propertyPresenter = propertyPresenterMock;
                this.collectionEditorPresenter = collectionPresenterMock;
            }
        });
        super.setup();
    }

    @Test
    public void getEditingBox() {
        editingBoxMock =  ((KeyValueEditingBoxPresenter)editingBoxPresenter).getEditingBox(TEST_KEY, TEST_KEY_PROPERTY_MAP, TEST_VALUE_PROPERTYY_MAP);
        verify(viewsProviderMock, times(1)).getKeyValueEditingBox();
        verify(keyValueEditingBoxMock, times(1)).init((KeyValueEditingBoxPresenter)editingBoxPresenter);
        verify(keyValueEditingBoxMock, times(1)).setKey(TEST_KEY);
        verify(editingBoxTitleMock, times(1)).setInnerText("Edit " + TEST_PROPERTYNAME);
        verify(keyContainerMock, times(1)).appendChild(editingPropertyFieldsMock);
        verify(valueContainerMock, times(1)).appendChild(editingPropertyFieldsMock);
        verify(keyValueEditingBoxMock, times(1)).getEditingBox();
        assertNotNull(editingBoxMock);
    }

    @Test
    public void save() {
        editingBoxPresenter.save();
        verify(propertyPresenterMock, times(1)).updateProperties("key");
        verify(propertyPresenterMock, times(1)).updateProperties(LOWER_CASE_VALUE);
        verify(collectionPresenterMock, times(1)).addMapItem(anyMap(), anyMap());
    }
}
