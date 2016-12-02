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
package org.drools.workbench.services.verifier.api.client.reporting;

import java.util.Arrays;
import java.util.HashSet;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ImpossibleMatchIssue
        extends Issue {

    private final String fieldFactType;
    private String fieldName;
    private final String conflictedItem;
    private final String conflictingItem;
    private String ruleId;

    public ImpossibleMatchIssue( @MapsTo("severity") final Severity severity,
                                 @MapsTo("explanationType") final ExplanationType explanationType,
                                 @MapsTo("ruleId") final String ruleId,
                                 @MapsTo("fieldFactType") final String fieldFactType,
                                 @MapsTo("fieldName") final String fieldName,
                                 @MapsTo("conflictedItem") final String conflictedItem,
                                 @MapsTo("conflictingItem") final String conflictingItem,
                                 @MapsTo("rowNumbers") final Integer... rowNumbers ) {
        super( severity,
               explanationType,
               new HashSet<>( Arrays.asList( rowNumbers ) )
             );

        this.ruleId = ruleId;
        this.fieldFactType = fieldFactType;
        this.fieldName = fieldName;
        this.conflictedItem = conflictedItem;
        this.conflictingItem = conflictingItem;
    }

    public String getConflictedItem() {
        return conflictedItem;
    }

    public String getConflictingItem() {
        return conflictingItem;
    }

    public String getFieldFactType() {
        return fieldFactType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getRuleId() {
        return ruleId;
    }
}
