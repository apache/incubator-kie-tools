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

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

import java.util.HashSet;
import java.util.Set;

public class ActionDetector {

    protected ActionDetectorKey key;

    protected Set<DTCellValue52> valueSet = null;

    protected boolean duplicated = false;

    public ActionDetector( ActionDetectorKey key,
                           DTCellValue52 value ) {
        this.key = key;
        valueSet = new HashSet<DTCellValue52>( 2 );
        valueSet.add( value );
    }

    protected ActionDetector( ActionDetector a,
                              ActionDetector b ) {
        if ( !a.key.equals( b.key ) ) {
            throw new IllegalArgumentException( "The ActionDetectorKey of a and b are not equal." );
        }
        key = a.key;
        valueSet = new HashSet<DTCellValue52>( a.valueSet );
        duplicated = a.duplicated || b.duplicated;
        for ( DTCellValue52 bValue : b.valueSet ) {
            duplicated = duplicated || !valueSet.add( bValue );
        }
    }

    public ActionDetectorKey getKey() {
        return key;
    }

    public boolean isDuplicated() {
        return duplicated;
    }

    public boolean isMultipleValuesForOneAction() {
        return valueSet.size() > 1;
    }

    public ActionDetector merge( ActionDetector other ) {
        return new ActionDetector( this, other );
    }

}
