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
package org.dashbuilder.displayer.client.widgets;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.dashbuilder.displayer.client.widgets.filter.DataSetFilterEditor;
import org.dashbuilder.displayer.client.widgets.group.ColumnFunctionEditor;
import org.dashbuilder.displayer.client.widgets.group.DataSetGroupDateEditor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;

@Dependent
public class DataSetLookupEditorView extends Composite
        implements DataSetLookupEditor.View {

    interface Binder extends UiBinder<Widget, DataSetLookupEditorView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    DataSetLookupEditor presenter;

    @UiField
    ListBox dataSetListBox;

    @UiField
    org.gwtbootstrap3.client.ui.Label statusLabel;

    @UiField
    Panel groupControlPanel;

    @UiField
    Label groupControlLabel;

    @UiField
    Icon groupDetailsIcon;

    @UiField
    ListBox groupColumnListBox;

    @UiField
    Panel groupDatePanel;

    @UiField(provided = true)
    DataSetGroupDateEditor groupDateEditor;

    @UiField
    Panel columnsControlPanel;

    @UiField
    Label columnsControlLabel;

    @UiField
    Panel columnsPanel;

    @UiField
    Button addColumnButton;

    @UiField
    Panel filtersControlPanel;

    @UiField(provided = true)
    DataSetFilterEditor filterEditor;

    boolean dataSetSelectorHintEnabled = false;
    boolean groupColumnSelectorHintEnabled = false;

    @Override
    public void init(DataSetLookupEditor presenter) {
        this.presenter = presenter;
        this.filterEditor = presenter.getFilterEditor();
        this.groupDateEditor = presenter.getGroupDateEditor();
        initWidget(uiBinder.createAndBindUi(this));

        groupDetailsIcon.setType(IconType.ARROW_DOWN);
        groupDetailsIcon.addDomHandler(this::expandCollapseGroupDetails, ClickEvent.getType());
    }

    @Override
    public void clearAll() {
        setFilterEnabled(false);
        setGroupEnabled(false);
        setColumnsSectionEnabled(false);
        clearDataSetSelector();
        clearGroupColumnSelector();
        clearColumnList();
    }

    @Override
    public void clearDataSetSelector() {
        dataSetListBox.clear();
    }

    @Override
    public void enableDataSetSelectorHint() {
        dataSetListBox.addItem(CommonConstants.INSTANCE.common_dropdown_select());
        dataSetSelectorHintEnabled = true;
    }

    @Override
    public void addDataSetItem(String name, String id) {
        dataSetListBox.addItem(name, id);
    }

    @Override
    public void removeDataSetItem(int index) {
        dataSetListBox.removeItem(dataSetSelectorHintEnabled ? index + 1 : index);
    }

    @Override
    public void setSelectedDataSetIndex(int index) {
        dataSetListBox.setSelectedIndex(dataSetSelectorHintEnabled ? index + 1 : index);
    }

    @Override
    public String getSelectedDataSetId() {
        int idx = dataSetListBox.getSelectedIndex();
        if (dataSetSelectorHintEnabled && idx == 0) {
            return null;
        }
        return dataSetListBox.getValue(idx);
    }

    @Override
    public void errorDataSetNotFound(String dataSetUUID) {
        statusLabel.setVisible(true);
        statusLabel.setText(CommonConstants.INSTANCE.dataset_lookup_dataset_notfound(dataSetUUID));
    }

    @Override
    public void error(ClientRuntimeError e) {
        statusLabel.setVisible(true);
        statusLabel.setText(e.getCause());

        if (e.getThrowable() != null) {
            GWT.log(e.getMessage(), e.getThrowable());
        } else {
            GWT.log(e.getMessage());
        }
    }

    @Override
    public void setFilterEnabled(boolean enabled) {
        filtersControlPanel.setVisible(enabled);
    }

    @Override
    public void setGroupEnabled(boolean enabled) {
        groupControlPanel.setVisible(enabled);
    }

    @Override
    public void clearGroupColumnSelector() {
        groupColumnListBox.clear();
    }

    @Override
    public void setGroupByDateEnabled(boolean enabled) {
        groupDetailsIcon.setVisible(enabled);
        if (!enabled) {
            collapseGroupDatePanel();
        }
    }

    public void collapseGroupDatePanel() {
        groupDatePanel.setVisible(false);
        groupDetailsIcon.setType(IconType.ARROW_DOWN);
    }

    public void expandGroupDatePanel() {
        groupDatePanel.setVisible(true);
        groupDetailsIcon.setType(IconType.ARROW_UP);
    }

    @Override
    public void setGroupColumnSelectorTitle(String title) {
        groupControlLabel.setText(title);
    }

    @Override
    public void enableGroupColumnSelectorHint() {
        groupColumnListBox.insertItem(CommonConstants.INSTANCE.dataset_lookup_group_columns_all(), 0);
        groupColumnSelectorHintEnabled = true;
    }

    @Override
    public void addGroupColumnItem(String column) {
        groupColumnListBox.addItem(column);
    }

    @Override
    public void setSelectedGroupColumnIndex(int index) {
        groupColumnListBox.setSelectedIndex(groupColumnSelectorHintEnabled ? index + 1 : index);
    }

    @Override
    public String getSelectedGroupColumnId() {
        int index = groupColumnListBox.getSelectedIndex();
        if (groupColumnSelectorHintEnabled && index == 0) {
            return null;
        }
        return groupColumnListBox.getValue(index);
    }

    @Override
    public void setColumnsSectionEnabled(boolean enabled) {
        columnsControlPanel.setVisible(enabled);
    }

    @Override
    public void clearColumnList() {
        columnsPanel.clear();
    }

    @Override
    public void setColumnSectionTitle(String title) {
        columnsControlLabel.setText(title);
    }

    @Override
    public void setAddColumnOptionEnabled(boolean enabled) {
        addColumnButton.setVisible(enabled);
    }

    @Override
    public void addColumnEditor(ColumnFunctionEditor editor) {
        columnsPanel.add(editor);
    }

    @Override
    public void removeColumnEditor(ColumnFunctionEditor editor) {
        columnsPanel.remove(editor);
    }

    // UI events

    @UiHandler(value = "dataSetListBox")
    public void onDataSetSelected(ChangeEvent changeEvent) {
        presenter.onDataSetSelected();
    }

    @UiHandler(value = "addColumnButton")
    public void onAddColumnClicked(ClickEvent clickEvent) {
        presenter.onAddColumn();
    }

    @UiHandler(value = "groupColumnListBox")
    public void onGroupColumnChanged(ChangeEvent changeEvent) {
        presenter.onGroupColumnSelected();
    }

    public void expandCollapseGroupDetails(ClickEvent event) {
        if (groupDatePanel.isVisible()) {
            collapseGroupDatePanel();
        } else {
            expandGroupDatePanel();
        }
    }
}
