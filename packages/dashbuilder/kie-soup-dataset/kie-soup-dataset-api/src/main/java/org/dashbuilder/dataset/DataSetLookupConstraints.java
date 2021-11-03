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
package org.dashbuilder.dataset;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.impl.DataSetLookupBuilderImpl;

/**
 * A set of constraints over the structure of a DataSetLookup instance.
 */
public class DataSetLookupConstraints extends DataSetConstraints<DataSetLookupConstraints> {

    public static final int ERROR_GROUP_NUMBER = 200;
    public static final int ERROR_GROUP_NOT_ALLOWED = 201;
    public static final int ERROR_GROUP_REQUIRED = 203;
    public static final int ERROR_DUPLICATED_COLUMN_ID = 204;

    protected boolean uniqueColumnIds = false;
    protected boolean filterAllowed = true;
    protected boolean groupAllowed = true;
    protected boolean groupRequired = false;
    protected int maxGroups = -1;
    protected String groupsTitle = "Rows";
    protected String columnsTitle = "Columns";
    protected boolean groupColumn = false;
    protected boolean functionRequired = false;
    protected Map<Integer,String> columnTitleMap = new HashMap<Integer,String>();

    public boolean isUniqueColumnIds() {
        return uniqueColumnIds;
    }

    public DataSetLookupConstraints setUniqueColumnIds(boolean uniqueColumnIds) {
        this.uniqueColumnIds = uniqueColumnIds;
        return this;
    }

    public boolean isFilterAllowed() {
        return filterAllowed;
    }

    public DataSetLookupConstraints setFilterAllowed(boolean filterAllowed) {
        this.filterAllowed = filterAllowed;
        return this;
    }

    public boolean isGroupAllowed() {
        return groupAllowed;
    }

    public DataSetLookupConstraints setGroupAllowed(boolean groupAllowed) {
        this.groupAllowed = groupAllowed;
        return this;
    }

    public boolean isGroupRequired() {
        return groupRequired;
    }

    public DataSetLookupConstraints setGroupRequired(boolean groupRequired) {
        this.groupRequired = groupRequired;
        return this;
    }

    public int getMaxGroups() {
        return maxGroups;
    }

    public DataSetLookupConstraints setMaxGroups(int maxGroups) {
        this.maxGroups = maxGroups;
        return this;
    }

    public String getGroupsTitle() {
        return groupsTitle;
    }

    public DataSetLookupConstraints setGroupsTitle(String groupsTitle) {
        this.groupsTitle = groupsTitle;
        return this;
    }

    public String getColumnsTitle() {
        return columnsTitle;
    }

    public DataSetLookupConstraints setColumnsTitle(String columnsTitle) {
        this.columnsTitle = columnsTitle;
        return this;
    }

    public DataSetLookupConstraints setColumnTitle(Integer index, String title) {
        columnTitleMap.put(index, title);
        return this;
    }

    public String getColumnTitle(Integer index) {
        return columnTitleMap.get(index);
    }

    public boolean isGroupColumn() {
        return groupColumn;
    }

    public DataSetLookupConstraints setGroupColumn(boolean groupColumn) {
        this.groupColumn = groupColumn;
        return this;
    }

    public boolean isFunctionRequired() {
        return functionRequired;
    }

    public DataSetLookupConstraints setFunctionRequired(boolean functionRequired) {
        this.functionRequired = functionRequired;
        return this;
    }

    public ValidationError check(DataSetLookup lookup) {
        return check(lookup, null);
    }

