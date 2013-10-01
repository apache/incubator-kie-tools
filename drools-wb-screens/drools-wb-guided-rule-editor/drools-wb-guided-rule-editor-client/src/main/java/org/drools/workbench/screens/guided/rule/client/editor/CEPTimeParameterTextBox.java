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
package org.drools.workbench.screens.guided.rule.client.editor;

import com.google.gwt.regexp.shared.RegExp;
import org.drools.workbench.models.datamodel.rule.HasParameterizedOperator;

/**
 * A TextBox to handle CEP 'time' parameters
 */
public class CEPTimeParameterTextBox extends AbstractCEPRestrictedEntryTextBox {

    // A valid Operator parameter expression (regex lifted from org.drools.core.time.TimeUtils.parseTimeString and improved)
    private static final RegExp VALID_TIME = RegExp.compile( "(^\\+?\\*?$)|(^\\-?\\*?$)|(^((\\d+)[Dd]?)?\\s*((\\d+)[Hh]?)?\\s*((\\d+)[Mm]?)?\\s*((\\d+)[Ss]?)?\\s*((\\d+)([Mm]?[Ss]?)?)?$)" );

    public CEPTimeParameterTextBox( HasParameterizedOperator hop,
                                    int index ) {
        super( hop,
               index );
    }

    @Override
    public boolean isValidValue( String value,
                                 boolean isOnFocusLost ) {
        return VALID_TIME.test( value );
    }

}
