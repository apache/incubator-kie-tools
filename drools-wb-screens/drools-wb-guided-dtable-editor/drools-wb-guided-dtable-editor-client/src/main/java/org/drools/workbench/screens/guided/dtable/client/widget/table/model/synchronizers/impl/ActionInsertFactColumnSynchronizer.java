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
import java.util.OptionalInt;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
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
    public boolean handlesAppend(final MetaData metaData) throws ModelSynchronizer.MoveColumnVetoException {
        return handlesUpdate(metaData);
    }

    @Override
    public void append(final ColumnMetaData metaData) throws ModelSynchronizer.MoveColumnVetoException {
        if (!handlesAppend(metaData)) {
            return;
        }

        final ActionInsertFactCol52 column = (ActionInsertFactCol52) metaData.getColumn();
        final OptionalInt targetIndex = findLastIndexOfActionInsertFactColumn(column);
        if (targetIndex.isPresent()) {
            model.getActionCols().add(targetIndex.getAsInt() + 1,
                                      column);
            synchroniseAppendColumn(column);
        } else {
            model.getActionCols().add(column);
            synchroniseAppendColumn(column);
        }
    }

    @Override
    public boolean handlesUpdate(final MetaData metaData) throws ModelSynchronizer.MoveColumnVetoException {
        if (!(metaData instanceof ColumnMetaData)) {
            return false;
        }
        return ((ColumnMetaData) metaData).getColumn() instanceof ActionInsertFactCol52;
    }

    @Override
    public List<BaseColumnFieldDiff> update(final ColumnMetaData originalMetaData,
                                            final ColumnMetaData editedMetaData) throws ModelSynchronizer.MoveColumnVetoException {
        //Check operation is supported
        if (!(handlesUpdate(originalMetaData) && handlesUpdate(editedMetaData))) {
            return Collections.emptyList();
        }

        //Get differences between original and edited column
        final ActionInsertFactCol52 originalColumn = (ActionInsertFactCol52) originalMetaData.getColumn();
        final ActionInsertFactCol52 editedColumn = (ActionInsertFactCol52) editedMetaData.getColumn();

        final List<BaseColumnFieldDiff> diffs = originalColumn.diff(editedColumn);

        update(originalColumn,
               editedColumn);

        final boolean isHideUpdated = BaseColumnFieldDiffImpl.hasChanged(ActionInsertFactCol52.FIELD_HIDE_COLUMN,
                                                                         diffs);
        final boolean isHeaderUpdated = BaseColumnFieldDiffImpl.hasChanged(ActionInsertFactCol52.FIELD_HEADER,
                                                                           diffs);
        final boolean isBoundNameUpdated = BaseColumnFieldDiffImpl.hasChanged(ActionInsertFactCol52.FIELD_BOUND_NAME,
                                                                              diffs);
        final boolean isFactTypeUpdated = BaseColumnFieldDiffImpl.hasChanged(ActionInsertFactCol52.FIELD_FACT_TYPE,
                                                                             diffs);
        final boolean isFactFieldUpdated = BaseColumnFieldDiffImpl.hasChanged(ActionInsertFactCol52.FIELD_FACT_FIELD,
                                                                              diffs);

        if (isBoundNameUpdated || isFactTypeUpdated || isFactFieldUpdated) {
            clearColumnData(originalColumn);
        } else {
            cleanColumnData(originalColumn,
                            editedColumn,
                            diffs);
        }

        synchroniseUpdateColumn(originalColumn);

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

    private void cleanColumnData(final ActionInsertFactCol52 originalColumn,
                                 final ActionInsertFactCol52 editedColumn,
                                 final List<BaseColumnFieldDiff> diffs) {
        final boolean isValueListUpdated = BaseColumnFieldDiffImpl.hasChanged(ActionInsertFactCol52.FIELD_VALUE_LIST,
                                                                              diffs);

        // Update column's cell content if the Optional Value list has changed
        if (isValueListUpdated) {
            updateCellsForOptionValueList(originalColumn,
                                          editedColumn);
        }
    }

    private void update(final ActionInsertFactCol52 originalColumn,
                        final ActionInsertFactCol52 editedColumn) {
        originalColumn.setBoundName(editedColumn.getBoundName());
        originalColumn.setType(editedColumn.getType());
        originalColumn.setFactField(editedColumn.getFactField());
        originalColumn.setHeader(editedColumn.getHeader());
        originalColumn.setValueList(editedColumn.getValueList());
        originalColumn.setDefaultValue(editedColumn.getDefaultValue());
        originalColumn.setHideColumn(editedColumn.isHideColumn());
        originalColumn.setFactType(editedColumn.getFactType());
        originalColumn.setInsertLogical(editedColumn.isInsertLogical());
        if (originalColumn instanceof LimitedEntryCol && editedColumn instanceof LimitedEntryCol) {
            ((LimitedEntryCol) originalColumn).setValue(((LimitedEntryCol) editedColumn).getValue());
        }
    }

    @Override
    public boolean handlesMoveColumnsTo(final List<? extends MetaData> metaData) throws ModelSynchronizer.MoveColumnVetoException {
        final boolean isActionInsetFactFragment = isActionInsertFactFragment(metaData);
        if (!isActionInsetFactFragment) {
            return false;
        }

        final List<ActionCol52> columnsToMove = metaData.stream()
                .map(c -> (MoveColumnToMetaData) c)
                .map(MoveColumnToMetaData::getColumn)
                .map(c -> (ActionInsertFactCol52) c)
                .collect(Collectors.toList());

        final String binding = ((ActionInsertFactCol52) columnsToMove.get(0)).getBoundName();
        final int tgtIndex = ((MoveColumnToMetaData) metaData.get(0)).getTargetColumnIndex();
        final List<BaseColumn> modelColumns = model.getExpandedColumns();

        //Cannot move "Insert and Set field" to before the Facts creation by WID
        return model.getActionCols().stream()
                .filter(c -> !columnsToMove.contains(c))
                .filter(c -> c instanceof ActionWorkItemInsertFactCol52)
                .map(c -> (ActionWorkItemInsertFactCol52) c)
                .filter(c -> c.getBoundName().equals(binding))
                .map(modelColumns::indexOf)
                .noneMatch(i -> i >= tgtIndex);
    }

    private boolean isActionInsertFactFragment(final List<? extends MetaData> metaData) {
        if (!metaData.stream().allMatch((c) -> (c instanceof MoveColumnToMetaData))) {
            return false;
        }
        if (!metaData.stream().map(c -> (MoveColumnToMetaData) c).allMatch(c -> c.getColumn() instanceof ActionInsertFactCol52)) {
            return false;
        }
        final int lastMetaDataIndex = metaData.size() - 1;
        final BaseColumn firstColumnInFragment = ((MoveColumnToMetaData) metaData.get(0)).getColumn();
        final BaseColumn lastColumnInFragment = ((MoveColumnToMetaData) metaData.get(lastMetaDataIndex)).getColumn();
        final int firstColumnIndex = model.getExpandedColumns().indexOf(firstColumnInFragment);
        final int lastColumnIndex = model.getExpandedColumns().indexOf(lastColumnInFragment);
        return lastColumnIndex - firstColumnIndex == lastMetaDataIndex;
    }

    @Override
    protected void setColumnHeader(final BaseColumn modelColumn,
                                   final String header) {
        modelColumn.setHeader(header);
        final int iModelColumn = model.getExpandedColumns().indexOf(modelColumn);
        uiModel.getColumns().get(iModelColumn).getHeaderMetaData().get(1).setTitle(header);
    }
}
