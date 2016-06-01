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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiCell;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;

public class GridWidgetCellFactoryImpl implements GridWidgetCellFactory {

    @Override
    public GuidedDecisionTableUiCell convertCell( final DTCellValue52 cell,
                                                  final BaseColumn column,
                                                  final CellUtilities cellUtilities,
                                                  final ColumnUtilities columnUtilities ) {
        //If the underlying modelCell does not have a value don't create a UiModelCell. The
        // underlying model data is fully populated whereas the uiModel is sparsely populated.
        if ( !cell.hasValue() ) {
            return null;
        }

        final DataType.DataTypes dataType = columnUtilities.getDataType( column );
        cellUtilities.convertDTCellValueType( dataType,
                                              cell );

        switch ( dataType ) {
            case NUMERIC:
                return new GuidedDecisionTableUiCell<Number>( cell.getNumericValue(),
                                                              cell.isOtherwise() );
            case NUMERIC_BIGDECIMAL:
                return new GuidedDecisionTableUiCell<BigDecimal>( (BigDecimal) cell.getNumericValue(),
                                                                  cell.isOtherwise() );
            case NUMERIC_BIGINTEGER:
                return new GuidedDecisionTableUiCell<BigInteger>( (BigInteger) cell.getNumericValue(),
                                                                  cell.isOtherwise() );
            case NUMERIC_BYTE:
                return new GuidedDecisionTableUiCell<Byte>( (Byte) cell.getNumericValue(),
                                                            cell.isOtherwise() );
            case NUMERIC_DOUBLE:
                return new GuidedDecisionTableUiCell<Double>( (Double) cell.getNumericValue(),
                                                              cell.isOtherwise() );
            case NUMERIC_FLOAT:
                return new GuidedDecisionTableUiCell<Float>( (Float) cell.getNumericValue(),
                                                             cell.isOtherwise() );
            case NUMERIC_INTEGER:
                return new GuidedDecisionTableUiCell<Integer>( (Integer) cell.getNumericValue(),
                                                               cell.isOtherwise() );
            case NUMERIC_LONG:
                return new GuidedDecisionTableUiCell<Long>( (Long) cell.getNumericValue(),
                                                            cell.isOtherwise() );
            case NUMERIC_SHORT:
                return new GuidedDecisionTableUiCell<Short>( (Short) cell.getNumericValue(),
                                                             cell.isOtherwise() );
            case DATE:
                return new GuidedDecisionTableUiCell<Date>( cell.getDateValue(),
                                                            cell.isOtherwise() );
            case BOOLEAN:
                return new GuidedDecisionTableUiCell<Boolean>( cell.getBooleanValue(),
                                                               cell.isOtherwise() );
            default:
                return new GuidedDecisionTableUiCell<String>( cell.getStringValue(),
                                                              cell.isOtherwise() );
        }
    }

}
