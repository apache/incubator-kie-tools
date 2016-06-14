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

import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiffImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;

@Dependent
public class ActionWorkItemSetFieldColumnSynchronizer extends ActionColumnSynchronizer {

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public boolean handlesAppend( final MetaData metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        return handlesUpdate( metaData );
    }

    @Override
    public boolean handlesUpdate( final MetaData metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        if ( !( metaData instanceof ColumnMetaData ) ) {
            return false;
        }
        return ( (ColumnMetaData) metaData ).getColumn() instanceof ActionWorkItemSetFieldCol52;
    }

    @Override
    public List<BaseColumnFieldDiff> update( final ColumnMetaData originalMetaData,
                                             final ColumnMetaData editedMetaData ) throws ModelSynchronizer.MoveColumnVetoException {
        //Check operation is supported
        if ( !( handlesUpdate( originalMetaData ) && handlesUpdate( editedMetaData ) ) ) {
            return Collections.emptyList();
        }

        //Get differences between original and edited column
        final ActionWorkItemSetFieldCol52 originalColumn = (ActionWorkItemSetFieldCol52) originalMetaData.getColumn();
        final ActionWorkItemSetFieldCol52 editedColumn = (ActionWorkItemSetFieldCol52) editedMetaData.getColumn();

        final List<BaseColumnFieldDiff> diffs = originalColumn.diff( editedColumn );

        update( originalColumn,
                editedColumn );

        //ActionWorkItem columns are always represented with a BooleanUiColumn
        final boolean isHideUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionWorkItemSetFieldCol52.FIELD_HIDE_COLUMN,
                                                                          diffs );
        final boolean isHeaderUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionWorkItemSetFieldCol52.FIELD_HIDE_COLUMN,
                                                                            diffs );

        if ( isHideUpdated ) {
            setColumnVisibility( originalColumn,
                                 originalColumn.isHideColumn() );
        }
        if ( isHeaderUpdated ) {
            setColumnHeader( originalColumn,
                             originalColumn.getHeader() );
        }

        return diffs;
    }

    private void update( final ActionWorkItemSetFieldCol52 originalColumn,
                         final ActionWorkItemSetFieldCol52 editedColumn ) {
        originalColumn.setBoundName( editedColumn.getBoundName() );
        originalColumn.setType( editedColumn.getType() );
        originalColumn.setFactField( editedColumn.getFactField() );
        originalColumn.setHeader( editedColumn.getHeader() );
        originalColumn.setHideColumn( editedColumn.isHideColumn() );
        originalColumn.setUpdate( editedColumn.isUpdate() );
        originalColumn.setWorkItemName( editedColumn.getWorkItemName() );
        originalColumn.setWorkItemResultParameterName( editedColumn.getWorkItemResultParameterName() );
        originalColumn.setParameterClassName( editedColumn.getParameterClassName() );
    }

}
