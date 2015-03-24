/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util;

import java.util.Collection;

public class Redundancy {

    public static boolean isRedundant( Collection collection,
                                       Collection otherCollection ) {
        return isSubsumptant( collection, otherCollection )
                && isSubsumptant( otherCollection, collection );
    }

    public static boolean isSubsumptant( Collection collection,
                                         Collection otherCollection ) {
        if ( collection == null || otherCollection == null ) {
            return false;
        }

        if ( Conflict.isConflicting( collection,
                                     otherCollection ) ) {
            return false;
        }

        for ( Object object : otherCollection ) {
            if ( !hasSubsumingObjectInList( collection, object ) ) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasSubsumingObjectInList( Collection otherCollection,
                                                    Object object ) {
        if ( object instanceof IsSubsuming ) {
            for ( Object otherObject : otherCollection ) {
                if ( ( (IsSubsuming) object ).subsumes( otherObject ) ) {
                    return true;
                }
            }
            return false;
        } else {
            return otherCollection.contains( object );
        }
    }

    public static boolean hasRedundantObjectInList( Collection otherCollection,
                                                    IsRedundant object ) {
        for ( Object otherObject : otherCollection ) {
            if ( object.isRedundant( otherObject ) ) {
                return true;
            }
        }
        return false;
    }
}
