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
import com.google.gwt.dom.client.UListElement;

public interface KeyValueElementView extends ElementView<KeyValueElementView.Presenter> {

    interface Presenter extends ElementView.Presenter<KeyValueElementView> {

        /**
         * @param itemId the id of the current item
         * @param keyPropertiesValues the properties to be put inside the <code>UListElement</code>
         * representing the key of a single item of the map
         * @param keyPropertiesValues the properties to be put inside the <code>UListElement</code>
         * representing the value of a single item of the map
         * @return the <code>LIElement</code> representing all the items of the map
         */
        LIElement getKeyValueContainer(String itemId, Map<String, String> keyPropertiesValues, Map<String, String> valuePropertiesValues);

        /**
         * Retrieves a <code>Map</code> with the <code>Map</code>s of all the items' key/value properties
         * @return
         */
        Map<Map<String, String>, Map<String, String>> getItemsProperties();
    }

    /**
     * @return the <code>UListElement</code> containing the <b>key</b> properties
     */
    UListElement getKeyContainer();

    /**
     * @return the <code>UListElement</code> containing the <b>value</b> properties
     */
    UListElement getValueContainer();

    LIElement getKeyLabel();

    LIElement getValueLabel();

}