    public ValidationError check(DataSetLookup lookup, DataSetMetadata metadata) {

        List<DataSetGroup> grOps = lookup.getOperationList(DataSetGroup.class);
        int lastGop = lookup.getLastGroupOpIndex(0);

        if (!groupAllowed && lastGop != -1) {
            DataSetGroup groupOp = lookup.getOperation(lastGop);
            if (groupOp.getColumnGroup() != null) {
                return createValidationError(ERROR_GROUP_NOT_ALLOWED);
            }
        }
        if (groupRequired && lastGop == -1) {
            return createValidationError(ERROR_GROUP_REQUIRED);
        }
        if (maxGroups != -1 && grOps.size() > maxGroups) {
            return createValidationError(ERROR_GROUP_NUMBER);
        }
        if (lastGop != -1) {
            DataSetGroup groupOp = lookup.getOperation(lastGop);
            if (groupRequired && groupOp.getColumnGroup() == null) {
                return createValidationError(ERROR_GROUP_REQUIRED);
            }
            List<GroupFunction> groupFunctions = groupOp.getGroupFunctions();
            if (minColumns != -1 && groupFunctions.size() < minColumns) {
                return createValidationError(ERROR_COLUMN_NUMBER);
            }
            if (maxColumns != -1 && groupFunctions.size() > maxColumns) {
                return createValidationError(ERROR_COLUMN_NUMBER);
            }
            if (uniqueColumnIds) {
                Set<String> columnIds = new HashSet<String>();
                for (GroupFunction groupFunction : groupFunctions) {
                    String columnId = groupFunction.getColumnId();
                    if (columnId != null) {
                        if (columnIds.contains(columnId)) {
                            return createValidationError(ERROR_DUPLICATED_COLUMN_ID, columnId);
                        } else {
                            columnIds.add(columnId);
                        }
                    }
                }
            }
            if (metadata != null) {
                int currentColumns  = -1;
                boolean ok = false;
                ValidationError error = null;
                for (ColumnType[] types : columnTypeList) {
                    if (currentColumns < 0 || currentColumns < types.length) {
                        currentColumns = types.length;
                    }
                    error = checkTypes(metadata, groupOp, types);
                    if (!ok && error == null) {
                        ok = true;
                    }
                }
                if (!ok) {
                    return error;
                }

                // Check extra columns type
                if (currentColumns > 0 && extraColumnsAllowed && extraColumnsType != null && groupFunctions.size() > currentColumns) {
                    for (int i = currentColumns; i < groupFunctions.size(); i++) {
                        GroupFunction gf = groupFunctions.get(i);
                        ColumnType columnType = metadata.getColumnType(gf.getSourceId());
                        if (!columnType.equals(extraColumnsType)) {
                            return createValidationError(ERROR_COLUMN_TYPE, i, extraColumnsType, columnType);
                        }
                    }
                }
                return null;
            }
        }
        return null;
    }

    private ValidationError checkTypes(DataSetMetadata metadata, DataSetGroup groupOp, ColumnType[] types) {
        ColumnGroup columnGroup = groupOp.getColumnGroup();
        List<GroupFunction> groupFunctions = groupOp.getGroupFunctions();
        for (int i = 0; i < groupFunctions.size(); i++) {

            GroupFunction gf = groupFunctions.get(i);
            ColumnType columnType = metadata.getColumnType(gf.getSourceId());
            if (i < types.length && !columnType.equals(types[i])) {

                boolean isGroupColumn = columnGroup != null && columnGroup.getSourceId().equals(gf.getSourceId());
                boolean isGroupLabel = isGroupColumn && types[i].equals(ColumnType.LABEL);
                boolean isFunctionColumn = gf.getFunction() != null && !columnType.equals(ColumnType.NUMBER);

                if (!isGroupLabel && !isFunctionColumn) {
                    return createValidationError(ERROR_COLUMN_TYPE, i, types[i], columnType);
                }
            }
        }
        return null;
    }

    protected ValidationError createValidationError(int error, Object... params) {
        switch (error) {
            case ERROR_GROUP_NOT_ALLOWED:
                return new ValidationError(error, "Group not allowed");
            case ERROR_GROUP_REQUIRED:
                String groupColumn = groupsTitle != null ? groupsTitle : "Group";
                return new ValidationError(error, groupColumn + " column required");
            case ERROR_GROUP_NUMBER:
                return new ValidationError(error, "Max. groups allowed exceeded " + maxGroups);
            case ERROR_DUPLICATED_COLUMN_ID:
                String columnId = (String) params[0];
                return new ValidationError(error, "Column id '" + columnId + "' is duplicated");
        }
        return super.createValidationError(error, params);
    }

