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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Conflict {

    public static boolean isConflicting( final Collection collection,
                                         final Collection otherCollection ) {

        if ( collection == null || otherCollection == null ) {
            return false;
        }

        for ( Object o : collection ) {
            if ( o instanceof IsConflicting ) {
                if ( hasConflictingObjectInList( otherCollection, (IsConflicting) o ) ) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasConflictingObjectInList( final Collection collection,
                                                      final IsConflicting isConflicting ) {
        return !getConflictingObjects( collection,
                                       isConflicting ).isEmpty();
    }

    public static List getConflictingObjects( final Collection collection,
                                              final IsConflicting isConflicting ) {
        ArrayList result = new ArrayList();

        if ( isConflicting == null || collection == null ) {
            return result;
        }

        for ( Object other : collection ) {
            if ( isConflicting.conflicts( other ) ) {
                result.add( isConflicting );
                result.add( other );
                return result;
            }
        }

        return result;
    }
}
