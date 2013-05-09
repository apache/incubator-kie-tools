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

import org.drools.decisiontable.parser.ActionType;
import org.drools.decisiontable.parser.ActionType.Code;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

/**
 * Default description builder for when an explicit column has not been defined
 * in the XLS file. Descriptions are empty Strings.
 */
public class DefaultDescriptionBuilder
        implements
        GuidedDecisionTableSourceBuilder {

    private List<DTCellValue52> values = new ArrayList<DTCellValue52>();

    public Code getActionTypeCode() {
        return ActionType.Code.DESCRIPTION;
    }

    public void populateDecisionTable( final GuidedDecisionTable52 dtable ) {
        for ( int iRow = 0; iRow < this.values.size(); iRow++ ) {
            dtable.getData().get( iRow ).add( 1,
                                              this.values.get( iRow ) );
        }
    }

    public void addCellValue( int row,
                              int column,
                              String value ) {
        this.values.add( new DTCellValue52( "Created from row " + ( row + 1 ) ) );
    }

    public void clearValues() {
        this.values.clear();
    }

    public boolean hasValues() {
        return this.values.size() > 0;
    }

    public String getResult() {
        throw new UnsupportedOperationException( "DefaultDescriptionBuilder does not return DRL." );
    }

    public void addTemplate( int row,
                             int col,
                             String content ) {
        throw new UnsupportedOperationException( "DefaultDescriptionBuilder does implement code snippets." );
    }

}
