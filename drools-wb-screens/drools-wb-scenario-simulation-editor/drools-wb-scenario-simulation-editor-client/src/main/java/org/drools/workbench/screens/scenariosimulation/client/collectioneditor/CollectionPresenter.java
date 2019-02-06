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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.editingbox.ItemEditingBoxPresenter;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.editingbox.KeyValueEditingBoxPresenter;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;

public class CollectionPresenter implements CollectionView.Presenter {

    @Inject
    protected ItemElementPresenter listElementPresenter;

    @Inject
    protected KeyValueElementPresenter mapElementPresenter;

    @Inject
    protected ViewsProvider viewsProvider;

    @Inject
    protected ItemEditingBoxPresenter listEditingBoxPresenter;

    @Inject
    protected KeyValueEditingBoxPresenter mapEditingBoxPresenter;

    /**
     * <code>Map</code> used to pair the <code>Map</code> with instance' properties classes with a specific <b>key</b> representing the property, i.e Classname#propertyname (e.g Author#books)
     */
    protected Map<String, Map<String, String>> instancePropertiesMap = new HashMap<>();

    protected CollectionView collectionView;

    protected LIElement objectSeparatorLI;

    protected boolean toRemove = false;

    @Override
    public void initListStructure(String key, Map<String, String> instancePropertyMap, CollectionView collectionView) {
        commonInit(key, collectionView);
        instancePropertiesMap.put(key, instancePropertyMap);
        listEditingBoxPresenter.setCollectionEditorPresenter(this);
        listElementPresenter.setCollectionEditorPresenter(this);
        listElementPresenter.onToggleRowExpansion(false);
    }

    @Override
    public void initMapStructure(String key, Map<String, String> keyPropertyMap, Map<String, String> valuePropertyMap, CollectionView collectionView) {
        commonInit(key, collectionView);
        instancePropertiesMap.put(key + "#key", keyPropertyMap);
        instancePropertiesMap.put(key + "#value", valuePropertyMap);
        mapEditingBoxPresenter.setCollectionEditorPresenter(this);
        mapElementPresenter.setCollectionEditorPresenter(this);
        mapElementPresenter.onToggleRowExpansion(false);
    }

    @Override
    public void setValue(String jsonString) {
        toRemove = false;
        if (jsonString == null || jsonString.isEmpty()) {
            return;
        }
        JSONValue jsonValue = getJSONValue(jsonString);
        if (collectionView.isListWidget()) {
            populateList(jsonValue);
        } else {
            populateMap(jsonValue);
        }
    }

    @Override
    public void showEditingBox() {
        String key = collectionView.getEditorTitle().getInnerText();
        if (collectionView.isListWidget()) {
            collectionView.getElementsContainer()
                    .appendChild(listEditingBoxPresenter.getEditingBox(key, instancePropertiesMap.get(key)));
        } else {
            collectionView.getElementsContainer()
                    .appendChild(mapEditingBoxPresenter.getEditingBox(key, instancePropertiesMap.get(key + "#key"), instancePropertiesMap.get(key + "#value")));
        }
    }

    @Override
    public void onToggleRowExpansion(boolean isShown) {
        collectionView.toggleRowExpansion();
        if (collectionView.isListWidget()) {
            listElementPresenter.onToggleRowExpansion(isShown);
        } else {
            mapElementPresenter.onToggleRowExpansion(isShown);
        }
    }

    @Override
    public void addListItem(Map<String, String> propertiesValues) {
        final UListElement elementsContainer = collectionView.getElementsContainer();
        String itemId = String.valueOf(elementsContainer.getChildCount() - 1);
        final LIElement itemElement = listElementPresenter.getItemContainer(itemId, propertiesValues);
        elementsContainer.insertBefore(itemElement, objectSeparatorLI);
    }

    @Override
    public void addMapItem(Map<String, String> keyPropertiesValues, Map<String, String> valuePropertiesValues) {
        final UListElement elementsContainer = collectionView.getElementsContainer();
        String itemId = String.valueOf(elementsContainer.getChildCount() - 1);
        final LIElement itemElement = mapElementPresenter.getKeyValueContainer(itemId, keyPropertiesValues, valuePropertiesValues);
        elementsContainer.insertBefore(itemElement, objectSeparatorLI);
    }

