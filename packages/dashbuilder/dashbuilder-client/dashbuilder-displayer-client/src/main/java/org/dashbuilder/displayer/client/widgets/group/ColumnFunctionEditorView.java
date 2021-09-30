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
package org.dashbuilder.displayer.client.widgets.group;

import javax.enterprise.context.Dependent;

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
import org.dashbuilder.dataset.client.resources.i18n.AggregateFunctionTypeConstants;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;

@Dependent
public class ColumnFunctionEditorView extends Composite implements ColumnFunctionEditor.View {

    interface Binder extends UiBinder<Widget, ColumnFunctionEditorView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    ListBox columnListBox;

    @UiField
    ListBox functionListBox;

    @UiField
    Icon columnDeleteIcon;

    @UiField
    Icon columnExpandIcon;

    @UiField
    Panel columnDetailsPanel;

    @UiField(provided = true)
    ColumnDetailsEditor columnDetailsEditor;

    ColumnFunctionEditor presenter = null;
    boolean voidFunctionEnabled = false;

    @Override
    public void init(final ColumnFunctionEditor presenter) {
        this.presenter = presenter;
        this.columnDetailsEditor = presenter.getColumnDetailsEditor();
        initWidget(uiBinder.createAndBindUi(this));

        columnExpandIcon.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                expandOrCollapse();
            }
        }, ClickEvent.getType());
        columnDeleteIcon.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                presenter.delete();
            }
        }, ClickEvent.getType());
    }

    @Override
    public void setDeleteOptionEnabled(boolean enabled) {
        columnDeleteIcon.setVisible(enabled);
    }

    @Override
    public void setColumnSelectorTitle(String title) {
        columnListBox.setTitle(title);
    }

    @Override
    public void clearColumnSelector() {
        columnListBox.clear();
    }

    @Override
    public void addColumnItem(String columnId) {
        columnListBox.addItem(columnId);
    }

    @Override
    public void setSelectedColumnIndex(int i) {
        columnListBox.setSelectedIndex(i);
    }

    @Override
    public String getSelectedColumnId() {
        return columnListBox.getValue(columnListBox.getSelectedIndex());
    }

    @Override
    public void setFunctionSelectorEnabled(boolean enabled) {
        if (enabled) {
            functionListBox.setVisible(true);
            columnListBox.setWidth("120px");
        } else {
            functionListBox.setVisible(false);
            columnListBox.setWidth("200px");
        }
    }

    @Override
    public void clearFunctionSelector() {
        functionListBox.clear();
    }

    @Override
    public void setVoidFunctionEnabled(boolean enabled) {
        voidFunctionEnabled = enabled;
        if (enabled) {
            functionListBox.addItem("---");
        }
    }

    @Override
    public void addFunctionItem(AggregateFunctionType functionType) {
        String functionName = AggregateFunctionTypeConstants.INSTANCE.getString(functionType.name());
        functionListBox.addItem(functionName);
    }

    @Override
    public void setSelectedFunctionIndex(int i) {
        functionListBox.setSelectedIndex(voidFunctionEnabled ? i+1 : i);
    }

    @Override
    public int getSelectedFunctionIndex() {
        int i = functionListBox.getSelectedIndex();
        return voidFunctionEnabled ? i-1 : i;
    }

    // UI events

    protected void expandOrCollapse() {
        if (columnDetailsPanel.isVisible()) {
            collapse();
        } else {
            expand();
        }
    }

    protected void expand() {
        columnDetailsPanel.setVisible(true);
        columnExpandIcon.setType(IconType.ARROW_UP);
    }

    protected void collapse() {
        columnDetailsPanel.setVisible(false);
        columnExpandIcon.setType(IconType.ARROW_DOWN);
    }

    @UiHandler(value = "columnListBox")
    protected void onColumnSelected(ChangeEvent changeEvent) {
        presenter.onColumnSelected();
    }

    @UiHandler(value = "functionListBox")
    protected void onFunctionSelected(ChangeEvent changeEvent) {
        presenter.onFunctionSelected();
    }
}

