/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

@Templated
public class MultipleInputComponentViewImpl<TYPE> implements MultipleInputComponentView<TYPE>,
                                                             IsElement {

    private static final Integer ROW_HEIGHT = 30;

    private Presenter<TYPE> presenter;

    private UberfirePagedTable<TableEntry<TYPE>> tableWidget;

    @Inject
    private TranslationService translationService;

    @Inject
    @DataField
    private Div toolbar;

    @Inject
    @DataField
    private Button addButton;

    @Inject
    @DataField
    private Button removeButton;

    @Inject
    @DataField
    private Button promoteButton;

    @Inject
    @DataField
    private Button degradeButton;

    @Inject
    @DataField
    private Div table;

    @Inject
    @DataField
    private Div errorContainer;

    @Inject
    @DataField
    private Div errorMessage;

    @Inject
    @DataField
    private Button hideErrorButton;

    @PostConstruct
    public void init() {
        addButton.setTitle(translationService.getTranslation(FormRenderingConstants.LOVCreationComponentViewImplAddButton));
        removeButton.setTitle(translationService.getTranslation(FormRenderingConstants.LOVCreationComponentViewImplRemoveButton));
        promoteButton.setTitle(translationService.getTranslation(FormRenderingConstants.LOVCreationComponentViewImplMoveUp));
        degradeButton.setTitle(translationService.getTranslation(FormRenderingConstants.LOVCreationComponentViewImplMoveDown));
    }

    @Override
    public void init(Presenter<TYPE> presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        toolbar.getStyle().removeProperty("display");
        if(readOnly) {
            toolbar.getStyle().setProperty("display", "none");
        }
    }

    @Override
    public void render() {
        hideErrorMessage();

        DOMUtil.removeAllChildren(table);

        tableWidget = new UberfirePagedTable<>(presenter.getPageSize());
        tableWidget.setHeight(ROW_HEIGHT * (presenter.getPageSize() + 1) + "px");
        tableWidget.setDataProvider(presenter.getProvider());

        tableWidget.setColumnPickerButtonVisible(false);

        if(!presenter.isReadOnly()) {
            Column<TableEntry<TYPE>, Boolean> select = new Column<TableEntry<TYPE>, Boolean>(new CheckboxCell()) {
                @Override
                public Boolean getValue(TableEntry<TYPE> object) {
                    return presenter.isSelected(object);
                }
            };

            tableWidget.addColumn(select,
                                  "");

            tableWidget.setColumnWidth(select,
                                       35,
                                       Style.Unit.PX);

            select.setFieldUpdater((index, tableEntry, value) -> {
                presenter.selectValue(tableEntry);
            });
        }

        EditableColumnGenerator<TYPE> columnGenerator = presenter.getColumnGenerator();

        columnGenerator.registerColumn(tableWidget,
                                       new CellEdtionHandlerImpl(),
                                       presenter.isReadOnly());

        tableWidget.setEmptyTableCaption(translationService.getTranslation(FormRenderingConstants.LOVCreationComponentViewImplNoItems));

        enableRemoveButton(false);
        enablePromoteButton(false);
        enableDegradeButton(false);

        refreshTable();

        DOMUtil.appendWidgetToElement(table,
                                      tableWidget);
    }

    @Override
    public void enableRemoveButton(boolean enable) {
        removeButton.setDisabled(!enable);
    }

    @Override
    public void enablePromoteButton(boolean enable) {
        promoteButton.setDisabled(!enable);
    }

    @Override
    public void enableDegradeButton(boolean enable) {
        degradeButton.setDisabled(!enable);
    }

    @Override
    public int getCurrentPage() {
        return tableWidget.getPageStart();
    }

    @Override
    public int getPageSize() {
        return tableWidget.getPageSize();
    }

    @Override
    public void refreshTable() {
        tableWidget.redraw();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("addButton")
    public void onAdd(Event event) {
        hideErrorMessage();
        presenter.newElement();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("removeButton")
    public void onRemove(Event event) {
        hideErrorMessage();
        presenter.removeSelectedValues();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("promoteButton")
    public void onPromote(Event event) {
        hideErrorMessage();
        presenter.promoteSelectedValues();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("degradeButton")
    public void onDegrade(Event event) {
        hideErrorMessage();
        presenter.degradeSelectedValues();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("hideErrorButton")
    public void onHideErrorButton(Event event) {
        hideErrorMessage();
    }

    public void hideErrorMessage() {
        this.errorContainer.getStyle().setProperty("display", "none");
    }

    public void showErrorMessage(String msg) {
        this.errorContainer.getStyle().removeProperty("display");
        this.errorMessage.setTextContent(msg);
    }

    class CellEdtionHandlerImpl implements CellEditionHandler<TYPE> {

        @Override
        public void clearValidationErrors() {
            hideErrorMessage();
        }

        @Override
        public void showValidationError(String errorMessage) {
            showErrorMessage(errorMessage);
        }

        @Override
        public void valueChanged(int index,
                                 TYPE value) {
            presenter.notifyChange(index, value);
        }
    }
}
