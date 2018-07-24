/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.data;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicData;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicDataRow;

/**
 * Tests for DynamicData
 */
public abstract class BaseDynamicDataTests {

    protected DynamicData data = new DynamicData();

    protected static final List<CellValue<? extends Comparable<?>>> EMPTY_COLUMN = new ArrayList<CellValue<? extends Comparable<?>>>();

    @Before
    public void setup() {
        data.clear();

        data.addColumn( 0,
                        EMPTY_COLUMN,
                        true );
        data.addColumn( 1,
                        EMPTY_COLUMN,
                        true );
        data.addColumn( 2,
                        EMPTY_COLUMN,
                        true );

        data.addRow( makeRow() );
        data.addRow( makeRow() );
        data.addRow( makeRow() );

    }

    protected DynamicDataRow makeRow() {
        DynamicDataRow row = new DynamicDataRow();
        for ( CellValue<?> cell : makeCellValueList() ) {
            row.add( cell );
        }
        return row;
    }

    protected List<CellValue<? extends Comparable<?>>> makeCellValueList() {
        List<CellValue<? extends Comparable<?>>> row = new ArrayList<CellValue<? extends Comparable<?>>>();
        row.add( new CellValue<String>( "" ) );
        row.add( new CellValue<String>( "" ) );
        row.add( new CellValue<String>( "" ) );
        return row;
    }

    protected List<CellValue<? extends Comparable<?>>> makeCellValueList( int size ) {
        List<CellValue<? extends Comparable<?>>> row = new ArrayList<CellValue<? extends Comparable<?>>>();
        for ( int i = 0; i < size; i++ ) {
            row.add( new CellValue<String>( "" ) );
        }
        return row;
    }

}
