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
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;

/**
 * This class is used to show a single <b>item</b> of a collection
 */
public abstract class ElementViewImpl<T extends ElementView.Presenter> implements ElementView<T> {

    protected Presenter presenter;

    protected String itemId;

    @DataField("itemContainer")
    protected LIElement itemContainer = Document.get().createLIElement();

    @DataField("innerItemContainer")
    protected UListElement innerItemContainer = Document.get().createULElement();

    @DataField("itemSeparator")
    protected LIElement itemSeparator = Document.get().createLIElement();

    @DataField("itemSeparatorText")
    protected SpanElement itemSeparatorText = Document.get().createSpanElement();

    @DataField("saveChange")
    protected LIElement saveChange = Document.get().createLIElement();

    @DataField("faAngleRight")
    protected SpanElement faAngleRight = Document.get().createSpanElement();

    @DataField("editItemButton")
    protected ButtonElement editItemButton = Document.get().createPushButtonElement();

    @DataField("deleteItemButton")
    protected ButtonElement deleteItemButton = Document.get().createPushButtonElement();

    @DataField("saveChangeButton")
    protected ButtonElement saveChangeButton = Document.get().createPushButtonElement();

    @DataField("saveChangeButtonSpanText")
    protected SpanElement saveChangeButtonSpanText = Document.get().createSpanElement();

    @DataField("cancelChangeButton")
    protected ButtonElement cancelChangeButton = Document.get().createPushButtonElement();

    @DataField("cancelButtonSpanText")
    protected SpanElement cancelButtonSpanText = Document.get().createSpanElement();

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
        itemSeparatorText.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.item());
        saveChangeButtonSpanText.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.saveButton());
        cancelButtonSpanText.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.cancelButton());
    }

    @Override
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public String getItemId() {
        return itemId;
    }

    @Override
    public void setItemSeparatorText(String itemSeparatorText) {
        this.itemSeparatorText.setInnerText(itemSeparatorText);
    }

    @Override
    public LIElement getItemContainer() {
        return itemContainer;
    }

    @Override
    public UListElement getInnerItemContainer() {
        return innerItemContainer;
    }

    @Override
    public LIElement getItemSeparator() {
        return itemSeparator;
    }

    @Override
    public LIElement getSaveChange() {
        return saveChange;
    }

    @Override
    public SpanElement getFaAngleRight() {
        return faAngleRight;
    }

    @Override
    public ButtonElement getEditItemButton() { return editItemButton; }

    @Override
    public ButtonElement getDeleteItemButton() {
        return deleteItemButton;
    }

    @Override
    public boolean isShown() {
        return CollectionEditorUtils.isShown(faAngleRight);
    }

    @Override
    public void toggleRowExpansion(boolean toExpand) {
        CollectionEditorUtils.toggleRowExpansion(faAngleRight, toExpand);
    }

    @EventHandler("itemContainer")
    public void onItemContainerClick(ClickEvent clickEvent) {
        clickEvent.stopPropagation();
    }

    @EventHandler("innerItemContainer")
    public void onInnerItemContainerClick(ClickEvent clickEvent) {
        clickEvent.stopPropagation();
    }

    @EventHandler("itemSeparator")
    public void onItemSeparatorClick(ClickEvent clickEvent) {
        clickEvent.stopPropagation();
    }

    @EventHandler("faAngleRight")
    public void onFaAngleRightClick(ClickEvent clickEvent) {
        presenter.onToggleRowExpansion(this, isShown());
        clickEvent.stopPropagation();
    }

    @EventHandler("editItemButton")
    public void onEditItemButtonClick(ClickEvent clickEvent) {
        presenter.onEditItem(this);
        clickEvent.stopPropagation();
    }

    @EventHandler("deleteItemButton")
    public void onDeleteItemButtonClick(ClickEvent clickEvent) {
        presenter.onDeleteItem(this);
        clickEvent.stopPropagation();
    }

    @EventHandler("saveChange")
    public void onSaveChangeClick(ClickEvent clickEvent) {
        clickEvent.stopPropagation();
    }

    @EventHandler("saveChangeButton")
    public void onSaveChangeButtonClick(ClickEvent clickEvent) {
        presenter.updateItem(this);
        clickEvent.stopPropagation();
    }

    @EventHandler("cancelChangeButton")
    public void onCancelChangeButton(ClickEvent clickEvent) {
        presenter.onStopEditingItem(this);
        clickEvent.stopPropagation();
    }
}
