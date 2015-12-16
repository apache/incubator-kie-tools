/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
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

    private static final int ROW_NUMBER_COLUMN = 0;
    private static final int DESCRIPTION_COLUMN = 1;

    // rowInspectorList's entries' index correspond to the rowIndex to which they relate. This removed the
    // need to have a separate RowInspectorList class that sorted RowInspectors "on demand" every time a
    // list of (ordered) RowInspectors was required.. which hit performance really badly.
    private final ArrayList<RowInspector> rowInspectorList = new ArrayList<RowInspector>();
    private final RowInspectorGenerator rowInspectorGenerator;

    private final Conditions conditions = new Conditions();
    private final UpdateHandler updateHandler;

    public RowInspectorCache( final AsyncPackageDataModelOracle oracle,
                              final GuidedDecisionTable52 model,
                              final UpdateHandler updateHandler ) {
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
        return rowInspectorList;
    }

    public Collection<RowInspector> all( final Filter filter ) {
        ArrayList<RowInspector> result = new ArrayList<RowInspector>();
        for ( RowInspector rowInspector : all() ) {
            if ( filter.accept( rowInspector ) ) {
                result.add( rowInspector );
            }
        }
        return result;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public void updateRowInspectors( final Set<Coordinate> coordinates,
                                     final List<List<DTCellValue52>> data ) {
        for ( Coordinate coordinate : coordinates ) {
            if ( coordinate.getCol() != ROW_NUMBER_COLUMN && coordinate.getCol() != DESCRIPTION_COLUMN ) {
                final int rowIndex = coordinate.getRow();
                List<DTCellValue52> row = data.get( rowIndex );
                RowInspector oldRowInspector = rowInspectorList.get( rowIndex );
                RowInspector newRowInspector = rowInspectorGenerator.generate( rowIndex,
                                                                               row );

                rowInspectorList.set( rowIndex,
                                      newRowInspector );
                updateHandler.updateRow( oldRowInspector,
                                         newRowInspector );
                indexRowInspectors();
            }
        }
    }

    private boolean add( final RowInspector rowInspector ) {
        boolean add = rowInspectorList.add( rowInspector );
        conditions.addAll( rowInspector.getConditions().allValues() );
        indexRowInspectors();
        return add;
    }

    public RowInspector removeRow( final int rowNumber ) {
        RowInspector removed = rowInspectorList.remove( rowNumber );
        indexRowInspectors();
        return removed;
    }

    public RowInspector addRow( final int index,
                                final List<DTCellValue52> row ) {
        RowInspector rowInspector = rowInspectorGenerator.generate( index,
                                                                    row );
        rowInspectorList.add( index,
                              rowInspector );
        conditions.addAll( rowInspector.getConditions().allValues() );
        indexRowInspectors();
        return rowInspector;
    }

    private void indexRowInspectors() {
        for ( int rowIndex = 0; rowIndex < rowInspectorList.size(); rowIndex++ ) {
            final RowInspector rowInspector = rowInspectorList.get( rowIndex );
            rowInspector.setRowIndex( rowIndex );
        }
    }

    public interface Filter {

        boolean accept( final RowInspector rowInspector );

    }
}