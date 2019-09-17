/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.drools.core.util.StringUtils;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.kie.soup.commons.validation.PortablePreconditions;

public class PatternRowBuilder {

    private static final int PATTERN_ROW = 6;

    private final GuidedDecisionTable52 dtable;
    private final Sheet sheet;
    private final Row patternRow;

    private int columnIndex = 0;

    public PatternRowBuilder(final Sheet sheet,
                             final GuidedDecisionTable52 dtable) {
        this.sheet = PortablePreconditions.checkNotNull("sheet", sheet);
        this.dtable = PortablePreconditions.checkNotNull("dtable", dtable);

        this.patternRow = sheet.createRow(PATTERN_ROW);
    }

    public void build() {

        final List<BaseColumn> expandedColumns = dtable.getExpandedColumns();

        for (int sourceIndex = 0; sourceIndex < expandedColumns.size(); sourceIndex++) {

            final BaseColumn baseColumn = expandedColumns.get(sourceIndex);

            if (baseColumn instanceof BRLConditionVariableColumn
                    || baseColumn instanceof BRLConditionColumn
                    || baseColumn instanceof BRLActionVariableColumn
                    || baseColumn instanceof BRLActionColumn) {
                throw new UnsupportedOperationException("Conversion of the BRL column is not supported.");
            } else if (baseColumn instanceof ConditionCol52) {

                final ConditionCol52 col = (ConditionCol52) baseColumn;
                final Pattern52 pattern = dtable.getPattern(col);
                final int columnWidth = getColumnWidth(pattern);

                final int endIndex = columnIndex + columnWidth - 1;

                for (int i = columnIndex; i <= endIndex; i++) {

                    if (hasEntryPoint(pattern)) {

                        throw new UnsupportedOperationException("Conversion of the entry points are not supported.");
                    } else if (pattern.isNegated()) {

                        patternRow.createCell(i).setCellValue(String.format("not %s",
                                                                            pattern.getFactType()));
                    } else {

                        patternRow.createCell(i).setCellValue(String.format("%s : %s",
                                                                            pattern.getBoundName(),
                                                                            pattern.getFactType()));
                    }
                }

                if (columnWidth > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(PATTERN_ROW, PATTERN_ROW, columnIndex, endIndex));
                    sheet.validateMergedRegions();
                    columnIndex = endIndex;
                    sourceIndex = sourceIndex + columnWidth - 1;
                }
            } else if (baseColumn instanceof RowNumberCol52) {
                // Ignore row column and do not up the columnIndex
                continue;
            }
            columnIndex++;
        }
    }

    private boolean hasEntryPoint(final Pattern52 pattern) {
        return !StringUtils.isEmpty(pattern.getEntryPointName());
    }

    private int getColumnWidth(final Pattern52 pattern) {
        if (StringUtils.isEmpty(pattern.getBoundName())) {
            return 1;
        } else {
            return pattern.getChildColumns().size();
        }
    }
}
