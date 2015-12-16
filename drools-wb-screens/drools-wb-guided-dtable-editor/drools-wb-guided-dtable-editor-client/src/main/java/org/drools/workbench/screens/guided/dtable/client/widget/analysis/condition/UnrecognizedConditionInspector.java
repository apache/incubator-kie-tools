/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;

public class UnrecognizedConditionInspector
        extends ConditionInspector {

    private final String operator;

    public UnrecognizedConditionInspector( final Pattern52 pattern,
                                           final String factField,
                                           final String operator ) {
        super( pattern,
               factField );
        this.operator = operator;
    }

    @Override
    public boolean isRedundant( Object b ) {
        return false;
    }

    @Override
    public boolean conflicts( Object other ) {
        return false;
    }

    @Override
    public boolean overlaps( Object other ) {
        return false;
    }

    @Override
    public boolean subsumes( Object other ) {
        return false;
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public String toHumanReadableString() {
        return getFactField() + " " + operator;
    }
}
