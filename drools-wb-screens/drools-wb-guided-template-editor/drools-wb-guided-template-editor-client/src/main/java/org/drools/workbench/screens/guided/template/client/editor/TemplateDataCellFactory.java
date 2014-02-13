/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.template.client.editor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractCellFactory;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.DecoratedGridCellValueAdaptor;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.AbstractProxyPopupDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupDateDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericBigDecimalDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericBigIntegerDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericByteDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericDoubleDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericFloatDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericIntegerDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericLongDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericShortDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupTextDropDownEditCell;

public class TemplateDataCellFactory
        extends AbstractCellFactory<TemplateDataColumn> {

    /**
     * Construct a Cell Factory for a specific Template Data Widget
     * @param oracle DataModelOracle to assist with drop-downs
     * @param dropDownManager DropDownManager for dependent cells
     * @param isReadOnly Should cells be created for a read-only mode of operation
     * @param eventBus EventBus to which cells can send update events
     */
    public TemplateDataCellFactory( AsyncPackageDataModelOracle oracle,
                                    TemplateDropDownManager dropDownManager,
                                    boolean isReadOnly,
                                    EventBus eventBus ) {
        super( oracle,
               dropDownManager,
               isReadOnly,
               eventBus );
    }

    /**
     * Create a Cell for the given TemplateDataColumn
     * @param column The Template Data Table model column
     * @return A Cell
     */
    public DecoratedGridCellValueAdaptor<? extends Comparable<?>> getCell( TemplateDataColumn column ) {

        DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = null;

        //Check if the column has an enumeration
        final String factType = column.getFactType();
        final String fieldName = column.getFactField();
        final String dataType = column.getDataType();
        if ( oracle.hasEnums( factType,
                              fieldName ) ) {
            cell = makeSingleSelectionEnumCell( factType,
                                                fieldName,
                                                dataType );

        } else {
            if ( column.getDataType().equals( DataType.TYPE_BOOLEAN ) ) {
                cell = makeBooleanCell();
            } else if ( column.getDataType().equals( DataType.TYPE_DATE ) ) {
                cell = makeDateCell();
            } else if ( dataType.equals( DataType.TYPE_NUMERIC ) ) {
                cell = makeNumericCell();
            } else if ( dataType.equals( DataType.TYPE_NUMERIC_BIGDECIMAL ) ) {
                cell = makeNumericBigDecimalCell();
            } else if ( dataType.equals( DataType.TYPE_NUMERIC_BIGINTEGER ) ) {
                cell = makeNumericBigIntegerCell();
            } else if ( dataType.equals( DataType.TYPE_NUMERIC_BYTE ) ) {
                cell = makeNumericByteCell();
            } else if ( dataType.equals( DataType.TYPE_NUMERIC_DOUBLE ) ) {
                cell = makeNumericDoubleCell();
            } else if ( dataType.equals( DataType.TYPE_NUMERIC_FLOAT ) ) {
                cell = makeNumericFloatCell();
            } else if ( dataType.equals( DataType.TYPE_NUMERIC_INTEGER ) ) {
                cell = makeNumericIntegerCell();
            } else if ( dataType.equals( DataType.TYPE_NUMERIC_LONG ) ) {
                cell = makeNumericLongCell();
            } else if ( dataType.equals( DataType.TYPE_NUMERIC_SHORT ) ) {
                cell = makeNumericShortCell();
            } else {
                cell = makeTextCell();
            }
        }

        return cell;

    }

    //Get a cell for a Value List
    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> makeSingleSelectionEnumCell( String factType,
                                                                                                String fieldName,
                                                                                                String dataType ) {
        DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell;
        if ( dataType.equals( DataType.TYPE_NUMERIC ) ) {
            final AbstractProxyPopupDropDownEditCell<BigDecimal, BigDecimal> pudd = new ProxyPopupNumericBigDecimalDropDownEditCell( factType,
                                                                                                                                     fieldName,
                                                                                                                                     oracle,
                                                                                                                                     dropDownManager,
                                                                                                                                     isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<BigDecimal>( pudd,
                                                                  eventBus );

        } else if ( dataType.equals( DataType.TYPE_NUMERIC_BIGDECIMAL ) ) {
            final AbstractProxyPopupDropDownEditCell<BigDecimal, BigDecimal> pudd = new ProxyPopupNumericBigDecimalDropDownEditCell( factType,
                                                                                                                                     fieldName,
                                                                                                                                     oracle,
                                                                                                                                     dropDownManager,
                                                                                                                                     isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<BigDecimal>( pudd,
                                                                  eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_BIGINTEGER ) ) {
            final AbstractProxyPopupDropDownEditCell<BigInteger, BigInteger> pudd = new ProxyPopupNumericBigIntegerDropDownEditCell( factType,
                                                                                                                                     fieldName,
                                                                                                                                     oracle,
                                                                                                                                     dropDownManager,
                                                                                                                                     isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<BigInteger>( pudd,
                                                                  eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_BYTE ) ) {
            final AbstractProxyPopupDropDownEditCell<Byte, Byte> pudd = new ProxyPopupNumericByteDropDownEditCell( factType,
                                                                                                                   fieldName,
                                                                                                                   oracle,
                                                                                                                   dropDownManager,
                                                                                                                   isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<Byte>( pudd,
                                                            eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_DOUBLE ) ) {
            final AbstractProxyPopupDropDownEditCell<Double, Double> pudd = new ProxyPopupNumericDoubleDropDownEditCell( factType,
                                                                                                                         fieldName,
                                                                                                                         oracle,
                                                                                                                         dropDownManager,
                                                                                                                         isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<Double>( pudd,
                                                              eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_FLOAT ) ) {
            final AbstractProxyPopupDropDownEditCell<Float, Float> pudd = new ProxyPopupNumericFloatDropDownEditCell( factType,
                                                                                                                      fieldName,
                                                                                                                      oracle,
                                                                                                                      dropDownManager,
                                                                                                                      isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<Float>( pudd,
                                                             eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_INTEGER ) ) {
            final AbstractProxyPopupDropDownEditCell<Integer, Integer> pudd = new ProxyPopupNumericIntegerDropDownEditCell( factType,
                                                                                                                            fieldName,
                                                                                                                            oracle,
                                                                                                                            dropDownManager,
                                                                                                                            isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<Integer>( pudd,
                                                               eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_LONG ) ) {
            final AbstractProxyPopupDropDownEditCell<Long, Long> pudd = new ProxyPopupNumericLongDropDownEditCell( factType,
                                                                                                                   fieldName,
                                                                                                                   oracle,
                                                                                                                   dropDownManager,
                                                                                                                   isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<Long>( pudd,
                                                            eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_SHORT ) ) {
            final AbstractProxyPopupDropDownEditCell<Short, Short> pudd = new ProxyPopupNumericShortDropDownEditCell( factType,
                                                                                                                      fieldName,
                                                                                                                      oracle,
                                                                                                                      dropDownManager,
                                                                                                                      isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<Short>( pudd,
                                                             eventBus );
        } else if ( dataType.equals( DataType.TYPE_BOOLEAN ) ) {
            cell = makeBooleanCell();
        } else if ( dataType.equals( DataType.TYPE_DATE ) ) {
            final AbstractProxyPopupDropDownEditCell<Date, Date> pudd = new ProxyPopupDateDropDownEditCell( factType,
                                                                                                            fieldName,
                                                                                                            oracle,
                                                                                                            dropDownManager,
                                                                                                            isReadOnly,
                                                                                                            DATE_FORMAT );
            cell = new DecoratedGridCellValueAdaptor<Date>( pudd,
                                                            eventBus );
        } else {
            final AbstractProxyPopupDropDownEditCell<String, String> pudd = new ProxyPopupTextDropDownEditCell( factType,
                                                                                                                fieldName,
                                                                                                                oracle,
                                                                                                                dropDownManager,
                                                                                                                isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<String>( pudd,
                                                              eventBus );
        }

        return cell;
    }

}
