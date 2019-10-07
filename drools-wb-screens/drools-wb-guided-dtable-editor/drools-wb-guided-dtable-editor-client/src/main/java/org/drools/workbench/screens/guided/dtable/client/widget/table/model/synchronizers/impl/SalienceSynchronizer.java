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

import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.SalienceUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

public class SalienceSynchronizer {


    private final GuidedDecisionTable52 model;
    private final GuidedDecisionTableUiModel uiModel;
    private final GridWidgetCellFactory gridWidgetCellFactory;
    private final CellUtilities cellUtilities;
    private final ColumnUtilities columnUtilities;

    public SalienceSynchronizer( final GuidedDecisionTable52 model,
                                 final GuidedDecisionTableUiModel uiModel,
                                 final GridWidgetCellFactory gridWidgetCellFactory,
                                 final CellUtilities cellUtilities,
                                 final ColumnUtilities columnUtilities ) {
        this.model = model;
        this.uiModel = uiModel;
        this.gridWidgetCellFactory = gridWidgetCellFactory;
        this.cellUtilities = cellUtilities;
        this.columnUtilities = columnUtilities;
    }

    /**
     * Update Salience column definition and values
     */
    public void updateSalienceColumnValues( final AttributeCol52 modelColumn ) {
        final int iModelColumn = model.getExpandedColumns().indexOf( modelColumn );

        final GridColumn<?> uiColumn = uiModel.getColumns().get( iModelColumn );

        if ( uiColumn instanceof SalienceUiColumn ) {
            ( (SalienceUiColumn) uiColumn ).setUseRowNumber( modelColumn.isUseRowNumber() );
        }

        //If Salience values are-user defined, exit
        if ( modelColumn.isUseRowNumber() ) {
            setSalienceByRowNumbers( modelColumn,
                                     iModelColumn );

        }
    }

    private void setSalienceByRowNumbers( final AttributeCol52 modelColumn,
                                          final int iModelColumn ) {
        //If Salience values are reverse order derive them and update column
        int salience = modelColumn.isReverseOrder() ? model.getData().size() : 1;
        for (int rowNumber = 0; rowNumber < model.getData().size(); rowNumber++) {
            final List<DTCellValue52> modelRow = model.getData().get( rowNumber );
            final DTCellValue52 modelCell = modelRow.get( iModelColumn );
            modelCell.setNumericValue( salience );

            uiModel.setCellValueInternal(rowNumber,
                                         iModelColumn,
                                         gridWidgetCellFactory.convertCell( modelCell,
                                                                        modelColumn,
                                                                        cellUtilities,
                                                                        columnUtilities ) );
            if ( modelColumn.isReverseOrder() ) {
                salience--;
            } else {
                salience++;
            }
        }
        uiModel.indexColumn( iModelColumn );
    }

}
