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
import java.util.Map;

import com.google.gwt.dom.client.LIElement;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

public class ItemEditingBoxPresenter extends EditingBoxPresenter implements ItemEditingBox.Presenter {

    protected List<String> nestedPropertiesNamesList = new ArrayList<>(); // Map a given ItemEditingBox' key with its nested properties

    @Override
    public LIElement getEditingBox(String key, Map<String, String> simplePropertiesMap, Map<String, Map<String, String>> expandablePropertiesMap) {
        String propertyName = key.substring(key.lastIndexOf("#") + 1);
        final ItemEditingBox listEditingBox = viewsProvider.getItemEditingBox();
        listEditingBox.init(this);
        listEditingBox.setKey(key);
        listEditingBox.getEditingBoxTitle().setInnerText("Edit " + propertyName);
        nestedPropertiesNamesList.clear();
        for (Map.Entry<String, String> entry : simplePropertiesMap.entrySet()) {
            String propertyKey = entry.getKey();
            listEditingBox.getPropertiesContainer().appendChild(propertyPresenter.getEditingPropertyFields(VALUE, propertyKey, ""));
        }
        for (Map.Entry<String, Map<String, String>> entry : expandablePropertiesMap.entrySet()) {
            String nestedPropertyName = entry.getKey();
            String nestedPropertyKey = key + "#" + propertyName;
            addExpandableItemEditingBox(listEditingBox, entry.getValue(), nestedPropertyKey, nestedPropertyName);
        }
        return listEditingBox.getEditingBox();
    }

    @Override
    public void save() {
        Map<String, String> simplePropertiesValues = propertyPresenter.updateProperties(VALUE);
        Map<String, Map<String, String>> expandablePropertiesValues = new HashMap<>();
        for (String nestedPropertyName : nestedPropertiesNamesList) {
            expandablePropertiesValues.put(nestedPropertyName, propertyPresenter.updateProperties(nestedPropertyName));
        }
        collectionEditorPresenter.addListItem(simplePropertiesValues, expandablePropertiesValues);
    }

    /**
     * @param containerItemEditingBox
     * @param propertiesMap
     * @param key
     * @param propertyName
     */
    protected void addExpandableItemEditingBox(ItemEditingBox containerItemEditingBox, Map<String, String> propertiesMap, String key, String propertyName) {
        final ItemEditingBox listEditingBox = viewsProvider.getItemEditingBox();
        listEditingBox.init(this);
        listEditingBox.setKey(key);
        listEditingBox.getEditingBoxTitle().setInnerText(propertyName);
        listEditingBox.removeButtonToolbar();
        nestedPropertiesNamesList.add(propertyName);
        for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
            String propertyKey = entry.getKey();
            listEditingBox.getPropertiesContainer().appendChild(propertyPresenter.getEditingPropertyFields(propertyName, propertyKey, ""));
        }
        containerItemEditingBox.getPropertiesContainer().appendChild(listEditingBox.getEditingBox());
    }
}
