/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.Analysis;
import org.drools.workbench.models.guided.dtable.shared.model.AnalysisCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.utils.DTCellValueUtilities;
import org.drools.workbench.screens.guided.rule.client.editor.RuleAttributeWidget;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractCellValueFactory;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue.CellState;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicDataRow;

/**
 * A Factory to create CellValues applicable to given columns.
 */
public class DecisionTableCellValueFactory extends AbstractCellValueFactory<BaseColumn, DTCellValue52> {

    // Model used to determine data-types etc for cells
    private final GuidedDecisionTable52 model;

    private final DTCellValueUtilities utilities;

    /**
     * Construct a Cell Value Factory for a specific Decision Table
     * @param oracle DataModelOracle to assist with drop-downs
     */
    public DecisionTableCellValueFactory( GuidedDecisionTable52 model,
                                          AsyncPackageDataModelOracle oracle ) {
        super( oracle );
        this.model = model;
        this.utilities = new DTCellValueUtilities( model,
                                                   oracle );
    }

    /**
     * Construct a new row of data for the underlying model
     * @return
     */
    public List<DTCellValue52> makeRowData() {
        List<DTCellValue52> data = new ArrayList<DTCellValue52>();
        List<BaseColumn> columns = model.getExpandedColumns();
        //Use allColumns.size() - 1 to exclude the Analysis column that is not stored in the general grid data
        for ( int iCol = 0; iCol < columns.size() - 1; iCol++ ) {
            BaseColumn column = columns.get( iCol );
            DTCellValue52 cell = makeModelCellValue( column );
            data.add( cell );
        }
        return data;
    }

    /**
     * Construct a new row of data for the MergableGridWidget
     * @return
     */
    @Override
    public DynamicDataRow makeUIRowData() {
        DynamicDataRow data = new DynamicDataRow();
        List<BaseColumn> columns = model.getExpandedColumns();
        for ( BaseColumn column : columns ) {
            DTCellValue52 dcv = makeModelCellValue( column );
            DataType.DataTypes dataType = utilities.getDataType( column );
            utilities.assertDTCellValue( dataType,
                                         dcv );
            CellValue<? extends Comparable<?>> cell = convertModelCellValue( column,
                                                                             dcv );
            data.add( cell );
        }

        return data;
    }

    /**
     * Construct a new column of data for the underlying model
     * @return
     */
    public List<DTCellValue52> makeColumnData( BaseColumn column ) {
        List<DTCellValue52> data = new ArrayList<DTCellValue52>();
        for ( int iRow = 0; iRow < model.getData().size(); iRow++ ) {
            DTCellValue52 cell = makeModelCellValue( column );
            data.add( cell );
        }
        return data;
    }

    /**
     * Convert a column of domain data to that suitable for the UI
     * @param column
     * @param columnData
     * @return
     */
    public List<CellValue<? extends Comparable<?>>> convertColumnData( BaseColumn column,
                                                                       List<DTCellValue52> columnData ) {
        List<CellValue<? extends Comparable<?>>> data = new ArrayList<CellValue<? extends Comparable<?>>>();
        for ( int iRow = 0; iRow < model.getData().size(); iRow++ ) {
            DTCellValue52 dcv = columnData.get( iRow );
            CellValue<? extends Comparable<?>> cell = convertModelCellValue( column,
                                                                             dcv );
            data.add( cell );
        }
        return data;
    }

    /**
     * Make a Model cell for the given column
     * @param column
     * @return
     */
    @Override
    public DTCellValue52 makeModelCellValue( BaseColumn column ) {
        DataType.DataTypes dataType = utilities.getDataType( column );
        DTCellValue52 dcv = null;
        if ( column instanceof LimitedEntryCol ) {
            dcv = new DTCellValue52( Boolean.FALSE );
        } else {
            dcv = new DTCellValue52( column.getDefaultValue() );
        }
        utilities.assertDTCellValue( dataType,
                                     dcv );
        return dcv;
    }

