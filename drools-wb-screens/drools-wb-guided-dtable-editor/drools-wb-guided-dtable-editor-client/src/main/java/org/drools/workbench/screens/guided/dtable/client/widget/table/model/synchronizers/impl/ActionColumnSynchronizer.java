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
import javax.enterprise.context.Dependent;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.BaseColumnSynchronizer.ColumnMetaData;

@Dependent
public class ActionColumnSynchronizer extends BaseColumnSynchronizer<ColumnMetaData, ColumnMetaData, ColumnMetaData> {

    @Override
    public boolean handlesAppend(final MetaData metaData) throws ModelSynchronizer.MoveColumnVetoException {
        if (!(metaData instanceof ColumnMetaData)) {
            return false;
        }
        return ((ColumnMetaData) metaData).getColumn() instanceof ActionCol52;
    }

    @Override
    public void append(final ColumnMetaData metaData) throws ModelSynchronizer.MoveColumnVetoException {
        //Check operation is supported
        if (!handlesAppend(metaData)) {
            return;
        }

        final ActionCol52 column = (ActionCol52) metaData.getColumn();
        model.getActionCols().add(column);
        synchroniseAppendColumn(column);
    }

    @Override
    public boolean handlesUpdate(final MetaData metaData) throws ModelSynchronizer.MoveColumnVetoException {
        //All sub-classes of ActionCol52 have their updates synchronized by specialised synchronizers
        return false;
    }

    @Override
    public List<BaseColumnFieldDiff> update(final ColumnMetaData originalMetaData,
                                            final ColumnMetaData editedMetaData) throws ModelSynchronizer.MoveColumnVetoException {
        //All sub-classes of ActionCol52 have their updates synchronized by specialised synchronizers
        return Collections.emptyList();
    }

    @Override
    public boolean handlesDelete(final MetaData metaData) throws ModelSynchronizer.MoveColumnVetoException {
        if (!(metaData instanceof ColumnMetaData)) {
            return false;
        }
        return ((ColumnMetaData) metaData).getColumn() instanceof ActionCol52;
    }

