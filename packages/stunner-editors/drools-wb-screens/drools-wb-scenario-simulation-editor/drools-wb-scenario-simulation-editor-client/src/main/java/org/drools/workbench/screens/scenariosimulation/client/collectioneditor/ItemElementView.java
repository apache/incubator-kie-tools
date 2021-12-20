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

import java.util.Map;

import com.google.gwt.dom.client.LIElement;

public interface ItemElementView extends ElementView<ItemElementView.Presenter> {


    interface Presenter extends ElementView.Presenter<ItemElementView> {

        /**
         *
         * @param itemId the id of the current item
         * @param simplePropertiesMap the properties to be put inside the <code>UListElement</code>
         * @param expandablePropertiesValues
         * representing a single item of the list
         *
         * @return the <code>LIElement</code> representing all the items of the list
         */
        LIElement getItemContainer(String itemId, Map<String, String> simplePropertiesMap, Map<String, Map<String, String>> expandablePropertiesValues);

        /**
         * Retrieves a <code>Map</code> with the <code>Map</code>s of all the items' <b>simple</b> properties
         *
         * @return a <code>Map</code> where the <b>key</b> is the itemId of the item container, and the value is the map <b>propertyName/propertyValue</b>
         */
        Map<String, Map<String, String>> getSimpleItemsProperties();

        /**
         * Retrieves a <code>Map</code> with the <code>Map</code>s of all the items' <b>nested</b> properties
         * @return a <code>Map</code> where the <b>key</b> is the itemId of the item container, and the value is the map of <b>nested</b> properties
         */
        Map<String, Map<String, Map<String, String>>> getExpandableItemsProperties();

    }

}
