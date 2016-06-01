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

import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiffImpl;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;

@Dependent
public class ActionRetractFactColumnSynchronizer extends ActionColumnSynchronizer {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean handlesUpdate( final MetaData metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        if ( !( metaData instanceof ColumnMetaData ) ) {
            return false;
        }
        return ( (ColumnMetaData) metaData ).getColumn() instanceof ActionRetractFactCol52;
    }

    @Override
    public List<BaseColumnFieldDiff> update( final ColumnMetaData originalMetaData,
                                             final ColumnMetaData editedMetaData ) throws ModelSynchronizer.MoveColumnVetoException {
        //Check operation is supported
        if ( !( handlesUpdate( originalMetaData ) && handlesUpdate( editedMetaData ) ) ) {
            return Collections.emptyList();
        }

        //Get differences between original and edited column
        final ActionRetractFactCol52 originalColumn = (ActionRetractFactCol52) originalMetaData.getColumn();
        final ActionRetractFactCol52 editedColumn = (ActionRetractFactCol52) editedMetaData.getColumn();

        final List<BaseColumnFieldDiff> diffs = originalColumn.diff( editedColumn );

        update( originalColumn,
                editedColumn );

        final boolean isHideUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionRetractFactCol52.FIELD_HIDE_COLUMN,
                                                                          diffs );
        final boolean isHeaderUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionRetractFactCol52.FIELD_HEADER,
                                                                            diffs );

        synchroniseUpdateColumn( originalColumn );

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

    private void update( final ActionRetractFactCol52 originalColumn,
                         final ActionRetractFactCol52 editedColumn ) {
        originalColumn.setHeader( editedColumn.getHeader() );
        originalColumn.setDefaultValue( editedColumn.getDefaultValue() );
        originalColumn.setHideColumn( editedColumn.isHideColumn() );
        if ( originalColumn instanceof LimitedEntryCol && editedColumn instanceof LimitedEntryCol ) {
            ( (LimitedEntryCol) originalColumn ).setValue( ( (LimitedEntryCol) editedColumn ).getValue() );
        }
    }

}
