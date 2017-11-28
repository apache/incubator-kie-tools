/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ColumnSettings {

    public static final String NUMBER_PATTERN = "#,##0.00";
    public static final String EXPRESSION = "value";

    public static final String DATE_PATTERN = "MMM dd, yyyy HH:mm";
    public static final String DATE_YEAR  = "yyyy";
    public static final String DATE_MONTH = "MMM yyyy";
    public static final String DATE_QUARTER = "MMM yyyy";
    public static final String DATE_DAY = "dd MMM";
    public static final String DATE_WEEK = "'Week' dd MMM";
    public static final String DATE_HOUR = "HH'h'";
    public static final String DATE_MINUTE = "mm'm'";
    public static final String DATE_SECOND = "ss's'";

    public static String getFixedExpression(DateIntervalType type) {
        if (DateIntervalType.SECOND.equals(type)) {
            return "value + \"\\\"\"";
        }
        if (DateIntervalType.MINUTE.equals(type)) {
            return "value + \"'\"";
        }
        if (DateIntervalType.HOUR.equals(type)) {
            return "value + \"h\"";
        }
        if (DateIntervalType.QUARTER.equals(type)) {
            return "\"Q\" + value";
        }
        return "value";
    }

    public static String getDatePattern(DateIntervalType type) {
        if (type.getIndex() <= DateIntervalType.SECOND.getIndex()) {
            return DATE_SECOND;
        }
        if (DateIntervalType.MINUTE.equals(type)) {
            return DATE_MINUTE;
        }
        if (DateIntervalType.HOUR.equals(type)) {
            return DATE_HOUR;
        }
        if (DateIntervalType.DAY.equals(type)) {
            return DATE_DAY;
        }
        if (DateIntervalType.WEEK.equals(type)) {
            return DATE_WEEK;
        }
        if (DateIntervalType.MONTH.equals(type)) {
            return DATE_MONTH;
        }
        if (DateIntervalType.QUARTER.equals(type)) {
            return DATE_QUARTER;
        }
        return DATE_YEAR;
    }

    public static ColumnSettings cloneWithDefaults(ColumnSettings columnSettings, DataColumn column) {

        ColumnSettings clone = columnSettings == null ? new ColumnSettings(column.getId()) : columnSettings.cloneInstance();
        ColumnType columnType = column.getColumnType();
        ColumnGroup columnGroup = column.getColumnGroup();
        DateIntervalType intervalType = DateIntervalType.getByName(column.getIntervalType());

        if (clone.columnName == null) {
            clone.columnName = column.getId();
        }
        if (clone.emptyTemplate == null) {
            clone.emptyTemplate = "---";
        }
        if (clone.valuePattern == null) {
            if (intervalType != null && columnGroup.getStrategy().equals(GroupStrategy.DYNAMIC)) {
                clone.valuePattern = getDatePattern(intervalType);
            }
            else if (ColumnType.DATE.equals(columnType)) {
                clone.valuePattern = DATE_PATTERN;
            }
            else if (ColumnType.NUMBER.equals(columnType)) {
                clone.valuePattern = NUMBER_PATTERN;
            }
        }
        if (clone.valueExpression == null) {
            if (intervalType != null && columnGroup.getStrategy().equals(GroupStrategy.FIXED)) {
                clone.valueExpression = getFixedExpression(intervalType);
            }
            else if (!ColumnType.DATE.equals(columnType)) {
                clone.valueExpression = EXPRESSION;
            }
        }
        return clone;
    }

    protected String columnId;
    protected String columnName;
    protected String valueExpression;
    protected String emptyTemplate;
    protected String valuePattern;

    public ColumnSettings() {
    }

    public ColumnSettings(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String name) {
        this.columnName = name;
    }

    public String getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(String valueExpression) {
        this.valueExpression = valueExpression;
    }

    public String getValuePattern() {
        return valuePattern;
    }

    public void setValuePattern(String pattern) {
        this.valuePattern = pattern;
    }

    public String getEmptyTemplate() {
        return emptyTemplate;
    }

    public void setEmptyTemplate(String emptyTemplate) {
        this.emptyTemplate = emptyTemplate;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        if (columnId != null) out.append(columnId).append(" ");
        if (columnName != null) out.append(columnName).append(" ");
        if (valuePattern != null) out.append(valuePattern).append(" ");
        if (valueExpression != null) out.append(valueExpression).append(" ");
        if (emptyTemplate != null) out.append(emptyTemplate).append(" ");
        return out.toString();
    }

    public ColumnSettings cloneInstance() {
        ColumnSettings other = new ColumnSettings();
        other.columnId = columnId;
        other.columnName = columnName;
        other.valuePattern = valuePattern;
        other.valueExpression = valueExpression;
        other.emptyTemplate = emptyTemplate;
        return other;
    }

    public boolean equals(Object obj) {
        try {
            ColumnSettings other = (ColumnSettings) obj;
            if (other == null) {
                return false;
            }
            if (columnId != null && !columnId.equals(other.columnId)) {
                return false;
            }
            if (columnName != null && !columnName.equals(other.columnName)) {
                return false;
            }
            if (valuePattern != null && !valuePattern.equals(other.valuePattern)) {
                return false;
            }
            if (valueExpression != null && !valueExpression.equals(other.valueExpression)) {
                return false;
            }
            if (emptyTemplate != null && !emptyTemplate.equals(other.emptyTemplate)) {
                return false;
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
