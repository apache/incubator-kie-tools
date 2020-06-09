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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.drools.core.util.DateUtils;
import org.drools.core.util.StringUtils;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.util.ColumnUtilitiesBase;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.ColumnContext;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;

import static org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint.TYPE_PREDICATE;
import static org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint.TYPE_RET_VALUE;

public class DataBuilder {

    private final static int FIRST_DATA_ROW = 9;

    private final Sheet sheet;
    private final GuidedDecisionTable52 dtable;
    private final ColumnContext columnContext;
    private final ColumnUtilitiesBase utilsWithRespectForLists;
    private final ColumnUtilitiesBase utilsWithNoRespectForLists;

    private int rowCount = FIRST_DATA_ROW;

    public DataBuilder(final Sheet sheet,
                       final GuidedDecisionTable52 dtable,
                       final PackageDataModelOracle dmo,
                       final ColumnContext columnContext) {
        this.sheet = PortablePreconditions.checkNotNull("sheet", sheet);
        this.dtable = PortablePreconditions.checkNotNull("dtable", dtable);
        this.columnContext = PortablePreconditions.checkNotNull("columnContext", columnContext);
        PortablePreconditions.checkNotNull("dmo", dmo);
        this.utilsWithRespectForLists = new XLSColumnUtilities(dtable,
                                                               dmo,
                                                               true);
        this.utilsWithNoRespectForLists = new XLSColumnUtilities(dtable,
                                                                 dmo,
                                                                 false);
    }

    public void build() {

        for (final List<DTCellValue52> row : dtable.getData()) {
            new DataRowBuilder(row).build();
        }
    }

    public class DataRowBuilder {

        private final Row xlsRow = sheet.createRow(rowCount);
        private final Set<String> addedInserts = new HashSet<>();
        private final List<DTCellValue52> row;

        private int sourceColumnIndex = 0;
        private int targetColumnIndex = -2;

        public GuidedDecisionTable52 getDtable() {
            return dtable;
        }

        public int getTargetColumnIndex() {
            return targetColumnIndex;
        }

        public void moveToNextTargetColumnIndex() {
            ++targetColumnIndex;
        }

        public int getSourceColumnIndex() {
            return sourceColumnIndex;
        }

        public void moveSourceColumnIndexForward() {
            sourceColumnIndex++;
        }

        public DataRowBuilder(final List<DTCellValue52> row) {
            this.row = row;
        }

        public void build() {

            for (; sourceColumnIndex < row.size(); sourceColumnIndex++) {
                final DTCellValue52 cell = row.get(sourceColumnIndex);

                if (sourceColumnIndex == GuidedDecisionTable52.RULE_DESCRIPTION_INDEX) {
                    xlsRow.createCell(targetColumnIndex)
                            .setCellValue(cell.getStringValue());
                } else if (sourceColumnIndex > GuidedDecisionTable52.RULE_DESCRIPTION_INDEX) {

                    final BaseColumn baseColumn = dtable.getExpandedColumns().get(sourceColumnIndex);

                    if (baseColumn instanceof BRLActionVariableColumn) {

                        addBRLActionVariableColumn((BRLActionVariableColumn) baseColumn);
                    } else if (baseColumn instanceof BRLConditionVariableColumn) {

                        addBRLConditionVariableColumn((BRLConditionVariableColumn) baseColumn);
                    } else {

                        if (baseColumn instanceof ActionInsertFactCol52) {

                            addActionInsertFirstColumn();
                        }

                        if (isOperator("== null") || isOperator("!= null")) {

                            addNullColumn();
                        } else if (isWorkItemColumn()) {

                            addWorkItemColumn(cell);
                        } else if (dtable.getExpandedColumns().get(sourceColumnIndex) instanceof ActionRetractFactCol52) {

                            addRetractColumn(cell);
                        } else {

                            final String value = getValue(cell,
                                                          getColumnDataType(sourceColumnIndex),
                                                          true);
                            if (value != null) {
                                xlsRow.createCell(targetColumnIndex)
                                        .setCellValue(value);
                            }
                        }
                    }
                }
                targetColumnIndex++;
            }
            rowCount++;
        }

        public void addRetractColumn(final DTCellValue52 cell) {
            if (!StringUtils.isEmpty(cell.getStringValue())) {
                xlsRow.createCell(targetColumnIndex)
                        .setCellValue(cell.getStringValue());
            }
        }

        public void addNullColumn() {
            xlsRow.createCell(targetColumnIndex)
                    .setCellValue("null");
        }

        public void addWorkItemColumn(final DTCellValue52 cell) {
            if (cell.getBooleanValue() != null
                    && cell.getBooleanValue()) {
                xlsRow.createCell(targetColumnIndex)
                        .setCellValue("X");
            }
        }

        public void addBRLConditionVariableColumn(final BRLConditionVariableColumn baseColumn) {
            final BRLConditionColumn brlColumn = dtable.getBRLColumn(baseColumn);
            BRLColumnDataBuilder.make(this,
                                      brlColumn,
                                      columnContext).build(brlColumn,
                                                           row,
                                                           xlsRow);
        }

        public void addBRLActionVariableColumn(final BRLActionVariableColumn baseColumn) {
            final BRLActionColumn brlColumn = dtable.getBRLColumn(baseColumn);
            BRLColumnDataBuilder.make(this,
                                      brlColumn,
                                      columnContext).build(brlColumn,
                                                           row,
                                                           xlsRow);
        }

        private boolean isWorkItemColumn() {
            final BaseColumn column = dtable.getExpandedColumns().get(sourceColumnIndex);
            return column instanceof ActionWorkItemCol52
                    || column instanceof ActionWorkItemSetFieldCol52;
        }