    @Override
    public void save() {
        String updatedValue;
        if (toRemove) {
            updatedValue = null;
        } else {
            if (collectionView.isListWidget()) {
                updatedValue = getListValue();
            } else {
                updatedValue = getMapValue();
            }
        }
        collectionView.updateValue(updatedValue);
    }

    @Override
    public void remove() {
        if (collectionView.isListWidget()) {
            listElementPresenter.remove();
        } else {
            mapElementPresenter.remove();
        }
        toRemove = true;
    }

    protected void commonInit(String key, CollectionView collectionView) {
        toRemove = false;
        this.collectionView = collectionView;
        String propertyName = key.substring(key.lastIndexOf("#") + 1);
        this.collectionView.getEditorTitle().setInnerText(key);
        this.collectionView.getPropertyTitle().setInnerText(propertyName);
        objectSeparatorLI = collectionView.getObjectSeparator();
    }

    protected void populateList(JSONValue jsonValue) {
        final JSONArray array = jsonValue.isArray();
        for (int i = 0; i < array.size(); i++) {
            Map<String, String> propertiesValues = new HashMap<>();
            final JSONObject jsonObject = array.get(i).isObject();
            jsonObject.keySet().forEach(propertyName -> propertiesValues.put(propertyName, jsonObject.get(propertyName).isString().stringValue())
            );
            addListItem(propertiesValues);
        }
    }

    protected void populateMap(JSONValue jsonValue) {
        final JSONArray array = jsonValue.isArray();
        for (int i = 0; i < array.size(); i++) {
            Map<String, String> keyPropertiesValues = new HashMap<>();
            Map<String, String> valuePropertiesValues = new HashMap<>();
            final JSONObject jsonObject = array.get(i).isObject();
            String jsonKey = jsonObject.keySet().iterator().next();
            final JSONValue jsonKeyValue = getJSONValue(jsonKey);
            if (jsonKeyValue == null) {
                keyPropertiesValues.put("value", jsonKey);
            } else {
                JSONObject nestedKey = jsonKeyValue.isObject();
                nestedKey.keySet().forEach(propertyName ->
                                                   keyPropertiesValues.put(propertyName, nestedKey.get(propertyName).isString().stringValue()));
            }
            JSONObject nestedValue = jsonObject.get(jsonKey).isObject();
            nestedValue.keySet().forEach(propertyName -> valuePropertiesValues.put(propertyName, nestedValue.get(propertyName).isString().stringValue()));
            addMapItem(keyPropertiesValues, valuePropertiesValues);
        }
    }

    protected JSONValue getJSONValue(String jsonString) {
        try {
            return JSONParser.parseStrict(jsonString);
        } catch (Exception e) {
            return null;
        }
    }

    protected String getListValue() {
        List<Map<String, String>> itemsProperties = listElementPresenter.getItemsProperties();
        JSONArray jsonArray = new JSONArray();
        AtomicInteger counter = new AtomicInteger();
        itemsProperties.forEach(stringStringMap -> {
            JSONObject nestedObject = new JSONObject();
            stringStringMap.forEach((propertyName, propertyValue) -> nestedObject.put(propertyName, new JSONString(propertyValue)));
            jsonArray.set(counter.getAndIncrement(), nestedObject);
        });
        return jsonArray.toString();
    }

    /**
     * @return
     */
    protected String getMapValue() {
        Map<Map<String, String>, Map<String, String>> itemsProperties = mapElementPresenter.getItemsProperties();
        JSONArray jsonArray = new JSONArray();
        AtomicInteger counter = new AtomicInteger();
        itemsProperties.forEach((keyPropertiesValues, valuePropertiesMap) -> {
            String jsonKey;
            if (keyPropertiesValues.size() == 1) { // simple object - TO CHECK WRONG ASSUMPTION
                jsonKey = keyPropertiesValues.values().iterator().next();
            } else {
                JSONObject nestedKey = new JSONObject();
                keyPropertiesValues.forEach((propertyName, propertyValue) -> nestedKey.put(propertyName, new JSONString(propertyValue)));
                jsonKey = nestedKey.toString();
            }
            JSONObject nestedValue = new JSONObject();
            valuePropertiesMap.forEach((propertyName, propertyValue) -> nestedValue.put(propertyName, new JSONString(propertyValue)));
            JSONObject nestedObject = new JSONObject();
            nestedObject.put(jsonKey, nestedValue);
            jsonArray.set(counter.getAndIncrement(), nestedObject);
        });
        return jsonArray.toString();
    }
}
