/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.dtablexls.backend.server.conversion.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.decisiontable.parser.ActionType;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionMessageType;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;

/**
 * Builder for Metadata columns
 */
public class GuidedDecisionTableMetadataBuilder
        implements
        GuidedDecisionTableSourceBuilder {

    private int headerRow;
    private int headerCol;
    private Map<Integer, String> definitions = new HashMap<Integer, String>();
    private List<DTCellValue52> values = new ArrayList<DTCellValue52>();

    private ConversionResult conversionResult;

    public GuidedDecisionTableMetadataBuilder( final int row,
                                               final int column,
                                               final ConversionResult conversionResult ) {
        this.headerRow = row;
        this.headerCol = column;
        this.conversionResult = conversionResult;
    }

    public void populateDecisionTable( final GuidedDecisionTable52 dtable ) {
        final MetadataCol52 column = new MetadataCol52();
        final String value = this.definitions.get( headerCol );
        column.setHideColumn( true );
        column.setMetadata( value );
        dtable.getMetadataCols().add( column );
        addColumnData( dtable,
                       column );
    }

    private void addColumnData( final GuidedDecisionTable52 dtable,
                                final DTColumnConfig52 column ) {

        final int rowCount = this.values.size();
        final int iColIndex = dtable.getExpandedColumns().indexOf( column );

        //Add column data
        for ( int iRow = 0; iRow < rowCount; iRow++ ) {
            final List<DTCellValue52> rowData = dtable.getData().get( iRow );
            rowData.add( iColIndex,
                         this.values.get( iRow ) );
        }
    }

    public void addTemplate( final int row,
                             final int column,
                             final String content ) {
        if ( definitions.containsKey( column ) ) {
            final String message = "Internal error: Can't have a code snippet added twice to one spreadsheet column.";
            this.conversionResult.addMessage( message,
                                              ConversionMessageType.ERROR );
        }
        definitions.put( column,
                         content.trim() );
    }

    public void addCellValue( final int row,
                              final int column,
                              final String value ) {
        final String content = this.definitions.get( column );
        if ( content == null ) {
            final String message = "No code snippet for METADATA in cell " + RuleSheetParserUtil.rc2name( this.headerRow + 2,
                                                                                                          this.headerCol );
            this.conversionResult.addMessage( message,
                                              ConversionMessageType.ERROR );
        }
        final DTCellValue52 dcv = new DTCellValue52( value );
        this.values.add( dcv );
    }

    public ActionType.Code getActionTypeCode() {
        return ActionType.Code.METADATA;
    }

    public void clearValues() {
        this.values.clear();
    }

    public boolean hasValues() {
        return this.values.size() > 0;
    }

    public String getResult() {
        throw new UnsupportedOperationException( "GuidedDecisionTableMetadataBuilder does not return DRL." );
    }

}
