/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.engine.filter;

import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.dataset.filter.LogicalExprFilter;
import org.dashbuilder.dataset.filter.LogicalExprType;

public class LogicalFunction extends DataSetFunction {

    private LogicalExprFilter logicalFunctionFilter;
    private List<DataSetFunction> functionTerms = new ArrayList<DataSetFunction>();

    public LogicalFunction(DataSetFilterContext ctx, LogicalExprFilter filter) {
        super(ctx, filter);
        this.logicalFunctionFilter = filter;
    }

    public LogicalFunction addFunctionTerm(DataSetFunction functionTerm) {
        functionTerms.add(functionTerm);
        return this;
    }

    public boolean pass() {
        if (functionTerms .isEmpty()) {
            return true;
        }

        LogicalExprType type = logicalFunctionFilter.getLogicalOperator();

        if (LogicalExprType.NOT.equals(type)) {
            for (DataSetFunction term : functionTerms) {
                boolean termOk = term.pass();
                if (termOk) return false;
            }
            return true;
        }
        if (LogicalExprType.AND.equals(type)) {
            for (DataSetFunction term : functionTerms) {
                boolean termOk = term.pass();
                if (!termOk) return false;
            }
            return true;
        }
        if (LogicalExprType.OR.equals(type)) {
            for (DataSetFunction term : functionTerms) {
                boolean termOk = term.pass();
                if (termOk) return true;
            }
            return false;
        }
        throw new IllegalArgumentException("Logical operator not supported: " + type);
    }
}
