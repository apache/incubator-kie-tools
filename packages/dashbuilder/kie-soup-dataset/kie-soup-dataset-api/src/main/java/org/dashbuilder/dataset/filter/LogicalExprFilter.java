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
package org.dashbuilder.dataset.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * A logical expression based filter definition.
 */
public class LogicalExprFilter extends ColumnFilter {

    protected LogicalExprType logicalOperator = LogicalExprType.AND;
    protected List<ColumnFilter> logicalTerms = new ArrayList<ColumnFilter>();

    public LogicalExprFilter() {
    }

    public LogicalExprFilter(String columnId, LogicalExprType operator, List<ColumnFilter> terms) {
        super(columnId);
        this.logicalOperator = operator;
        setLogicalTerms(terms);
        setColumnId(columnId);
    }

    public LogicalExprFilter(String columnId, LogicalExprType operator, ColumnFilter... terms) {
        super(columnId);
        this.logicalOperator = operator;
        setLogicalTerms(terms);
        setColumnId(columnId);
    }

    public void setColumnId(String columnId) {
        String oldColumnId = getColumnId();
        super.setColumnId(columnId);

        // Ensure children column refs are synced with its parent.
        for (ColumnFilter childFunction : logicalTerms) {
            String childColumnId = childFunction.getColumnId();
            if (childColumnId == null || childColumnId.equals(oldColumnId)) {
                childFunction.setColumnId(columnId);
            }
        }
    }

    public LogicalExprType getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(LogicalExprType logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public List<ColumnFilter> getLogicalTerms() {
        return logicalTerms;
    }

    public void setLogicalTerms(List<ColumnFilter> logicalTerms) {
        this.logicalTerms = logicalTerms;
    }

    public void addLogicalTerm(ColumnFilter logicalTerm) {
        // Functions with no column reference inherit the column from its parent
        String childColumnId = logicalTerm.getColumnId();
        if (childColumnId == null) {
            logicalTerm.setColumnId(this.getColumnId());
        }
        this.logicalTerms.add(logicalTerm);
    }

    public void setLogicalTerms(ColumnFilter... logicalTerms) {
        this.logicalTerms.clear();
        for (ColumnFilter term : logicalTerms) {
            addLogicalTerm(term);
        }
    }

    public ColumnFilter cloneInstance() {
        LogicalExprFilter clone = new LogicalExprFilter();
        clone.columnId = columnId;
        clone.logicalOperator = logicalOperator;
        for (ColumnFilter term : logicalTerms) {
            clone.logicalTerms.add(term.cloneInstance());
        }
        return clone;
    }

    public boolean equals(Object obj) {
        try {
            LogicalExprFilter other = (LogicalExprFilter) obj;
            if (!super.equals(other)) return false;

            if (logicalOperator != null && !logicalOperator.equals(other.logicalOperator)) return false;
            if (logicalTerms.size() != other.logicalTerms.size()) return false;
            for (ColumnFilter fc : logicalTerms) {
                if (!other.logicalTerms.contains(fc)) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("(");
        for (int i=0; i<logicalTerms.size(); i++) {
            if (i > 0) out.append(" ").append(logicalOperator).append(" ");
            out.append(logicalTerms.get(i).toString());
        }
        out.append(")");
        return out.toString();
    }
}
