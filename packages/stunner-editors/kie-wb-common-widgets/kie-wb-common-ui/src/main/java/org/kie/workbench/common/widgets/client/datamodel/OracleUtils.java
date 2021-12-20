/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.widgets.client.datamodel;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Utilities shared between the different Oracles
 */
public class OracleUtils {

    /**
     * Join an arbitrary number of arrays together
     * @param first
     * @param others
     * @return
     */
    public static String[] joinArrays( final String[] first,
                                       final String[]... others ) {
        int totalLength = first.length;
        for ( String[] other : others ) {
            totalLength = totalLength + other.length;
        }
        String[] result = new String[ totalLength ];

        System.arraycopy( first,
                          0,
                          result,
                          0,
                          first.length );
        int offset = first.length;
        for ( String[] other : others ) {
            System.arraycopy( other,
                              0,
                              result,
                              offset,
                              other.length );
            offset = offset + other.length;
        }
        return result;
    }

    /**
     * Return a Set as a String array
     * @param set
     * @return
     */
    public static String[] toStringArray( final Set<?> set ) {
        final String[] f = new String[ set.size() ];
        int i = 0;
        for ( final Iterator<?> iter = set.iterator(); iter.hasNext(); i++ ) {
            f[ i ] = iter.next().toString();
        }
        return f;
    }

    /**
     * Return a List as a String array
     * @param list
     * @return
     */
    public static String[] toStringArray( final List<?> list ) {
        final String[] f = new String[ list.size() ];
        int i = 0;
        for ( final Iterator<?> iter = list.iterator(); iter.hasNext(); i++ ) {
            f[ i ] = iter.next().toString();
        }
        return f;
    }

}
