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

public class Gap<T extends Number>
        implements Comparable<Gap> {

    private T from;
    private T to;

    public Gap( T from,
                T to ) {
        this.from = from;
        this.to = to;
    }

    public T getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }

    @Override
    public int compareTo( Gap gap ) {
        if ( from == null ) {
            return -1;
        } else if ( to == null ) {
            return 1;
        } else {
            if ( from instanceof Integer ) {
                return compare( to.intValue(),
                                gap.getFrom().intValue() );
            } else {
                return 0;
            }
        }
    }

    private int compare( int x,
                         int y ) {
        return x < y ? -1 : ( x == y ? 0 : 1 );
    }
}
