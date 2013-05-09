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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

/**
 * A ValueBuilder for templates with multiple parameters; $1, $2...$n
 */
public class IndexedParametersValueBuilder
        implements
        ParameterizedValueBuilder {

    private static final Pattern delimiter = Pattern.compile( "(.*?[^\\\\])(,|\\z)" );

    private final String template;

    private final List<String> parameters = new ArrayList<String>();

    private List<List<DTCellValue52>> values = new ArrayList<List<DTCellValue52>>();

    public IndexedParametersValueBuilder( final String template,
                                          final ParameterUtilities parameterUtilities ) {
        this.template = parameterUtilities.convertIndexedParametersToTemplateKeys( template );
        this.parameters.addAll( parameterUtilities.extractTemplateKeys( this.template ) );
    }

    public void addCellValue( final int row,
                              final int column,
                              final String value ) {
        final List<String> cellVals = split( value );
        final List<DTCellValue52> rowValues = new ArrayList<DTCellValue52>();
        for ( int parameterIndex = 0; parameterIndex < getParameters().size(); parameterIndex++ ) {
            final String cv = cellVals.size() > parameterIndex ? cellVals.get( parameterIndex ) : "";
            rowValues.add( new DTCellValue52( cv ) );
        }
        this.values.add( rowValues );
    }

    private List<String> split( final String input ) {
        final Matcher m = delimiter.matcher( input );
        final List<String> result = new ArrayList<String>();
        while ( m.find() ) {
            result.add( m.group( 1 ).replaceAll( "\\\\,",
                                                 "," ) );
        }
        return result;
    }

    @Override
    public String getTemplate() {
        return this.template;
    }

    @Override
    public List<String> getParameters() {
        return this.parameters;
    }

    @Override
    public List<List<DTCellValue52>> getColumnData() {
        return this.values;
    }

}
