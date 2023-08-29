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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.user.cellview.client.Column;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.CellEditionHandler;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.ColumnFieldUpdater;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.TableEntry;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.uberfire.ext.widgets.table.client.CheckboxCellImpl;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

@Dependent
public class BooleanEditableColumnGenerator extends AbstractEditableColumnGenerator<Boolean> {

    @Inject
    public BooleanEditableColumnGenerator(TranslationService translationService) {
        super(translationService);
    }

    @Override
    public String[] getTypes() {
        return new String[]{Boolean.class.getName()};
    }

    @Override
    protected Column<TableEntry<Boolean>, String> getEditableColumn(UberfirePagedTable<TableEntry<Boolean>> table,
                                                                     CellEditionHandler<Boolean> cellEditionHandler) {

        final String yesLiteral = translationService.getTranslation(FormRenderingConstants.BooleanEditableColumnGeneratorYes);
        final String noLiteral = translationService.getTranslation(FormRenderingConstants.BooleanEditableColumnGeneratorNo);

        final List<String> values = new ArrayList<>();
        values.add(yesLiteral);
        values.add(noLiteral);

        Column<TableEntry<Boolean>, String> column = new Column<TableEntry<Boolean>, String>(new SelectionCell(values)) {
            @Override
            public String getValue(TableEntry<Boolean> model) {
                if (model.getValue() == null) {
                    model.setValue(Boolean.FALSE);
                }

                if(model.getValue()) {
                    return yesLiteral;
                }

                return noLiteral;
            }
        };

        ColumnFieldUpdater<Boolean, String> updater = new ColumnFieldUpdater<Boolean, String>(table, column) {

            @Override
            protected boolean validate(String value,
                                       TableEntry<Boolean> model) {
                return true;
            }

            @Override
            protected Boolean convert(String flatValue) {

                if(flatValue.equals(yesLiteral)) {
                    return Boolean.TRUE;
                }

                return Boolean.FALSE;
            }
        };

        updater.setCellEditionHandler(cellEditionHandler);

        column.setFieldUpdater(updater);

        return column;
    }

    @Override
    protected Column<TableEntry<Boolean>, Boolean> getReadOnlyColumn() {
        Column<TableEntry<Boolean>, Boolean> column = new Column<TableEntry<Boolean>, Boolean>(new CheckboxCellImpl(true)) {
            @Override
            public Boolean getValue(TableEntry<Boolean> model) {
                return Boolean.TRUE.equals(model.getValue());
            }
        };

        return column;
    }
}
