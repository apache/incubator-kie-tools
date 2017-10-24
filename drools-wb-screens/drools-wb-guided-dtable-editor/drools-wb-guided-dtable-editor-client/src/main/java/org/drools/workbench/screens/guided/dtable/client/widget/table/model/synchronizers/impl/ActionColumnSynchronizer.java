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
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.enterprise.context.Dependent;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.BaseColumnSynchronizer.ColumnMetaData;

@Dependent
public class ActionColumnSynchronizer extends BaseColumnSynchronizer<ColumnMetaData, ColumnMetaData, ColumnMetaData> {

    @Override
    public boolean handlesAppend(final MetaData metaData) throws VetoException {
        //All sub-classes of ActionCol52 have their appends synchronized by specialised synchronizers
        return false;
    }

    @Override
    public void append(final ColumnMetaData metaData) throws VetoException {
        //All sub-classes of ActionCol52 have their appends synchronized by specialised synchronizers
    }

    @Override
    public boolean handlesUpdate(final MetaData metaData) throws VetoException {
        //All sub-classes of ActionCol52 have their updates synchronized by specialised synchronizers
        return false;
    }

    @Override
    public List<BaseColumnFieldDiff> update(final ColumnMetaData originalMetaData,
                                            final ColumnMetaData editedMetaData) throws VetoException {
        //All sub-classes of ActionCol52 have their updates synchronized by specialised synchronizers
        return Collections.emptyList();
    }

    @Override
    public boolean handlesDelete(final MetaData metaData) throws VetoException {
        if (!(metaData instanceof ColumnMetaData)) {
            return false;
        }
        return ((ColumnMetaData) metaData).getColumn() instanceof ActionCol52;
    }

    @Override
    public void delete(final ColumnMetaData metaData) throws VetoException {
        //Check operation is supported
        if (!handlesDelete(metaData)) {
            return;
        }

        final ActionCol52 column = (ActionCol52) metaData.getColumn();
        final int columnIndex = model.getExpandedColumns().indexOf(column);
        model.getActionCols().remove(column);
        synchroniseDeleteColumn(columnIndex);
    }

    @Override
    public boolean handlesMoveColumnsTo(final List<? extends MetaData> metaData) throws VetoException {
        //All sub-classes of ActionCol52 have their updates synchronized by specialised synchronizers
        return false;
    }

    @Override
    public void moveColumnsTo(final List<MoveColumnToMetaData> metaData) throws VetoException {
        //Check operation is supported
        if (!handlesMoveColumnsTo(metaData)) {
            return;
        }
        doMoveActionFragment(metaData);
    }

    protected boolean isWorkItemFragment(final List<? extends MetaData> metaData) {
        if (!metaData.stream().allMatch((c) -> (c instanceof MoveColumnToMetaData))) {
            return false;
        }
        if (!metaData.stream().map(c -> (MoveColumnToMetaData) c).allMatch(this::isWorkItemActionColumn)) {
            return false;
        }
        final BaseColumn firstColumnInFragment = ((MoveColumnToMetaData) metaData.get(0)).getColumn();
        final BaseColumn lastColumnInFragment = ((MoveColumnToMetaData) metaData.get(metaData.size() - 1)).getColumn();
        final int firstColumnIndex = model.getExpandedColumns().indexOf(firstColumnInFragment);
        final int lastColumnIndex = model.getExpandedColumns().indexOf(lastColumnInFragment);
        return lastColumnIndex - firstColumnIndex == metaData.size() - 1;
    }

    protected boolean isWorkItemActionColumn(final MoveColumnToMetaData metaData) {
        final BaseColumn column = metaData.getColumn();
        return column instanceof ActionWorkItemCol52
                || column instanceof ActionWorkItemInsertFactCol52
                || column instanceof ActionWorkItemSetFieldCol52;
    }

    protected boolean isWorkItemFragmentBeforeInsertFactCol(final List<? extends MetaData> metaData) {
        final List<ActionCol52> columnsToMove = metaData.stream()
                .map(c -> (MoveColumnToMetaData) c)
                .map(MoveColumnToMetaData::getColumn)
                .map(c -> (ActionCol52) c)
                .collect(Collectors.toList());

        final List<String> bindingsUsedByWID = new ArrayList<>();
        bindingsUsedByWID.addAll(columnsToMove.stream()
                                         .filter(c -> c instanceof ActionWorkItemInsertFactCol52)
                                         .map(c -> (ActionWorkItemInsertFactCol52) c)
                                         .map(ActionWorkItemInsertFactCol52::getBoundName)
                                         .collect(Collectors.toList()));

        final AtomicBoolean result = new AtomicBoolean(true);
        final int tgtIndex = ((MoveColumnToMetaData) metaData.get(0)).getTargetColumnIndex();
        final List<BaseColumn> modelColumns = model.getExpandedColumns();

        //Cannot move "Insert and Set field" created by the WID to after other "Insert and Set field"
        for (String binding : bindingsUsedByWID) {
            model.getActionCols().stream()
                    .filter(c -> !columnsToMove.contains(c))
                    .filter(c -> c instanceof ActionInsertFactCol52)
                    .filter(c -> !(c instanceof ActionWorkItemInsertFactCol52))
                    .map(c -> (ActionInsertFactCol52) c)
                    .filter(c -> c.getBoundName().equals(binding))
                    .map(modelColumns::indexOf)
                    .filter(i -> tgtIndex >= i)
                    .findFirst()
                    .ifPresent(i -> result.set(false));
        }

        return result.get();
    }

