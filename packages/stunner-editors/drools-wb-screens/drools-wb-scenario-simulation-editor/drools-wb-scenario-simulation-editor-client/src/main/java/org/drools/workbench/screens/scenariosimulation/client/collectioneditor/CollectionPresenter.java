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
import org.drools.workbench.screens.scenariosimulation.client.popup.ConfirmPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.ScenarioConfirmationPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

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

    @Inject
    protected ScenarioConfirmationPopupPresenter scenarioConfirmationPopupPresenter;

    @Inject
    protected ConfirmPopupPresenter confirmPopupPresenter;

    /**
     * <code>Map</code> used to pair the <code>Map</code> with instance' <b>simple properties</b> classes with a specific <b>key</b> representing the property, i.e Classname#propertyname (e.g Author#books)
     */
    protected Map<String, Map<String, String>> simplePropertiesMap = new HashMap<>();

    /**
     * <code>Map</code> used to pair the <code>Map</code> with instance' <b>expandable properties</b> classes with a specific <b>key</b> representing the property, i.e Classname#propertyname (e.g Author#books)
     */
    protected Map<String, Map<String, Map<String, String>>> expandablePropertiesMap = new HashMap<>();

    protected CollectionView collectionView;

    @Override
    public void initListStructure(String key, Map<String, String> simplePropertiesMap, Map<String, Map<String, String>> expandablePropertiesMap, CollectionView collectionView) {
        commonInit(key, collectionView);
        this.simplePropertiesMap.put(key, simplePropertiesMap);
        this.expandablePropertiesMap.put(key, expandablePropertiesMap);
        listEditingBoxPresenter.setCollectionEditorPresenter(this);
        listElementPresenter.setCollectionEditorPresenter(this);
        listElementPresenter.onToggleRowExpansion(false);
    }

    @Override
    public void initMapStructure(String key, Map<String, String> keyPropertyMap, Map<String, String> valuePropertyMap, CollectionView collectionView) {
        commonInit(key, collectionView);
        simplePropertiesMap.put(key + "#key", keyPropertyMap);
        simplePropertiesMap.put(key + "#value", valuePropertyMap);
        mapEditingBoxPresenter.setCollectionEditorPresenter(this);
        mapElementPresenter.setCollectionEditorPresenter(this);
        mapElementPresenter.onToggleRowExpansion(false);
    }

    @Override
    public void setValue(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return;
        }
        JSONValue jsonValue = getJSONValue(jsonString);
        if (jsonValue instanceof JSONString) {
            populateExpression(jsonValue);
        } else {
            populateCreateCollection(jsonValue);
        }
    }

    /**
     * It populates the guided "Create Collection" editor
     * @param value
     */
    protected void populateCreateCollection(JSONValue value) {
        if (collectionView.isListWidget()) {
            populateList(value);
        } else {
            populateMap(value);
        }
    }

    @Override
    public void showEditingBox() {
        String key = collectionView.getEditorTitle().getInnerText();
        LIElement editingBox = collectionView.isListWidget() ?
                listEditingBoxPresenter.getEditingBox(key, simplePropertiesMap.get(key), expandablePropertiesMap.get(key)) :
                mapEditingBoxPresenter.getEditingBox(key, simplePropertiesMap.get(key + "#key"), simplePropertiesMap.get(key + "#value"));
        collectionView.getElementsContainer().appendChild(editingBox);
        editingBox.scrollIntoView();
        toggleEditingStatus(true);
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
    public void updateRowExpansionStatus(boolean isShown) {
        collectionView.updateRowExpansionStatus(isShown);
    }

    @Override
    public void addListItem(Map<String, String> simplePropertiesValues, Map<String, Map<String, String>> expandablePropertiesValues) {
        final UListElement elementsContainer = collectionView.getElementsContainer();
        String itemId = String.valueOf(elementsContainer.getChildCount() - 1);
        final LIElement itemElement = listElementPresenter.getItemContainer(itemId, simplePropertiesValues, expandablePropertiesValues);
        elementsContainer.appendChild(itemElement);
        toggleEditingStatus(false);
    }

    @Override
    public void addMapItem(Map<String, String> keyPropertiesValues, Map<String, String> valuePropertiesValues) {
        final UListElement elementsContainer = collectionView.getElementsContainer();
        String itemId = String.valueOf(elementsContainer.getChildCount() - 1);
        final LIElement itemElement = mapElementPresenter.getKeyValueContainer(itemId, keyPropertiesValues, valuePropertiesValues);
        elementsContainer.appendChild(itemElement);
        toggleEditingStatus(false);
    }

    @Override
    public void save() {
        try {
            String updatedValue;
            if (collectionView.isExpressionWidget()) {
                updatedValue = getExpressionValue();
            } else {
                updatedValue = getValueFromCreateCollection();
            }
            collectionView.updateValue(updatedValue);
        } catch (IllegalStateException e) {
            confirmPopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.collectionError(), e.getMessage());
        }
    }

    /**
     * It gets the guided "Create Collection" editor
     */
    protected String getValueFromCreateCollection() {
        if (collectionView.isListWidget()) {
            return getListValue();
        } else {
            return getMapValue();
        }
    }

    @Override
    public void remove() {
        org.uberfire.mvp.Command okRemoveCommand = this::okRemoveCommandMethod;
        scenarioConfirmationPopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.removeCollectionMainTitle(),
                                                ScenarioSimulationEditorConstants.INSTANCE.removeCollectionMainQuestion(),
                                                ScenarioSimulationEditorConstants.INSTANCE.removeCollectionText1(),
                                                ScenarioSimulationEditorConstants.INSTANCE.removeCollectionQuestion(),
                                                ScenarioSimulationEditorConstants.INSTANCE.removeCollectionWarningText(),
                                                ScenarioSimulationEditorConstants.INSTANCE.remove(),
                                                okRemoveCommand);
    }

    @Override
    public void toggleEditingStatus(boolean editingStatus) {
        collectionView.enableEditingMode(editingStatus);
        mapElementPresenter.toggleEditingStatus(editingStatus);
        listElementPresenter.toggleEditingStatus(editingStatus);
    }

    // Indirection add for test
    protected void okRemoveCommandMethod() {
        if (collectionView.isListWidget()) {
            listElementPresenter.remove();
        } else {
            mapElementPresenter.remove();
        }
        collectionView.updateValue(null);
        collectionView.close();
    }

    protected void commonInit(String key, CollectionView collectionView) {
        this.collectionView = collectionView;
        String propertyName = key.substring(key.lastIndexOf('#') + 1);
        this.collectionView.getEditorTitle().setInnerText(key);
        this.collectionView.getPropertyTitle().setInnerText(propertyName);
    }

    protected void populateList(JSONValue jsonValue) {
        final JSONArray array = jsonValue.isArray();
        for (int i = 0; i < array.size(); i++) {
            final JSONObject jsonObject = array.get(i).isObject();
            final Map<String, String> propertiesValues = getSimplePropertiesMap(jsonObject);
            final Map<String, Map<String, String>> expandablePropertiesValues = getExpandablePropertiesValues(jsonObject);
            addListItem(propertiesValues, expandablePropertiesValues);
        }
    }

    protected void populateMap(JSONValue jsonValue) {
        final JSONObject jsValueObject = jsonValue.isObject();
        jsValueObject.keySet().forEach(key -> {
            Map<String, String> keyPropertiesValues = new HashMap<>();
            Map<String, String> valuePropertiesValues = new HashMap<>();
            final JSONObject jsonObjectKey = getJSONObject(key);
            if (jsonObjectKey == null) {
                keyPropertiesValues.put(VALUE, key);
            } else {
                jsonObjectKey.keySet().forEach(propertyName ->
                                                       keyPropertiesValues.put(propertyName, jsonObjectKey.get(propertyName).isString().stringValue()));
            }
            JSONObject jsonObjectValue = jsValueObject.get(key).isObject();
            if (jsonObjectValue != null) {
                jsonObjectValue.keySet().forEach(propertyName -> valuePropertiesValues.put(propertyName, jsonObjectValue.get(propertyName).isString().stringValue()));
            } else {
                valuePropertiesValues.put(VALUE, jsValueObject.get(key).toString());
            }
            addMapItem(keyPropertiesValues, valuePropertiesValues);
        });
    }

    protected void populateExpression(JSONValue jsonValue) {
        final JSONString jsonString = jsonValue.isString();
        collectionView.setExpression(jsonString.stringValue());
    }

    protected JSONObject getJSONObject(String jsonString) {
        try {
            return getJSONValue(jsonString).isObject();
        } catch (Exception e) {
            return null;
        }
    }

    protected JSONValue getJSONValue(String jsonString) {
        try {
            return JSONParser.parseStrict(jsonString);
        } catch (Exception e) {
            return null;
        }
    }

    protected String getExpressionValue() {
        final JSONString jsonString = new JSONString(collectionView.getExpression());
        return jsonString.toString();
    }

    protected String getListValue() {
        Map<String, Map<String, String>> simpleItemsProperties = listElementPresenter.getSimpleItemsProperties();
        Map<String, Map<String, Map<String, String>>> nestedItemsProperties = listElementPresenter.getExpandableItemsProperties();
        JSONArray jsonArray = new JSONArray();
        AtomicInteger counter = new AtomicInteger();
        simpleItemsProperties.forEach((itemId, simpleProperties) -> {
            final JSONObject jsonObject = getJSONObject(simpleProperties);
            Map<String, Map<String, String>> nestedProperties = nestedItemsProperties.get(itemId);
            if (nestedProperties != null) {
                nestedProperties.forEach((nestedPropertyName, nestedPropertyValues) -> {
                    JSONObject nestedJSONObject = getJSONObject(nestedPropertyValues);
                    jsonObject.put(nestedPropertyName, nestedJSONObject);
                });
            }
            jsonArray.set(counter.getAndIncrement(), jsonObject);
        });
        return jsonArray.toString();
    }

    /**
     * Translates a <code>Map</code> to a <code>JSONObject</code>
     * @param properties
     * @return
     */
    protected JSONObject getJSONObject(Map<String, String> properties) {
        JSONObject toReturn = new JSONObject();
        properties.forEach((propertyName, propertyValue) -> toReturn.put(propertyName, new JSONString(propertyValue)));
        return toReturn;
    }

    /**
     * @param jsonObject
     * @return a <code>Map</code> with <b>propertyName/propertyValue</b>
     */
    protected Map<String, String> getSimplePropertiesMap(JSONObject jsonObject) {
        Map<String, String> toReturn = new HashMap<>();
        jsonObject.keySet().forEach(propertyName -> {
            final JSONValue jsonValue = jsonObject.get(propertyName);
            if (jsonValue.isString() != null) {
                toReturn.put(propertyName, jsonValue.isString().stringValue());
            }
        });
        return toReturn;
    }

    /**
     * @param jsonObject
     * @return a <code>Map</code> where the <b>key</b> is the name of the complex property, and the value is a a <code>Map</code> with
     * the nested <b>propertyName/propertyValue</b>
     */
    protected Map<String, Map<String, String>> getExpandablePropertiesValues(JSONObject jsonObject) {
        Map<String, Map<String, String>> toReturn = new HashMap<>();
        jsonObject.keySet().forEach(propertyName -> {
            final JSONValue jsonValue = jsonObject.get(propertyName);
            if (jsonValue.isObject() != null) {
                toReturn.put(propertyName, getSimplePropertiesMap(jsonValue.isObject()));
            }
        });
        return toReturn;
    }

    /**
     * @return
     */
    protected String getMapValue() {
        Map<Map<String, String>, Map<String, String>> itemsProperties = mapElementPresenter.getItemsProperties();
        JSONObject toReturnModel = new JSONObject();
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
            toReturnModel.put(jsonKey, nestedValue);
        });
        return toReturnModel.toString();
    }
}
