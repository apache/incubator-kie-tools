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

public enum Operator {

    NONE( "" ),
    EQUALS( "==" ),
    GREATER_THAN( ">" ),
    LESS_THAN( "<" ),
    GREATER_THAN_OR_EQUALS( ">=" ),
    LESS_THAN_OR_EQUALS( "<=" ),
    NOT_EQUALS( "!=" ),

    IN( "in" );

    private String operator;

    Operator( String operator ) {
        this.operator = operator;
    }

    public static Operator resolve( String operator ) {
        if ( operator.equals( "==" ) ) {
            return EQUALS;
        } else if ( operator.equals( "!=" ) ) {
            return NOT_EQUALS;
        } else if ( operator.equals( "<" ) ) {
            return LESS_THAN;
        } else if ( operator.equals( "<=" ) ) {
            return LESS_THAN_OR_EQUALS;
        } else if ( operator.equals( ">" ) ) {
            return GREATER_THAN;
        } else if ( operator.equals( ">=" ) ) {
            return GREATER_THAN_OR_EQUALS;
        } else if ( operator.equals( "in" ) ) {
            return IN;
        } else {
            return NONE;
        }
    }

}
