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

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;

public interface PropertyView {

    interface Presenter {

        /**
         * Get the text shown in the property value
         * @param propertyName the property fro which we are retrieving the value
         * @return
         * @throws Exception if the given property value is not found
         */
        String getPropertyValue(String propertyName) throws Exception;

        /**
         * Show the <code>InputElement</code>s to edit the properties shown at the given <b>baseNodeId</b> and hide their <code>SpanElement</code>s
         * @param itemId the id of the item containing the <code>LIElement</code>
         */
        void editProperties(String itemId);

        /**
         * Show the <code>SpanElement</code>s of the properties shown at the given <b>baseNodeId</b> <b>without</b> change their values, and and hide their  <code>InputElement</code>s
         * @param itemId the id of the item containing the <code>LIElement</code>
         */
        void stopEditProperties(String itemId);

        /**
         * Retrieve the <b>simple</b> properties shown at the given <b>itemId</b>
         *
         * @param itemId the id of the item containing the <code>LIElement</code>
         * @return the map with updated values
         */
        Map<String, String> getSimpleProperties(String itemId);

        /**
         * Show the <code>SpanElement</code>s of the properties shown at the given <b>baseNodeId</b> with the value of their <code>InputElement</code>s, and hide the latters
         * @param itemId the id of the item containing the <code>LIElement</code>
         *
         * @return the map with updated values
         */
        Map<String, String> updateProperties(String itemId);

        /**
         * @param itemId the id of the item containing the <code>LIElement</code>
         * @param propertyName
         * @param propertyValue
         * @return the <code>LIElement</code> containing the property' <b>fields</b>
         */
        LIElement getPropertyFields(String itemId, String propertyName, String propertyValue);

        /**
         * @param itemId the id of the item containing the <code>LIElement</code>
         * @param propertyName
         * @param propertyValue
         * @return the <code>LIElement</code> containing the property' <b>fields</b> in <b>editing</b> mode
         */
        LIElement getEditingPropertyFields(String itemId, String propertyName, String propertyValue);

        /**
         *
         * @param itemId the id of the item containing the <code>LIElement</code>
         * @param isShown <code>true</code> it the item is currently shown
         */
        void onToggleRowExpansion(String itemId, boolean isShown);

        /**
         * Remove all the <b>properties</b> belonging to the given <b>baseNodeId</b> from both <code>DOM</code> and internal <code>Map</code>
         *
         * @param itemId the id of the item containing the <code>LIElement</code>
         */
        void deleteProperties(String itemId);

    }

    /**
     * @return the <code>LIElement</code> containing the property' <b>fields</b>
     */
    LIElement getPropertyFields();

    /**
     * @return the <code>SpanElement</code> showing the property' <b>name</b>
     */
    SpanElement getPropertyName();

    /**
     * @return the <code>SpanElement</code> showing the property' <b>value</b>
     */
    SpanElement getPropertyValueSpan();

    /**
     * @return the <code>InputElement</code> editing the property' <b>value</b>
     */
    InputElement getPropertyValueInput();
}
