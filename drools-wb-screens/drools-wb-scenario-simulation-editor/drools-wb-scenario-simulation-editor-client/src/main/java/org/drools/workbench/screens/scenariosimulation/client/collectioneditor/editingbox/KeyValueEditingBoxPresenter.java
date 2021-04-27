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

import java.util.Map;

import com.google.gwt.dom.client.LIElement;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

public class KeyValueEditingBoxPresenter extends EditingBoxPresenter implements KeyValueEditingBox.Presenter {

    @Override
    public LIElement getEditingBox(String key, Map<String, String> keyPropertyMap, Map<String, String> valuePropertyMap) {
        String propertyName = key.substring(key.lastIndexOf("#") + 1);
        final KeyValueEditingBox mapEditingBox = viewsProvider.getKeyValueEditingBox();
        mapEditingBox.init(this);
        mapEditingBox.setKey(key);
        mapEditingBox.getEditingBoxTitle().setInnerText("Edit " + propertyName);
        keyPropertyMap.forEach((propertyKey, value) -> mapEditingBox.getKeyContainer().appendChild(propertyPresenter.getEditingPropertyFields("key", propertyKey, ""))
        );
        valuePropertyMap.forEach((propertyKey, value) -> mapEditingBox.getValueContainer().appendChild(propertyPresenter.getEditingPropertyFields(VALUE, propertyKey, "")));
        return mapEditingBox.getEditingBox();
    }

    @Override
    public void save() {
        Map<String, String> keyPropertiesValues = propertyPresenter.updateProperties("key");
        Map<String, String> valuePropertiesMap = propertyPresenter.updateProperties(VALUE);
        collectionEditorPresenter.addMapItem(keyPropertiesValues, valuePropertiesMap);
    }
}
