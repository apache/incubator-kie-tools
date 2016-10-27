/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.lookup.criteria;

import org.kie.workbench.common.stunner.core.lookup.AbstractLookupManager;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;

import java.util.*;
import java.util.logging.Logger;

public abstract class AbstractCriteriaLookupManager<I, T, R extends LookupManager.LookupRequest>
        extends AbstractLookupManager<I, T, R> {

    private static Logger LOGGER = Logger.getLogger( AbstractCriteriaLookupManager.class.getName() );

    protected static final String CRITERIA_SEPARATOR = ";";
    protected static final String VALUE_SEPARATOR = "=";
    protected static final String COLLECTION_START_CHAR = "[";
    protected static final String COLLECTION_END_CHAR = "]";
    protected static final String COLLECTION_DELIMITER = ",";

    protected abstract boolean matches( String key, String value, I item );

    @Override
    protected boolean matches( final String criteria, final I item ) {
        final Map<String, String> criterias = parseCriteria( criteria );
        if ( null != criterias ) {
            for ( final Map.Entry<String, String> entry : criterias.entrySet() ) {
                if ( !matches( entry.getKey(), entry.getValue(), item ) ) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static Map<String, String> parseCriteria( final String criteria ) {
        if ( null != criteria ) {
            final Map<String, String> result = new HashMap<>( criteria.length() );
            final String[] criterias = criteria.split( CRITERIA_SEPARATOR );
            for ( String c : criterias ) {
                final String[] pair = parseCriteriaPair( c );
                if ( null != pair ) {
                    result.put( pair[ 0 ], pair[ 1 ] );
                }
            }
            return result;
        }
        return null;
    }

    public static String[] parseCriteriaPair( final String criteria ) {
        if ( null != criteria ) {
            final String[] s = criteria.split( VALUE_SEPARATOR );
            if ( s.length == 0 ) {
                return null;
            }
            if ( s.length != 2 ) {
                throw new IllegalArgumentException( "Cannot parse lookup request criteria [pair='" + criteria + "']" );
            }
            return new String[]{ s[ 0 ].trim(), s[ 1 ] };
        }
        return null;
    }

    protected Set<String> toSet( final String s ) {
        if ( s != null && !s.startsWith( COLLECTION_START_CHAR ) ) {
            return new HashSet<>( 0 );

        } else if ( s != null && s.startsWith( COLLECTION_START_CHAR ) && s.endsWith( COLLECTION_END_CHAR ) ) {
            final String toParse = s.substring( 1, s.length() - 2 );
            final String[] parsed = toParse.split( COLLECTION_DELIMITER );
            final HashSet<String> result = new HashSet<>( parsed.length );
            for ( String p : parsed ) {
                result.add( p.trim() );
            }
            return result;
        }
        return null;
    }

    protected static <T> boolean isIntersect( final Collection<T> c1, final Collection<T> c2 ) {
        if ( c1 == null && c2 == null ) {
            return true;

        } else if ( c1 == null ) {
            return false;

        } else {
            for ( T t : c1 ) {
                if ( c2.contains( t ) ) {
                    return true;
                }
            }
            return false;
        }

    }

}
