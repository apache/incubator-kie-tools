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

import java.util.Set;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ImpossibleMatchIssue
        extends Issue {

    private final String fieldFactType;
    private final String fieldName;
    private final String conflictedItem;
    private final String conflictingItem;
    private final String ruleId;

    public ImpossibleMatchIssue( @MapsTo("severity") final Severity severity,
                                 @MapsTo("checkType") final CheckType checkType,
                                 @MapsTo("ruleId") final String ruleId,
                                 @MapsTo("fieldFactType") final String fieldFactType,
                                 @MapsTo("fieldName") final String fieldName,
                                 @MapsTo("conflictedItem") final String conflictedItem,
                                 @MapsTo("conflictingItem") final String conflictingItem,
                                 @MapsTo("rowNumbers") final Set<Integer> rowNumbers ) {
        super( severity,
               checkType,
               rowNumbers
             );

        this.ruleId = ruleId;
        this.fieldFactType = fieldFactType;
        this.fieldName = fieldName;
        this.conflictedItem = conflictedItem;
        this.conflictingItem = conflictingItem;
    }

    public String getFieldFactType() {
        return fieldFactType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getConflictedItem() {
        return conflictedItem;
    }

    public String getConflictingItem() {
        return conflictingItem;
    }

    public String getRuleId() {
        return ruleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ImpossibleMatchIssue that = (ImpossibleMatchIssue) o;

        if (fieldFactType != null ? !fieldFactType.equals(that.fieldFactType) : that.fieldFactType != null) {
            return false;
        }
        if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) {
            return false;
        }
        if (conflictedItem != null ? !conflictedItem.equals(that.conflictedItem) : that.conflictedItem != null) {
            return false;
        }
        if (conflictingItem != null ? !conflictingItem.equals(that.conflictingItem) : that.conflictingItem != null) {
            return false;
        }
        return ruleId != null ? ruleId.equals(that.ruleId) : that.ruleId == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fieldFactType != null ? ~~fieldFactType.hashCode() : 0);
        result = 31 * result + (fieldName != null ? ~~fieldName.hashCode() : 0);
        result = 31 * result + (conflictedItem != null ? ~~conflictedItem.hashCode() : 0);
        result = 31 * result + (conflictingItem != null ? ~~conflictingItem.hashCode() : 0);
        result = 31 * result + (ruleId != null ? ~~ruleId.hashCode() : 0);
        return result;
    }
}
