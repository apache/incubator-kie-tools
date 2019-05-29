/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import java.util.List;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.uberfire.client.mvp.HasPresenter;

public interface ListGroupItemView extends IsWidget,
                                           HasPresenter<ListGroupItemView.Presenter> {

    void setToExpand(boolean toExpand);

    boolean isToExpand();

    void setFactName(String factName);

    /**
     * @param parentPath the <b>parent</b>' path
     */
    void setParentPath(String parentPath);

    /**
     * @param factName the property' name
     * @param factType the property' type
     */
    void setFactNameAndType(String factName, String factType);

    String getParentPath();

    String getFactName();

    String getFactType();

    void addFactField(LIElement fieldElement);

    /**
     * This add and a <b>complex</b> (i.e. expandable) property, i.e. a class containing other properties
     * @param fieldElement
     */
    void addExpandableFactField(DivElement fieldElement);

    DivElement getListGroupExpansion();

    DivElement getListGroupItem();

    void closeRow();

    void expandRow();

    String getActualClassName();

    void unselect();

    boolean isShown();

    void setInstanceAssigned(boolean assigned);

    boolean isInstanceAssigned();

    interface Presenter {

        /**
         * Use this when click on grid' <i>instance</i> header.
         * Call this method to show all the first-level data models <b>enabled</b> (i.e. <b>double-clickable</b> to map to an <i>instance</i> header/column)
         * and their properties <b>disabled</b> (i.e. <b>not double-clickable</b>)
         */
        void enable();

        /**
         * Use this when click on grid' <i>instance</i> header.
         * Call this method to show all the first-level data models <b>enabled</b> (i.e. <b>double-clickable</b> to map to an <i>instance</i> header/column)
         * and their properties <b>disabled</b> (i.e. <b>not double-clickable</b>)
         * @param factName
         */
        void enable(String factName);

        void disable();

        /**
         * This method returns a <b>top-level</b> <code>DivElement</code> representing a <b>complex</b> (i.e. expandable) property, i.e. a class containing other properties
         * @param factName
         * @param factModelTree
         */
        DivElement getDivElement(String factName, FactModelTree factModelTree);

        /**
         * This method returns a <b>nested</b> <code>DivElement</code> representing a <b>complex</b> (i.e. expandable) property, i.e. a class containing other properties
         * @param parentPath the <b>parent</b> path
         * @param factName the property' name
         * @param factModelTreeClass the property' type
         */
        DivElement getDivElement(String parentPath, String factName, String factModelTreeClass);

        void onToggleRowExpansion(ListGroupItemView listGroupItemView, boolean currentlyShown);

        void init(TestToolsView.Presenter testToolsPresenter);

        /**
         * Method to set the "selected" information - use this to set the <i>instance</i> level header
         * @param selected
         */
        void onSelectedElement(ListGroupItemView selected);

        /**
         * Method to set the "selected" information - use this to set the <i>property</i> level header
         * @param selected
         */
        void onSelectedElement(FieldItemView selected);

        void unselectAll();

        void showAll();

        /**
         * Expand the node and select the given property
         * @param factName
         * @param propertyParts
         */
        void selectProperty(String factName, List<String> propertyParts);

        /**
         * Hide the node of the given property
         * @param factName
         * @param propertyParts
         */
        void hideProperty(String factName, List<String> propertyParts);

        boolean isInstanceAssigned(String factName);

        void setInstanceAssigned(String factName, boolean assigned);

        void reset();
    }
}
