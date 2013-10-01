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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drools.decisiontable.parser.ActionType.Code;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.template.model.SnippetBuilder;
import org.drools.template.model.SnippetBuilder.SnippetType;
import org.drools.template.parser.DecisionTableParseException;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionMessageType;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

/**
 * Builder for Action columns
 */
public class GuidedDecisionTableRHSBuilder
        implements
        HasColumnHeadings,
        GuidedDecisionTableSourceBuilder {

    private final int headerRow;
    private final int headerCol;
    private final String variable;

    //Map of column headers, keyed on XLS column index
    private final Map<Integer, String> columnHeaders = new HashMap<Integer, String>();

    //Map of column value parsers, keyed on XLS column index
    private final Map<Integer, ParameterizedValueBuilder> valueBuilders = new HashMap<Integer, ParameterizedValueBuilder>();

    //Utility class to convert XLS parameters to BRLFragment Template keys
    private final ParameterUtilities parameterUtilities;

    private ConversionResult conversionResult;

    public GuidedDecisionTableRHSBuilder( final int row,
                                          final int column,
                                          final String boundVariable,
                                          final ParameterUtilities parameterUtilities,
                                          final ConversionResult conversionResult ) {
        this.headerRow = row;
        this.headerCol = column;
        this.variable = boundVariable == null ? "" : boundVariable.trim();
        this.parameterUtilities = parameterUtilities;
        this.conversionResult = conversionResult;
    }

    public void populateDecisionTable( final GuidedDecisionTable52 dtable ) {
        //Sort column builders by column index to ensure Actions are added in the correct sequence
        final Set<Integer> sortedIndexes = new TreeSet<Integer>( this.valueBuilders.keySet() );

        for ( Integer index : sortedIndexes ) {
            final ParameterizedValueBuilder vb = this.valueBuilders.get( index );
            addColumn( dtable,
                       vb,
                       index );
        }
    }

    private void addColumn( final GuidedDecisionTable52 dtable,
                            final ParameterizedValueBuilder vb,
                            final int index ) {
        if ( vb instanceof LiteralValueBuilder ) {
            addLiteralColumn( dtable,
                              (LiteralValueBuilder) vb,
                              index );
        } else {
            addBRLFragmentColumn( dtable,
                                  vb,
                                  index );
        }
    }

    private void addLiteralColumn( final GuidedDecisionTable52 dtable,
                                   final LiteralValueBuilder vb,
                                   final int index ) {
        //Create column - Everything is a BRL fragment (for now)
        final BRLActionColumn column = new BRLActionColumn();
        final FreeFormLine ffl = new FreeFormLine();
        ffl.setText( vb.getTemplate() );
        column.getDefinition().add( ffl );
        final BRLActionVariableColumn parameterColumn = new BRLActionVariableColumn( "",
                                                                                     DataType.TYPE_BOOLEAN );
        column.getChildColumns().add( parameterColumn );
        column.setHeader( this.columnHeaders.get( index ) );
        dtable.getActionCols().add( column );

        //Add column data
        final List<List<DTCellValue52>> columnData = vb.getColumnData();
        final int iColIndex = dtable.getExpandedColumns().indexOf( column.getChildColumns().get( 0 ) );
        for ( int iRow = 0; iRow < columnData.size(); iRow++ ) {
            final List<DTCellValue52> rowData = dtable.getData().get( iRow );
            rowData.addAll( iColIndex,
                            columnData.get( iRow ) );
        }

    }

    private void addBRLFragmentColumn( final GuidedDecisionTable52 dtable,
                                       final ParameterizedValueBuilder vb,
                                       final int index ) {
        //Create column - Everything is a BRL fragment (for now)
        final BRLActionColumn column = new BRLActionColumn();
        final FreeFormLine ffl = new FreeFormLine();
        ffl.setText( vb.getTemplate() );
        column.getDefinition().add( ffl );

        for ( String parameter : vb.getParameters() ) {
            final BRLActionVariableColumn parameterColumn = new BRLActionVariableColumn( parameter,
                                                                                         DataType.TYPE_OBJECT );
            column.getChildColumns().add( parameterColumn );
        }
        column.setHeader( this.columnHeaders.get( index ) );
        dtable.getActionCols().add( column );

        //Add column data
        final List<List<DTCellValue52>> columnData = vb.getColumnData();

        //We can use the index of the first child column to add all data
        final int iColIndex = dtable.getExpandedColumns().indexOf( column.getChildColumns().get( 0 ) );
        for ( int iRow = 0; iRow < columnData.size(); iRow++ ) {
            List<DTCellValue52> rowData = dtable.getData().get( iRow );
            rowData.addAll( iColIndex,
                            columnData.get( iRow ) );
        }
    }

    public void addTemplate( final int row,
                             final int column,
                             final String content ) {
        //Validate column template
        if ( valueBuilders.containsKey( column ) ) {
            final String message = "Internal error: Can't have a code snippet added twice to one spreadsheet column.";
            this.conversionResult.addMessage( message,
                                              ConversionMessageType.ERROR );
            return;
        }

        //Add new template
        String template = content.trim();
        if ( isBoundVar() ) {
            template = variable + "." + template;
        }
        if ( !template.endsWith( ";" ) ) {
            template = template + ";";
        }
        try {
            this.valueBuilders.put( column,
                                    getValueBuilder( template ) );
        } catch ( DecisionTableParseException pe ) {
            this.conversionResult.addMessage( pe.getMessage(),
                                              ConversionMessageType.WARNING );
        }
    }

    private boolean isBoundVar() {
        return !( "".equals( variable ) );
    }

    @Override
    public void setColumnHeader( final int column,
                                 final String value ) {
        this.columnHeaders.put( column,
                                value.trim() );
    }

    private ParameterizedValueBuilder getValueBuilder( final String template ) {
        final SnippetType type = SnippetBuilder.getType( template );
        switch ( type ) {
            case INDEXED:
                return new IndexedParametersValueBuilder( template,
                                                          parameterUtilities );
            case PARAM:
                return new SingleParameterValueBuilder( template,
                                                        parameterUtilities );
            case SINGLE:
                return new LiteralValueBuilder( template );
        }
        throw new DecisionTableParseException( "SnippetBuilder.SnippetType '" + type.toString() + "' is not supported. The column will not be added." );
    }

    public void addCellValue( final int row,
                              final int column,
                              final String value ) {
        //Add new row to column data
        final ParameterizedValueBuilder vb = this.valueBuilders.get( column );
        if ( vb == null ) {
            final String message = "No code snippet for ACTION, above cell " +
                    RuleSheetParserUtil.rc2name( this.headerRow + 2,
                                                 this.headerCol );
            this.conversionResult.addMessage( message,
                                              ConversionMessageType.ERROR );
            return;
        }
        vb.addCellValue( row,
                         column,
                         value );
    }

    public String getResult() {
        throw new UnsupportedOperationException( "GuidedDecisionTableRHSBuilder does not return DRL." );
    }

    public Code getActionTypeCode() {
        return Code.ACTION;
    }

    public void clearValues() {
        throw new UnsupportedOperationException();
    }

    public boolean hasValues() {
        throw new UnsupportedOperationException();
    }

}
