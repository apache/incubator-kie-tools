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

import java.util.Comparator;
import java.util.TreeSet;

public class Scale
        extends TreeSet<Point> {

    public Scale() {
        super( new Comparator<Point>() {
            @Override
            public int compare( Point point,
                                Point t1 ) {

                if ( point.getValue().equals( t1.getValue() ) ) {
                    switch ( point.getOperator() ) {
                        case EQUALS:
                        case GREATER_THAN_OR_EQUALS:
                        case LESS_THAN_OR_EQUALS:
                            switch ( t1.getOperator() ) {
                                case GREATER_THAN:
                                    return 1;
                                case LESS_THAN:
                                    return -1;
                            }
                            break;
                        case GREATER_THAN:
                            switch ( t1.getOperator() ) {
                                case EQUALS:
                                case GREATER_THAN_OR_EQUALS:
                                case LESS_THAN_OR_EQUALS:
                                case LESS_THAN:
                                    return -1;
                            }
                            break;
                        case LESS_THAN:
                            switch ( t1.getOperator() ) {
                                case EQUALS:
                                case GREATER_THAN_OR_EQUALS:
                                case LESS_THAN_OR_EQUALS:
                                case GREATER_THAN:
                                    return 1;
                            }
                            break;
                    }
                }

                return point.getValue().compareTo( t1.getValue() );
            }
        } );
    }

    public TreeSet<Gap> getGaps() {
        TreeSet<Gap> gaps = new TreeSet<Gap>();
        for ( Point point : this ) {
            int value = point.getValue();
            int previous = value - 1;
            int next = value + 1;

            if ( !covers( previous ) ) {
                if ( covers( value ) ) {
                    gaps.add( new Gap( null, previous ) );
                } else {
                    gaps.add( new Gap( null, value ) );
                }
            } else if ( !covers( next ) ) {
                if ( covers( value ) ) {
                    gaps.add( new Gap( value, null ) );
                } else {
                    gaps.add( new Gap( next, null ) );
                }
            }
        }

        return gaps;
    }

    private boolean covers( int value ) {
        for ( Point point : this ) {
//            if (point.covers(value)) {
//                return true;
//            }
        }

        return false;
    }
}
