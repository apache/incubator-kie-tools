/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;
import java.util.Objects;

import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;

public class SystemControlledColumnValuesSynchronizer {

    private final GuidedDecisionTable52 model;
    private final GuidedDecisionTableUiModel uiModel;
    private final GridWidgetCellFactory gridWidgetCellFactory;
    private final CellUtilities cellUtilities;
    private final ColumnUtilities columnUtilities;
    private final SalienceSynchronizer salienceSynchronizer;
    private final PrioritySynchronizer prioritySynchronizer;

    private interface PostSyncOperation {

        void execute(final int rowNumberColumnIndex,
                     final PrioritySynchronizer.RowNumberChanges rowNumberChanges);
    }

    public SystemControlledColumnValuesSynchronizer(final GuidedDecisionTable52 model,
                                                    final GuidedDecisionTableUiModel uiModel,
                                                    final GridWidgetCellFactory gridWidgetCellFactory,
                                                    final CellUtilities cellUtilities,
                                                    final ColumnUtilities columnUtilities) {
        this.model = model;
        this.uiModel = uiModel;
        this.gridWidgetCellFactory = gridWidgetCellFactory;
        this.cellUtilities = cellUtilities;
        this.columnUtilities = columnUtilities;

        salienceSynchronizer = new SalienceSynchronizer(model,
                                                        uiModel,
                                                        gridWidgetCellFactory,
                                                        cellUtilities,
                                                        columnUtilities);
        prioritySynchronizer = new PrioritySynchronizer(model,
                                                        uiModel,
                                                        gridWidgetCellFactory,
                                                        cellUtilities,
                                                        columnUtilities);
    }

    public void updateSystemControlledColumnValues() {
        updateSystemControlledColumnValues(prioritySynchronizer::update);
    }

    private void updateSystemControlledColumnValues(final PostSyncOperation postSyncOperation) {
        for (final BaseColumn column : model.getExpandedColumns()) {
            if (column instanceof RowNumberCol52) {
                updateRowNumberColumnValues((RowNumberCol52) column,
                                            postSyncOperation);
            } else if (column instanceof AttributeCol52) {
                final AttributeCol52 attrCol = (AttributeCol52) column;
                if (Objects.equals(attrCol.getAttribute(), Attribute.SALIENCE.getAttributeName())) {
                    salienceSynchronizer.updateSalienceColumnValues(attrCol);
                }
            }
        }
    }

    /**
     * Update Row Number column values
     */
    private void updateRowNumberColumnValues(final RowNumberCol52 modelColumn,
                                             final PostSyncOperation postSyncOperation) {
        final PrioritySynchronizer.RowNumberChanges rowNumberChanges = new PrioritySynchronizer.RowNumberChanges();

        final int iModelColumn = model.getExpandedColumns().indexOf(modelColumn);
        for (int rowNumber = 0; rowNumber < model.getData().size(); rowNumber++) {

            final List<DTCellValue52> modelRow = model.getData().get(rowNumber);
            final DTCellValue52 modelCell = modelRow.get(iModelColumn);

            final int oldRowNumber = (Integer) modelCell.getNumericValue();
            final int newRowNumber = rowNumber + 1;

            rowNumberChanges.put(oldRowNumber,
                                 newRowNumber);

            modelCell.setNumericValue(newRowNumber);

            uiModel.setCellValueInternal(rowNumber,
                                         iModelColumn,
                                         gridWidgetCellFactory.convertCell(modelCell,
                                                                      modelColumn,
                                                                      cellUtilities,
                                                                      columnUtilities));
        }

        postSyncOperation.execute(iModelColumn,
                                  rowNumberChanges);
    }

    public void deleteRow(final int rowIndex) {
        prioritySynchronizer.deleteRow(rowIndex);
        updateSystemControlledColumnValues((a, b) -> {/*None*/});
    }

    public void insertRow(final int rowIndex) {
        prioritySynchronizer.insertRow(rowIndex);
        updateSystemControlledColumnValues((a, b) -> {/*None*/});
    }

    public void appendRow() {
        updateSystemControlledColumnValues();
    }
}
