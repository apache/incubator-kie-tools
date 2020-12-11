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

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

public class BRLColumnDataBuilderDefault
        implements BRLColumnDataBuilder {

    private final DataBuilder.DataRowBuilder dataRowBuilder;

    public BRLColumnDataBuilderDefault(final DataBuilder.DataRowBuilder dataRowBuilder) {
        this.dataRowBuilder = dataRowBuilder;
    }

    @Override
    public void build(final BRLActionColumn brlColumn,
                      final List<DTCellValue52> row,
                      final Row xlsRow) {

        addColumnValuesToRow(xlsRow, new CombinedValueProvider(row,
                                                               brlColumn,
                                                               false));
    }

    @Override
    public void build(final BRLConditionColumn brlColumn,
                      final List<DTCellValue52> row,
                      final Row xlsRow) {

        addColumnValuesToRow(xlsRow,
                             new CombinedValueProvider(row,
                                                       brlColumn,
                                                       true));
    }

    private void addColumnValuesToRow(final Row xlsRow,
                                      final CombinedValueProvider combinedValueProvider) {

        if (combinedValueProvider.isContentValid()) {
            xlsRow.createCell(dataRowBuilder.getTargetColumnIndex())
                    .setCellValue(combinedValueProvider.getValue());
        }
    }

    class CombinedValueProvider {

        private final String value;
        private boolean isContentValid = false;
        private final List<DTCellValue52> row;
        private final boolean missingVariable;
        private final int amountOfChildColumns;
        private final boolean trimQuotesOff;

        public CombinedValueProvider(final List<DTCellValue52> row,
                                     final BRLColumn brlColumn,
                                     final boolean trimQuotesOff) {
            this.row = row;

            missingVariable = isMissingVariable(brlColumn);
            amountOfChildColumns = brlColumn.getChildColumns().size();
            this.trimQuotesOff = trimQuotesOff;
            value = build();
        }

        public String getValue() {
            return value;
        }

        public boolean isContentValid() {
            return isContentValid;
        }

        private String build() {
            final StringBuilder result = new StringBuilder();

            for (int i = 0; i < amountOfChildColumns; i++) {

                final String cellValue = dataRowBuilder.getValue(row,
                                                                 dataRowBuilder.getSourceColumnIndex());
                if (cellValue != null && missingVariable) {
                    if (BooleanUtils.toBoolean(cellValue.toLowerCase())) {
                        result.append("X");
                        isContentValid = true;
                    }
                    break;
                } else if (StringUtils.isNotEmpty(cellValue)) {
                    result.append(trimQuotesOff(cellValue));
                    isContentValid = true;
                }
                if (i < amountOfChildColumns - 1) {
                    result.append(", ");
                    dataRowBuilder.moveSourceColumnIndexForward();
                }
            }
            return result.toString();
        }

        private boolean isMissingVariable(final BRLColumn brlColumn) {
            for (final Object childColumn : brlColumn.getChildColumns()) {
                final String variableName = getVariableName(childColumn);
                if (!StringUtils.isEmpty(variableName)) {
                    return false;
                }
            }
            return true;
        }

        private String getVariableName(final Object childColumn) {
            if (childColumn instanceof BRLActionVariableColumn) {
                return ((BRLActionVariableColumn) childColumn).getVarName();
            } else if (childColumn instanceof BRLConditionVariableColumn) {
                return ((BRLConditionVariableColumn) childColumn).getVarName();
            } else {
                return "";
            }
        }

        private String trimQuotesOff(final String value) {
            if (trimQuotesOff && isSurroundedByQuotes(value)) {
                return value.substring(1, value.length() - 1);
            } else {
                return value;
            }
        }

        private boolean isSurroundedByQuotes(final String value) {
            return StringUtils.isNotEmpty(value) && value.length() >= 2
                    && value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"';
        }
    }
}
