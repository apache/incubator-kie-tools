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

import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.LIElement;

public interface ItemElementView extends ElementView<ItemElementView.Presenter> {


    interface Presenter extends ElementView.Presenter<ItemElementView> {

        /**
         *
         * @param itemId the id of the current item
         * @param propertiesMap the properties to be put inside the <code>UListElement</code>
         * representing a single item of the list
         *
         * @return the <code>LIElement</code> representing all the items of the list
         */
        LIElement getItemContainer(String itemId, Map<String, String> propertiesMap);

        /**
         * Retrieves a <code>List</code> with the <code>Map</code>s of all the items' properties
         * @return
         */
        List<Map<String, String>> getItemsProperties();
    }

}
