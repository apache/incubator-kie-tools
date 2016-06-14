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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;

import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.gwt.BoundFactsChangedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;

@Dependent
public class BRLConditionColumnSynchronizer extends BaseColumnSynchronizer<BaseColumnSynchronizer.ColumnMetaData, BaseColumnSynchronizer.ColumnMetaData, BaseColumnSynchronizer.ColumnMetaData> {

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public boolean handlesAppend( final MetaData metaData ) {
        return handlesUpdate( metaData );
    }

    @Override
    public void append( final ColumnMetaData metaData ) {
        //Check operation is supported
        if ( !handlesAppend( metaData ) ) {
            return;
        }

        final BRLConditionColumn column = (BRLConditionColumn) metaData.getColumn();
        model.getConditions().add( column );
        for ( BRLConditionVariableColumn childModelColumn : column.getChildColumns() ) {
            synchroniseAppendColumn( childModelColumn );
        }
    }

    @Override
    public boolean handlesUpdate( final MetaData metaData ) {
        if ( !( metaData instanceof ColumnMetaData ) ) {
            return false;
        }
        return ( (ColumnMetaData) metaData ).getColumn() instanceof BRLConditionColumn;
    }

    @Override
    public List<BaseColumnFieldDiff> update( final ColumnMetaData originalMetaData,
                                             final ColumnMetaData editedMetaData ) {
        //Check operation is supported
        if ( !( handlesUpdate( originalMetaData ) && handlesUpdate( editedMetaData ) ) ) {
            return Collections.emptyList();
        }

        final BRLConditionColumn originalColumn = (BRLConditionColumn) originalMetaData.getColumn();
        final BRLConditionColumn editedColumn = (BRLConditionColumn) editedMetaData.getColumn();

        final List<BaseColumnFieldDiff> diffs = originalColumn.diff( editedColumn );

        //Copy existing data for re-use if applicable
        final Map<String, List<DTCellValue52>> originalColumnsData = new HashMap<String, List<DTCellValue52>>();
        for ( BRLConditionVariableColumn variable : originalColumn.getChildColumns() ) {
            int iColumnIndex = model.getExpandedColumns().indexOf( variable );
            final List<DTCellValue52> originalColumnData = new ArrayList<DTCellValue52>();
            final String key = makeUpdateBRLConditionColumnKey( variable );
            for ( List<DTCellValue52> row : model.getData() ) {
                originalColumnData.add( row.get( iColumnIndex ) );
            }
            originalColumnsData.put( key,
                                     originalColumnData );
        }

        //Insert new columns setting data from that above, if applicable. Column visibility is handled here too.
        model.getConditions().add( model.getConditions().indexOf( originalColumn ),
                                   editedColumn );
        for ( BRLConditionVariableColumn childModelColumn : editedColumn.getChildColumns() ) {
            final String key = makeUpdateBRLConditionColumnKey( childModelColumn );
            if ( originalColumnsData.containsKey( key ) ) {
                final List<DTCellValue52> originalColumnData = originalColumnsData.get( key );
                synchroniseAppendColumn( childModelColumn,
                                         originalColumnData );
            } else {
                synchroniseAppendColumn( childModelColumn );
            }
        }

        //Delete columns for the original definition
        delete( originalMetaData );

        //Signal patterns changed event to Decision Table Widget
        final BoundFactsChangedEvent pce = new BoundFactsChangedEvent( rm.getLHSBoundFacts() );
        eventBus.fireEvent( pce );

        return diffs;
    }

    @Override
    public boolean handlesDelete( final MetaData metaData ) {
        if ( !( metaData instanceof ColumnMetaData ) ) {
            return false;
        }
        return ( (ColumnMetaData) metaData ).getColumn() instanceof BRLConditionColumn;
    }

    @Override
    public void delete( final ColumnMetaData metaData ) {
        //Check operation is supported
        if ( !handlesDelete( metaData ) ) {
            return;
        }

        final BRLConditionColumn column = (BRLConditionColumn) metaData.getColumn();
        if ( column.getChildColumns().size() > 0 ) {
            final int iFirstColumnIndex = model.getExpandedColumns().indexOf( column.getChildColumns().get( 0 ) );
            for ( int iColumnIndex = 0; iColumnIndex < column.getChildColumns().size(); iColumnIndex++ ) {
                synchroniseDeleteColumn( iFirstColumnIndex );
            }
        }
        model.getConditions().remove( column );
    }

    @Override
    public boolean handlesMoveColumnsTo( final List<? extends MetaData> metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        //TODO {manstis} Individual BRLActionVariableColumn's cannot be moved
        return false;
    }

    @Override
    public void moveColumnsTo( final List<MoveColumnToMetaData> metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        //Check operation is supported
        if ( !handlesMoveColumnsTo( metaData ) ) {
            return;
        }
        //TODO {manstis} Individual BRLConditionVariableColumn's cannot be moved
    }

    private String makeUpdateBRLConditionColumnKey( final BRLConditionVariableColumn variable ) {
        StringBuilder key = new StringBuilder( variable.getVarName() ).append( ":" ).append( variable.getFieldType() ).append( ":" ).append( variable.getFactField() ).append( ":" ).append( variable.getFactType() );
        return key.toString();
    }

}
