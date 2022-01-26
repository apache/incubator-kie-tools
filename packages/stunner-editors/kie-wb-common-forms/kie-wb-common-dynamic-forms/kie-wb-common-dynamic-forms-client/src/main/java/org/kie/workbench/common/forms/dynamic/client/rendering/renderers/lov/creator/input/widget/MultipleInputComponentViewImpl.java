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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Event;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.cell.client.CheckboxCell;
import org.gwtproject.dom.client.Style;
import org.gwtproject.user.cellview.client.Column;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

@Templated
@Dependent
public class MultipleInputComponentViewImpl<TYPE> implements MultipleInputComponentView<TYPE>,
                                                             IsElement {

    private static final Integer ROW_HEIGHT = 30;

    private Presenter<TYPE> presenter;

    private UberfirePagedTable<TableEntry<TYPE>> tableWidget;

    //@Inject
    //private TranslationService translationService;

    @Inject
    @DataField
    private HTMLDivElement toolbar;

    @Inject
    @DataField
    private HTMLButtonElement addButton;

    @Inject
    @DataField
    private HTMLButtonElement removeButton;

    @Inject
    @DataField
    private HTMLButtonElement promoteButton;

    @Inject
    @DataField
    private HTMLButtonElement degradeButton;

    @Inject
    @DataField
    private HTMLDivElement table;

    @Inject
    @DataField
    private HTMLDivElement errorContainer;

    @Inject
    @DataField
    private HTMLDivElement errorMessage;

    @Inject
    @DataField
    private HTMLButtonElement hideErrorButton;

    @PostConstruct
    public void init() {
        addButton.title = "addButton";
        removeButton.title = "removeButton";
        promoteButton.title = "moveUp";
        degradeButton.title = "moveDown";

        //addButton.setTitle(translationService.getTranslation(FormRenderingConstants.LOVCreationComponentViewImplAddButton));
        //removeButton.setTitle(translationService.getTranslation(FormRenderingConstants.LOVCreationComponentViewImplRemoveButton));
        //promoteButton.setTitle(translationService.getTranslation(FormRenderingConstants.LOVCreationComponentViewImplMoveUp));
        //degradeButton.setTitle(translationService.getTranslation(FormRenderingConstants.LOVCreationComponentViewImplMoveDown));
    }

    @Override
    public void init(Presenter<TYPE> presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        toolbar.style.removeProperty("display");
        if(readOnly) {
            toolbar.style.setProperty("display", "none");
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

        tableWidget.setEmptyTableCaption("noItems");
        //tableWidget.setEmptyTableCaption(translationService.getTranslation(FormRenderingConstants.LOVCreationComponentViewImplNoItems));

        enableRemoveButton(false);
        enablePromoteButton(false);
        enableDegradeButton(false);

        refreshTable();

        DOMUtil.appendWidgetToElement(table,
                                      tableWidget);
    }

    @Override
    public void enableRemoveButton(boolean enable) {
        removeButton.disabled = (!enable);
    }

    @Override
    public void enablePromoteButton(boolean enable) {
        promoteButton.disabled = (!enable);
    }

    @Override
    public void enableDegradeButton(boolean enable) {
        degradeButton.disabled = (!enable);
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

    //@SinkNative(Event.ONCLICK)
    @EventHandler("addButton")
    public void onAdd(@ForEvent("click") Event event) {
        hideErrorMessage();
        presenter.newElement();
    }

    //@SinkNative(Event.ONCLICK)
    @EventHandler("removeButton")
    public void onRemove(@ForEvent("click") Event event) {
        hideErrorMessage();
        presenter.removeSelectedValues();
    }

    //@SinkNative(Event.ONCLICK)
    @EventHandler("promoteButton")
    public void onPromote(@ForEvent("click")  Event event) {
        hideErrorMessage();
        presenter.promoteSelectedValues();
    }

    //@SinkNative(Event.ONCLICK)
    @EventHandler("degradeButton")
    public void onDegrade(@ForEvent("click")  Event event) {
        hideErrorMessage();
        presenter.degradeSelectedValues();
    }

    //@SinkNative(Event.ONCLICK)
    @EventHandler("hideErrorButton")
    public void onHideErrorButton(@ForEvent("click")  Event event) {
        hideErrorMessage();
    }

    public void hideErrorMessage() {
        this.errorContainer.style.setProperty("display", "none");
    }

    public void showErrorMessage(String msg) {
        this.errorContainer.style.removeProperty("display");
        this.errorMessage.textContent = (msg);
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
