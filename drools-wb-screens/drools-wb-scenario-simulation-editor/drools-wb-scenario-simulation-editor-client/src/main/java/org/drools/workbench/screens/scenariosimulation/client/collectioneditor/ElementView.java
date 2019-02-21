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

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import org.uberfire.client.mvp.HasPresenter;

public interface ElementView<T extends ElementView.Presenter> extends HasPresenter<T> {

    interface Presenter<E extends ElementView> {

        void setCollectionEditorPresenter(CollectionView.Presenter collectionEditorPresenter);

        /**
         *
         * @param isShown The <b>current</b> status of the <code>ElementView</code>
         */
        void onToggleRowExpansion(boolean isShown);

        void onToggleRowExpansion(E itemElementView, boolean shown);

        /**
         * Update the toggle status of the main collection container if all the contained <code>ElementView</code>
         * have the same one
         * @param shown
         */
        void updateCommonToggleStatus(boolean shown);

        /**
         * Start editing properties of the given <code>itemElementView</code>
         * @param itemElementView
         */
        void onEditItem(E itemElementView);

        /**
         * Update the values of the properties shown in the given <code>itemElementView</code>
         * and stop the editing mode
         * @param itemElementView
         */
        void updateItem(E itemElementView);

        /**
         * Stop editing properties of the given <code>itemElementView</code>
         * <b>without</b> updating the properties
         * @param itemElementView
         */
        void onStopEditingItem(E itemElementView);

        /**
         * Delete the item and its properties shown on the given <code>itemElementView</code>
         * @param itemElementView
         */
        void onDeleteItem(E itemElementView);

        /**
         * Completely remove the elements of the given <code>Collection</code>
         */
        void remove();

        void toggleEditingStatus(boolean toDisable);
    }

    ButtonElement getEditItemButton();

    ButtonElement getDeleteItemButton();

    boolean isShown();

    void onFaAngleRightClick(ClickEvent clickEvent);

    void onEditItemButtonClick(ClickEvent clickEvent);

    void onDeleteItemButtonClick(ClickEvent clickEvent);

    void onSaveChangeButtonClick(ClickEvent clickEvent);

    void onCancelChangeButton(ClickEvent clickEvent);

    void toggleRowExpansion(boolean toExpand);

    /**
     * Set the <b>id</b> of the item shown by the current <code><ListEditorElementView/code>
     * @param itemId
     */
    void setItemId(String itemId);

    /**
     * @return the <b>id</b> of the item shown by the current <code><ListEditorElementView/code>
     */
    String getItemId();

    /**
     * @return the <code>LIElement</code> containing all the item properties
     */
    LIElement getItemContainer();

    /**
     * @return the (inner) <code>UListElement</code> containing all the item properties
     */
    UListElement getInnerItemContainer();

    /**
     * @return the <code>LIElement</code> separating each item
     */
    LIElement getItemSeparator();

    /**
     * @return the <code>LIElement</code> with the item' save/cancel buttons
     */
    LIElement getSaveChange();

    /**
     * @return the <code>SpanElement</code> with the angle arrow
     */
    SpanElement getFaAngleRight();
}
