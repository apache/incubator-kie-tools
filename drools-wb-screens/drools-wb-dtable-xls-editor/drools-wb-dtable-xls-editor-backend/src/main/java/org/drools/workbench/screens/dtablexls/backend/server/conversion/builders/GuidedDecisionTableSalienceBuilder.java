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

import org.drools.decisiontable.parser.ActionType;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionMessageType;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

/**
 * Builder for Salience Attribute columns
 */
public class GuidedDecisionTableSalienceBuilder extends AbstractGuidedDecisionTableAttributeBuilder {

    private final boolean isSequential;

    public GuidedDecisionTableSalienceBuilder( final int row,
                                               final int column,
                                               final boolean isSequential,
                                               final ConversionResult conversionResult ) {
        super( row,
               column,
               ActionType.Code.SALIENCE,
               conversionResult );
        this.isSequential = isSequential;
    }

    @Override
    public void populateDecisionTable( final GuidedDecisionTable52 dtable,
                                       final int maxRowCount ) {
        final AttributeCol52 column = new AttributeCol52();
        column.setAttribute(Attribute.SALIENCE.getAttributeName());

        //If sequential set column to use reverse row number
        if ( isSequential ) {
            column.setUseRowNumber( true );
            column.setReverseOrder( true );
            final int maxRow = this.values.size();
            for ( int iRow = 0; iRow < maxRow; iRow++ ) {
                final DTCellValue52 dcv = this.values.get( iRow );
                dcv.setNumericValue( Integer.valueOf( maxRow - iRow ) );
            }
        }
        dtable.getAttributeCols().add( column );

        if ( this.values.size() < maxRowCount ) {
            for ( int iRow = this.values.size(); iRow < maxRowCount; iRow++ ) {
                final DTCellValue52 dcv = new DTCellValue52( 0 );
                this.values.add( dcv );
            }
        }

        addColumnData( dtable,
                       column );
    }

    @Override
    public void addCellValue( final int row,
                              final int column,
                              final String content ) {
        String value = content;
        if ( value.startsWith( "(" ) && value.endsWith( ")" ) ) {
            value = value.substring( 1,
                                     value.lastIndexOf( ")" ) - 1 );
        }
        final DTCellValue52 dcv = new DTCellValue52( 0 );
        try {
            dcv.setNumericValue( Integer.valueOf( value ) );

        } catch ( NumberFormatException nfe ) {
            final String message = "Priority is not an integer literal, in cell " + RuleSheetParserUtil.rc2name( row,
                                                                                                                 column );
            this.conversionResult.addMessage( message,
                                              ConversionMessageType.WARNING );
        }
        this.values.add( dcv );
    }

}
