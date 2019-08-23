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
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.util.ColumnUtilitiesBase;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;

public class DataBuilder {

    private final static int FIRST_DATA_ROW = 9;
    private final static String DROOLS_DATE_TIME_FORMAT_KEY = "drools.dateformat";
    private final static String DEFAULT_DATE_TIME_FORMAT = "dd-MMM-yyyy";

    private final Sheet sheet;
    private final GuidedDecisionTable52 dtable;
    private final ColumnUtilitiesBase utilsWithRespectForLists;
    private final ColumnUtilitiesBase utilsWithNoRespectForLists;
    private int rowCount = FIRST_DATA_ROW;

    public DataBuilder(final Sheet sheet,
                       final GuidedDecisionTable52 dtable,
                       final PackageDataModelOracle dmo) {
        this.sheet = PortablePreconditions.checkNotNull("sheet", sheet);
        this.dtable = PortablePreconditions.checkNotNull("dtable", dtable);
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

    class DataRowBuilder {

        private final Row xlsRow = sheet.createRow(rowCount);
        private final Set<String> addedInserts = new HashSet<>();
        private final List<DTCellValue52> row;
        private int sourceColumnIndex = 0;
        private int targetColumnIndex = -1;

        public DataRowBuilder(List<DTCellValue52> row) {
            this.row = row;
        }

        public void build() {

            for (final DTCellValue52 cell : row) {

                if (sourceColumnIndex == 1) {
                    xlsRow.createCell(targetColumnIndex)
                            .setCellValue(cell.getStringValue());
                } else if (sourceColumnIndex > 1) {

                    if (dtable.getExpandedColumns().get(sourceColumnIndex) instanceof ActionInsertFactCol52) {
                        final ActionInsertFactCol52 column = (ActionInsertFactCol52) dtable.getExpandedColumns().get(sourceColumnIndex);

                        if (!addedInserts.contains(column.getBoundName())) {

                            addedInserts.add(column.getBoundName());

                            xlsRow.createCell(targetColumnIndex)
                                    .setCellValue("X");

                            targetColumnIndex++;
                        }
                    }

                    if (dtable.getExpandedColumns().get(sourceColumnIndex) instanceof ActionRetractFactCol52) {
                        if (cell.getStringValue() != null && !cell.getStringValue().trim().isEmpty()) {
                            xlsRow.createCell(targetColumnIndex)
                                    .setCellValue(cell.getStringValue());
                        }
                    } else {

                        final DataType.DataTypes dataType = getColumnDataType(sourceColumnIndex);

                        setValue(cell, dataType);
                    }
                }
                sourceColumnIndex++;
                targetColumnIndex++;
            }
            rowCount++;
        }

        private void setValue(final DTCellValue52 cell,
                              final DataType.DataTypes dataType) {
            switch (dataType) {

                case STRING:

                    setStringValue(cell);
                    break;
                case NUMERIC:
                case NUMERIC_BIGDECIMAL:
                case NUMERIC_BIGINTEGER:
                case NUMERIC_BYTE:
                case NUMERIC_DOUBLE:
                case NUMERIC_FLOAT:
                case NUMERIC_INTEGER:
                case NUMERIC_LONG:
                case NUMERIC_SHORT:
                    setNumericValue(cell);
                    break;
                case DATE:
                    setDateValue(cell);
                    break;
                case BOOLEAN:
                    setBooleanValue(cell);
                    break;
            }
        }

        private void setNumericValue(final DTCellValue52 cell) {
            final Number numericValue = cell.getNumericValue();
            if (numericValue != null) {
                xlsRow.createCell(targetColumnIndex)
                        .setCellValue(numericValue.toString());
            }
        }

        private void setBooleanValue(final DTCellValue52 cell) {
            final Boolean booleanValue = cell.getBooleanValue();
            if (booleanValue != null) {
                xlsRow.createCell(targetColumnIndex)
                        .setCellValue(booleanValue.toString());
            }
        }

        private void setDateValue(final DTCellValue52 cell) {
            final Date dateValue = cell.getDateValue();
            if (dateValue != null) {

                xlsRow.createCell(targetColumnIndex)
                        .setCellValue(String.format("\"%s\"", DateUtils.format(dateValue)));
            }
        }

        private void setStringValue(final DTCellValue52 cell) {
            if (isTheRealCellValueString(sourceColumnIndex) && cell.getStringValue() != null) {

                if (cell.getStringValue() != null && !cell.getStringValue().isEmpty()) {

                    xlsRow.createCell(targetColumnIndex)
                            .setCellValue(String.format("\"%s\"", fixStringValue(cell)));
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

                    setValue(cell,
                             cell.getDataType());
                }
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
                    && Objects.equals(DataType.DataTypes.STRING, utilsWithNoRespectForLists.getTypeSafeType(dtable.getExpandedColumns().get(sourceColumnIndex)));
        }

        private DataType.DataTypes getColumnDataType(final int columnIndex) {
            return utilsWithRespectForLists.getTypeSafeType(dtable.getExpandedColumns().get(columnIndex));
        }
    }
}
