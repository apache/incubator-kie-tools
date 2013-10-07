/*
 * Copyright 2012 JBoss Inc
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
package org.drools.workbench.screens.guided.template.client.editor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.Cell.Context;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellTableDropDownDataValueMapProvider;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicData;

/**
 * A utility class to get the values of all InterpolationVariables in the scope
 * of a Template Key to drive dependent enumerations. A value is in scope if it
 * is on a Constraint or Action on the same Pattern of the base column.
 */
public class TemplateDropDownManager
        implements
        CellTableDropDownDataValueMapProvider {

    private final TemplateModel model;
    private final AsyncPackageDataModelOracle oracle;
    private final TemplateDataCellValueFactory cellValueFactory;
    private DynamicData data;

    public TemplateDropDownManager( final TemplateModel model,
                                    final AsyncPackageDataModelOracle oracle ) {
        if ( model == null ) {
            throw new IllegalArgumentException( "data cannot be null" );
        }
        if ( oracle == null ) {
            throw new IllegalArgumentException( "oracle cannot be null" );
        }
        this.cellValueFactory = new TemplateDataCellValueFactory( model,
                                                                  oracle );
        this.model = model;
        this.oracle = oracle;
    }

    public TemplateDropDownManager( final TemplateModel model,
                                    final AsyncPackageDataModelOracle oracle,
                                    final DynamicData data ) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        if ( data == null ) {
            throw new IllegalArgumentException( "data cannot be null" );
        }
        if ( oracle == null ) {
            throw new IllegalArgumentException( "oracle cannot be null" );
        }
        this.cellValueFactory = new TemplateDataCellValueFactory( model,
                                                                  oracle );
        this.model = model;
        this.oracle = oracle;
        this.data = data;
    }

    @Override
    public void setData( DynamicData data ) {
        if ( data == null ) {
            throw new IllegalArgumentException( "data cannot be null" );
        }
        this.data = data;
    }

    /**
     * Create a map of Field Values keyed on Field Names used by
     * SuggestionCompletionEngine.getEnums(String, String, Map<String, String>)
     * to drive dependent enumerations.
     * @param context The Context of the cell being edited containing physical
     * coordinate in the data-space.
     */
    @Override
    public Map<String, String> getCurrentValueMap( Context context ) {
        Map<String, String> currentValueMap = new HashMap<String, String>();

        final int iBaseRowIndex = context.getIndex();
        final int iBaseColIndex = context.getColumn();

        //Get variable for the column being edited
        InterpolationVariable[] allVariables = this.model.getInterpolationVariablesList();
        InterpolationVariable baseVariable = allVariables[ iBaseColIndex ];
        final String baseVariableName = baseVariable.getVarName();

        //Get other variables (and literals) in the same scope as the base variable
        final RuleModelPeerVariableVisitor peerVariableVisitor = new RuleModelPeerVariableVisitor( model,
                                                                                                   baseVariableName );
        List<RuleModelPeerVariableVisitor.ValueHolder> peerVariables = peerVariableVisitor.getPeerVariables();

        //Add other variables values
        for ( RuleModelPeerVariableVisitor.ValueHolder valueHolder : peerVariables ) {
            switch ( valueHolder.getType() ) {
                case TEMPLATE_KEY:
                    final int iCol = getVariableColumnIndex( valueHolder.getValue() );
                    final InterpolationVariable variable = allVariables[ iCol ];
                    final String field = variable.getFactField();

                    //The generic class CellValue can have different data-types so 
                    //we need to convert the cell's value to a String used by the 
                    //dependent enumerations services
                    final CellValue<?> cv = this.data.get( iBaseRowIndex ).get( iCol );
                    final TemplateDataColumn column = cellValueFactory.makeModelColumn( variable );
                    final String value = cellValueFactory.convertToModelCell( column,
                                                                              cv );
                    currentValueMap.put( field,
                                         value );
                    break;
                case VALUE:
                    currentValueMap.put( valueHolder.getFieldName(),
                                         valueHolder.getValue() );
            }
        }

        return currentValueMap;
    }

    private int getVariableColumnIndex( final String variableName ) {
        final InterpolationVariable[] allVariables = this.model.getInterpolationVariablesList();
        for ( int iCol = 0; iCol < allVariables.length; iCol++ ) {
            final InterpolationVariable var = allVariables[ iCol ];
            if ( var.getVarName().equals( variableName ) ) {
                return iCol;
            }
        }
        //This should never happen
        throw new IllegalArgumentException( "Variable '" + variableName + "' not found. This suggests an programming error." );
    }

    @Override
    public Set<Integer> getDependentColumnIndexes( final Context context ) {

        final int iBaseColIndex = context.getColumn();
        final Set<Integer> dependentColumnIndexes = new HashSet<Integer>();

        //Get variable for the column being edited
        final InterpolationVariable[] allVariables = this.model.getInterpolationVariablesList();
        final InterpolationVariable baseVariable = allVariables[ iBaseColIndex ];
        final String baseVariableName = baseVariable.getVarName();

        //Get other variables (and literals) in the same scope as the base variable
        final RuleModelPeerVariableVisitor peerVariableVisitor = new RuleModelPeerVariableVisitor( model,
                                                                                                   baseVariableName );
        List<RuleModelPeerVariableVisitor.ValueHolder> peerVariables = peerVariableVisitor.getPeerVariables();

        //Add other variables values
        for ( RuleModelPeerVariableVisitor.ValueHolder valueHolder : peerVariables ) {
            switch ( valueHolder.getType() ) {
                case TEMPLATE_KEY:
                    final int iCol = getVariableColumnIndex( valueHolder.getValue() );
                    final InterpolationVariable variable = allVariables[ iCol ];
                    final String field = variable.getFactField();

                    if ( oracle.isDependentEnum( baseVariable.getFactType(),
                                                 baseVariable.getFactField(),
                                                 field ) ) {
                        dependentColumnIndexes.add( iCol );
                    }
                    break;
            }
        }

        return dependentColumnIndexes;
    }

}