    @Override
    public void delete(final ColumnMetaData metaData) throws ModelSynchronizer.MoveColumnVetoException {
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
    public boolean handlesMoveColumnsTo(final List<? extends MetaData> metaData) throws ModelSynchronizer.MoveColumnVetoException {
        for (MetaData md : metaData) {
            if (!(md instanceof MoveColumnToMetaData)) {
                return false;
            }
            final BaseColumn column = ((MoveColumnToMetaData) md).getColumn();
            if (!(column instanceof ActionCol52)) {
                return false;
            }
        }
        if (isBRLFragment(metaData)) {
            return true;
        } else if (metaData.size() > 1) {
            throw new ModelSynchronizer.MoveColumnVetoException();
        }
        return true;
    }

    @Override
    public void moveColumnsTo(final List<MoveColumnToMetaData> metaData) throws ModelSynchronizer.MoveColumnVetoException {
        //Check operation is supported
        if (!handlesMoveColumnsTo(metaData)) {
            return;
        }
        if (isBRLFragment(metaData)) {
            doMoveBRLFragment(metaData);
        } else if (isSingleAction(metaData)) {
            doMoveSingleAction(metaData.get(0));
        } else {
            throw new ModelSynchronizer.MoveColumnVetoException();
        }
    }

    private boolean isBRLFragment(final List<? extends MetaData> metaData) {
        if (!metaData.stream().allMatch((c) -> (c instanceof MoveColumnToMetaData))) {
            return false;
        }
        if (!metaData.stream().allMatch((c) -> ((MoveColumnToMetaData) c).getColumn() instanceof BRLActionVariableColumn)) {
            return false;
        }
        final MoveColumnToMetaData md = (MoveColumnToMetaData) metaData.get(0);
        final BRLActionVariableColumn srcModelColumn = (BRLActionVariableColumn) md.getColumn();
        final BRLActionColumn srcModelPattern = model.getBRLColumn(srcModelColumn);
        return srcModelPattern.getChildColumns().size() == metaData.size();
    }

    private boolean isSingleAction(final List<MoveColumnToMetaData> metaData) {
        if (metaData.size() != 1) {
            return false;
        }
        if (metaData.get(0).getColumn() instanceof BRLActionVariableColumn) {
            return false;
        }
        return metaData.get(0).getColumn() instanceof ActionCol52;
    }

    private void doMoveBRLFragment(final List<MoveColumnToMetaData> metaData) throws ModelSynchronizer.MoveColumnVetoException {
        final MoveColumnToMetaData md = metaData.get(0);
        final BRLActionVariableColumn srcModelColumn = (BRLActionVariableColumn) md.getColumn();
        final BRLActionColumn srcModelBRLFragment = model.getBRLColumn(srcModelColumn);
        if (srcModelBRLFragment == null) {
            throw new ModelSynchronizer.MoveColumnVetoException();
        }
        final List<BRLActionVariableColumn> srcModelBRLFragmentColumns = srcModelBRLFragment.getChildColumns();
        final int srcModelBRLFragmentColumnsCount = srcModelBRLFragmentColumns.size();
        if (srcModelBRLFragmentColumnsCount == 0) {
            throw new ModelSynchronizer.MoveColumnVetoException();
        }
        if (srcModelBRLFragmentColumnsCount != metaData.size()) {
            throw new ModelSynchronizer.MoveColumnVetoException();
        }

        final int tgtColumnIndex = md.getTargetColumnIndex();
        final int tgtActionIndex = findTargetActionIndex(md);
        final List<BaseColumn> allModelColumns = model.getExpandedColumns();

        moveModelData(tgtColumnIndex,
                      allModelColumns.indexOf(srcModelBRLFragmentColumns.get(0)),
                      allModelColumns.indexOf(srcModelBRLFragmentColumns.get(0)) + srcModelBRLFragmentColumnsCount - 1);

        model.getActionCols().remove(srcModelBRLFragment);
        model.getActionCols().add(tgtActionIndex,
                                  srcModelBRLFragment);
    }

    private void doMoveSingleAction(final MoveColumnToMetaData metaData) throws ModelSynchronizer.MoveColumnVetoException {
        final ActionCol52 srcModelColumn = (ActionCol52) metaData.getColumn();
        final List<ActionCol52> srcModelActionColumns = model.getActionCols();
        final int srcModelActionColumnCount = srcModelActionColumns.size();
        if (srcModelActionColumnCount == 0) {
            throw new ModelSynchronizer.MoveColumnVetoException();
        }

        final int minColumnIndex = getMinActionColumnIndex();
        final int maxColumnIndex = getMaxActionColumnIndex();
        final int tgtColumnIndex = metaData.getTargetColumnIndex();
        final int tgtActionIndex = findTargetActionIndex(metaData);
        final int srcColumnIndex = metaData.getSourceColumnIndex();

        if (tgtColumnIndex < minColumnIndex || tgtColumnIndex > maxColumnIndex) {
            throw new ModelSynchronizer.MoveColumnVetoException();
        }

        moveModelData(tgtColumnIndex,
                      srcColumnIndex,
                      srcColumnIndex);

        srcModelActionColumns.remove(srcModelColumn);
        srcModelActionColumns.add(tgtActionIndex,
                                  srcModelColumn);
    }

    private int getMinActionColumnIndex() {
        final List<BaseColumn> allModelColumns = model.getExpandedColumns();
        final List<ActionCol52> modelActionColumns = model.getActionCols();
        final ActionCol52 actionCol = modelActionColumns.get(0);

        if (actionCol instanceof BRLActionColumn) {
            final BRLActionColumn brlActionCol = (BRLActionColumn) actionCol;
            final List<BRLActionVariableColumn> brlActionColChildren = brlActionCol.getChildColumns();
            return allModelColumns.indexOf(brlActionColChildren.get(0));
        }
        return allModelColumns.indexOf(actionCol);
    }

    private int getMaxActionColumnIndex() {
        final List<BaseColumn> allModelColumns = model.getExpandedColumns();
        final List<ActionCol52> modelActionColumns = model.getActionCols();
        final ActionCol52 actionCol = modelActionColumns.get(modelActionColumns.size() - 1);

        if (actionCol instanceof BRLActionColumn) {
            final BRLActionColumn brlActionCol = (BRLActionColumn) actionCol;
            final List<BRLActionVariableColumn> brlActionColChildren = brlActionCol.getChildColumns();
            return allModelColumns.indexOf(brlActionColChildren.get(brlActionColChildren.size() - 1));
        }
        return allModelColumns.indexOf(actionCol);
    }

    private int findTargetActionIndex(final MoveColumnToMetaData md) throws ModelSynchronizer.MoveColumnVetoException {
        int tgtActionIndex = -1;
        final int tgtColumnIndex = md.getTargetColumnIndex();
        final List<BaseColumn> allModelColumns = model.getExpandedColumns();
        final List<ActionCol52> allModelActions = model.getActionCols();
        for (int actionIndex = 0; actionIndex < allModelActions.size(); actionIndex++) {
            final ActionCol52 ac = allModelActions.get(actionIndex);
            final List<ActionCol52> children = getChildren(ac);
            if (children == null || children.isEmpty()) {
                continue;
            }
            final BaseColumn firstChild = children.get(0);
            final BaseColumn lastChild = children.get(children.size() - 1);
            final int firstChildIndex = allModelColumns.indexOf(firstChild);
            final int lastChildIndex = allModelColumns.indexOf(lastChild);
            if (tgtColumnIndex >= firstChildIndex && tgtColumnIndex <= lastChildIndex) {
                tgtActionIndex = actionIndex;
                break;
            }
        }

        if (tgtActionIndex < 0) {
            throw new ModelSynchronizer.MoveColumnVetoException();
        }
        return tgtActionIndex;
    }

    private List<ActionCol52> getChildren(final ActionCol52 ac) {
        final List<ActionCol52> children = new ArrayList<>();
        if (ac instanceof BRLActionColumn) {
            children.addAll(((BRLActionColumn) ac).getChildColumns());
        } else {
            children.add(ac);
        }
        return children;
    }
}
