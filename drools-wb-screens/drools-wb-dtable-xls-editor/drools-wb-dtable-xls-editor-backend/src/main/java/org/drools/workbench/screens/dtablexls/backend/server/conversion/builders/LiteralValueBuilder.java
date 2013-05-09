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
import java.util.Collections;
import java.util.List;

import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

/**
 * A ValueBuilder for literal values (i.e. no parameters)
 */
public class LiteralValueBuilder
        implements
        ParameterizedValueBuilder {

    private static final List<String> NO_PARAMETERS = Collections.emptyList();

    private final String template;

    private final List<String> parameters = new ArrayList<String>();

    private final List<List<DTCellValue52>> values = new ArrayList<List<DTCellValue52>>();

    public LiteralValueBuilder( final String template ) {
        this.template = template;
        this.parameters.addAll( NO_PARAMETERS );
    }

    public void addCellValue( final int row,
                              final int column,
                              final String value ) {
        final List<DTCellValue52> rowValues = new ArrayList<DTCellValue52>();
        rowValues.add( new DTCellValue52( RuleSheetParserUtil.isStringMeaningTrue( value ) ) );
        values.add( rowValues );
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
