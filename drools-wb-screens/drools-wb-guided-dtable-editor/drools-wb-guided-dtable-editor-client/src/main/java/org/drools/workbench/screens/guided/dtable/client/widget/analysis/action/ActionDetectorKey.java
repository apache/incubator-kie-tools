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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.action;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;

public abstract class ActionDetectorKey {

    protected ActionCol52 actionCol;

    protected ActionDetectorKey( ActionCol52 actionCol ) {
        this.actionCol = actionCol;
    }

    @Override
    public boolean equals( Object o ) {
        // Basically it's never equal to any other column but it is to the same column
        if ( this == o ) {
            return true;
        } else if ( o instanceof ActionDetectorKey ) {
            SetFieldColActionDetectorKey other = (SetFieldColActionDetectorKey) o;
            return actionCol == other.actionCol;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return System.identityHashCode( actionCol );
    }

}
