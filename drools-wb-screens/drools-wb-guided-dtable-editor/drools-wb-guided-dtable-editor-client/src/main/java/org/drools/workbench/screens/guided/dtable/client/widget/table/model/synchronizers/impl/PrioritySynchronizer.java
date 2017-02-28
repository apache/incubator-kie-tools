/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;

public class PrioritySynchronizer {


    private final GuidedDecisionTable52 model;
    private final GuidedDecisionTableUiModel uiModel;
    private final GridWidgetCellFactory gridWidgetCellFactory;
    private final CellUtilities cellUtilities;
    private final ColumnUtilities columnUtilities;

    public PrioritySynchronizer( final GuidedDecisionTable52 model,
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

    public void update( final int rowNumberColumnIndex,
                        final RowNumberChanges rowNumberChanges ) {

        final Optional<BaseColumnInfo> optional = getPriorityColumnInfo();

        if ( optional.isPresent() ) {

            final BaseColumnInfo baseColumnInfo = optional.get();

            for ( final List<DTCellValue52> row : model.getData() ) {

                final DTCellValue52 dtCellValue52 = row.get( baseColumnInfo.getColumnIndex() );
                final int oldValue = getNumber( dtCellValue52 );
                final int rowNumber = row.get( rowNumberColumnIndex )
                        .getNumericValue()
                        .intValue() - 1;

                if ( oldValue != 0 ) {

                    if ( oldValue > rowNumber ) {
                        dtCellValue52.setStringValue( "" );
                    } else {
                        dtCellValue52.setStringValue( Integer.toString( rowNumberChanges.get( oldValue ) ) );
                    }

                    uiModel.setCellInternal( rowNumber,
                                             baseColumnInfo.getColumnIndex(),
                                             gridWidgetCellFactory.convertCell( dtCellValue52,
                                                                                baseColumnInfo.getBaseColumn(),
                                                                                cellUtilities,
                                                                                columnUtilities ) );
                }
            }
        }
    }

    private int getNumber( final DTCellValue52 dtCellValue52 ) {
        try {
            return Integer.parseInt( dtCellValue52.getStringValue() );
        } catch ( final NumberFormatException e ) {
            return 0;
        }
    }

    private Optional<BaseColumnInfo> getPriorityColumnInfo() {

        int attributeColumnIndex = 0;

        for ( final BaseColumn baseColumn : model.getExpandedColumns() ) {
            if ( baseColumn instanceof MetadataCol52
                    && GuidedDecisionTable52.HitPolicy.RESOLVED_HIT_METADATA_NAME.equals( ( (MetadataCol52) baseColumn ).getMetadata() ) ) {

                return Optional.of( new BaseColumnInfo( attributeColumnIndex,
                                                        baseColumn ) );
            } else {
                attributeColumnIndex++;
            }
        }
        return Optional.empty();
    }

    public void deleteRow( final int deletedRowIndex ) {


        final int deletedRowNumber = deletedRowIndex + 1;

        if ( GuidedDecisionTable52.HitPolicy.RESOLVED_HIT.equals( model.getHitPolicy() ) ) {

            final Optional<BaseColumnInfo> optional = getPriorityColumnInfo();

            if ( optional.isPresent() ) {

                final BaseColumnInfo baseColumnInfo = optional.get();

                int rowNumber = 0;

                for ( final List<DTCellValue52> row : model.getData() ) {

                    final DTCellValue52 dtCellValue52 = row.get( baseColumnInfo.getColumnIndex() );
                    final int oldValue = getNumber( dtCellValue52 );

                    if ( oldValue == deletedRowNumber ) {
                        dtCellValue52.setNumericValue( 0 );

                        uiModel.setCellInternal( rowNumber,
                                                 baseColumnInfo.getColumnIndex(),
                                                 gridWidgetCellFactory.convertCell( dtCellValue52,
                                                                                    baseColumnInfo.getBaseColumn(),
                                                                                    cellUtilities,
                                                                                    columnUtilities ) );
                    }

                    rowNumber++;
                }
            }
        }
    }

    /**
     * Key = Old row number
     * Value = New row number
     */
    public static class RowNumberChanges
            extends HashMap<Integer, Integer> {

    }

    private class BaseColumnInfo {
        private int columnIndex;
        private BaseColumn baseColumn;

        public BaseColumnInfo( final int columnIndex,
                               final BaseColumn baseColumn ) {
            this.columnIndex = columnIndex;
            this.baseColumn = baseColumn;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        public BaseColumn getBaseColumn() {
            return baseColumn;
        }
    }
}
