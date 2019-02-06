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

public class ItemEditingBoxPresenter extends EditingBoxPresenter implements ItemEditingBox.Presenter {


    @Override
    public LIElement getEditingBox(String key, Map<String, String> instancePropertyMap) {
        String propertyName = key.substring(key.lastIndexOf("#") + 1);
        final ItemEditingBox listEditingBox = viewsProvider.getItemEditingBox();
        listEditingBox.init(this);
        listEditingBox.setKey(key);
        listEditingBox.getEditingBoxTitle().setInnerText("Edit " + propertyName);
        for (Map.Entry<String, String> entry : instancePropertyMap.entrySet()) {
            String propertyKey = entry.getKey();
            listEditingBox.getPropertiesContainer().appendChild(propertyPresenter.getEditingPropertyFields("value", propertyKey, ""));
        }
        return listEditingBox.getEditingBox();
    }

    @Override
    public void save() {
        Map<String, String> propertiesValues = propertyPresenter.updateProperties("value");
        collectionEditorPresenter.addListItem(propertiesValues);
    }

}
