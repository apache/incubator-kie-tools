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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.RowSynchronizer.RowMetaData;

@Dependent
public class RowSynchronizer extends BaseSynchronizer<RowMetaData, RowMetaData, RowMetaData> {

    public interface RowMetaData extends Synchronizer.MetaData {

        int getRowIndex();
    }

    public static class RowMetaDataImpl implements RowMetaData {

        private final int rowIndex;

        public RowMetaDataImpl() {
            this(-1);
        }

        public RowMetaDataImpl(final int rowIndex) {
            this.rowIndex = rowIndex;
        }

        @Override
        public int getRowIndex() {
            return rowIndex;
        }
    }

    @Override
    public boolean handlesAppend(final MetaData metaData) throws VetoException {
        return metaData instanceof RowMetaData;
    }

    @Override
    public void append(final RowMetaData metaData) throws VetoException {
        if (!handlesAppend(metaData)) {
            return;
        }
        final List<DTCellValue52> modelRow = new ArrayList<DTCellValue52>();
        model.getData().add(modelRow);

        final GridRow uiModelRow = new BaseGridRow(GuidedDecisionTableView.ROW_HEIGHT);
        uiModel.appendRow(uiModelRow);

        final int rowIndex = uiModel.getRowCount() - 1;
        initialiseRowData(rowIndex);
    }

    @Override
    public boolean handlesInsert(final MetaData metaData) throws VetoException {
        return metaData instanceof RowMetaData;
    }

    @Override
    public void insert(final RowMetaData metaData) throws VetoException {
        if (!handlesAppend(metaData)) {
            return;
        }
        final int rowIndex = metaData.getRowIndex();
        final List<DTCellValue52> modelRow = new ArrayList<DTCellValue52>();
        model.getData().add(rowIndex,
                            modelRow);

        final GridRow uiModelRow = new BaseGridRow(GuidedDecisionTableView.ROW_HEIGHT);
        uiModel.insertRow(rowIndex,
                          uiModelRow);

        initialiseRowData(rowIndex);
    }

    private void initialiseRowData(final int rowIndex) {
        final List<BaseColumn> modelColumns = model.getExpandedColumns();
        final List<DTCellValue52> modelRow = model.getData().get(rowIndex);
        for (int columnIndex = 0; columnIndex < modelColumns.size(); columnIndex++) {
            final BaseColumn modelColumn = modelColumns.get(columnIndex);
            final DTCellValue52 modelCell = makeModelCellValue(modelColumn);
            modelRow.add(modelCell);

            //BaseGridData is sparsely populated; only add values if needed.
            if (modelCell.hasValue()) {
                uiModel.setCellValueInternal(rowIndex,
                                             columnIndex,
                                             gridWidgetCellFactory.convertCell(modelCell,
                                                                               modelColumn,
                                                                               cellUtilities,
                                                                               columnUtilities));
            }
            uiModel.indexColumn(columnIndex);

            //Set-up SelectionManager for Row Number column, to select entire row.
            if (modelColumn instanceof RowNumberCol52) {
                uiModel.getCell(rowIndex,
                                columnIndex).setSelectionStrategy(RowSelectionStrategy.INSTANCE);
            }
        }
    }

    @Override
    public boolean handlesUpdate(final MetaData metaData) throws VetoException {
        //We don't support updating a row at present, but we could; e.g. clear all values etc
        return false;
    }

    @Override
    public List<BaseColumnFieldDiff> update(final RowMetaData originalMetaData,
                                            final RowMetaData editedMetaData) throws VetoException {
        //We don't support updating a row at present, but we could; e.g. clear all values etc
        return Collections.emptyList();
    }

    @Override
    public boolean handlesDelete(final MetaData metaData) throws VetoException {
        return metaData instanceof RowMetaData;
    }

    @Override
    public void delete(final RowMetaData metaData) throws VetoException {
        if (!handlesDelete(metaData)) {
            return;
        }
        final int rowIndex = metaData.getRowIndex();
        final GridData.Range rowRange = uiModel.deleteRow(rowIndex);
        final int minRowIndex = rowRange.getMinRowIndex();
        final int maxRowIndex = rowRange.getMaxRowIndex();
        for (int ri = minRowIndex; ri <= maxRowIndex; ri++) {
            model.getData().remove(minRowIndex);
        }
    }

    @Override
    public boolean handlesMoveColumnsTo(final List<? extends MetaData> metaData) throws VetoException {
        //Moving Row data is delegated to each respective Column Synchronizer
        return false;
    }

    @Override
    public void moveColumnsTo(final List<MoveColumnToMetaData> metaData) throws VetoException {
        //Moving Row data is delegated to each respective Column Synchronizer
    }

    @Override
    public boolean handlesMoveRowsTo(final List<? extends MetaData> metaData) throws VetoException {
        for (MetaData md : metaData) {
            if (!(md instanceof MoveRowToMetaData)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void moveRowsTo(final List<MoveRowToMetaData> metaData) throws VetoException {
        //Check operation is supported
        if (!handlesMoveRowsTo(metaData)) {
            return;
        }

        for (int idx = 0; idx < metaData.size(); idx++) {
            final MoveRowToMetaData md = metaData.get(idx);
            final int sourceRowIndex = md.getSourceRowIndex();
            final int targetRowIndex = md.getTargetRowIndex();
            final List<DTCellValue52> row = md.getRow();

            if (targetRowIndex < sourceRowIndex) {
                model.getData().remove(sourceRowIndex);
                model.getData().add(targetRowIndex,
                                    row);
            } else if (targetRowIndex > sourceRowIndex) {
                model.getData().remove(sourceRowIndex - idx);
                model.getData().add(targetRowIndex - idx,
                                    row);
            }
        }
    }

    @Override
    public boolean handlesSort() throws VetoException {
        return true;
    }

    @Override
    public void sort(final List<Integer> sortOrder) throws VetoException {
        //Check operation is supported
        if (!handlesSort()) {
            return;
        }
        if (sortOrder.size() != model.getData().size()) {
            throw new VetoException();
        }

        final Map<Integer, Integer> sourceToTarget = new HashMap<>();

        int targetRowIndex = 0;
        for (Integer sourceRowIndex : sortOrder) {
            sourceToTarget.put(sourceRowIndex, targetRowIndex++);
        }

        Collections.sort(model.getData(), (o1, o2) -> {
            final int rowIndex1 = o1.get(0).getNumericValue().intValue() - 1;
            final int rowIndex2 = o2.get(0).getNumericValue().intValue() - 1;

            return sourceToTarget.get(rowIndex1) - sourceToTarget.get(rowIndex2);
        });

    }
}
