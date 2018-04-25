/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.ListBox;

public class DataSetFilterEditorView extends Composite implements DataSetFilterEditor.View {

    interface Binder extends UiBinder<Widget, DataSetFilterEditorView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    ListBox newFilterListBox;

    @UiField
    Panel filterListPanel;

    @UiField
    Button addFilterButton;

    @UiField
    Panel addFilterPanel;

    @UiField
    Button filterDeleteIcon;

    DataSetFilterEditor presenter = null;

    public DataSetFilterEditorView() {
        initWidget(uiBinder.createAndBindUi(this));
        filterDeleteIcon.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                onNewFilterClosed(event);
            }
        }, ClickEvent.getType());
    }

    @Override
    public void init(DataSetFilterEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showNewFilterHome() {
        addFilterButton.setVisible(true);
        addFilterPanel.setVisible(false);
    }

    @Override
    public void clearColumnSelector() {
        newFilterListBox.clear();
        newFilterListBox.addItem(CommonConstants.INSTANCE.filter_editor_selectcolumn());
    }

    @Override
    public void showColumnSelector() {
        addFilterButton.setVisible(false);
        addFilterPanel.setVisible(true);
    }

    @Override
    public void addColumn(String column) {
        newFilterListBox.addItem(column);
    }

    @Override
    public int getSelectedColumnIndex() {
        return newFilterListBox.getSelectedIndex() - 1;
    }

    @Override
    public void resetSelectedColumn() {
        newFilterListBox.setSelectedIndex(0);
    }

    @Override
    public void clearColumnFilterEditors() {
        filterListPanel.clear();
    }

    @Override
    public void addColumnFilterEditor(ColumnFilterEditor editor) {
        filterListPanel.add(editor);
    }

    @Override
    public void removeColumnFilterEditor(ColumnFilterEditor editor) {
        filterListPanel.remove(editor);
    }

    // UI events

    @UiHandler(value = "addFilterButton")
    public void onAddFilterClicked(ClickEvent event) {
        presenter.onNewFilterStart();
    }

    public void onNewFilterClosed(ClickEvent event) {
        presenter.onNewFilterCancel();
    }

    @UiHandler(value = "newFilterListBox")
    public void onNewFilterSelected(ChangeEvent changeEvent) {
        presenter.onCreateFilter();
    }
}
