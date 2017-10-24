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

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiffImpl;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLConditionColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.BaseColumnSynchronizer.ColumnMetaData;

@Dependent
public class LimitedEntryBRLConditionColumnSynchronizer extends BaseColumnSynchronizer<ColumnMetaData, ColumnMetaData, ColumnMetaData> {

    @Override
    public int priority() {
        return 4;
    }

    @Override
    public boolean handlesAppend(final MetaData metaData) throws VetoException {
        return handlesUpdate(metaData);
    }

    @Override
    public void append(final ColumnMetaData metaData) throws VetoException {
        //Check operation is supported
        if (!handlesAppend(metaData)) {
            return;
        }

        final LimitedEntryBRLConditionColumn column = (LimitedEntryBRLConditionColumn) metaData.getColumn();
        model.getConditions().add(column);
        synchroniseAppendColumn(column);
    }

    @Override
    public boolean handlesUpdate(final MetaData metaData) throws VetoException {
        if (!(metaData instanceof ColumnMetaData)) {
            return false;
        }
        return ((ColumnMetaData) metaData).getColumn() instanceof LimitedEntryBRLConditionColumn;
    }

    @Override
    public List<BaseColumnFieldDiff> update(final ColumnMetaData originalMetaData,
                                            final ColumnMetaData editedMetaData) throws VetoException {
        //Check operation is supported
        if (!(handlesUpdate(originalMetaData) && handlesUpdate(editedMetaData))) {
            return Collections.emptyList();
        }

        //Get differences between original and edited column
        final LimitedEntryBRLConditionColumn originalColumn = (LimitedEntryBRLConditionColumn) originalMetaData.getColumn();
        final LimitedEntryBRLConditionColumn editedColumn = (LimitedEntryBRLConditionColumn) editedMetaData.getColumn();

        final List<BaseColumnFieldDiff> diffs = originalColumn.diff(editedColumn);

        update(originalColumn,
               editedColumn);

        //LimitedEntry columns are always represented with a BooleanUiColumn
        final boolean isHideUpdated = BaseColumnFieldDiffImpl.hasChanged(ConditionCol52.FIELD_HIDE_COLUMN,
                                                                         diffs);
        final boolean isHeaderUpdated = BaseColumnFieldDiffImpl.hasChanged(ConditionCol52.FIELD_HIDE_COLUMN,
                                                                           diffs);

        if (isHideUpdated) {
            setColumnVisibility(originalColumn,
                                originalColumn.isHideColumn());
        }
        if (isHeaderUpdated) {
            setColumnHeader(originalColumn,
                            originalColumn.getHeader());
        }

        return diffs;
    }

    @Override
    public boolean handlesDelete(final MetaData metaData) throws VetoException {
        if (!(metaData instanceof ColumnMetaData)) {
            return false;
        }
        return ((ColumnMetaData) metaData).getColumn() instanceof LimitedEntryBRLConditionColumn;
    }

    @Override
    public void delete(final ColumnMetaData metaData) throws VetoException {
        //Check operation is supported
        if (!handlesDelete(metaData)) {
            return;
        }

        final LimitedEntryBRLConditionColumn column = (LimitedEntryBRLConditionColumn) metaData.getColumn();
        final int columnIndex = model.getExpandedColumns().indexOf(column);
        model.getConditions().remove(column);
        synchroniseDeleteColumn(columnIndex);
    }

    @Override
    public boolean handlesMoveColumnsTo(final List<? extends MetaData> metaData) throws VetoException {
        for (MetaData md : metaData) {
            if (!(md instanceof MoveColumnToMetaData)) {
                return false;
            }
            final BaseColumn column = ((MoveColumnToMetaData) md).getColumn();
            if (!(column instanceof LimitedEntryBRLConditionColumn)) {
                return false;
            }
        }
        return metaData.size() == 1;
    }

    @Override
    public void moveColumnsTo(final List<MoveColumnToMetaData> metaData) throws VetoException {
        //Check operation is supported
        if (!handlesMoveColumnsTo(metaData)) {
            return;
        }

        final MoveColumnToMetaData md = metaData.get(0);
        final LimitedEntryBRLConditionColumn modelColumn = (LimitedEntryBRLConditionColumn) md.getColumn();

        final List<CompositeColumn<? extends BaseColumn>> modelConditionColumns = model.getConditions();
        final int modelConditionColumnCount = modelConditionColumns.size();
        if (modelConditionColumnCount == 0) {
            throw new ModelSynchronizer.MoveVetoException();
        }

        final List<BaseColumn> allModelColumns = model.getExpandedColumns();
        final int minColumnIndex = allModelColumns.indexOf(modelConditionColumns.get(0));
        final int maxColumnIndex = allModelColumns.indexOf(modelConditionColumns.get(modelConditionColumnCount - 1));

        final int targetColumnIndex = md.getTargetColumnIndex();
        final int sourceColumnIndex = md.getSourceColumnIndex();
        if (targetColumnIndex < minColumnIndex || targetColumnIndex > maxColumnIndex) {
            throw new ModelSynchronizer.MoveVetoException();
        }

        moveModelData(targetColumnIndex,
                      sourceColumnIndex,
                      sourceColumnIndex);

        modelConditionColumns.remove(modelColumn);
        modelConditionColumns.add(targetColumnIndex - minColumnIndex,
                                  modelColumn);
    }

    private void update(final LimitedEntryBRLConditionColumn originalColumn,
                        final LimitedEntryBRLConditionColumn editedColumn) {
        originalColumn.setHeader(editedColumn.getHeader());
        originalColumn.setDefaultValue(editedColumn.getDefaultValue());
        originalColumn.setHideColumn(editedColumn.isHideColumn());
        originalColumn.setDefinition(editedColumn.getDefinition());
    }
}
