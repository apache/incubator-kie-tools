/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys;

import java.util.ArrayList;
import java.util.Set;

public class Values<T extends Comparable>
        extends ArrayList<Comparable> {

    public Values( final Comparable... list ) {
        super();

        for ( final Comparable comparable : list ) {
            add( comparable );
        }
    }

    public Values() {
    }

    public boolean isThereChanges( final Values otherValues ) {
        if ( this.isEmpty() && !otherValues.isEmpty() ) {
            return true;
        } else if ( !this.isEmpty() && otherValues.isEmpty() ) {
            return true;
        } else if ( this.isEmpty() && otherValues.isEmpty() ) {
            return false;
        } else if ( this.size() != otherValues.size() ) {
            return true;
        } else if ( !areValuesEqual( otherValues ) ) {
            return true;
        } else {
            return false;
        }
    }

    private boolean areValuesEqual( final Values otherValues ) {

        if ( !otherValues.containsAll( this ) ) {
            return false;
        }

        if ( !containsAll( otherValues ) ) {
            return false;
        }

        return true;
    }

    public boolean areEqual( final Set<Value> valueSet ) {
        final Values values = toValues( valueSet );

        return areValuesEqual( values );
    }

    public static Values toValues( final Set<Value> set ) {
        final Values values = new Values();
        for ( final Value value : set ) {
            values.add( value.getComparable() );
        }
        return values;
    }

    private boolean isOnlyValueNull( final Set<Comparable> comparable ) {
        return comparable.size() == 1 && comparable.iterator().next() == null;
    }

    public boolean containsAny( final Values values ) {
        for ( final Object value : values ) {
            if ( contains( value ) ) {
                return true;
            }
        }

        return false;
    }

    public static <T extends Comparable> Values<T> nullValue() {
        final Values<T> comparables = new Values<>();
        comparables.add( null );
        return comparables;
    }
}
