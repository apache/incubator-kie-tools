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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.date.TimeFrame;

public class CoreFunction extends DataSetFunction {

    private CoreFunctionFilter coreFunctionFilter = null;

    public CoreFunction(DataSetFilterContext ctx, CoreFunctionFilter coreFunctionFilter) {
        super(ctx, coreFunctionFilter);
        this.coreFunctionFilter = coreFunctionFilter;
    }

    public Comparable getCurrentValue() {
        return (Comparable) getDataColumn().getValues().get(getContext().getCurrentRow());
    }

    public Comparable getParameter(int index) {
        if (index >= coreFunctionFilter.getParameters().size()) {
            return null;
        }
        return (Comparable) coreFunctionFilter.getParameters().get(index);
    }

    public List<Comparable> getParameters() {
        return coreFunctionFilter.getParameters();
    }

    public boolean pass() {
        CoreFunctionType type = coreFunctionFilter.getType();

        if (CoreFunctionType.IS_NULL.equals(type)) {
            return isNull(getCurrentValue());
        }
        if (CoreFunctionType.NOT_NULL.equals(type)) {
            return isNotNull(getCurrentValue());
        }
        if (CoreFunctionType.EQUALS_TO.equals(type)) {
            return isEqualsTo(getCurrentValue());
        }
        if (CoreFunctionType.NOT_EQUALS_TO.equals(type)) {
            return isNotEqualsTo(getCurrentValue());
        }
        if (CoreFunctionType.LIKE_TO.equals(type)) {
            return isLikeTo(getCurrentValue());
        }
        if (CoreFunctionType.LOWER_THAN.equals(type)) {
            return isLowerThan(getCurrentValue());
        }
        if (CoreFunctionType.LOWER_OR_EQUALS_TO.equals(type)) {
            return isLowerThanOrEqualsTo(getCurrentValue());
        }
        if (CoreFunctionType.GREATER_THAN.equals(type)) {
            return isGreaterThan(getCurrentValue());
        }
        if (CoreFunctionType.GREATER_OR_EQUALS_TO.equals(type)) {
            return isGreaterThanOrEqualsTo(getCurrentValue());
        }
        if (CoreFunctionType.BETWEEN.equals(type)) {
            return isBetween(getCurrentValue());
        }
        if (CoreFunctionType.TIME_FRAME.equals(type)) {
            return timeFrame(getCurrentValue());
        }
        if (CoreFunctionType.IN.equals(type)) {
            return isEqualsTo(getCurrentValue());
        }
        if (CoreFunctionType.NOT_IN.equals(type)) {
            return isNotEqualsTo(getCurrentValue());
        }
        throw new IllegalArgumentException("Core function type not supported: " + type);
    }

    public boolean isNull(Comparable value) {
        return value == null;
    }

    public boolean isNotNull(Comparable value) {
        return !isNull(value);
    }

    public boolean compare(Comparable c1, Comparable c2) {
        if (c1 == null && c2 == null) {
            return true;
        }
        if (c1 != null && c2 != null) {
            if ((c1 instanceof Number) && (c2 instanceof Number)) {
                return ((Number) c1).doubleValue() == ((Number) c2).doubleValue();
            }
            return c1.toString().equals(c2.toString());
        }
        return false;
    }

    public boolean isEqualsTo(Comparable value) {
        // No parameters to compare => return true
        if (getParameters().isEmpty()) {
            return true;
        }
        // At least one parameter match is required
        for (Comparable param : getParameters()) {
            if (compare(param, value)) {
                return true;
            }
        }
        return false;
    }

    public boolean isNotEqualsTo(Comparable value) {
        // No parameters to compare => return true
        if (getParameters().isEmpty()) {
            return true;
        }
        return !isEqualsTo(value);
    }

