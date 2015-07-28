/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;

public class CRUDListBoxViewImpl
        extends Composite
        implements CRUDListBoxView {

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, CRUDListBoxViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Button addButton;

    @UiField
    Button deleteButton;

    @UiField
    ListBox listBox;
    
    @Inject
    public CRUDListBoxViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getSelectedItem() {
        return listBox.getValue(listBox.getSelectedIndex());
    }

    @Override
    public void removeItem(String itemName) {
        for (int i = 0; i < listBox.getItemCount(); i++) {
            if (listBox.getValue(i).equals(itemName)) {
                listBox.removeItem(i);
            }
        }
        RemoveItemEvent.fire(this, itemName);
    }

    @Override
    public void addItemAndFireEvent(String name) {
        AddItemEvent.fire(this, name);
        addItem(name);
    }

    @Override
    public void makeReadOnly() {
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    @Override
    public void makeEditable() {
        addButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }

    @Override
    public void addItem(String name) {
        listBox.addItem(name);
    }

    @Override
    public void clear() {
        listBox.clear();
    }

    @Override
    public HandlerRegistration addRemoveItemHandler(RemoveItemHandler handler) {
        return addHandler(handler, RemoveItemEvent.getType());
    }

    @Override
    public HandlerRegistration addAddItemHandler(AddItemHandler handler) {
        return addHandler(handler, AddItemEvent.getType());
    }

    @UiHandler("addButton")
    public void onAdd(ClickEvent clickEvent) {
        presenter.onAdd();
    }

    @UiHandler("deleteButton")
    public void onDelete(ClickEvent clickEvent) {
        presenter.onDelete();
    }
    
}
