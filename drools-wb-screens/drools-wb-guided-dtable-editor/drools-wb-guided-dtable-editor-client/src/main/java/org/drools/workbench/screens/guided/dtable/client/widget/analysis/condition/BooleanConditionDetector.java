/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;

public class BooleanConditionDetector extends ConditionDetector<BooleanConditionDetector> {

    public Boolean value = null;

    public BooleanConditionDetector( Pattern52 pattern,
                                     String factField,
                                     Boolean value,
                                     String operator ) {
        super( pattern, factField );
        if ( operator.equals( "==" ) ) {
            this.value = value;
        } else if ( operator.equals( "!=" ) ) {
            this.value = !value;
        } else {
            hasUnrecognizedConstraint = true;
        }
    }

    public BooleanConditionDetector( BooleanConditionDetector a,
                                     BooleanConditionDetector b ) {
        super( a, b );
        if ( b.value == null ) {
            value = a.value;
        } else if ( a.value == null ) {
            value = b.value;
        } else if ( a.value.equals( b.value ) ) {
            value = a.value;
        } else {
            impossibleMatch = true;
            value = null;
        }
    }

    public BooleanConditionDetector merge( BooleanConditionDetector other ) {
        return new BooleanConditionDetector( this, other );
    }

}