    /**
     * Convert a Model cell to one that can be used in the UI
     * @param dcv
     * @return
     */
    @Override
    public CellValue<? extends Comparable<?>> convertModelCellValue( BaseColumn column,
                                                                     DTCellValue52 dcv ) {

        //Analysis cells do not use data-type
        if ( column instanceof AnalysisCol52 ) {
            return makeNewAnalysisCellValue();
        }

        //Other cells do use data-type
        DataType.DataTypes dataType = utilities.getDataType( column );
        utilities.assertDTCellValue( dataType,
                                     dcv );

        CellValue<? extends Comparable<?>> cell = null;
        switch ( dataType ) {
            case BOOLEAN:
                cell = makeNewBooleanCellValue( dcv.getBooleanValue() );
                break;
            case DATE:
                cell = makeNewDateCellValue( dcv.getDateValue() );
                break;
            case NUMERIC:
                cell = makeNewNumericCellValue( (BigDecimal) dcv.getNumericValue() );
                break;
            case NUMERIC_BIGDECIMAL:
                cell = makeNewBigDecimalCellValue( (BigDecimal) dcv.getNumericValue() );
                break;
            case NUMERIC_BIGINTEGER:
                cell = makeNewBigIntegerCellValue( (BigInteger) dcv.getNumericValue() );
                break;
            case NUMERIC_BYTE:
                cell = makeNewByteCellValue( (Byte) dcv.getNumericValue() );
                break;
            case NUMERIC_DOUBLE:
                cell = makeNewDoubleCellValue( (Double) dcv.getNumericValue() );
                break;
            case NUMERIC_FLOAT:
                cell = makeNewFloatCellValue( (Float) dcv.getNumericValue() );
                break;
            case NUMERIC_INTEGER:
                cell = makeNewIntegerCellValue( (Integer) dcv.getNumericValue() );
                break;
            case NUMERIC_LONG:
                if ( column instanceof RowNumberCol52 ) {
                    cell = makeNewRowNumberCellValue( (Long) dcv.getNumericValue() );
                } else {
                    cell = makeNewLongCellValue( (Long) dcv.getNumericValue() );
                    if ( column instanceof AttributeCol52 ) {
                        AttributeCol52 at = (AttributeCol52) column;
                        if ( at.getAttribute().equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                            if ( at.isUseRowNumber() ) {
                                cell = makeNewRowNumberCellValue( (Long) dcv.getNumericValue() );
                            }
                        }
                    }
                }
                break;
            case NUMERIC_SHORT:
                cell = makeNewShortCellValue( (Short) dcv.getNumericValue() );
                break;
            default:
                cell = makeNewStringCellValue( dcv.getStringValue() );
                if ( column instanceof AttributeCol52 ) {
                    AttributeCol52 ac = (AttributeCol52) column;
                    if ( ac.getAttribute().equals( RuleAttributeWidget.DIALECT_ATTR ) ) {
                        cell = makeNewDialectCellValue( dcv.getStringValue() );
                    }
                }
        }

        if ( dcv.isOtherwise() ) {
            cell.addState( CellState.OTHERWISE );
        }

        return cell;
    }

    public CellValue<Long> makeNewRowNumberCellValue( Long initialValue ) {
        // Rows are 0-based internally but 1-based in the UI
        CellValue<Long> cv = makeNewLongCellValue( initialValue );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    public CellValue<Analysis> makeNewAnalysisCellValue() {
        Analysis analysis = new Analysis();
        return new CellValue<Analysis>( analysis );
    }

    /**
     * Convert a type-safe UI CellValue into a type-safe Model CellValue
     * @param column Model column from which data-type can be derived
     * @param cell UI CellValue to convert into Model CellValue
     * @return
     */
    public DTCellValue52 convertToModelCell( BaseColumn column,
                                             CellValue<?> cell ) {
        DataType.DataTypes dt = utilities.getDataType( column );
        DTCellValue52 dtCell = null;

        switch ( dt ) {
            case BOOLEAN:
                dtCell = new DTCellValue52( (Boolean) cell.getValue() );
                break;
            case DATE:
                dtCell = new DTCellValue52( (Date) cell.getValue() );
                break;
            case NUMERIC:
                dtCell = new DTCellValue52( (BigDecimal) cell.getValue() );
                break;
            case NUMERIC_BIGDECIMAL:
                dtCell = new DTCellValue52( (BigDecimal) cell.getValue() );
                break;
            case NUMERIC_BIGINTEGER:
                dtCell = new DTCellValue52( (BigInteger) cell.getValue() );
                break;
            case NUMERIC_BYTE:
                dtCell = new DTCellValue52( (Byte) cell.getValue() );
                break;
            case NUMERIC_DOUBLE:
                dtCell = new DTCellValue52( (Double) cell.getValue() );
                break;
            case NUMERIC_FLOAT:
                dtCell = new DTCellValue52( (Float) cell.getValue() );
                break;
            case NUMERIC_INTEGER:
                dtCell = new DTCellValue52( (Integer) cell.getValue() );
                break;
            case NUMERIC_LONG:
                dtCell = new DTCellValue52( (Long) cell.getValue() );
                break;
            case NUMERIC_SHORT:
                dtCell = new DTCellValue52( (Short) cell.getValue() );
                break;
            default:
                dtCell = new DTCellValue52( (String) cell.getValue() );
        }
        dtCell.setOtherwise( cell.isOtherwise() );
        return dtCell;
    }

}
