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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.editingbox.ItemEditingBoxPresenter;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.editingbox.KeyValueEditingBoxPresenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CollectionPresenterTest extends AbstractCollectionEditorTest {

    private static final String TEST_JSON = "TEST-JSON";
    private final static String TEST_CLASSNAME = "TEST-CLASSNAME";
    private final static String TEST_PROPERTYNAME = "TEST-PROPERTYNAME";
    private final static String TEST_KEY = TEST_CLASSNAME + "#" + TEST_PROPERTYNAME;
    private final static int CHILD_COUNT = 3;
    private final static String ITEM_ID = String.valueOf(CHILD_COUNT - 1);
    private final static String UPDATED_VALUE = "UPDATED_VALUE";
    private final static int JSON_ARRAY_SIZE = 2;
    private final static Set<String> KEY_SET = new HashSet<>(Arrays.asList("prop1", "prop2"));

    @Mock
    private ItemElementPresenter listElementPresenterMock;

    @Mock
    private KeyValueElementPresenter mapElementPresenterMock;

    @Mock
    private ItemEditingBoxPresenter listEditingBoxPresenterMock;

    @Mock
    private KeyValueEditingBoxPresenter mapEditingBoxPresenterMock;

    @Mock
    private CollectionView collectionViewMock;

    private Map<String, Map<String, String>> instancePropertiesMapLocal = new HashMap<>();

    private Map<String, String> keyPropertyMapLocal = new HashMap<>();
    private Map<String, String> propertyMapLocal = new HashMap<>();

    @Mock
    private UListElement elementsContainerMock;

    @Mock
    private LIElement listEditingBoxMock;

    @Mock
    private LIElement mapEditingBoxMock;

    @Mock
    private LIElement itemElementMock;

    @Mock
    private Style styleMock;

    @Mock
    private JSONValue jsonValueMock;

    @Mock
    private JSONArray jsonArrayMock;

    @Mock
    private JSONValue jsonValueChildMock;

    @Mock
    private JSONObject jsonObjectMock;

    @Mock
    private JSONValue jsonValueNeph1Mock;

    @Mock
    private JSONValue jsonValueNeph2Mock;

    @Mock
    private JSONString jsonStringProp1Mock;

    @Mock
    private JSONString jsonStringProp2Mock;

    @Mock
    private JSONObject nestedValue1Mock;

    @Mock
    private JSONObject nestedValue2Mock;

    @Mock
    private LIElement objectSeparatorLIMock;

    @Mock
    private HeadingElement editorTitleMock;

    @Mock
    private SpanElement propertyTitleMock;

    private CollectionPresenter collectionEditorPresenter;

    @Before
    public void setup() {
        when(elementsContainerMock.getChildCount()).thenReturn(CHILD_COUNT);
        when(editorTitleMock.getInnerText()).thenReturn(TEST_KEY);
        when(collectionViewMock.getElementsContainer()).thenReturn(elementsContainerMock);
        when(collectionViewMock.getEditorTitle()).thenReturn(editorTitleMock);
        when(collectionViewMock.getPropertyTitle()).thenReturn(propertyTitleMock);
        when(collectionViewMock.getObjectSeparator()).thenReturn(objectSeparatorLIMock);
        when(objectSeparatorLIMock.getStyle()).thenReturn(styleMock);

        when(nestedValue1Mock.keySet()).thenReturn(KEY_SET);
        when(nestedValue1Mock.get(eq("prop1"))).thenReturn(jsonValueNeph1Mock);
        when(nestedValue1Mock.get(eq("prop2"))).thenReturn(jsonValueNeph2Mock);

        when(nestedValue2Mock.keySet()).thenReturn(KEY_SET);
        when(nestedValue2Mock.get(eq("prop1"))).thenReturn(jsonValueNeph1Mock);
        when(nestedValue2Mock.get(eq("prop2"))).thenReturn(jsonValueNeph2Mock);

        when(jsonValueNeph1Mock.isString()).thenReturn(jsonStringProp1Mock);
        when(jsonValueNeph2Mock.isString()).thenReturn(jsonStringProp2Mock);
        when(jsonValueNeph1Mock.isObject()).thenReturn(nestedValue1Mock);
        when(jsonValueNeph2Mock.isObject()).thenReturn(nestedValue2Mock);

        when(jsonObjectMock.keySet()).thenReturn(KEY_SET);
        when(jsonObjectMock.get(eq("prop1"))).thenReturn(jsonValueNeph1Mock);
        when(jsonObjectMock.get(eq("prop2"))).thenReturn(jsonValueNeph2Mock);

        when(jsonValueChildMock.isObject()).thenReturn(jsonObjectMock);
        when(jsonArrayMock.size()).thenReturn(JSON_ARRAY_SIZE);
        when(jsonArrayMock.get(anyInt())).thenReturn(jsonValueChildMock);
        when(jsonValueMock.isArray()).thenReturn(jsonArrayMock);
        when(jsonValueMock.isObject()).thenReturn(jsonObjectMock);
        this.collectionEditorPresenter = spy(new CollectionPresenter() {
            {
                this.viewsProvider = viewsProviderMock;
                this.listElementPresenter = listElementPresenterMock;
                this.mapElementPresenter = mapElementPresenterMock;
                this.listEditingBoxPresenter = listEditingBoxPresenterMock;
                this.mapEditingBoxPresenter = mapEditingBoxPresenterMock;
                this.instancePropertiesMap = instancePropertiesMapLocal;
                this.collectionView = collectionViewMock;
                this.objectSeparatorLI = objectSeparatorLIMock;
            }

            @Override
            protected JSONValue getJSONValue(String jsonString) {
                return jsonValueMock;
            }

            @Override
            protected String getListValue() {
                return UPDATED_VALUE;
            }

            @Override
            protected String getMapValue() {
                return UPDATED_VALUE;
            }
        });
        instancePropertiesMapLocal.clear();
        when(listElementPresenterMock.getItemContainer(anyString(), anyMap())).thenReturn(itemElementMock);
        when(mapElementPresenterMock.getKeyValueContainer(anyString(), anyMap(), anyMap())).thenReturn(itemElementMock);
        when(listEditingBoxPresenterMock.getEditingBox(anyString(), anyMap())).thenReturn(listEditingBoxMock);
        when(mapEditingBoxPresenterMock.getEditingBox(anyString(), anyMap(), anyMap())).thenReturn(mapEditingBoxMock);
    }

    @Test
    public void initListStructure() {
        collectionEditorPresenter.initListStructure(TEST_KEY, propertyMapLocal, collectionViewMock);
        verify(collectionEditorPresenter, times(1)).commonInit(eq(TEST_KEY), eq(collectionViewMock));
        assertTrue(instancePropertiesMapLocal.containsKey(TEST_KEY));
        assertEquals(instancePropertiesMapLocal.get(TEST_KEY), propertyMapLocal);
        verify(listEditingBoxPresenterMock, times(1)).setCollectionEditorPresenter(eq(collectionEditorPresenter));
        verify(listElementPresenterMock, times(1)).setCollectionEditorPresenter(eq(collectionEditorPresenter));
    }

    @Test
    public void initMapStructure() {
        collectionEditorPresenter.initMapStructure(TEST_KEY, keyPropertyMapLocal, propertyMapLocal, collectionViewMock);
        verify(collectionEditorPresenter, times(1)).commonInit(eq(TEST_KEY), eq(collectionViewMock));
        assertTrue(instancePropertiesMapLocal.containsKey(TEST_KEY + "#key"));
        assertEquals(instancePropertiesMapLocal.get(TEST_KEY + "#key"), keyPropertyMapLocal);
        assertTrue(instancePropertiesMapLocal.containsKey(TEST_KEY + "#value"));
        assertEquals(instancePropertiesMapLocal.get(TEST_KEY + "#value"), propertyMapLocal);
        verify(mapEditingBoxPresenterMock, times(1)).setCollectionEditorPresenter(eq(collectionEditorPresenter));
        verify(mapElementPresenterMock, times(1)).setCollectionEditorPresenter(eq(collectionEditorPresenter));
    }

    @Test
    public void setValueIsListWidgetTrue() {
        commonSetValue(true);
    }

    @Test
    public void setValueIsListWidgetFalse() {
        commonSetValue(false);
    }

    @Test
    public void showEditingBoxIsListWidgetTrue() {
        when(collectionViewMock.isListWidget()).thenReturn(true);
        collectionEditorPresenter.showEditingBox();
        verify(collectionViewMock, times(1)).getElementsContainer();
        verify(listEditingBoxPresenterMock, times(1)).getEditingBox(eq(TEST_KEY), anyMap());
        verify(elementsContainerMock, times(1)).appendChild(eq(listEditingBoxMock));
    }

    @Test
    public void showEditingBoxIsListWidgetFalse() {
        when(collectionViewMock.isListWidget()).thenReturn(false);
        collectionEditorPresenter.showEditingBox();
        verify(collectionViewMock, times(1)).getElementsContainer();
        verify(mapEditingBoxPresenterMock, times(1)).getEditingBox(eq(TEST_KEY), anyMap(), anyMap());
        verify(elementsContainerMock, times(1)).appendChild(eq(mapEditingBoxMock));
    }

    @Test
    public void onToggleRowExpansionIsShownTrue() {
        commonOnToggleRowExpansionIsShown(true, true);
        commonOnToggleRowExpansionIsShown(true, false);
    }

    @Test
    public void onToggleRowExpansionIsShownFalse() {
        commonOnToggleRowExpansionIsShown(false, true);
        commonOnToggleRowExpansionIsShown(false, false);
    }

    @Test
    public void addListItem() {
        collectionEditorPresenter.addListItem(propertyMapLocal);
        verify(collectionViewMock, times(1)).getElementsContainer();
        verify(elementsContainerMock, times(1)).getChildCount();
        verify(listElementPresenterMock, times(1)).getItemContainer(eq(ITEM_ID), eq(propertyMapLocal));
        verify(elementsContainerMock, times(1)).appendChild(eq(itemElementMock));
    }

    @Test
    public void addMapItem() {
        collectionEditorPresenter.addMapItem(keyPropertyMapLocal, propertyMapLocal);
        verify(collectionViewMock, times(1)).getElementsContainer();
        verify(elementsContainerMock, times(1)).getChildCount();
        verify(mapElementPresenterMock, times(1)).getKeyValueContainer(eq(ITEM_ID), eq(keyPropertyMapLocal), eq(propertyMapLocal));
        verify(elementsContainerMock, times(1)).appendChild(eq(itemElementMock));
    }

    @Test
    public void saveIsListWidgetTrue() {
        commonSave(true, true);
        commonSave(true, false);
    }

    @Test
    public void saveIsListWidgetFalse() {
        commonSave(true, true);
        commonSave(true, false);
    }

    @Test
    public void removeIsListWidgetTrue() {
        when(collectionViewMock.isListWidget()).thenReturn(true);
        collectionEditorPresenter.remove();
        verify(listElementPresenterMock, times(1)).remove();
        verify(mapElementPresenterMock, never()).remove();
    }

    @Test
    public void removeIsListWidgetFalse() {
        when(collectionViewMock.isListWidget()).thenReturn(false);
        collectionEditorPresenter.remove();
        verify(mapElementPresenterMock, times(1)).remove();
        verify(listElementPresenterMock, never()).remove();
    }

    @Test
    public void commonInit() {
        collectionEditorPresenter.collectionView = null;
        collectionEditorPresenter.objectSeparatorLI = null;
        collectionEditorPresenter.commonInit(TEST_KEY, collectionViewMock);
        assertEquals(collectionEditorPresenter.collectionView, collectionViewMock);
        verify(editorTitleMock, times(1)).setInnerText(TEST_KEY);
        verify(propertyTitleMock, times(1)).setInnerText(TEST_PROPERTYNAME);
        assertEquals(collectionEditorPresenter.objectSeparatorLI, objectSeparatorLIMock);
    }

    @Test
    public void populateList() {
        collectionEditorPresenter.populateList(jsonValueMock);
        for (int i = 0; i < JSON_ARRAY_SIZE; i++) {
            verify(jsonArrayMock, times(1)).get(eq(i));
        }
        verify(jsonObjectMock, times(JSON_ARRAY_SIZE)).get("prop1");
        verify(jsonObjectMock, times(JSON_ARRAY_SIZE)).get("prop2");
        verify(jsonValueNeph1Mock, times(JSON_ARRAY_SIZE)).isString();
        verify(jsonValueNeph2Mock, times(JSON_ARRAY_SIZE)).isString();
        verify(jsonStringProp1Mock, times(JSON_ARRAY_SIZE)).stringValue();
        verify(jsonStringProp2Mock, times(JSON_ARRAY_SIZE)).stringValue();
        verify(collectionEditorPresenter, times(JSON_ARRAY_SIZE)).addListItem(anyMap());
    }

    @Test
    public void populateMap() {
        collectionEditorPresenter.populateMap(jsonValueMock);
        verify(collectionEditorPresenter, times(JSON_ARRAY_SIZE)).addMapItem(anyMap(), anyMap());
    }

    private void commonSetValue(boolean isListWidget) {
        collectionEditorPresenter.setValue(null);
        verify(collectionEditorPresenter, never()).getJSONValue(anyString());
        reset(collectionEditorPresenter);
        collectionEditorPresenter.setValue("");
        verify(collectionEditorPresenter, never()).getJSONValue(anyString());
        reset(collectionEditorPresenter);
        when(collectionViewMock.isListWidget()).thenReturn(isListWidget);
        collectionEditorPresenter.setValue(TEST_JSON);
        if (isListWidget) {
            verify(collectionEditorPresenter, times(1)).populateList(isA(JSONValue.class));
            verify(collectionEditorPresenter, never()).populateMap(isA(JSONValue.class));
        } else {
            verify(collectionEditorPresenter, times(1)).populateMap(isA(JSONValue.class));
            verify(collectionEditorPresenter, never()).populateList(isA(JSONValue.class));
        }
    }

    private void commonOnToggleRowExpansionIsShown(boolean isShown, boolean isListWidget) {
        when(collectionViewMock.isListWidget()).thenReturn(isListWidget);
        collectionEditorPresenter.onToggleRowExpansion(isShown);
        verify(collectionViewMock, times(1)).toggleRowExpansion();
        if (isListWidget) {
            verify(listElementPresenterMock, times(1)).onToggleRowExpansion(eq(isShown));
        } else {
            verify(mapElementPresenterMock, times(1)).onToggleRowExpansion(eq(isShown));
        }
        reset(collectionViewMock);
        reset(listElementPresenterMock);
        reset(mapElementPresenterMock);
    }

    private void commonSave(boolean isListWidget, boolean toRemove) {
        when(collectionViewMock.isListWidget()).thenReturn(isListWidget);
        collectionEditorPresenter.toRemove = toRemove;
        collectionEditorPresenter.save();
        if (toRemove) {
            verify(collectionViewMock, times(1)).updateValue(eq(null));
        } else {
            if (isListWidget) {
                verify(collectionEditorPresenter, times(1)).getListValue();
            } else {
                verify(collectionEditorPresenter, times(1)).getMapValue();
            }
            verify(collectionViewMock, times(1)).updateValue(eq(UPDATED_VALUE));
        }
        reset(collectionEditorPresenter);
        reset(collectionViewMock);
    }
}
