/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.impl;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiCell;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.kie.soup.project.datamodel.oracle.DataType;

public class GridWidgetCellFactoryImpl implements GridWidgetCellFactory {

    @Override
    public GuidedDecisionTableUiCell convertCell(final DTCellValue52 cell,
                                                 final BaseColumn column,
                                                 final CellUtilities cellUtilities,
                                                 final ColumnUtilities columnUtilities) {
        //If the underlying modelCell does not have a value don't create a UiModelCell. The
        // underlying model data is fully populated whereas the uiModel is sparsely populated.
        if (!cell.hasValue()) {
            return null;
        }

        final DataType.DataTypes dataType = columnUtilities.getDataType(column);
        cellUtilities.convertDTCellValueType(dataType,
                                             cell);

        switch (dataType) {
            case NUMERIC:
                return new GuidedDecisionTableUiCell<>((Number) cellUtilities.convertToBigDecimal(cell),
                                                       cell.isOtherwise());
            case NUMERIC_BIGDECIMAL:
                return new GuidedDecisionTableUiCell<>(cellUtilities.convertToBigDecimal(cell),
                                                       cell.isOtherwise());
            case NUMERIC_BIGINTEGER:
                return new GuidedDecisionTableUiCell<>(cellUtilities.convertToBigInteger(cell),
                                                       cell.isOtherwise());
            case NUMERIC_BYTE:
                return new GuidedDecisionTableUiCell<>(cellUtilities.convertToByte(cell),
                                                       cell.isOtherwise());
            case NUMERIC_DOUBLE:
                return new GuidedDecisionTableUiCell<>(cellUtilities.convertToDouble(cell),
                                                       cell.isOtherwise());
            case NUMERIC_FLOAT:
                return new GuidedDecisionTableUiCell<>(cellUtilities.convertToFloat(cell),
                                                       cell.isOtherwise());
            case NUMERIC_INTEGER:
                return new GuidedDecisionTableUiCell<>(cellUtilities.convertToInteger(cell),
                                                       cell.isOtherwise());
            case NUMERIC_LONG:
                return new GuidedDecisionTableUiCell<>(cellUtilities.convertToLong(cell),
                                                       cell.isOtherwise());
            case NUMERIC_SHORT:
                return new GuidedDecisionTableUiCell<>(cellUtilities.convertToShort(cell),
                                                       cell.isOtherwise());
            case DATE:
                return new GuidedDecisionTableUiCell<>(cellUtilities.convertToDate(cell),
                                                       cell.isOtherwise());
            case BOOLEAN:
                return new GuidedDecisionTableUiCell<>(cellUtilities.convertToBoolean(cell),
                                                       cell.isOtherwise());
            default:
                return new GuidedDecisionTableUiCell<>(cellUtilities.convertToString(cell),
                                                       cell.isOtherwise());
        }
    }
}
