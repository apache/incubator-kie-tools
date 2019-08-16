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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.kie.soup.commons.validation.PortablePreconditions;

public class SubHeaderBuilder {

    private static final int COLUMN_TYPE_ROW = 5;
    private static final int FIELD_ROW = 7;
    private static final int HEADER_TITLE_ROW = 8;

    private final GuidedDecisionTable52 dtable;
    private final String ACTION = "ACTION";
    private final String CONDITION = "CONDITION";

    private int columnIndex = 0;
    private Row headerRow;
    private Row fieldRow;
    private Row headerTitleRow;
    private List<String> addedInserts = new ArrayList<>();

    public SubHeaderBuilder(final Sheet sheet,
                            final GuidedDecisionTable52 dtable) {
        PortablePreconditions.checkNotNull("sheet", sheet);
        this.dtable = PortablePreconditions.checkNotNull("dtable", dtable);

        this.headerRow = sheet.createRow(COLUMN_TYPE_ROW);
        this.fieldRow = sheet.createRow(FIELD_ROW);
        this.headerTitleRow = sheet.createRow(HEADER_TITLE_ROW);
    }

    public void build() {

        final List<BaseColumn> expandedColumns = dtable.getExpandedColumns();

        for (int sourceIndex = 0; sourceIndex < expandedColumns.size(); sourceIndex++) {

            final BaseColumn baseColumn = expandedColumns.get(sourceIndex);

            if (baseColumn instanceof AttributeCol52) {
                makeAttribute((AttributeCol52) baseColumn);
            } else if (baseColumn instanceof MetadataCol52) {
                // TODO
            } else if (baseColumn instanceof ConditionCol52) {
                makeCondition((ConditionCol52) baseColumn);
            } else if (baseColumn instanceof ActionCol52) {
                makeAction(baseColumn);
            } else if (baseColumn instanceof RowNumberCol52) {
                // Ignore and do not add to count
                continue;
            } else if (baseColumn instanceof DescriptionCol52) {
                // This is actually a column, but header is not written down in XLS
            } else {
                throw new IllegalArgumentException("TODO REMOTE THIS");
            }
            columnIndex++;
        }
    }

    private void makeAction(final BaseColumn baseColumn) {

        if (baseColumn instanceof ActionSetFieldCol52) {
            makeSetField((ActionSetFieldCol52) baseColumn);
        } else if (baseColumn instanceof ActionInsertFactCol52) {
            makeInsert((ActionInsertFactCol52) baseColumn);
        } else if (baseColumn instanceof ActionRetractFactCol52) {
            makeRetract((ActionRetractFactCol52) baseColumn);
        }
    }

    private void makeHeaderAndTitle(final String action,
                                    final String header) {
        headerRow.createCell(columnIndex)
                .setCellValue(action);

        headerTitleRow.createCell(columnIndex)
                .setCellValue(header);
    }

    private void makeRetract(final ActionRetractFactCol52 column) {

        makeHeaderAndTitle(ACTION,
                           column.getHeader());
        fieldRow.createCell(columnIndex).setCellValue("retract( $param );");
    }

    private void makeInsert(final ActionInsertFactCol52 column) {

        if (!addedInserts.contains(column.getBoundName())) {

            makeHeaderAndTitle(ACTION,
                               "");

            fieldRow.createCell(columnIndex).setCellValue(column.getFactType() + " " + column.getBoundName() + " = new " + column.getFactType() + "(); insert( " + column.getBoundName() + " );");

            addedInserts.add(column.getBoundName());
            columnIndex++;
        }

        makeHeaderAndTitle(ACTION,
                           column.getHeader());

        fieldRow.createCell(columnIndex).setCellValue(makeSetMethod(column.getBoundName(), column.getFactField()));
    }

    private void makeSetField(final ActionSetFieldCol52 column) {

        makeHeaderAndTitle(ACTION,
                           column.getHeader());
        fieldRow.createCell(columnIndex).setCellValue(makeSetMethod(column.getBoundName(), column.getFactField()));
    }

    private void makeCondition(final ConditionCol52 column) {

        makeHeaderAndTitle(CONDITION,
                           column.getHeader());

        fieldRow.createCell(columnIndex).setCellValue(String.format("%s %s $param", column.getFactField(), column.getOperator()));
    }

    private void makeAttribute(final AttributeCol52 column) {
        if (Objects.equals("negate", column.getAttribute().toLowerCase())) {
            throw new UnsupportedOperationException("Conversion of the negate attribute is not supported.");
        } else {
            headerRow.createCell(columnIndex)
                    .setCellValue(getAttribute(column));
        }
    }

    private String getAttribute(final AttributeCol52 column) {
        if (Objects.equals("salience", column.getAttribute())) {
            return "PRIORITY";
        } else {
            return column.getAttribute().toUpperCase();
        }
    }

    private String makeSetMethod(final String boundName,
                                 final String factField) {
        return String.format("%s.set%s%s( $param );", boundName, factField.substring(0, 1).toUpperCase(), factField.substring(1));
    }
}
