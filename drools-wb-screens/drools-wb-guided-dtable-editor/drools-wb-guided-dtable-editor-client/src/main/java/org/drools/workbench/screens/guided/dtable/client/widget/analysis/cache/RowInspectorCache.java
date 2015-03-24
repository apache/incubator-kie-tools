/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspectorGenerator;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.UpdateHandler;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;

public class RowInspectorCache {

    private static final int DESCRIPTION_COLUMN = 1;

    private final RowInspectorList rowInspectorList = new RowInspectorList();
    private final Conditions conditions = new Conditions();
    private final RowInspectorGenerator rowInspectorGenerator;
    private final UpdateHandler updateHandler;

    public RowInspectorCache( AsyncPackageDataModelOracle oracle,
                              GuidedDecisionTable52 model,
                              UpdateHandler updateHandler ) {

        rowInspectorGenerator = new RowInspectorGenerator( oracle,
                                                           model,
                                                           this );
        this.updateHandler = updateHandler;

        reset();
    }

    public void reset() {
        rowInspectorList.clear();
        conditions.clear();
        for ( RowInspector rowInspector : rowInspectorGenerator.generate() ) {
            add( rowInspector );
        }
    }

    public Collection<RowInspector> all() {
        return rowInspectorList.getSortedList();
    }

    public Conditions getConditions() {
        return conditions;
    }

    public void updateRowInspectors( Set<Coordinate> coordinates,
                                     List<List<DTCellValue52>> data ) {
        for ( Coordinate coordinate : coordinates ) {
            if ( coordinate.getCol() != DESCRIPTION_COLUMN ) {
                RowInspector oldRow = rowInspectorList.getRowInspector( coordinate.getRow() );
                List<DTCellValue52> row = data.get( coordinate.getRow() );
                RowInspector rowInspector = rowInspectorGenerator.generate( coordinate.getRow(), row );

                updateHandler.updateRow( oldRow, rowInspector );
                rowInspectorList.set( coordinate.getRow(), rowInspector );
            }
        }

    }

    private boolean add( RowInspector rowInspector ) {
        boolean add = rowInspectorList.add( rowInspector );
        conditions.addAll( rowInspector.getConditions().allValues() );
        return add;
    }

    public RowInspector removeRow( int rowNumber ) {
        RowInspector removed = rowInspectorList.removeRowInspector( rowNumber );
        rowInspectorList.decreaseRowNumbers( rowNumber );
        return removed;
    }

    public RowInspector addRow( int index,
                                List<DTCellValue52> row ) {
        RowInspector rowInspector = rowInspectorGenerator.generate( index, row );
        rowInspectorList.increaseRowNumbers( index );
        add( rowInspector );
        return rowInspector;
    }

    public interface Selector {

        boolean accept( RowInspector rowInspector );

    }
}