    protected void doMoveActionFragment(final List<MoveColumnToMetaData> metaData) throws VetoException {
        final MoveColumnToMetaData md = metaData.get(0);
        final BaseColumn firstColumnInFragment = md.getColumn();
        final BaseColumn lastColumnInFragment = metaData.get(metaData.size() - 1).getColumn();
        final int srcColumnFirstIndex = model.getExpandedColumns().indexOf(firstColumnInFragment);
        final int srcColumnLastIndex = model.getExpandedColumns().indexOf(lastColumnInFragment);

        final List<ActionCol52> srcModelFragmentColumns = IntStream.rangeClosed(srcColumnFirstIndex,
                                                                                srcColumnLastIndex)
                .mapToObj(i -> model.getExpandedColumns().get(i))
                .map(column -> (ActionCol52) column)
                .collect(Collectors.toList());

        final int srcModelFragmentColumnsCount = srcModelFragmentColumns.size();
        if (srcModelFragmentColumnsCount == 0) {
            throw new ModelSynchronizer.MoveVetoException();
        }
        if (srcModelFragmentColumnsCount != metaData.size()) {
            throw new ModelSynchronizer.MoveVetoException();
        }

        final int tgtColumnIndex = md.getTargetColumnIndex();
        final int tgtActionIndex = findTargetActionIndex(md);
        final List<BaseColumn> allModelColumns = model.getExpandedColumns();

        moveModelData(tgtColumnIndex,
                      allModelColumns.indexOf(srcModelFragmentColumns.get(0)),
                      allModelColumns.indexOf(srcModelFragmentColumns.get(0)) + srcModelFragmentColumnsCount - 1);

        //Moving left
        if (tgtColumnIndex < srcColumnFirstIndex) {
            final AtomicInteger offset = new AtomicInteger(0);
            srcModelFragmentColumns.forEach(column -> {
                model.getActionCols().remove(column);
                model.getActionCols().add(tgtActionIndex + offset.getAndIncrement(),
                                          column);
            });
        }

        //Moving right
        if (tgtColumnIndex > srcColumnFirstIndex) {
            srcModelFragmentColumns.forEach(column -> {
                model.getActionCols().remove(column);
                model.getActionCols().add(tgtActionIndex,
                                          column);
            });
        }
    }

    protected OptionalInt findLastIndexOfActionInsertFactColumn(final ActionInsertFactCol52 column) {
        final String binding = column.getBoundName();
        final List<ActionCol52> relatedColumns = new ArrayList<>();
        relatedColumns.addAll(model.getActionCols().stream()
                                      .filter(c -> c instanceof ActionInsertFactCol52)
                                      .map(c -> (ActionInsertFactCol52) c)
                                      .filter(c -> c.getBoundName().equals(binding))
                                      .collect(Collectors.toList()));

        return relatedColumns.stream().mapToInt(c -> model.getActionCols().indexOf(c)).max();
    }

    protected OptionalInt findLastIndexOfActionSetFieldColumn(final ActionSetFieldCol52 column) {
        final String binding = column.getBoundName();
        final List<ActionCol52> relatedColumns = new ArrayList<>();
        relatedColumns.addAll(model.getActionCols().stream()
                                      .filter(c -> c instanceof ActionSetFieldCol52)
                                      .map(c -> (ActionSetFieldCol52) c)
                                      .filter(c -> c.getBoundName().equals(binding))
                                      .collect(Collectors.toList()));

        return relatedColumns.stream().mapToInt(c -> model.getActionCols().indexOf(c)).max();
    }

    protected OptionalInt findLastIndexOfWorkItemColumn(final ActionWorkItemCol52 column) {
        final String workItemName = column.getWorkItemDefinition().getName();
        final List<ActionCol52> relatedColumns = new ArrayList<>();
        relatedColumns.add(column);

        relatedColumns.addAll(model.getActionCols().stream()
                                      .filter(c -> c instanceof ActionWorkItemInsertFactCol52)
                                      .map(c -> (ActionWorkItemInsertFactCol52) c)
                                      .filter(c -> c.getWorkItemName().equals(workItemName))
                                      .collect(Collectors.toList()));

        relatedColumns.addAll(model.getActionCols().stream()
                                      .filter(c -> c instanceof ActionWorkItemSetFieldCol52)
                                      .map(c -> (ActionWorkItemSetFieldCol52) c)
                                      .filter(c -> c.getWorkItemName().equals(workItemName))
                                      .collect(Collectors.toList()));

        return relatedColumns.stream().mapToInt(c -> model.getActionCols().indexOf(c)).max();
    }
}
