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

public abstract class ConditionDetector<T extends ConditionDetector> {

    protected ConditionDetectorKey key;

    protected boolean hasUnrecognizedConstraint = false;
    protected boolean impossibleMatch = false;

    protected ConditionDetector( Pattern52 pattern,
                                 String factField ) {
        this.key = new ConditionDetectorKey( pattern, factField );
    }

    protected ConditionDetector( T a,
                                 T b ) {
        if ( !a.getKey().equals( b.getKey() ) ) {
            throw new IllegalArgumentException( "The ConditionDetectorKey of a and b are not equal." );
        }
        this.key = a.getKey();
        hasUnrecognizedConstraint = a.hasUnrecognizedConstraint() || b.hasUnrecognizedConstraint;
    }

    public ConditionDetectorKey getKey() {
        return key;
    }

    public Pattern52 getPattern() {
        return key.getPattern();
    }

    public String getFactField() {
        return key.getFactField();
    }

    public boolean hasUnrecognizedConstraint() {
        return hasUnrecognizedConstraint;
    }

    public boolean isImpossibleMatch() {
        return impossibleMatch;
    }

    public abstract T merge( T other );

}
