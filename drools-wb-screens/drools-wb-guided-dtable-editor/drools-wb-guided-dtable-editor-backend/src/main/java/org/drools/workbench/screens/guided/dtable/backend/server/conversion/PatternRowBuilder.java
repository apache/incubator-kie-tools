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

import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.drools.core.util.StringUtils;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RuleNameColumn;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.ColumnContext;
import org.kie.soup.commons.validation.PortablePreconditions;

import static org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.BRLColumnUtil.canThisColumnBeSplitToMultiple;

public class PatternRowBuilder {

    private static final int PATTERN_ROW = 6;

    private final GuidedDecisionTable52 dtable;
    private final Sheet sheet;
    private final Row patternRow;
    private ColumnContext columnContext;

    private int columnIndex = 0;
    private int sourceIndex = 0;

    public PatternRowBuilder(final Sheet sheet,
                             final GuidedDecisionTable52 dtable,
                             final ColumnContext columnContext) {
        this.sheet = PortablePreconditions.checkNotNull("sheet", sheet);
        this.dtable = PortablePreconditions.checkNotNull("dtable", dtable);
        this.columnContext = PortablePreconditions.checkNotNull("brlColumnIndex", columnContext);

        this.patternRow = sheet.createRow(PATTERN_ROW);
    }

    public void build() {

        final List<BaseColumn> expandedColumns = dtable.getExpandedColumns();

        for (; sourceIndex < expandedColumns.size(); sourceIndex++) {

            final BaseColumn baseColumn = expandedColumns.get(sourceIndex);

            if (baseColumn instanceof BRLActionVariableColumn) {

                sourceIndex = sourceIndex + dtable.getBRLColumn((BRLActionVariableColumn) baseColumn).getChildColumns().size() - 1;
            } else if (baseColumn instanceof BRLConditionVariableColumn) {

                addBRLConditionVariableColumn((BRLConditionVariableColumn) baseColumn);
            } else if (baseColumn instanceof ConditionCol52) {

                addConditionCol52((ConditionCol52) baseColumn);
            } else if (baseColumn instanceof RowNumberCol52 || baseColumn instanceof RuleNameColumn) {
                // Ignore row column and do not up the columnIndex
                continue;
            }
            columnIndex++;
        }
    }

    public void addConditionCol52(final ConditionCol52 baseColumn) {
        final ConditionCol52 col = baseColumn;
        final Pattern52 pattern = dtable.getPattern(col);
        final int columnWidth = getColumnWidth(pattern);

        addPattern(columnWidth,
                   hasEntryPoint(pattern),
                   pattern.isNegated(),
                   pattern.getBoundName(),
                   pattern.getFactType());
    }

    public void addBRLConditionVariableColumn(final BRLConditionVariableColumn baseColumn) {
        final BRLConditionColumn brlColumn = dtable.getBRLColumn(baseColumn);

        if (canThisColumnBeSplitToMultiple(brlColumn)) {
            final Iterator<IPattern> patternIterator = brlColumn.getDefinition().iterator();
            while (patternIterator.hasNext()) {
                final IPattern iPattern = patternIterator.next();
                if (iPattern instanceof FactPattern) {
                    FactPattern factPattern = (FactPattern) iPattern;
                    int amountOfUniqueVariables = columnContext.getAmountOfUniqueVariables(iPattern);

                    addPattern(amountOfUniqueVariables,
                               false,
                               factPattern.isNegated(),
                               factPattern.getBoundName(),
                               factPattern.getFactType());
                    sourceIndex = sourceIndex + amountOfUniqueVariables - 1;
                }
                if (patternIterator.hasNext()) {
                    columnIndex++;
                }
            }
        } else {
            sourceIndex = sourceIndex + brlColumn.getChildColumns().size() - 1;
        }
    }

    private void addPattern(final int columnWidth,
                            final boolean hasEntryPoint,
                            final boolean negated,
                            final String boundName,
                            final String factType) {
        final int endIndex = columnIndex + columnWidth - 1;

        for (int i = columnIndex; i <= endIndex; i++) {

            if (hasEntryPoint) {

                throw new UnsupportedOperationException("Conversion of the entry points are not supported.");
            } else if (negated) {

                patternRow.createCell(i).setCellValue(String.format("not %s",
                                                                    factType));
            } else if (boundName == null) {

                patternRow.createCell(i).setCellValue(factType);
            } else {

                patternRow.createCell(i).setCellValue(String.format("%s : %s",
                                                                    boundName,
                                                                    factType));
            }
        }

        if (columnWidth > 1) {
            sheet.addMergedRegion(new CellRangeAddress(PATTERN_ROW, PATTERN_ROW, columnIndex, endIndex));
            sheet.validateMergedRegions();
            columnIndex = endIndex;
            sourceIndex = sourceIndex + columnWidth - 1;
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
