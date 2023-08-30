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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.CellEditionHandler;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.ColumnFieldUpdater;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.TableEntry;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

@Dependent
public class CharacterEditableColumnGenerator extends AbstractEditableColumnGenerator<String> {

    @Inject
    public CharacterEditableColumnGenerator(TranslationService translationService) {
        super(translationService);
    }

    @Override
    public String[] getTypes() {
        return new String[]{Character.class.getName()};
    }

    @Override
    protected Column<TableEntry<String>, String> getEditableColumn(UberfirePagedTable<TableEntry<String>> table,
                                                                   CellEditionHandler<String> cellEditionHandler) {
        Column<TableEntry<String>, String> column = new Column<TableEntry<String>, String>(new EditTextCell()) {
            @Override
            public String getValue(TableEntry<String> model) {
                if (model.getValue() == null) {
                    model.setValue("");
                }
                return model.getValue();
            }
        };

        ColumnFieldUpdater<String, String> updater = new ColumnFieldUpdater<String, String>(table,
                                                                                            column) {

            @Override
            protected boolean validate(String value,
                                       TableEntry<String> model) {
                if (value != null && !value.isEmpty()) {
                    if (value.length() != 1) {
                        cellEditionHandler.showValidationError(translationService.getTranslation(FormRenderingConstants.CharacterEditableColumnGeneratorValidationError));
                        return false;
                    }
                }

                return true;
            }

            @Override
            protected String convert(String flatValue) {
                if(flatValue == null || flatValue.isEmpty()) {
                    return null;
                }
                return super.convert(flatValue);
            }
        };

        updater.setCellEditionHandler(cellEditionHandler);

        column.setFieldUpdater(updater);

        return column;
    }

    @Override
    protected Column<TableEntry<String>, String> getReadOnlyColumn() {
        Column<TableEntry<String>, String> column = new Column<TableEntry<String>, String>(new TextCell()) {
            @Override
            public String getValue(TableEntry<String> model) {
                if (model.getValue() == null) {
                    model.setValue("");
                }
                return model.getValue();
            }
        };

        return column;
    }
}
