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

import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiffImpl;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.BaseColumnSynchronizer.ColumnMetaData;

@Dependent
public class AttributeColumnSynchronizer extends BaseColumnSynchronizer<ColumnMetaData, ColumnMetaData, ColumnMetaData> {

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

        final AttributeCol52 column = (AttributeCol52) metaData.getColumn();
        model.getAttributeCols().add(column);
        synchroniseAppendColumn(column);
    }

    @Override
    public boolean handlesUpdate(final MetaData metaData) throws VetoException {
        if (!(metaData instanceof ColumnMetaData)) {
            return false;
        }
        return ((ColumnMetaData) metaData).getColumn() instanceof AttributeCol52;
    }

    @Override
    public List<BaseColumnFieldDiff> update(final ColumnMetaData originalMetaData,
                                            final ColumnMetaData editedMetaData) throws VetoException {
        //Check operation is supported
        if (!(handlesUpdate(originalMetaData) && handlesUpdate(editedMetaData))) {
            return Collections.emptyList();
        }

        final AttributeCol52 originalColumn = (AttributeCol52) originalMetaData.getColumn();
        final AttributeCol52 editedColumn = (AttributeCol52) editedMetaData.getColumn();
        final List<BaseColumnFieldDiff> diffs = originalColumn.diff(editedColumn);

        update(originalColumn,
               editedColumn);

        final boolean isAttributeUpdated = BaseColumnFieldDiffImpl.hasChanged(AttributeCol52.FIELD_ATTRIBUTE,
                                                                              diffs);
        final boolean isHideUpdated = BaseColumnFieldDiffImpl.hasChanged(DTColumnConfig52.FIELD_HIDE_COLUMN,
                                                                         diffs);

        if (isAttributeUpdated) {
            clearColumnData(originalColumn);
        }

        synchroniseUpdateColumn(originalColumn);

        if (isAttributeUpdated) {
            setColumnHeader(originalColumn,
                            originalColumn.getAttribute());
        }
        if (isHideUpdated) {
            setColumnVisibility(originalColumn,
                                originalColumn.isHideColumn());
        }

        return diffs;
    }

    private void update(final AttributeCol52 originalColumn,
                        final AttributeCol52 editedColumn) {
        originalColumn.setAttribute(editedColumn.getAttribute());
        originalColumn.setReverseOrder(editedColumn.isReverseOrder());
        originalColumn.setUseRowNumber(editedColumn.isUseRowNumber());
        originalColumn.setHideColumn(editedColumn.isHideColumn());
        originalColumn.setHeader(editedColumn.getHeader());
        originalColumn.setDefaultValue(editedColumn.getDefaultValue());
    }

    @Override
    public boolean handlesDelete(final MetaData metaData) throws VetoException {
        if (!(metaData instanceof ColumnMetaData)) {
            return false;
        }
        return ((ColumnMetaData) metaData).getColumn() instanceof AttributeCol52;
    }

    @Override
    public void delete(final ColumnMetaData metaData) throws VetoException {
        //Check operation is supported
        if (!handlesDelete(metaData)) {
            return;
        }

        final AttributeCol52 column = (AttributeCol52) metaData.getColumn();
        final int columnIndex = model.getExpandedColumns().indexOf(column);
        model.getAttributeCols().remove(column);
        synchroniseDeleteColumn(columnIndex);
    }

    @Override
    public boolean handlesMoveColumnsTo(final List<? extends MetaData> metaData) throws VetoException {
        for (MetaData md : metaData) {
            if (!(md instanceof MoveColumnToMetaData)) {
                return false;
            }
            final BaseColumn column = ((MoveColumnToMetaData) md).getColumn();
            if (!(column instanceof AttributeCol52)) {
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
        final AttributeCol52 modelColumn = (AttributeCol52) md.getColumn();

        final List<AttributeCol52> modelAttributeColumns = model.getAttributeCols();
        final int modelAttributeColumnCount = modelAttributeColumns.size();
        if (modelAttributeColumnCount == 0) {
            throw new ModelSynchronizer.MoveVetoException();
        }

        final List<BaseColumn> allModelColumns = model.getExpandedColumns();
        final int minColumnIndex = allModelColumns.indexOf(modelAttributeColumns.get(0));
        final int maxColumnIndex = allModelColumns.indexOf(modelAttributeColumns.get(modelAttributeColumnCount - 1));

        final int targetColumnIndex = md.getTargetColumnIndex();
        final int sourceColumnIndex = md.getSourceColumnIndex();
        if (targetColumnIndex < minColumnIndex || targetColumnIndex > maxColumnIndex) {
            throw new ModelSynchronizer.MoveVetoException();
        }

        moveModelData(targetColumnIndex,
                      sourceColumnIndex,
                      sourceColumnIndex);

        modelAttributeColumns.remove(modelColumn);
        modelAttributeColumns.add(targetColumnIndex - minColumnIndex,
                                  modelColumn);
    }
}