    /**
     * <p>The <code>LIKE_TO</code> operator is intended to emulate the SQL like operator.It's used to search for a specified pattern in a data set's column.</p>
     * <p>Allowed wildcards are:</p>
     * <ul>
     *     <li><code>_</code> - A substitute for a single character.</li>
     *     <li><code>%</code> - A substitute for zero or more characters.</li>
     *     <li><code>[charlist]</code> - Sets and ranges of characters to match.</li>
     *     <li><code>[^charlist]</code> - Matches only a character NOT specified within the brackets.</li>
     * </ul>
     *
     * <p>The call <code>getParameter(0)</code> returns the given user's input pattern used for searching.</p>
     *
     * <p>The implementation is supported for TEXT or LABEL column types and it's case sensitive or
     * unsensitive depending on the boolean value returned by getParameter(1).</p>
     *
     * @param value The existing data set column's value at a given row.
     * @return If the string on current data set's column is like (as the SQL operator) the given user's pattern.
     */
    public boolean isLikeTo(Comparable value) {
        if (value == null) {
            return false;
        }
        final Comparable param0 = getParameter(0);
        if (param0 == null) {
            return false;
        }
        // Case sensitive parameter
        final Boolean caseSensitive = getParameter(1) != null ? Boolean.parseBoolean(getParameter(1).toString()) : true;
        String pattern = caseSensitive ? param0.toString() : param0.toString().toLowerCase();
        String strValue = caseSensitive ? value.toString() : value.toString().toLowerCase();

        // Replace the user's wilcards that come from the UI request for valid regular expression patterns.
        pattern = pattern.replace(".", "\\."); // "\\" is escaped to "\"
        pattern = pattern.replace("%", ".*");
        pattern = pattern.replace("_", ".");

        // Perform the regexp match operation.
        return strValue.matches(pattern);
    }
    
    public boolean isLowerThan(Comparable value) {
        return !isGreaterThanOrEqualsTo(value);
    }

    public boolean isLowerThanOrEqualsTo(Comparable value) {
        return !isGreaterThan(value);
    }

    public boolean isGreaterThan(Comparable value) {
        if (value == null) {
            return false;
        }
        Comparable ref = getParameter(0);
        return ref == null || value.compareTo(ref) == 1;
    }

    public boolean isGreaterThanOrEqualsTo(Comparable value) {
        Comparable ref = getParameter(0);
        if (ref == null) {
            return true;
        } else {
            return value != null && value.compareTo(ref) != -1;
        }
    }

    public boolean isBetween(Comparable value) {
        Comparable low = getParameter(0);
        Comparable high = getParameter(1);

        if (value == null) {
            return low == null;
        }
        if (low != null && value.compareTo(low) == -1) {
            return false;
        }
        if (high != null && value.compareTo(high) == 1) {
            return false;
        }
        return true;
    }

    Map<String, TimeFrameLimits> _timeFrameExprCache = new HashMap<String, TimeFrameLimits>();

    public boolean timeFrame(Comparable value) {
        String timeFrameExpr = getParameter(0).toString();
        if (timeFrameExpr == null || value == null) {
            return false;
        }
        if (!(value instanceof Date)) {
            return false;
        }
        Date target = (Date) value;

        TimeFrameLimits timeFrameLimits = _timeFrameExprCache.get(timeFrameExpr);
        if (timeFrameLimits == null) {
            TimeFrame timeFrame = TimeFrame.parse(getParameter(0).toString());
            _timeFrameExprCache.put(timeFrameExpr, timeFrameLimits = new TimeFrameLimits(timeFrame));
        }

        if (target.before(timeFrameLimits.from)) return false;
        if (target.after(timeFrameLimits.to)) return false;
        return true;
    }

    public class TimeFrameLimits {

        Date from = null;
        Date to = null;

        public TimeFrameLimits(TimeFrame timeFrame) {
            from = timeFrame.getFrom().getTimeInstant();
            to = timeFrame.getTo().getTimeInstant();
        }
    }
}