    public DataSetLookup newDataSetLookup(DataSetMetadata metatada) {
        DataSetLookupBuilder<DataSetLookupBuilderImpl> builder = DataSetLookupFactory.newDataSetLookupBuilder();
        builder.dataset(metatada.getUUID());

        Set<Integer> exclude = new HashSet<Integer>();
        int startIndex = 0;

        // A group lookup requires to add a group-ready column
        if (groupRequired) {
            int groupIdx = getGroupColumn(metatada);
            if (groupIdx == -1) {
                throw new IllegalStateException("The data set does not contains group-able columns (label or date)");
            }
            // Add the group column
            exclude.add(groupIdx);
            builder.group(metatada.getColumnId(groupIdx));
            builder.column(metatada.getColumnId(groupIdx));
            startIndex = 1;
        }
        // If no target columns has been specified then take the minimum requested
        ColumnType[] types = getColumnTypes();
        if (types == null || types.length == 0) {

            if (minColumns > 0 && minColumns < metatada.getNumberOfColumns()) {
                types = new ColumnType[minColumns];
            }
            else {
                types = new ColumnType[metatada.getNumberOfColumns()];
            }

            for (int i = 0; i < types.length; i++) {
                types[i] = metatada.getColumnType(i);
            }
        }
        // Add the columns to the lookup
        for (int i=startIndex; i<types.length; i++) {
            ColumnType targetType = types[i];

            // Do the best to get a new (not already added) column for the targetType.
            int idx = getTargetColumn(metatada, targetType, exclude);

            // Otherwise, get the first column available.
            if (idx == -1) {
                idx = getTargetColumn(metatada, exclude);
            }

            String columnId = metatada.getColumnId(idx);
            ColumnType columnType = metatada.getColumnType(idx);
            exclude.add(idx);
            DataSetLookup currentLookup = builder.buildLookup();
            String uniqueColumnId = buildUniqueColumnId(currentLookup, columnId);
            String uniqueCountId = buildUniqueColumnId(currentLookup, "#items");

            if (ColumnType.NUMBER.equals(targetType)) {
                if (groupRequired || functionRequired) {
                    if (ColumnType.NUMBER.equals(columnType)) {
                        builder.column(columnId, AggregateFunctionType.SUM, uniqueColumnId);
                    } else {
                        builder.column(AggregateFunctionType.COUNT, uniqueCountId);
                    }
                } else {
                    builder.column(columnId, uniqueColumnId);
                }
            } else {
                if (functionRequired) {
                    builder.column(AggregateFunctionType.COUNT, uniqueCountId);
                } else {
                    builder.column(columnId, uniqueColumnId);
                }
            }
        }
        return builder.buildLookup();
    }

    public String buildUniqueColumnId(DataSetLookup lookup, String targetId) {
        return buildUniqueColumnId(lookup, new GroupFunction(targetId, targetId, null));
    }

    public String buildUniqueColumnId(DataSetLookup lookup, GroupFunction column) {
        String targetId = column.getSourceId();
        targetId = targetId == null ? column.getColumnId() : targetId;

        int lastGop = lookup.getLastGroupOpIndex(0);
        if (lastGop != -1) {
            DataSetGroup groupOp = lookup.getOperation(lastGop);
            List<GroupFunction> columnList = groupOp.getGroupFunctions();

            String newColumnId = targetId;
            int counter = 1;
            while (true) {
                boolean unique = true;
                for (int i=0; i<columnList.size() && unique; i++) {
                    GroupFunction gf = columnList.get(i);
                    if (gf != column && newColumnId.equals(gf.getColumnId())) {
                        newColumnId = targetId + "_" + (++counter);
                        unique = false;
                    }
                }
                if (unique) {
                    return newColumnId;
                }
            }
        }
        return targetId;
    }

    private int getGroupColumn(DataSetMetadata metatada) {
        for (int i=0; i<metatada.getNumberOfColumns(); i++) {
            ColumnType type = metatada.getColumnType(i);
            if (type.equals(ColumnType.LABEL)) return i;
        }
        for (int i=0; i<metatada.getNumberOfColumns(); i++) {
            ColumnType type = metatada.getColumnType(i);
            if (type.equals(ColumnType.DATE)) return i;
        }
        return -1;
    }

    private int getTargetColumn(DataSetMetadata metatada, ColumnType type, Set<Integer> exclude) {
        int target = -1;
        for (int i=0; i<metatada.getNumberOfColumns(); i++) {
            if (type.equals(metatada.getColumnType(i))) {
                if (target == -1) {
                    target = i;
                }
                if (!exclude.contains(i)) {
                    return i;
                }
            }
        }
        return target;
    }

    private int getTargetColumn(DataSetMetadata metatada, Set<Integer> exclude) {
        for (int i=0; i<metatada.getNumberOfColumns(); i++) {
            if (!exclude.contains(i)) {
                return i;
            }
        }
        return 0;
    }
}