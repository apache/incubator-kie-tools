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


package org.kie.workbench.common.forms.crud.client.component;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.FormDisplayer;
import org.kie.workbench.common.forms.crud.client.resources.i18n.CrudComponentConstants;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

@Dependent
@Templated
public class CrudComponentViewImpl<MODEL, FORM_MODEL> extends Composite implements CrudComponent.CrudComponentView<MODEL, FORM_MODEL> {

    private CrudComponent<MODEL, FORM_MODEL> presenter;

    private UberfirePagedTable<MODEL> table;

    protected FormDisplayer displayer;

    @Inject
    @DataField
    protected FlowPanel content = new FlowPanel();

    private final TranslationService translationService;

    @Inject
    public CrudComponentViewImpl(final TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void setPresenter(final CrudComponent<MODEL, FORM_MODEL> presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setDataProvider(final AsyncDataProvider<MODEL> dataProvider) {
        table.setDataProvider(dataProvider);
    }

    @Override
    public void showDeleteButtons() {
        final Column<MODEL, String> column = new Column<MODEL, String>(new ButtonCell(IconType.TRASH,
                                                                                      ButtonType.DANGER,
                                                                                      ButtonSize.SMALL)) {
            @Override
            public String getValue(final MODEL model) {
                return translationService.getTranslation(CrudComponentConstants.CrudComponentViewImplDeleteInstance);
            }
        };
        column.setFieldUpdater(new FieldUpdater<MODEL, String>() {
            @Override
            public void update(final int index,
                               final Object model,
                               final String s) {
                if (Window.confirm(translationService.getTranslation(CrudComponentConstants.CrudComponentViewImplDeleteBody))) {
                    presenter.deleteInstance(index);
                }
            }
        });
        table.addColumn(column,
                        "");
    }

    @Override
    public void showEditButtons() {
        final Column<MODEL, String> column = new Column<MODEL, String>(new ButtonCell(IconType.EDIT,
                                                                                      ButtonType.PRIMARY,
                                                                                      ButtonSize.SMALL)) {
            @Override
            public String getValue(final Object model) {
                return translationService.getTranslation(CrudComponentConstants.CrudComponentViewImplEditInstanceButton);
            }
        };
        column.setFieldUpdater(new FieldUpdater<MODEL, String>() {
            @Override
            public void update(final int index,
                               final Object model,
                               final String s) {
                presenter.editInstance(index);
            }
        });
        table.addColumn(column,
                        "");
    }

    @Override
    public void initTableView(final List<ColumnMeta<MODEL>> dataColumns,
                              final int pageSize) {
        content.clear();
        table = new UberfirePagedTable<>(pageSize);
        table.getRightToolbar().clear();
        final List<ColumnMeta<MODEL>> columns = new ArrayList<>(dataColumns);
        table.addColumns(columns);
        content.add(table);
    }

    @Override
    public void showCreateButton() {
        final Button createButton = new Button(translationService.getTranslation(CrudComponentConstants.CrudComponentViewImplNewInstanceButton));
        createButton.setType(ButtonType.PRIMARY);
        createButton.setIcon(IconType.PLUS);
        table.getLeftToolbar().add(createButton);
        createButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent clickEvent) {
                presenter.createInstance();
            }
        });
    }

    @Override
    public int getCurrentPage() {
        return table.getPageStart();
    }

    @Override
    public void addDisplayer(final FormDisplayer displayer) {
        content.clear();
        content.add(displayer);
    }

    @Override
    public void removeDisplayer(final FormDisplayer displayer) {
        content.remove(displayer);
        content.add(table);
    }
}
