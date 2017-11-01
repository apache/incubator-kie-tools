/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiffImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;

@Dependent
public class ActionWorkItemInsertFactColumnSynchronizer extends ActionColumnSynchronizer {

    @Override
    public int priority() {
        return 2;
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

        final ActionWorkItemInsertFactCol52 column = (ActionWorkItemInsertFactCol52) metaData.getColumn();
        final String workItemName = column.getWorkItemName();

        model.getActionCols()
                .stream()
                .filter(c -> c instanceof ActionWorkItemCol52)
                .map(c -> ((ActionWorkItemCol52) c))
                .filter(c -> c.getWorkItemDefinition().getName().equals(workItemName))
                .findFirst()
                .ifPresent(c -> findLastIndexOfWorkItemColumn(c).ifPresent(index -> {
                    model.getActionCols().add(index + 1,
                                              column);
                    synchroniseAppendColumn(column);
                }));
    }

    @Override
    public boolean handlesUpdate(final MetaData metaData) throws VetoException {
        if (!(metaData instanceof ColumnMetaData)) {
            return false;
        }
        return ((ColumnMetaData) metaData).getColumn() instanceof ActionWorkItemInsertFactCol52;
    }

    @Override
    public List<BaseColumnFieldDiff> update(final ColumnMetaData originalMetaData,
                                            final ColumnMetaData editedMetaData) throws VetoException {
        //Check operation is supported
        if (!(handlesUpdate(originalMetaData) && handlesUpdate(editedMetaData))) {
            return Collections.emptyList();
        }

        //Get differences between original and edited column
        final ActionWorkItemInsertFactCol52 originalColumn = (ActionWorkItemInsertFactCol52) originalMetaData.getColumn();
        final ActionWorkItemInsertFactCol52 editedColumn = (ActionWorkItemInsertFactCol52) editedMetaData.getColumn();

        final List<BaseColumnFieldDiff> diffs = originalColumn.diff(editedColumn);

        update(originalColumn,
               editedColumn);

        //ActionWorkItem columns are always represented with a BooleanUiColumn
        final boolean isHideUpdated = BaseColumnFieldDiffImpl.hasChanged(ActionWorkItemInsertFactCol52.FIELD_HIDE_COLUMN,
                                                                         diffs);
        final boolean isHeaderUpdated = BaseColumnFieldDiffImpl.hasChanged(ActionWorkItemInsertFactCol52.FIELD_HEADER,
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

    private void update(final ActionWorkItemInsertFactCol52 originalColumn,
                        final ActionWorkItemInsertFactCol52 editedColumn) {
        originalColumn.setBoundName(editedColumn.getBoundName());
        originalColumn.setType(editedColumn.getType());
        originalColumn.setFactType(editedColumn.getFactType());
        originalColumn.setFactField(editedColumn.getFactField());
        originalColumn.setHeader(editedColumn.getHeader());
        originalColumn.setHideColumn(editedColumn.isHideColumn());
        originalColumn.setWorkItemName(editedColumn.getWorkItemName());
        originalColumn.setWorkItemResultParameterName(editedColumn.getWorkItemResultParameterName());
        originalColumn.setParameterClassName(editedColumn.getParameterClassName());
    }

    @Override
    public boolean handlesMoveColumnsTo(final List<? extends MetaData> metaData) throws VetoException {
        return isWorkItemFragment(metaData) && isWorkItemFragmentBeforeInsertFactCol(metaData);
    }

    @Override
    protected void setColumnHeader(final BaseColumn modelColumn,
                                   final String header) {
        modelColumn.setHeader(header);
        final int iModelColumn = model.getExpandedColumns().indexOf(modelColumn);
        uiModel.getColumns().get(iModelColumn).getHeaderMetaData().get(1).setTitle(header);
    }
}
