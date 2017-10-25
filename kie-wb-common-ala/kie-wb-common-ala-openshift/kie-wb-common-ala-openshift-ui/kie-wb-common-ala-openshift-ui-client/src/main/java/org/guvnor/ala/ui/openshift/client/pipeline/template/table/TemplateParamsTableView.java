/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.openshift.client.pipeline.template.table;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.HasData;
import org.guvnor.ala.ui.openshift.model.TemplateParam;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;

import static org.guvnor.ala.ui.client.util.UIUtil.EMPTY_STRING;
import static org.guvnor.ala.ui.openshift.client.resources.i18n.GuvnorAlaOpenShiftUIConstants.TemplateParamsTableView_ParamNameColumn;
import static org.guvnor.ala.ui.openshift.client.resources.i18n.GuvnorAlaOpenShiftUIConstants.TemplateParamsTableView_ParamRequiredColumn;
import static org.guvnor.ala.ui.openshift.client.resources.i18n.GuvnorAlaOpenShiftUIConstants.TemplateParamsTableView_ParamValueColumn;
import static org.guvnor.ala.ui.openshift.client.resources.i18n.GuvnorAlaOpenShiftUIConstants.TemplateParamsTableView_ParamsEmptyTableCaption;

@Templated
public class TemplateParamsTableView
        implements TemplateParamsTablePresenter.View,
                   IsElement {

    @DataField("params-table")
    private SimpleTable<TemplateParam> dataGrid = new SimpleTable<>();

    @Inject
    private TranslationService translationService;

    private TemplateParamsTablePresenter presenter;

    @PostConstruct
    private void init() {
        dataGrid.setColumnPickerButtonVisible(false);
        dataGrid.setToolBarVisible(false);
        dataGrid.setEmptyTableCaption(translationService.getTranslation(TemplateParamsTableView_ParamsEmptyTableCaption));
        addParamNameColumn();
        addParamRequiredColumn();
        addParamValueColumn();
    }

    @Override
    public void init(final TemplateParamsTablePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasData<TemplateParam> getDisplay() {
        return dataGrid;
    }

    @Override
    public void redraw() {
        dataGrid.redraw();
    }

    private void addParamNameColumn() {
        Column<TemplateParam, String> column = new Column<TemplateParam, String>(new TextCell()) {
            @Override
            public String getValue(TemplateParam templateParam) {
                return templateParam.isRequired() ? templateParam.getName() : templateParam.getName();
            }
        };

        dataGrid.addColumn(column,
                           translationService.getTranslation(TemplateParamsTableView_ParamNameColumn));
        dataGrid.setColumnWidth(column,
                                40,
                                Style.Unit.PCT);
    }

    private void addParamRequiredColumn() {
        Column<TemplateParam, String> column = new Column<TemplateParam, String>(new TextCell()) {
            @Override
            public String getValue(TemplateParam templateParam) {
                return templateParam.isRequired() ? "*" : "";
            }
        };

        dataGrid.addColumn(column,
                           " ");
        dataGrid.setColumnWidth(column,
                                30,
                                Style.Unit.PX);
    }

    private void addParamValueColumn() {
        final Column<TemplateParam, String> column = new Column<TemplateParam, String>(new EditTextCell()) {
            @Override
            public String getValue(TemplateParam templateParam) {
                if (templateParam.getValue() != null) {
                    return templateParam.getValue();
                } else {
                    return EMPTY_STRING;
                }
            }
        };

        column.setFieldUpdater(new ParamValueFieldUpdater((EditTextCell) column.getCell()));

        dataGrid.addColumn(column,
                           translationService.getTranslation(TemplateParamsTableView_ParamValueColumn));
        dataGrid.setColumnWidth(column,
                                55,
                                Style.Unit.PCT);
    }

    private class ParamValueFieldUpdater implements FieldUpdater<TemplateParam, String> {

        private EditTextCell cell;

        ParamValueFieldUpdater(EditTextCell cell) {
            this.cell = cell;
        }

        @Override
        public void update(final int index,
                           final TemplateParam templateParam,
                           final String value) {
            String oldValue = templateParam.getValue();
            templateParam.setValue(value);
            presenter.onParamChange(templateParam.getName(),
                                    value,
                                    oldValue);
        }
    }
}
