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
package org.drools.workbench.screens.dtablexls.backend.server.conversion.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.decisiontable.parser.ActionType;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionMessageType;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

/**
 * Abstract builder for all Attribute columns
 */
public abstract class AbstractGuidedDecisionTableAttributeBuilder
        implements
        GuidedDecisionTableSourceBuilderDirect {

    protected final int headerRow;
    protected final int headerCol;
    protected final ActionType.Code actionType;
    protected final Map<Integer, String> definitions;
    protected final List<DTCellValue52> values;

    protected final ConversionResult conversionResult;

    public AbstractGuidedDecisionTableAttributeBuilder( final int row,
                                                        final int column,
                                                        final ActionType.Code actionType,
                                                        final ConversionResult conversionResult ) {
        this.headerRow = row;
        this.headerCol = column;
        this.actionType = actionType;
        this.definitions = new HashMap<Integer, String>();
        this.values = new ArrayList<DTCellValue52>();
        this.conversionResult = conversionResult;
    }

    protected void addColumnData( final GuidedDecisionTable52 dtable,
                                  final DTColumnConfig52 column ) {
        int rowCount = this.values.size();
        int iColIndex = dtable.getExpandedColumns().indexOf( column );

        //Add column data
        for ( int iRow = 0; iRow < rowCount; iRow++ ) {
            List<DTCellValue52> rowData = dtable.getData().get( iRow );
            rowData.add( iColIndex,
                         this.values.get( iRow ) );
        }
    }

    @Override
    public ActionType.Code getActionTypeCode() {
        return this.actionType;
    }

    @Override
    public void clearValues() {
        this.values.clear();
    }

    @Override
    public boolean hasValues() {
        return this.values.size() > 0;
    }

    @Override
    public void addTemplate( final int row,
                             final int column,
                             final String content ) {
        if ( content == null || content.trim().equals( "" ) ) {
            return;
        }
        final String message = "Internal error: ActionType '" + actionType.getColHeader() + "' does not need a code snippet.";
        this.conversionResult.addMessage( message,
                                          ConversionMessageType.ERROR );
    }

    @Override
    public String getResult() {
        throw new UnsupportedOperationException( this.getClass().getSimpleName() + " does not return DRL." );
    }

    @Override
    public int getRowCount() {
        return values.size();
    }

    @Override
    public int getColumn() {
        return headerCol;
    }
}
