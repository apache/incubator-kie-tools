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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.dom.client.UListElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LOWER_CASE_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MAP_TEST_KEY;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_INSTANCE_PROPERTY_MAP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_KEY;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST_PROPERTYNAME;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ItemEditingBoxPresenterTest extends AbstractEditingBoxPresenterTest {


    @Mock
    private ItemEditingBox listEditingBoxMock;


    private List<String> nestedPropertiesNamesListLocal = new ArrayList<>();


    @Before
    public void setup() {
        editingBoxToCloseMock = listEditingBoxMock;
        when(viewsProviderMock.getItemEditingBox()).thenReturn(listEditingBoxMock);
        editingBoxPresenter = spy(new ItemEditingBoxPresenter() {
            {
                this.viewsProvider = viewsProviderMock;
                this.propertyPresenter = propertyPresenterMock;
                this.collectionEditorPresenter = collectionPresenterMock;
                this.nestedPropertiesNamesList = nestedPropertiesNamesListLocal;
            }
        });
        super.setup(); // This call needs to be made after editingBoxPresenter instantiation
        nestedPropertiesNamesListLocal.clear();
    }

    @Test
    public void getEditingBox() {
        ((ItemEditingBoxPresenter)editingBoxPresenter).getEditingBox(TEST_KEY, TEST_INSTANCE_PROPERTY_MAP, new HashMap<>());
        verify(viewsProviderMock, times(1)).getItemEditingBox();
        verify(listEditingBoxMock, times(1)).init(((ItemEditingBoxPresenter)editingBoxPresenter));
        verify(listEditingBoxMock, times(1)).setKey(TEST_KEY);
        verify(editingBoxTitleMock, times(1)).setInnerText("Edit " + TEST_PROPERTYNAME);
        verify(listEditingBoxMock, times(1)).getEditingBox();
    }

    @Test
    public void save() {
        editingBoxPresenter.save();
        verify(propertyPresenterMock, times(1)).updateProperties(LOWER_CASE_VALUE);
        verify(collectionPresenterMock, times(1)).addListItem(anyMap(), anyMap());
    }


    @Test
    public void addExpandableItemEditingBox() {
        reset(propertyPresenterMock);
        assertFalse(nestedPropertiesNamesListLocal.contains(TEST_KEY));
        ItemEditingBox containerItemEditingBoxMock = mock(ItemEditingBox.class);
        UListElement containerPropertiesContainerMock = mock(UListElement.class);
        when(containerItemEditingBoxMock.getPropertiesContainer()).thenReturn(containerPropertiesContainerMock);
        ((ItemEditingBoxPresenter)editingBoxPresenter).addExpandableItemEditingBox(containerItemEditingBoxMock, TEST_INSTANCE_PROPERTY_MAP, TEST_KEY, TEST_PROPERTYNAME);
        verify(viewsProviderMock, times(1)).getItemEditingBox();
        verify(listEditingBoxMock, times(1)).init(((ItemEditingBoxPresenter)editingBoxPresenter));
        verify(listEditingBoxMock, times(1)).setKey(TEST_KEY);
        verify(editingBoxTitleMock, times(1)).setInnerText(TEST_PROPERTYNAME);
        verify(listEditingBoxMock, times(1)).removeButtonToolbar();
        assertTrue(nestedPropertiesNamesListLocal.contains(TEST_PROPERTYNAME));
        verify(propertyPresenterMock, times(1)).getEditingPropertyFields(eq(TEST_PROPERTYNAME), eq(MAP_TEST_KEY), eq(""));
        // verify nested properties added to expandable container
        verify(propertiesContainerMock, times(1)).appendChild(any());
        // Verify expandable item added to main container
        verify(containerPropertiesContainerMock, times(1)).appendChild(eq(editingBoxMock));

    }


}
