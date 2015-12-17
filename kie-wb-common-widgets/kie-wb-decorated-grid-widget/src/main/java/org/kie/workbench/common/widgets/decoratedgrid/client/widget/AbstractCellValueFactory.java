/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicDataRow;

/**
 * A Factory to create CellValues applicable to given columns.
 */
public abstract class AbstractCellValueFactory<C, V> {

    // Data Model Oracle to aid data-type resolution etc
    protected AsyncPackageDataModelOracle oracle;

    public AbstractCellValueFactory( AsyncPackageDataModelOracle oracle ) {
        if ( oracle == null ) {
            throw new IllegalArgumentException( "oracle cannot be null" );
        }
        this.oracle = oracle;
    }

    /**
     * Construct a new row of data for the underlying model
     * @return
     */
    public abstract List<V> makeRowData();

    /**
     * Construct a new row of data for the MergableGridWidget
     * @return
     */
    public abstract DynamicDataRow makeUIRowData();

    /**
     * Construct a new column of data for the underlying model
     * @param column
     * @return
     */
    public abstract List<V> makeColumnData( C column );

    /**
     * Convert a column of domain data to that suitable for the UI
     * @param column
     * @param columnData
     * @return
     */
    public abstract List<CellValue<? extends Comparable<?>>> convertColumnData( C column,
                                                                                List<V> columnData );

    /**
     * Make a Model cell for the given column
     * @param column
     * @return
     */
    protected abstract V makeModelCellValue( C column );

    /**
     * Convert a Model cell to one that can be used in the UI
     * @param cell
     * @return
     */
    protected abstract CellValue<? extends Comparable<?>> convertModelCellValue( C column,
                                                                                 V cell );

    /**
     * Convert a type-safe UI CellValue into a type-safe Model CellValue
     * @param column Model column from which data-type can be derived
     * @param cell UI CellValue to convert into Model CellValue
     * @return
     */
    protected abstract V convertToModelCell( C column,
                                             CellValue<?> cell );

    protected CellValue<Boolean> makeNewBooleanCellValue() {
        CellValue<Boolean> cv = new CellValue<Boolean>( Boolean.FALSE );
        return cv;
    }

    protected CellValue<Boolean> makeNewBooleanCellValue( Boolean initialValue ) {
        CellValue<Boolean> cv = makeNewBooleanCellValue();
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<Date> makeNewDateCellValue() {
        CellValue<Date> cv = new CellValue<Date>( null );
        return cv;
    }

    protected CellValue<Date> makeNewDateCellValue( Date initialValue ) {
        CellValue<Date> cv = makeNewDateCellValue();
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<String> makeNewDialectCellValue() {
        CellValue<String> cv = new CellValue<String>( "java" );
        return cv;
    }

    protected CellValue<String> makeNewDialectCellValue( String initialValue ) {
        CellValue<String> cv = makeNewDialectCellValue();
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<BigDecimal> makeNewNumericCellValue() {
        CellValue<BigDecimal> cv = new CellValue<BigDecimal>( null );
        return cv;
    }

    protected CellValue<BigDecimal> makeNewNumericCellValue( BigDecimal initialValue ) {
        CellValue<BigDecimal> cv = new CellValue<BigDecimal>( null );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<BigDecimal> makeNewBigDecimalCellValue( BigDecimal initialValue ) {
        CellValue<BigDecimal> cv = new CellValue<BigDecimal>( null );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<BigInteger> makeNewBigIntegerCellValue( BigInteger initialValue ) {
        CellValue<BigInteger> cv = new CellValue<BigInteger>( null );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<Byte> makeNewByteCellValue( Byte initialValue ) {
        CellValue<Byte> cv = new CellValue<Byte>( null );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<Double> makeNewDoubleCellValue( Double initialValue ) {
        CellValue<Double> cv = new CellValue<Double>( null );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<Float> makeNewFloatCellValue( Float initialValue ) {
        CellValue<Float> cv = new CellValue<Float>( null );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<Integer> makeNewIntegerCellValue( Integer initialValue ) {
        CellValue<Integer> cv = new CellValue<Integer>( null );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<Long> makeNewLongCellValue( Long initialValue ) {
        CellValue<Long> cv = new CellValue<Long>( null );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<Short> makeNewShortCellValue( Short initialValue ) {
        CellValue<Short> cv = new CellValue<Short>( null );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<String> makeNewStringCellValue() {
        CellValue<String> cv = new CellValue<String>( null );
        return cv;
    }

    protected CellValue<String> makeNewStringCellValue( Object initialValue ) {
        CellValue<String> cv = makeNewStringCellValue();
        if ( initialValue != null && !initialValue.equals( "" ) ) {
            cv.setValue( initialValue.toString() );
        }
        return cv;
    }

}
