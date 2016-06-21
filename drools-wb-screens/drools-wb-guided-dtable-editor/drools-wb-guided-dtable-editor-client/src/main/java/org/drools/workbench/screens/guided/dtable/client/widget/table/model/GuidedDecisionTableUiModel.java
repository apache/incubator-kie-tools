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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model;

import java.util.List;

import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;

public class GuidedDecisionTableUiModel extends BaseGridData {

    //Guided Decision Tables headers span 2 rows. Condition Columns are split over 2 rows, all others need to occupy 2 rows height.
    private static final int HEADER_ROW_COUNT = 2;

    private final ModelSynchronizer synchronizer;

    public GuidedDecisionTableUiModel( final ModelSynchronizer synchronizer ) {
        this.synchronizer = PortablePreconditions.checkNotNull( "synchronizer",
                                                                synchronizer );
        setMerged( false );
        setHeaderRowCount( HEADER_ROW_COUNT );
    }

    @Override
    //Override to sync underlying Model with UiModel
    public Range setCell( final int rowIndex,
                          final int columnIndex,
                          final GridCellValue<?> value ) {
        final Range range = super.setCell( rowIndex,
                                           columnIndex,
                                           value );
        synchronizer.setCell( range,
                              columnIndex,
                              value );
        return range;
    }

    @Override
    //Override to sync underlying Model with UiModel
    public Range deleteCell( final int rowIndex,
                             final int columnIndex ) {
        final Range range = super.deleteCell( rowIndex,
                                              columnIndex );
        synchronizer.deleteCell( range,
                                 columnIndex );
        return range;
    }

    @Override
    public void moveColumnsTo( final int index,
                               final List<GridColumn<?>> columns ) {
        try {
            synchronizer.moveColumnsTo( index,
                                        columns );
            super.moveColumnsTo( index,
                                 columns );
        } catch ( ModelSynchronizer.MoveColumnVetoException ignore ) {
            //Do nothing. The move has been vetoed.
        }
    }

    @Override
    public void moveRowsTo( final int index,
                            final List<GridRow> rows ) {
        try {
            synchronizer.moveRowsTo( index,
                                     rows );
            super.moveRowsTo( index,
                              rows );
            synchronizer.updateSystemControlledColumnValues();

        } catch ( ModelSynchronizer.MoveColumnVetoException ignore ) {
            //Do nothing. The move has been vetoed.
        }
    }

    public void indexColumn( final int columnIndex ) {
        if ( isMerged() ) {
            indexManager.indexColumn( columnIndex );
        }
    }

    public Range setCellInternal( final int rowIndex,
                                  final int columnIndex,
                                  final GridCellValue<?> value ) {
        final boolean isMerged = isMerged();
        try {
            this.isMerged = false;
            return super.setCell( rowIndex,
                                  columnIndex,
                                  value );

        } finally {
            this.isMerged = isMerged;
        }
    }

    public Range deleteCellInternal( final int rowIndex,
                                     final int columnIndex ) {
        final boolean isMerged = isMerged();
        try {
            this.isMerged = false;
            return super.deleteCell( rowIndex,
                                     columnIndex );

        } finally {
            this.isMerged = isMerged;
        }
    }

}
