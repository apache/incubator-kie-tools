/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

    public static boolean isRedundant( final Collection collection,
                                       final Collection otherCollection ) {
        return subsumes( collection,
                         otherCollection )
                && subsumes( otherCollection,
                             collection );
    }

    /**
     * @param collection
     * @param otherCollection
     * @return True if every object in otherCollection is subsumed by an item in collection.
     */
    public static boolean subsumes( final Collection collection,
                                    final Collection otherCollection ) {
        if ( collection == null || otherCollection == null ) {
            return false;
        }

        if ( Conflict.isConflicting( collection,
                                     otherCollection ) ) {
            return false;
        }

        // Every object in other collection is subsumed by an object in collection.
        for ( Object object : otherCollection ) {
            if ( !isSubsumedByAnObjectInThisList( collection,
                                                  object ) ) {
                return false;
            }
        }

        return true;
    }

    public static boolean isSubsumedByAnObjectInThisList( final Collection otherCollection,
                                                          final Object object ) {
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

}
