/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.testutil.AnalyzerProvider;
import org.junit.Before;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;

public abstract class AnalyzerUpdateTestBase {

    protected AnalyzerProvider      analyzerProvider;
    protected GuidedDecisionTable52 table52;
    protected DecisionTableAnalyzer analyzer;

    @GwtMock
    AnalysisConstants analysisConstants;

    @GwtMock
    DateTimeFormat dateTimeFormat;

    @Before
    public void setUp() throws Exception {
        analyzerProvider = new AnalyzerProvider();
    }

    protected void fireUpAnalyzer() {
        if ( analyzer == null ) {
            analyzer = analyzerProvider.makeAnalyser( table52 );
        }
        analyzer.analyze( Collections.emptyList() );
    }

    protected void removeRow( final int rowIndex ) {
        table52.getData().remove( rowIndex );

        final DeleteRowEvent event = new DeleteRowEvent( rowIndex );
        analyzer.deleteRow( event.getIndex() );
        analyzer.updateColumns( table52.getData().size() );
    }

    protected void removeActionColumn( final int columnDataIndex,
                                       final int columnActionIndex ) {
        table52.getActionCols().remove( columnActionIndex );
        for ( final List<DTCellValue52> row : table52.getData() ) {
            row.remove( columnDataIndex );
        }

        analyzer.deleteColumns( columnDataIndex, 1 );
    }

    public ValueSetter setCoordinate() {
        return new ValueSetter();
    }

    public class ValueSetter {

        public ColumnValueSetter row( final int row ) {
            return new ColumnValueSetter( row );
        }

        public class ColumnValueSetter {
            private int row;

            public ColumnValueSetter( final int row ) {
                this.row = row;
            }

            public CellValueSetter column( final int column ) {
                return new CellValueSetter( column );
            }

            public class CellValueSetter {
                private int column;

                public CellValueSetter( final int column ) {
                    this.column = column;
                }

                public void toValue( final String value ) {
                    setValue( row, column, value );
                }

                public void toValue( final Number value ) {
                    setValue( row, column, value );
                }
            }
        }
    }

    protected void setValue( final int rowIndex,
                             final int columnIndex,
                             final Number value ) {
        table52.getData().get( rowIndex ).get( columnIndex ).setNumericValue( value );
        analyzer.analyze( getUpdates( rowIndex, columnIndex ) );
    }

    protected void setValue( final int rowIndex,
                             final int columnIndex,
                             final String value ) {
        table52.getData().get( rowIndex ).get( columnIndex ).setStringValue( value );
        analyzer.analyze( getUpdates( rowIndex, columnIndex ) );
    }

    protected void appendColumn( final int columnNumber,
                                 final ActionSetFieldCol52 actionSetField,
                                 final Comparable... cellValues ) {
        table52.getActionCols().add( actionSetField );

        for ( int i = 0; i < cellValues.length; i++ ) {
            table52.getData().get( i ).add( new DTCellValue52( cellValues[i] ) );
        }

        analyzer.insertColumn( columnNumber );
    }

    protected void insertRow( final int rowNumber ) {
        table52.getData().add( rowNumber, new ArrayList<>() );
        final InsertRowEvent event = new InsertRowEvent( rowNumber );
        analyzer.insertRow( event.getIndex() );
        analyzer.updateColumns( table52.getData().size() );
    }

    protected void appendRow( final DataType.DataTypes... dataTypes) {


        final ArrayList<DTCellValue52> row = new ArrayList<>();

        // Row number
        row.add( new DTCellValue52() );
        // Explanation
        row.add( new DTCellValue52() );

        for ( final DataType.DataTypes dataType : dataTypes ) {
            row.add( new DTCellValue52( dataType,
                                        true ) );
        }

        table52.getData().add( row );
        analyzer.appendRow();
        analyzer.updateColumns( table52.getData().size() );
    }

    protected ArrayList<Coordinate> getUpdates( final int x,
                                                final int y ) {
        final ArrayList<Coordinate> updates = new ArrayList<>();
        updates.add( new Coordinate( x, y ) );
        return updates;
    }

}
