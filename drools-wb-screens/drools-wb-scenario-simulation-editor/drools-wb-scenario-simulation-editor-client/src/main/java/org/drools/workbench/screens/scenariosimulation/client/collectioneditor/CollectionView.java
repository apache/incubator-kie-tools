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

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;

/**
 * Interface defining the contract for actual implementations
 */
public interface CollectionView {

    interface Presenter {

        /**
         * Actual implementations should invoke this method first to retrieve information about the collection
         * generic type and the structure of such type
         * @param key The key representing the property, i.e classname#propertyname (e.g Author#books)
         * @param instancePropertyMap
         * @param expandablePropertiesMap
         * @param collectionView
         */
        void initListStructure(String key, Map<String, String> instancePropertyMap, Map<String, Map<String, String>> expandablePropertiesMap, CollectionView collectionView);

        /**
         * Actual implementations should invoke this method first to retrieve information about the collection
         * generic type and the structure of such type
         * @param key The key representing the property, i.e classname#propertyname (e.g Author#books)
         * @param keyPropertyMap
         * @param valuePropertyMap
         * @param collectionEditorView
         */
        void initMapStructure(String key, Map<String, String> keyPropertyMap, Map<String, String> valuePropertyMap, CollectionView collectionEditorView);

        /**
         * Actual implementations are meant to transform that json representation to a <code>com.google.gwt.json.client.JSONValue</code> and use that to populate the
         * current <code>CollectionEditorView</code>
         *
         * @param jsonString
         */
        void setValue(String jsonString);

        /**
         * Show the editing box in the current <code>CollectionEditorView</code>
         *
         */
        void showEditingBox();

        /**
         * Toggle the expansion of the items included in the collection.
         *
         * @param isShown the <b>current</b> expansion status of the collection
         */
        void onToggleRowExpansion(boolean isShown);

        /**
         * Updates the <b>expanded</b> status of main collection container to reflect
         * the status of all contained items, when they have the same <b>expanded</b> status
         *
         * @param isShown the <b>current</b> expansion status of the collection
         */
        void updateRowExpansionStatus(boolean isShown);

        /**
         * Creates a new single <b>item</b> element with values taken from given <code>Map</code>
         *
         * @param simplePropertiesValues
         * @param expandablePropertiesValues
         */
        void addListItem(Map<String, String> simplePropertiesValues, Map<String, Map<String, String>> expandablePropertiesValues);

        /**
         * Creates a new <b>key/value</b> <b>item</b> element with values taken from given <code>Map</code>
         *
         * @param keyPropertiesValues
         * @param valuePropertiesValues
         */
        void addMapItem(Map<String, String> keyPropertiesValues, Map<String, String> valuePropertiesValues);

        /**
         * Actual implementations are meant to retrieve the json representation of the content of the
         * current <code>CollectionEditorView</code> and save it.
         *
         */
        void save();

        /**
         * Completely remove the given <code>Collection</code>, i.e. set it to <code>null</code>
         */
        void remove();

        /**
         * Toggles the status of the addItem button
         *
         * @param toDisable
         */
        void toggleEditingStatus(boolean toDisable);
    }

    /**
     * Actual implementations are meant to call the <code>Presenter</code> to be populated by this json representation
     *
     * @param jsonString
     */
    void setValue(String jsonString);

    /**
     * Actual implementations are meant to call the <code>Presenter</code> to retrieve the json representation of their contents
     * @return the json representation of the current content
     */
    String getValue();

    /**
     * Returns <code>true</code> if the current instance is managing an user defined expression.
     */
    boolean isExpressionWidget();

    /**
     * Returns <code>true</code> if the current instance will manage a <code>List</code>,
     * <code>false</code> for a <code>Map</code>.
     * @return
     */
    boolean isListWidget();

    UListElement getElementsContainer();

    HeadingElement getEditorTitle();

    SpanElement getPropertyTitle();

    ButtonElement getAddItemButton();

    ButtonElement getCancelButton();

    ButtonElement getRemoveButton();

    ButtonElement getSaveButton();

    String getExpression();

    void setExpression(String expressionValue);

    void toggleRowExpansion();

    /**
     * Updates the <b>expanded</b> status of main collection container to reflect
     * the status of all contained items, when they have the same <b>expanded</b> status
     *
     * @param isShown the <b>current</b> expansion status of the collection
     */
    void updateRowExpansionStatus(boolean isShown);

    /**
     * Updates the <b>json</b> representation of the values shown by this editor
     * @param toString
     */
    void updateValue(String toString);

    /**
     * Close the current <code>CollectionView</code>
     */
    void close();

    /**
     * Set the <b>name</b> of the property and the <code>Map</code> to be used to create the skeleton of the current <code>CollectionViewImpl</code> editor
     * showing a <b>List</b> of elements
     * @param key The key representing the property, i.e Classname#propertyname (e.g Author#books)
     * @param simplePropertiesMap
     * @param expandablePropertiesMap
     * @param type
     */
    void initListStructure(String key, Map<String, String> simplePropertiesMap, Map<String, Map<String, String>> expandablePropertiesMap, ScenarioSimulationModel.Type type);

    /**
     * Set the <b>name</b> of the property and the <code>Map</code>s to be used to create the skeleton of the current <code>CollectionViewImpl</code> editor
     * showing a <b>Map</b> of elements
     * @param key The key representing the property, i.e Classname#propertyname (e.g Author#books)
     * @param keyPropertyMap
     * @param valuePropertyMap
     * @param type
     */
    void initMapStructure(String key, Map<String, String> keyPropertyMap, Map<String, String> valuePropertyMap, ScenarioSimulationModel.Type type);

    void setFixedHeight(double value, Style.Unit px);

}