        private void addActionInsertFirstColumn() {
            final ActionInsertFactCol52 column = (ActionInsertFactCol52) dtable.getExpandedColumns().get(sourceColumnIndex);

            if (!addedInserts.contains(column.getBoundName())) {

                addedInserts.add(column.getBoundName());

                xlsRow.createCell(targetColumnIndex)
                        .setCellValue("X");

                targetColumnIndex++;
            }
        }

        public String getValue(final List<DTCellValue52> row,
                               final int sourceColumnIndex,
                               final boolean addQuotes) {
            final DTCellValue52 newCell = row.get(sourceColumnIndex);
            return getValue(newCell,
                            getColumnDataType(sourceColumnIndex),
                            addQuotes); // TODO needed?

        }
        public String getValue(final List<DTCellValue52> row,
                               final int sourceColumnIndex) {
            final DTCellValue52 newCell = row.get(sourceColumnIndex);
            return getValue(newCell,
                            getColumnDataType(sourceColumnIndex),
                            false); // TODO true or false?
        }

        public String getValue(final DTCellValue52 cell,
                               final DataType.DataTypes dataType,
                               final boolean addQuotes) {
            switch (dataType) {

                case STRING:

                    return getStringValue(cell,
                                          addQuotes);
                case NUMERIC:
                case NUMERIC_BIGDECIMAL:
                case NUMERIC_BIGINTEGER:
                case NUMERIC_BYTE:
                case NUMERIC_DOUBLE:
                case NUMERIC_FLOAT:
                case NUMERIC_INTEGER:
                case NUMERIC_LONG:
                case NUMERIC_SHORT:
                    return getNumericValue(cell);
                case DATE:
                    return getDateValue(cell,
                                        addQuotes);
                case BOOLEAN:
                    return getBooleanValue(cell);
            }
            return null;
        }

        private String getNumericValue(final DTCellValue52 cell) {
            final Number numericValue = cell.getNumericValue();
            if (numericValue != null) {
                return numericValue.toString();
            }
            return null;
        }

        private String getBooleanValue(final DTCellValue52 cell) {
            final Boolean booleanValue = cell.getBooleanValue();
            if (booleanValue != null) {

                return booleanValue.toString();
            }
            return null;
        }

        private String getDateValue(final DTCellValue52 cell,
                                    final boolean addQuotes) {
            final Date dateValue = cell.getDateValue();
            if (dateValue != null) {
                if (addQuotes) {
                    return String.format("\"%s\"", DateUtils.format(dateValue));
                } else {
                    return DateUtils.format(dateValue);
                }
            }
            return null;
        }

        private String getStringValue(final DTCellValue52 cell,
                                      final boolean addQuotes) {
            if (isTheRealCellValueString(sourceColumnIndex) && cell.getStringValue() != null) {

                if (cell.getStringValue() != null && !cell.getStringValue().isEmpty()) {
                    if (isOperator("in")) {
                        return String.format("(%s)", fixStringValue(cell));
                    } else if (addQuotes) {
                        return String.format("\"%s\"", fixStringValue(cell));
                    } else {
                        return cell.getStringValue();
                    }
                }
            } else {
                /*
                 * Values from lists of dates or enumerations are stored as Strings.
                 * For this reason we check the real data type and drop the surrounding "" if the real value was not String.
                 */
                if (Objects.equals(DataType.DataTypes.STRING, cell.getDataType())) {

                    xlsRow.createCell(targetColumnIndex)
                            .setCellValue(cell.getStringValue());
                } else {

                    return getValue(cell,
                                    cell.getDataType(),
                                    addQuotes);
                }
            }
            return null;
        }

        private boolean isOperator(final String operator) {

            if (dtable.getExpandedColumns().get(sourceColumnIndex) instanceof ConditionCol52) {

                final ConditionCol52 column = (ConditionCol52) dtable.getExpandedColumns().get(sourceColumnIndex);
                return Objects.equals(column.getOperator(), operator);
            } else {

                return false;
            }
        }

        private String fixStringValue(final DTCellValue52 cell) {
            if (cell.getStringValue().length() > 2 && cell.getStringValue().startsWith("\"") && cell.getStringValue().endsWith("\"")) {
                return cell.getStringValue().substring(1, cell.getStringValue().length() - 1);
            } else {
                return cell.getStringValue();
            }
        }

        private boolean isTheRealCellValueString(final int sourceColumnIndex) {
            return !(dtable.getExpandedColumns().get(sourceColumnIndex) instanceof AttributeCol52)
                    && !(dtable.getExpandedColumns().get(sourceColumnIndex) instanceof MetadataCol52)
                    && !isFormula(sourceColumnIndex)
                    && Objects.equals(DataType.DataTypes.STRING, utilsWithNoRespectForLists.getTypeSafeType(dtable.getExpandedColumns().get(sourceColumnIndex)));
        }

        private boolean isFormula(final int sourceColumnIndex) {

            if (dtable.getExpandedColumns().get(sourceColumnIndex) instanceof ConditionCol52) {

                final int constraintValueType = ((ConditionCol52) dtable.getExpandedColumns().get(sourceColumnIndex)).getConstraintValueType();

                if (constraintValueType == TYPE_RET_VALUE || constraintValueType == TYPE_PREDICATE) {
                    return true;
                }
            }

            return false;
        }

        public DataType.DataTypes getColumnDataType(final int columnIndex) {
            return utilsWithRespectForLists.getTypeSafeType(dtable.getExpandedColumns().get(columnIndex));
        }
    }
}
