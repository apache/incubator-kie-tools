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

public interface ItemEditingBox extends EditingBox<ItemEditingBox.Presenter> {

    interface Presenter extends EditingBox.Presenter  {

        /**
         *
         * @param key The key representing the property, i.e Classname#propertyname (e.g Author#books)
         * @param simplePropertiesMap the simple properties tho show
         * @param expandablePropertiesMap the expandable properties to show
         * @return
         */
        LIElement getEditingBox(String key, Map<String, String> simplePropertiesMap, Map<String, Map<String, String>> expandablePropertiesMap);
    }

}
