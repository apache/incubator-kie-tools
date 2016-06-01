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

import java.util.Collections;
import java.util.List;
import javax.enterprise.context.Dependent;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.BaseColumnSynchronizer.*;

@Dependent
public class ActionColumnSynchronizer extends BaseColumnSynchronizer<ColumnMetaData, ColumnMetaData, ColumnMetaData> {

    @Override
    public boolean handlesAppend( final MetaData metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        if ( !( metaData instanceof ColumnMetaData ) ) {
            return false;
        }
        return ( (ColumnMetaData) metaData ).getColumn() instanceof ActionCol52;
    }

    @Override
    public void append( final ColumnMetaData metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        //Check operation is supported
        if ( !handlesAppend( metaData ) ) {
            return;
        }

        final ActionCol52 column = (ActionCol52) metaData.getColumn();
        model.getActionCols().add( column );
        synchroniseAppendColumn( column );
    }

    @Override
    public boolean handlesUpdate( final MetaData metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        //All sub-classes of ActionCol52 have their updates synchronized by specialised synchronizers
        return false;
    }

    @Override
    public List<BaseColumnFieldDiff> update( final ColumnMetaData originalMetaData,
                                             final ColumnMetaData editedMetaData ) throws ModelSynchronizer.MoveColumnVetoException {
        //All sub-classes of ActionCol52 have their updates synchronized by specialised synchronizers
        return Collections.emptyList();
    }

    @Override
    public boolean handlesDelete( final MetaData metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        if ( !( metaData instanceof ColumnMetaData ) ) {
            return false;
        }
        return ( (ColumnMetaData) metaData ).getColumn() instanceof ActionCol52;
    }

    @Override
    public void delete( final ColumnMetaData metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        //Check operation is supported
        if ( !handlesDelete( metaData ) ) {
            return;
        }

        final ActionCol52 column = (ActionCol52) metaData.getColumn();
        final int columnIndex = model.getExpandedColumns().indexOf( column );
        model.getActionCols().remove( column );
        synchroniseDeleteColumn( columnIndex );
    }

    @Override
    public boolean handlesMoveColumnsTo( final List<? extends MetaData> metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        for ( MetaData md : metaData ) {
            if ( !( md instanceof MoveColumnToMetaData ) ) {
                return false;
            }
            final BaseColumn column = ( (MoveColumnToMetaData) md ).getColumn();
            if ( !( column instanceof ActionCol52 ) ) {
                return false;
            }
        }
        if ( metaData.size() > 1 ) {
            throw new ModelSynchronizer.MoveColumnVetoException();
        }
        return true;
    }

    @Override
    public void moveColumnsTo( final List<MoveColumnToMetaData> metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        //Check operation is supported
        if ( !handlesMoveColumnsTo( metaData ) ) {
            return;
        }

        final MoveColumnToMetaData md = metaData.get( 0 );
        final ActionCol52 modelColumn = (ActionCol52) md.getColumn();

        final List<ActionCol52> modelActionColumns = model.getActionCols();
        final int modelActionColumnCount = modelActionColumns.size();
        if ( modelActionColumnCount == 0 ) {
            throw new ModelSynchronizer.MoveColumnVetoException();
        }

        final List<BaseColumn> allModelColumns = model.getExpandedColumns();
        final int minColumnIndex = allModelColumns.indexOf( modelActionColumns.get( 0 ) );
        final int maxColumnIndex = allModelColumns.indexOf( modelActionColumns.get( modelActionColumnCount - 1 ) );

        final int targetColumnIndex = md.getTargetColumnIndex();
        final int sourceColumnIndex = md.getSourceColumnIndex();
        if ( targetColumnIndex < minColumnIndex || targetColumnIndex > maxColumnIndex ) {
            throw new ModelSynchronizer.MoveColumnVetoException();
        }

        moveModelData( targetColumnIndex,
                       sourceColumnIndex,
                       sourceColumnIndex );

        modelActionColumns.remove( modelColumn );
        modelActionColumns.add( targetColumnIndex - minColumnIndex,
                                modelColumn );

    }

}
