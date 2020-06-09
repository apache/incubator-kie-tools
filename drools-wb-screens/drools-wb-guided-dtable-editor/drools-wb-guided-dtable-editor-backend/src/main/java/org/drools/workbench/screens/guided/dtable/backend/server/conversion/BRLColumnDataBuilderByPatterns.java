/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.backend.server.conversion;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.ColumnContext;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.FromTo;

public class BRLColumnDataBuilderByPatterns
        implements BRLColumnDataBuilder {

    private DataBuilder.DataRowBuilder dataRowBuilder;
    private ColumnContext columnContext;

    public BRLColumnDataBuilderByPatterns(final DataBuilder.DataRowBuilder dataRowBuilder,
                                          final ColumnContext columnContext) {
        this.dataRowBuilder = dataRowBuilder;
        this.columnContext = columnContext;
    }

    @Override
    public void build(final BRLConditionColumn baseColumn,
                      final List<DTCellValue52> row,
                      final Row xlsRow) {
        addColumnValuesToRow(row, xlsRow, columnContext.getCols(baseColumn));
        advanceSourceColumnIndex(baseColumn);
    }

    @Override
    public void build(final BRLActionColumn baseColumn,
                      final List<DTCellValue52> row,
                      final Row xlsRow) {
        addColumnValuesToRow(row, xlsRow, columnContext.getCols(baseColumn));
        advanceSourceColumnIndex(baseColumn);
    }

    private void addColumnValuesToRow(final List<DTCellValue52> row,
                                      final Row xlsRow,
                                      final List<FromTo> columns) {
        final Iterator<FromTo> iterator = columns.iterator();
        while (iterator.hasNext()) {
            final FromTo fromTo = iterator.next();

            if (fromTo.shouldUseATickAsValue() && containsValues(columns, row)) {
                xlsRow.createCell(fromTo.getToColumnIndex())
                        .setCellValue("X");
            } else {

                final String value = dataRowBuilder.getValue(row,
                                                             fromTo.getFromColumnIndex(),
                                                             true);
                if (StringUtils.isNotEmpty(value)) {
                    xlsRow.createCell(dataRowBuilder.getTargetColumnIndex())
                            .setCellValue(value);
                }
            }
            if (iterator.hasNext()) {
                dataRowBuilder.moveToNextTargetColumnIndex();
            }
        }
    }

    private boolean containsValues(final List<FromTo> cols,
                                   final List<DTCellValue52> row) {
        for (final FromTo fromTo : cols) {
            if (!fromTo.shouldUseATickAsValue() && StringUtils.isNotEmpty(dataRowBuilder.getValue(row, fromTo.getToColumnIndex()))) {
                return true;
            }
        }
        return false;
    }

    private void advanceSourceColumnIndex(final CompositeColumn baseColumn) {
        final Iterator<BRLConditionVariableColumn> iterator = baseColumn.getChildColumns().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            if (iterator.hasNext()) {
                dataRowBuilder.moveSourceColumnIndexForward();
            }
        }
    }
}
