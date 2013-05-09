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

import org.drools.decisiontable.parser.ActionType;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

/**
 * Builder for Name Attribute columns
 */
public class GuidedDecisionTableNameBuilder extends AbstractGuidedDecisionTableAttributeBuilder {

    public GuidedDecisionTableNameBuilder( final int row,
                                           final int column,
                                           final ConversionResult conversionResult ) {
        super( row,
               column,
               ActionType.Code.NAME,
               conversionResult );
    }

    public void populateDecisionTable( final GuidedDecisionTable52 dtable ) {
        //ActionType.Code.NAME columns do not have an equivalent
    }

    public void addCellValue( int row,
                              int column,
                              String value ) {
        //ActionType.Code.NAME columns do not have an equivalent
    }

}
