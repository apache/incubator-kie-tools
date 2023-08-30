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

import java.util.Date;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.CellEditionHandler;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.ColumnFieldUpdater;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.TableEntry;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

@Dependent
public class DateEditableColumnGenerator extends AbstractEditableColumnGenerator<Date> {

    public static String DEFAULT_DATE_AND_TIME_FORMAT_MASK = "dd/MM/yyyy HH:mm";

    private ManagedInstance<DateTimePickerCell> dateTimePickerCells;

    @Inject
    public DateEditableColumnGenerator(TranslationService translationService, ManagedInstance<DateTimePickerCell> dateTimePickerCells) {
        super(translationService);
        this.dateTimePickerCells = dateTimePickerCells;
    }

    @Override
    public String[] getTypes() {
        return new String[]{Date.class.getName(), "java.time.LocalDate", "java.time.LocalDateTime", "java.time.LocalTime", "java.time.OffsetDateTime"};
    }

    @Override
    protected Column<TableEntry<Date>, Date> getEditableColumn(UberfirePagedTable<TableEntry<Date>> table,
                                                               CellEditionHandler<Date> cellEditionHandler) {

        Column<TableEntry<Date>, Date> column = new Column<TableEntry<Date>, Date>(dateTimePickerCells.get()) {
            @Override
            public Date getValue(TableEntry<Date> model) {
                if (model.getValue() == null) {
                    model.setValue(new Date());
                }
                return model.getValue();
            }
        };

        ColumnFieldUpdater<Date, Date> updater = new ColumnFieldUpdater<Date, Date>(table, column) {

            @Override
            protected boolean validate(Date value,
                                       TableEntry<Date> model) {
                return true;
            }
        };

        updater.setCellEditionHandler(cellEditionHandler);

        column.setFieldUpdater(updater);

        return column;
    }

    @Override
    protected Column<TableEntry<Date>, String> getReadOnlyColumn() {

        Column<TableEntry<Date>, String> column = new Column<TableEntry<Date>, String>(new TextCell()) {
            @Override
            public String getValue(TableEntry<Date> model) {
                if (model.getValue() == null) {
                    return "";
                }
                DateTimeFormat format = DateTimeFormat.getFormat(DEFAULT_DATE_AND_TIME_FORMAT_MASK);
                return format.format(model.getValue());
            }
        };

        return column;
    }

    @PreDestroy
    public void destroy() {
        dateTimePickerCells.destroyAll();
    }
}
