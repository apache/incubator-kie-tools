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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.CellEditionHandler;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.ColumnFieldUpdater;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.TableEntry;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.AbstractEditableColumnGenerator;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

public abstract class AbstractNumericEditableColumnGenerator<NUMBER> extends AbstractEditableColumnGenerator<NUMBER> {

    public AbstractNumericEditableColumnGenerator(TranslationService translationService) {
        super(translationService);
    }

    @Override
    protected Column<TableEntry<NUMBER>, String> getEditableColumn(UberfirePagedTable<TableEntry<NUMBER>> table,
                                                                   CellEditionHandler<NUMBER> cellEditionHandler) {
        Column<TableEntry<NUMBER>, String> column = new Column<TableEntry<NUMBER>, String>(new EditTextCell()) {
            @Override
            public String getValue(TableEntry<NUMBER> model) {
                if (model.getValue() == null) {
                    return "";
                }
                return model.getValue().toString();
            }
        };

        ColumnFieldUpdater<NUMBER, String> updater = new ColumnFieldUpdater<NUMBER, String>(table,
                                                                                            column) {

            @Override
            protected boolean validate(String flatValue,
                                       TableEntry<NUMBER> model) {
                return doValidate(flatValue,
                                  model,
                                  cellEditionHandler);
            }

            @Override
            protected NUMBER convert(String flatValue) {
                if(flatValue == null || flatValue.isEmpty()) {
                    return null;
                }
                return doConvert(flatValue);
            }
        };

        updater.setCellEditionHandler(cellEditionHandler);

        column.setFieldUpdater(updater);

        return column;
    }

    protected abstract boolean doValidate(String flatValue,
                                          TableEntry<NUMBER> model,
                                          CellEditionHandler<NUMBER> cellEditionHandler);

    protected abstract NUMBER doConvert(String flatValue);

    @Override
    protected Column<TableEntry<NUMBER>, String> getReadOnlyColumn() {
        Column<TableEntry<NUMBER>, String> column = new Column<TableEntry<NUMBER>, String>(new TextCell()) {
            @Override
            public String getValue(TableEntry<NUMBER> model) {
                if (model.getValue() == null) {
                    return "";
                }
                return model.getValue().toString();
            }
        };

        return column;
    }
}
