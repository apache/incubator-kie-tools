/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.api.client.reporting;

import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;


@Portable
public class MultipleValuesForOneActionIssue
        extends Issue{

    private final String conflictedItem;
    private final String conflictingItem;

    public MultipleValuesForOneActionIssue( @MapsTo("severity") final Severity severity,
                                            @MapsTo("checkType") final CheckType checkType,
                                            @MapsTo("conflictedItem") final String conflictedItem,
                                            @MapsTo("conflictingItem") final String conflictingItem,
                                            @MapsTo("rowNumbers") final Set<Integer> rowNumbers ) {
        super( severity,
               checkType,
               rowNumbers );

        this.conflictedItem = conflictedItem;
        this.conflictingItem = conflictingItem;
    }

    public String getConflictedItem() {
        return conflictedItem;
    }

    public String getConflictingItem() {
        return conflictingItem;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
