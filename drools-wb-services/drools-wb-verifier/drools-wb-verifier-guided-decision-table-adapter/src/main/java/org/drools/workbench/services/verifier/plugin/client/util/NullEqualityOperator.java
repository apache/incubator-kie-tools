/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.services.verifier.plugin.client.util;

public enum NullEqualityOperator {

    IS_NULL( "== null",
             "==" ),
    IS_NOT_NULL( "!= null",
                 "!=" );

    private String fullOperator;
    private String shortOperator;

    NullEqualityOperator( final String fullOperator,
                          final String shortOperator ) {
        this.fullOperator = fullOperator;
        this.shortOperator = shortOperator;
    }

    public static boolean contains( final String operator ) {
        for ( final NullEqualityOperator nullEqualityOperator : values() ) {
            if ( nullEqualityOperator.fullOperator.equals( operator ) ) {
                return true;
            }
        }
        return false;
    }

    public static String resolveOperator( final String operator ) {
        for ( final NullEqualityOperator nullEqualityOperator : values() ) {
            if ( nullEqualityOperator.fullOperator.equals( operator ) ) {
                return nullEqualityOperator.shortOperator;
            }
        }

        return operator;
    }

    public boolean matches( final String operator ) {
        return fullOperator.equals( operator );
    }
}
