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
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;

@Dependent
public class MetaDataColumnSynchronizer extends BaseColumnSynchronizer<BaseColumnSynchronizer.ColumnMetaData, BaseColumnSynchronizer.ColumnMetaData, BaseColumnSynchronizer.ColumnMetaData> {

    @Override
    public boolean handlesAppend(final MetaData metaData) throws VetoException {
        return handlesUpdate(metaData);
    }

    @Override
    public void append(final BaseColumnSynchronizer.ColumnMetaData metaData) throws VetoException {
        //Check operation is supported
        if (!handlesAppend(metaData)) {
            return;
        }

        final MetadataCol52 column = (MetadataCol52) metaData.getColumn();
        model.getMetadataCols().add(column);
        synchroniseAppendColumn(column);
    }

    @Override
    public boolean handlesUpdate(final MetaData metaData) throws VetoException {
        if (!(metaData instanceof ColumnMetaData)) {
            return false;
        }
        return ((ColumnMetaData) metaData).getColumn() instanceof MetadataCol52;
    }

    @Override
    public List<BaseColumnFieldDiff> update(final BaseColumnSynchronizer.ColumnMetaData originalMetaData,
                                            final BaseColumnSynchronizer.ColumnMetaData editedMetaData) throws VetoException {
        //Check operation is supported
        if (!(handlesUpdate(originalMetaData) && handlesUpdate(editedMetaData))) {
            return Collections.emptyList();
        }

        final MetadataCol52 originalColumn = (MetadataCol52) originalMetaData.getColumn();
        final MetadataCol52 editedColumn = (MetadataCol52) editedMetaData.getColumn();
        final List<BaseColumnFieldDiff> diffs = originalColumn.diff(editedColumn);

        update(originalColumn,
               editedColumn);

        final boolean isMetaDataUpdated = BaseColumnFieldDiffImpl.hasChanged(MetadataCol52.FIELD_METADATA,
                                                                             diffs);
        final boolean isHideUpdated = BaseColumnFieldDiffImpl.hasChanged(DTColumnConfig52.FIELD_HIDE_COLUMN,
                                                                         diffs);

        if (isMetaDataUpdated) {
            setColumnHeader(originalColumn,
                            originalColumn.getMetadata());
        }
        if (isHideUpdated) {
            setColumnVisibility(originalColumn,
                                originalColumn.isHideColumn());
        }

        return diffs;
    }

    private void update(final MetadataCol52 originalColumn,
                        final MetadataCol52 editedColumn) {
        originalColumn.setMetadata(editedColumn.getMetadata());
        originalColumn.setHideColumn(editedColumn.isHideColumn());
        originalColumn.setHeader(editedColumn.getHeader());
        originalColumn.setDefaultValue(editedColumn.getDefaultValue());
    }

    @Override
    public boolean handlesDelete(final MetaData metaData) throws VetoException {
        if (!(metaData instanceof ColumnMetaData)) {
            return false;
        }
        return ((ColumnMetaData) metaData).getColumn() instanceof MetadataCol52;
    }

    @Override
    public void delete(final BaseColumnSynchronizer.ColumnMetaData metaData) throws VetoException {
        //Check operation is supported
        if (!handlesDelete(metaData)) {
            return;
        }

        final MetadataCol52 column = (MetadataCol52) metaData.getColumn();
        final int columnIndex = model.getExpandedColumns().indexOf(column);
        model.getMetadataCols().remove(column);
        synchroniseDeleteColumn(columnIndex);
    }

    @Override
    public boolean handlesMoveColumnsTo(final List<? extends MetaData> metaData) throws VetoException {
        for (MetaData md : metaData) {
            if (!(md instanceof MoveColumnToMetaData)) {
                return false;
            }
            final BaseColumn column = ((MoveColumnToMetaData) md).getColumn();
            if (!(column instanceof MetadataCol52)) {
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
        final MetadataCol52 modelColumn = (MetadataCol52) md.getColumn();

        final List<MetadataCol52> modelMetaDataColumns = model.getMetadataCols();
        final int modelMetaDataColumnCount = modelMetaDataColumns.size();
        if (modelMetaDataColumnCount == 0) {
            throw new ModelSynchronizer.MoveVetoException();
        }

        final List<BaseColumn> allModelColumns = model.getExpandedColumns();
        final int minColumnIndex = allModelColumns.indexOf(modelMetaDataColumns.get(0));
        final int maxColumnIndex = allModelColumns.indexOf(modelMetaDataColumns.get(modelMetaDataColumnCount - 1));

        final int targetColumnIndex = md.getTargetColumnIndex();
        final int sourceColumnIndex = md.getSourceColumnIndex();
        if (targetColumnIndex < minColumnIndex || targetColumnIndex > maxColumnIndex) {
            throw new ModelSynchronizer.MoveVetoException();
        }

        moveModelData(targetColumnIndex,
                      sourceColumnIndex,
                      sourceColumnIndex);

        modelMetaDataColumns.remove(modelColumn);
        modelMetaDataColumns.add(targetColumnIndex - minColumnIndex,
                                 modelColumn);
    }
}
