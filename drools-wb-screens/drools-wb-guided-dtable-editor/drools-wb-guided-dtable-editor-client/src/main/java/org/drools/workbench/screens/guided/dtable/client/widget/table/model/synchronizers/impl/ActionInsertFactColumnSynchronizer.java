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

import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiffImpl;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;

@Dependent
public class ActionInsertFactColumnSynchronizer extends ActionColumnSynchronizer {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean handlesUpdate( final MetaData metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        if ( !( metaData instanceof ColumnMetaData ) ) {
            return false;
        }
        return ( (ColumnMetaData) metaData ).getColumn() instanceof ActionInsertFactCol52;
    }

    @Override
    public List<BaseColumnFieldDiff> update( final ColumnMetaData originalMetaData,
                                             final ColumnMetaData editedMetaData ) throws ModelSynchronizer.MoveColumnVetoException {
        //Check operation is supported
        if ( !( handlesUpdate( originalMetaData ) && handlesUpdate( editedMetaData ) ) ) {
            return Collections.emptyList();
        }

        //Get differences between original and edited column
        final ActionInsertFactCol52 originalColumn = (ActionInsertFactCol52) originalMetaData.getColumn();
        final ActionInsertFactCol52 editedColumn = (ActionInsertFactCol52) editedMetaData.getColumn();

        final List<BaseColumnFieldDiff> diffs = originalColumn.diff( editedColumn );

        update( originalColumn,
                editedColumn );

        final boolean isHideUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionInsertFactCol52.FIELD_HIDE_COLUMN,
                                                                          diffs );
        final boolean isHeaderUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionInsertFactCol52.FIELD_HEADER,
                                                                            diffs );
        final boolean isBoundNameUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionInsertFactCol52.FIELD_BOUND_NAME,
                                                                               diffs );
        final boolean isFactTypeUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionInsertFactCol52.FIELD_FACT_TYPE,
                                                                              diffs );
        final boolean isFactFieldUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionInsertFactCol52.FIELD_FACT_FIELD,
                                                                               diffs );

        if ( isBoundNameUpdated || isFactTypeUpdated || isFactFieldUpdated ) {
            clearColumnData( originalColumn );
        } else {
            cleanColumnData( originalColumn,
                             editedColumn,
                             diffs );
        }

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

    private void cleanColumnData( final ActionInsertFactCol52 originalColumn,
                                  final ActionInsertFactCol52 editedColumn,
                                  final List<BaseColumnFieldDiff> diffs ) {
        final boolean isValueListUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionInsertFactCol52.FIELD_VALUE_LIST,
                                                                               diffs );

        // Update column's cell content if the Optional Value list has changed
        if ( isValueListUpdated ) {
            updateCellsForOptionValueList( originalColumn,
                                           editedColumn );
        }
    }

    private void update( final ActionInsertFactCol52 originalColumn,
                         final ActionInsertFactCol52 editedColumn ) {
        originalColumn.setBoundName( editedColumn.getBoundName() );
        originalColumn.setType( editedColumn.getType() );
        originalColumn.setFactField( editedColumn.getFactField() );
        originalColumn.setHeader( editedColumn.getHeader() );
        originalColumn.setValueList( editedColumn.getValueList() );
        originalColumn.setDefaultValue( editedColumn.getDefaultValue() );
        originalColumn.setHideColumn( editedColumn.isHideColumn() );
        originalColumn.setFactType( editedColumn.getFactType() );
        originalColumn.setInsertLogical( editedColumn.isInsertLogical() );
        if ( originalColumn instanceof LimitedEntryCol && editedColumn instanceof LimitedEntryCol ) {
            ( (LimitedEntryCol) originalColumn ).setValue( ( (LimitedEntryCol) editedColumn ).getValue() );
        }
    }

}
