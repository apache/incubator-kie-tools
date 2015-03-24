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

public class BooleanConditionInspector
        extends ConditionInspector {

    public Boolean value = null;

    public BooleanConditionInspector( Pattern52 pattern,
                                      String factField,
                                      Boolean value,
                                      String operator ) {
        super( pattern,
               factField );
        if ( operator.equals( "==" ) ) {
            this.value = value;
        } else if ( operator.equals( "!=" ) ) {
            this.value = !value;
        }
    }

    @Override
    public boolean isRedundant( Object other ) {
        if ( other instanceof BooleanConditionInspector ) {
            return value.compareTo( ( (BooleanConditionInspector) other ).value ) == 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean conflicts( Object other ) {
        return !isRedundant( other );
    }

    @Override
    public boolean overlaps( Object other ) {
        return isRedundant( other );
    }

    @Override
    public boolean subsumes( Object other ) {
        return isRedundant( other );
    }

    @Override
    public boolean hasValue() {
        return value != null;
    }
}
