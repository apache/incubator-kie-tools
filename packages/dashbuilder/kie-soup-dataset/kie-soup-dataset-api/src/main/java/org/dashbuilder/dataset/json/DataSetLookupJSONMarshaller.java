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
package org.dashbuilder.dataset.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.LogicalExprFilter;
import org.dashbuilder.dataset.filter.LogicalExprType;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.sort.ColumnSort;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonException;
import org.dashbuilder.json.JsonNull;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.json.JsonType;
import org.dashbuilder.json.JsonValue;

public class DataSetLookupJSONMarshaller {

    private static final String UUID = "dataSetUuid";
    private static final String ROWCOUNT = "rowCount";
    private static final String ROWOFFSET = "rowOffset";

    protected static final String COLUMN = "column";
    private static final String SOURCE = "source";
    private static final String FILTEROPS = "filterOps";

    protected static final String FUNCTION_TYPE = "function";
    protected static final String FUNCTION_ARGS = "args";
    protected static final String FUNCTION_LABEL_VALUE = "labelValue";

    private static final String GROUPOPS = "groupOps";
    private static final String COLUMNGROUP = "columnGroup";
    private static final String GROUPSTRATEGY = "groupStrategy";
    private static final String MAXINTERVALS = "maxIntervals";
    private static final String INTERVALSIZE = "intervalSize";
    private static final String EMPTYINTERVALS = "emptyIntervals";
    private static final String ASCENDING = "asc";
    private static final String FIRSTMONTHOFYEAR = "firstMonthOfYear";
    private static final String FIRSTDAYOFWEEK = "firstDayOfWeek";

    private static final String GROUPFUNCTIONS = "groupFunctions";
    protected static final String FUNCTION = "function";

    private static final String SELECTEDINTERVALS = "selected";
    private static final String INTERVAL_NAME = "name";
    private static final String INTERVAL_TYPE = "type";
    private static final String INTERVAL_IDX = "index";
    private static final String INTERVAL_MIN = "min";
    private static final String INTERVAL_MAX = "max";
    private static final String JOIN = "join";

    private static final String SORTOPS = "sortOps";
    private static final String SORTORDER = "sortOrder";

    private static Map<String, Collection<String>> _keysAliasMap = new HashMap<String, Collection<String>>();
    static {
        _keysAliasMap.put(FUNCTION_TYPE, Arrays.asList(FUNCTION_TYPE, "functionType"));
        _keysAliasMap.put(FUNCTION_ARGS, Arrays.asList(FUNCTION_ARGS, "terms"));
        _keysAliasMap.put(COLUMN, Arrays.asList(COLUMN, "columnId"));
        _keysAliasMap.put(SOURCE, Arrays.asList(SOURCE, "sourceId"));
        _keysAliasMap.put(FUNCTION_LABEL_VALUE, Arrays.asList(FUNCTION_LABEL_VALUE, "labelValue"));
        _keysAliasMap.put(SELECTEDINTERVALS, Arrays.asList(SELECTEDINTERVALS, "selectedIntervals"));
    }

    private static DataSetLookupJSONMarshaller SINGLETON = new DataSetLookupJSONMarshaller();

    public static DataSetLookupJSONMarshaller get() {
        return SINGLETON;
    }

    private Collection<String> keySet(String key) {
        return _keysAliasMap.get(key);
    }

    private List<String> coreFunctionTypes = new ArrayList<String>();
    private List<String> logicalFunctionTypes = new ArrayList<String>();

    public DataSetLookupJSONMarshaller() {
        for (LogicalExprType type : LogicalExprType.values()) {
            logicalFunctionTypes.add(type.toString());
        }
        for (CoreFunctionType type : CoreFunctionType.values()) {
            coreFunctionTypes.add(type.toString());
        }
    }

    public JsonObject toJson(DataSetLookup dataSetLookup) throws JsonException {
        JsonObject json = Json.createObject();
        if ( dataSetLookup != null ) {
            String uuid = dataSetLookup.getDataSetUUID();

            if (uuid != null && !uuid.trim().isEmpty()) json.put(UUID, uuid);
            json.put(ROWCOUNT, Integer.toString(dataSetLookup.getNumberOfRows()));
            json.put(ROWOFFSET, Integer.toString(dataSetLookup.getRowOffset()));

            List<DataSetFilter> filterOps = dataSetLookup.getOperationList(DataSetFilter.class);
            if (!filterOps.isEmpty()) {
                json.set(FILTEROPS, formatFilterOperations(filterOps));
            }
            List<DataSetGroup> groupOps = dataSetLookup.getOperationList(DataSetGroup.class);
            if (!groupOps.isEmpty()) {
                json.set(GROUPOPS, formatGroupOperations(groupOps));
            }
            List<DataSetSort> sortOps = dataSetLookup.getOperationList(DataSetSort.class);
            if (!sortOps.isEmpty()) {
                json.set(SORTOPS, formatSortOperations(sortOps));
            }
        }
        return json;
    }

    public JsonArray formatFilterOperations(List<DataSetFilter> filterOps) throws JsonException {
        if ( filterOps.isEmpty() ) {
            return null;
        }
        // There should be only one DataSetFilter
        return formatColumnFilters(filterOps.get( 0 ).getColumnFilterList());
    }

    public JsonArray formatColumnFilters(List<ColumnFilter> columnFilters) throws JsonException {
        JsonArray colFiltersJsonArray = Json.createArray();
        int colFilterCounter = 0;
        // DataSetFilter ==> ColumnFilter[]
        for (ColumnFilter columnFilter : columnFilters) {
            colFiltersJsonArray.set(colFilterCounter++, formatColumnFilter(columnFilter));
        }
        return colFiltersJsonArray;
    }

    public JsonObject formatColumnFilter(ColumnFilter columnFilter) throws JsonException {
        if ( columnFilter == null ) return null;
        JsonObject colFilterJson = Json.createObject();
        // LogicalExprFilter o CoreFunctionFilter
        if ( columnFilter instanceof LogicalExprFilter ) {
            LogicalExprFilter lef = (LogicalExprFilter) columnFilter;
            colFilterJson.put(COLUMN, lef.getColumnId());
            colFilterJson.put(FUNCTION_TYPE, lef.getLogicalOperator().toString());
            colFilterJson.put(FUNCTION_ARGS, formatColumnFilters(lef.getLogicalTerms()));
        }
        else if (columnFilter instanceof CoreFunctionFilter) {
            CoreFunctionFilter cff = (CoreFunctionFilter) columnFilter;
            colFilterJson.put(COLUMN, cff.getColumnId());
            colFilterJson.put(FUNCTION_TYPE, cff.getType().toString());
            colFilterJson.put(FUNCTION_LABEL_VALUE, cff.getLabelValue());
            JsonArray paramsJsonArray = Json.createArray();
            int paramCounter = 0;
            for (Object param : cff.getParameters()) {
                JsonValue jsonParam = formatValue(param);
                paramsJsonArray.set(paramCounter++, jsonParam);
            }
            colFilterJson.put(FUNCTION_ARGS, paramsJsonArray );

        } else {
            throw new IllegalArgumentException("Unsupported column filter");
        }
        return colFilterJson;
    }

    public JsonArray formatGroupOperations(List<DataSetGroup> groupOps) throws JsonException {
        if (groupOps.isEmpty()) {
            return null;
        }
        JsonArray groupOpsJsonArray = Json.createArray();
        int groupOpCounter = 0;
        for (DataSetGroup groupOp : groupOps) {
            groupOpsJsonArray.set(groupOpCounter++, formatDataSetGroup(groupOp));
        }
        return groupOpsJsonArray;
    }

    public JsonObject formatDataSetGroup(DataSetGroup dataSetGroup) throws JsonException {
        if (dataSetGroup == null) {
            return null;
        }
        JsonObject dataSetGroupJson = Json.createObject();
        dataSetGroupJson.put(COLUMNGROUP, formatColumnGroup(dataSetGroup.getColumnGroup()));
        dataSetGroupJson.put(GROUPFUNCTIONS, formatGroupFunctions(dataSetGroup.getGroupFunctions()));
        dataSetGroupJson.put(SELECTEDINTERVALS, formatSelectedIntervals(dataSetGroup.getSelectedIntervalList()));
        dataSetGroupJson.put(JOIN, dataSetGroup.isJoin() ? "true" : "false");
        return dataSetGroupJson;
    }

    public JsonObject formatColumnGroup(ColumnGroup columnGroup) throws JsonException {
        if (columnGroup == null) {
            return null;
        }
        JsonObject columnGroupJson = Json.createObject();
        columnGroupJson.put(SOURCE, columnGroup.getSourceId() != null ? columnGroup.getSourceId() : null);
        columnGroupJson.put(COLUMN, columnGroup.getColumnId() != null ? columnGroup.getColumnId() : null);
        columnGroupJson.put(GROUPSTRATEGY, columnGroup.getStrategy() != null ? columnGroup.getStrategy().toString() : null);
        columnGroupJson.put(MAXINTERVALS, Integer.toString( columnGroup.getMaxIntervals()));
        columnGroupJson.put(INTERVALSIZE, columnGroup.getIntervalSize() != null ? columnGroup.getIntervalSize() : null);
        columnGroupJson.put(EMPTYINTERVALS, columnGroup.areEmptyIntervalsAllowed() ? "true" : "false" );
        columnGroupJson.put(ASCENDING, columnGroup.isAscendingOrder() ? "true" : "false");
        columnGroupJson.put(FIRSTMONTHOFYEAR, columnGroup.getFirstMonthOfYear() != null ? columnGroup.getFirstMonthOfYear().toString() : null);
        columnGroupJson.put(FIRSTDAYOFWEEK, columnGroup.getFirstDayOfWeek() != null ? columnGroup.getFirstDayOfWeek().toString() : null);
        return columnGroupJson;
    }

    public JsonArray formatGroupFunctions(List<GroupFunction> groupFunctions) throws JsonException {
        if (groupFunctions.isEmpty()) {
            return null;
        }
        JsonArray groupOpsJsonArray = Json.createArray();
        int groupFunctionCounter = 0;
        for (GroupFunction groupFunction : groupFunctions) {
            groupOpsJsonArray.set(groupFunctionCounter++, formatGroupFunction(groupFunction));
        }
        return groupOpsJsonArray;
    }

    public JsonObject formatGroupFunction(GroupFunction groupFunction) throws JsonException {
        if (groupFunction == null) {
            return null;
        }
        JsonObject groupFunctionJson = Json.createObject();
        groupFunctionJson.put(SOURCE, groupFunction.getSourceId() != null ? groupFunction.getSourceId() : null);
        groupFunctionJson.put(COLUMN, groupFunction.getColumnId() != null ? groupFunction.getColumnId() : null);
        groupFunctionJson.put(FUNCTION, groupFunction.getFunction() != null ? groupFunction.getFunction().toString() : null);
        return groupFunctionJson;
    }

    public JsonArray formatSelectedIntervals(List<Interval> selectedIntervalList) throws JsonException {
        if (selectedIntervalList.isEmpty()) {
            return null;
        }
        JsonArray selectedIntervalNamesJsonArray = Json.createArray();
        int intervalNamesCounter = 0;
        for (Interval interval : selectedIntervalList) {
            selectedIntervalNamesJsonArray.set(intervalNamesCounter++, formatInterval(interval));
        }
        return selectedIntervalNamesJsonArray;
    }

    public JsonObject formatInterval(Interval interval) throws JsonException {
        if (interval == null) {
            return null;
        }
        JsonObject jsonObj = Json.createObject();
        jsonObj.put(INTERVAL_NAME, interval.getName());
        jsonObj.put(INTERVAL_IDX, Integer.toString(interval.getIndex()));
        if (interval.getType() != null) {
            jsonObj.put(INTERVAL_TYPE, interval.getName());
        }
        if (interval.getMinValue() != null) {
            jsonObj.put(INTERVAL_MIN, formatValue(interval.getMinValue()));
        }
        if (interval.getMinValue() != null) {
            jsonObj.put(INTERVAL_MAX, formatValue(interval.getMaxValue()));
        }
        return jsonObj;
    }

    public JsonArray formatSortOperations(List<DataSetSort> sortOps) throws JsonException {
        if (sortOps.isEmpty()) {
            return null;
        }
        // There should be only one DataSetFilter
        return formatColumnSorts(sortOps.get(0).getColumnSortList());
    }

    public JsonArray formatColumnSorts(List<ColumnSort> columnSorts) throws JsonException {
        if (columnSorts.isEmpty()) {
            return null;
        }
        JsonArray columnSortsJsonArray = Json.createArray();
        int columnSortCounter = 0;
        for (ColumnSort columnSort : columnSorts) {
            columnSortsJsonArray.set(columnSortCounter++, formatColumnSort(columnSort));
        }
        return columnSortsJsonArray;
    }

    public JsonObject formatColumnSort(ColumnSort columnSort) throws JsonException {
        if (columnSort == null) {
            return null;
        }
        JsonObject columnSortJson = Json.createObject();
        columnSortJson.put(COLUMN, columnSort.getColumnId() != null ? columnSort.getColumnId() : null);
        columnSortJson.put(SORTORDER, columnSort.getOrder() != null ? columnSort.getOrder().toString() : null);
        return columnSortJson;
    }

    public DataSetLookup fromJson(String jsonString) throws JsonException {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        JsonObject dataSetLookupJson = Json.parse(jsonString);
        return fromJson(dataSetLookupJson);
    }

    public DataSetLookup fromJson(JsonObject json) throws JsonException {
        if (json == null) {
            return null;
        }
        DataSetLookup dataSetLookup = new DataSetLookup();
        dataSetLookup.setDataSetUUID(json.get(UUID) != null ? json.getString(UUID) : null);
        dataSetLookup.setNumberOfRows(json.get(ROWCOUNT) != null ? Integer.parseInt(json.getString(ROWCOUNT), 10) : -1);
        dataSetLookup.setRowOffset(json.get(ROWOFFSET) != null ? Integer.parseInt(json.getString(ROWOFFSET), 10) : 0);

        List<DataSetOp> dataSetOpList = dataSetLookup.getOperationList();

        Collection c = null;
        if ((c = parseFilterOperations(json.getArray(FILTEROPS))) != null) {
            dataSetOpList.addAll(c);
        }
        if ((c = parseGroupOperations(json.getArray(GROUPOPS))) != null) {
            dataSetOpList.addAll(c);
        }
        if ((c = parseSortOperations(json.getArray(SORTOPS))) != null) {
            dataSetOpList.addAll(c);
        }

        return dataSetLookup;
    }

    public List<DataSetFilter> parseFilterOperations(JsonArray columnFiltersJsonArray) {
        if (columnFiltersJsonArray == null) {
            return null;
        }

        // There's only one DataSetFilter, the json array is an array of column filters
        List<DataSetFilter> dataSetFilters = new ArrayList<DataSetFilter>();
        DataSetFilter dataSetFilter = parseFilterOperation(columnFiltersJsonArray);
        if (dataSetFilter != null) {
            dataSetFilters.add(dataSetFilter);
        }
        return dataSetFilters;
    }

    public DataSetFilter parseFilterOperation(JsonArray columnFiltersJsonArray) {
        if (columnFiltersJsonArray == null) {
            return null;
        }

        DataSetFilter dataSetFilter = new DataSetFilter();
        List<ColumnFilter> columnFilters = parseColumnFilters(columnFiltersJsonArray);
        if (columnFilters != null) {
            dataSetFilter.getColumnFilterList().addAll(columnFilters);
        }
        return dataSetFilter;
    }

    public List<ColumnFilter> parseColumnFilters(JsonArray columnFiltersJsonArray) {
        if (columnFiltersJsonArray == null) {
            return null;
        }
        List<ColumnFilter> columnFilters = new ArrayList<ColumnFilter>(columnFiltersJsonArray.length());
        for (int i = 0; i < columnFiltersJsonArray.length(); i++) {
            // TODO: can be null, if someone puts a {} in the column list
            columnFilters.add(parseColumnFilter(columnFiltersJsonArray.getObject(i)));
        }
        return columnFilters;
    }

    public ColumnFilter parseColumnFilter(JsonObject columnFilterJson) {
        if (columnFilterJson == null) {
            return null;
        }

        String columnId = columnFilterJson.getString(keySet(COLUMN));
        String functionType = columnFilterJson.getString(keySet(FUNCTION_TYPE));
        JsonArray terms = columnFilterJson.getArray(keySet(FUNCTION_ARGS));
        if (functionType == null) {
            throw new RuntimeException("Dataset lookup column filter null function type");
        }

        if (isCoreFilter(functionType)) {
            String labelValue = columnFilterJson.getString(keySet(FUNCTION_LABEL_VALUE));
            CoreFunctionFilter cff = new CoreFunctionFilter();
            cff.setColumnId(columnId);
            cff.setType(CoreFunctionType.getByName(functionType));
            cff.setParameters(parseCoreFunctionParameters(terms));
            cff.setLabelValue(labelValue);
            return cff;

        } else if (isLogicalFilter(functionType)) {
            LogicalExprFilter lef = new LogicalExprFilter();
            lef.setColumnId(columnId);
            lef.setLogicalOperator(LogicalExprType.getByName(functionType));

            // Logical expression terms are an an array of column filters
            lef.setLogicalTerms(parseColumnFilters(terms));
            return lef;
        }
        else {
            throw new RuntimeException("Dataset lookup column filter wrong type");
        }
    }

    public List<Comparable> parseCoreFunctionParameters(JsonArray paramsJsonArray) {
        if (paramsJsonArray == null) {
            return null;
        }
        List<Comparable> params = new ArrayList<Comparable>(paramsJsonArray.length());
        for (int i = 0; i < paramsJsonArray.length(); i++) {
            JsonValue jsonValue = paramsJsonArray.get(i);
            params.add(parseValue(jsonValue));
        }
        return params;
    }

    public List<DataSetGroup> parseGroupOperations( JsonArray groupOpsJsonArray ) {
        if (groupOpsJsonArray == null) {
            return null;
        }
        List<DataSetGroup> dataSetGroups = new ArrayList<DataSetGroup>();
        for (int i = 0; i < groupOpsJsonArray.length(); i++) {
            JsonObject dataSetGroupOpJson = groupOpsJsonArray.getObject(i);
            dataSetGroups.add(parseDataSetGroup(dataSetGroupOpJson));
        }
        return dataSetGroups;
    }

    public DataSetGroup parseDataSetGroup(JsonObject dataSetGroupJson) {
        if (dataSetGroupJson == null) {
            return null;
        }

        DataSetGroup dataSetGroup = new DataSetGroup();
        dataSetGroup.setColumnGroup(null);
        JsonObject value = dataSetGroupJson.getObject(COLUMNGROUP);
        if (value != null) {
            dataSetGroup.setColumnGroup(parseColumnGroup(value));
        }

        List<GroupFunction> groupFunctions = parseGroupFunctions(dataSetGroupJson.getArray(GROUPFUNCTIONS));
        if (groupFunctions != null) {
            dataSetGroup.getGroupFunctions().addAll( groupFunctions );
        }

        dataSetGroup.setSelectedIntervalList(parseSelectedIntervals(dataSetGroupJson.getArray(keySet(SELECTEDINTERVALS))));
        dataSetGroup.setJoin(dataSetGroupJson.getBoolean(JOIN));
        return dataSetGroup;
    }

    public ColumnGroup parseColumnGroup( JsonObject columnGroupJson ) {
        if (columnGroupJson == null) {
            return null;
        }
        ColumnGroup columnGroup = new ColumnGroup();
        columnGroup.setSourceId(columnGroupJson.getString(keySet(SOURCE)));
        columnGroup.setColumnId(columnGroupJson.getString(keySet(COLUMN)));
        columnGroup.setStrategy(GroupStrategy.getByName(columnGroupJson.getString(GROUPSTRATEGY)));
        columnGroup.setMaxIntervals(columnGroupJson.getNumber(MAXINTERVALS, -1).intValue());
        columnGroup.setIntervalSize(columnGroupJson.getString(INTERVALSIZE));
        columnGroup.setEmptyIntervalsAllowed(columnGroupJson.getBoolean(EMPTYINTERVALS));
        columnGroup.setAscendingOrder(columnGroupJson.getBoolean(ASCENDING));
        columnGroup.setFirstMonthOfYear(Month.getByName(columnGroupJson.getString(FIRSTMONTHOFYEAR)));
        columnGroup.setFirstDayOfWeek(DayOfWeek.getByName(columnGroupJson.getString(FIRSTDAYOFWEEK)));
        return columnGroup;
    }

    public List<GroupFunction> parseGroupFunctions(JsonArray groupFunctionsJson) {
        if (groupFunctionsJson == null) {
            return null;
        }
        List<GroupFunction> groupFunctions = new ArrayList<GroupFunction>(groupFunctionsJson.length());
        for (int i = 0; i < groupFunctionsJson.length(); i++) {
            groupFunctions.add(parseGroupFunction(groupFunctionsJson.getObject(i)));
        }
        return groupFunctions;
    }

    public GroupFunction parseGroupFunction( JsonObject groupFunctionJson ) {
        if (groupFunctionJson == null) {
            return null;
        }
        GroupFunction groupFunction = new GroupFunction();
        groupFunction.setSourceId(groupFunctionJson.getString(keySet(SOURCE)));
        groupFunction.setColumnId(groupFunctionJson.getString(keySet(COLUMN)));
        groupFunction.setFunction(AggregateFunctionType.getByName(groupFunctionJson.getString(keySet(FUNCTION))));
        return groupFunction;
    }

    public List<Interval> parseSelectedIntervals(JsonArray selectedIntervalsJson) {
        if (selectedIntervalsJson == null) {
            return null;
        }
        List<Interval> intervalList = new ArrayList<Interval>(selectedIntervalsJson.length());
        for ( int i = 0; i < selectedIntervalsJson.length(); i++) {
            intervalList.add(parseInterval(selectedIntervalsJson.getObject(i)));
        }
        return intervalList;
    }

    public Interval parseInterval(JsonObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        Interval interval = new Interval();
        interval.setName(jsonObj.getString(INTERVAL_NAME));
        interval.setType(jsonObj.getString(INTERVAL_TYPE));
        interval.setIndex(jsonObj.getNumber(INTERVAL_IDX, 0).intValue());
        interval.setMinValue(parseValue(jsonObj.get(INTERVAL_MIN)));
        interval.setMaxValue(parseValue(jsonObj.get(INTERVAL_MAX)));
        return interval;
    }

    public List<DataSetSort> parseSortOperations(JsonArray columnSortsJsonArray) {
        if (columnSortsJsonArray == null) {
            return null;
        }
        List<DataSetSort> dataSetSorts = new ArrayList<DataSetSort>();
        // There's only one DataSetSort, the json array is an array of column sorts
        DataSetSort dataSetSort = new DataSetSort();
        dataSetSorts.add(dataSetSort);

        List<ColumnSort> columnSorts = parseColumnSorts(columnSortsJsonArray);
        if (columnSorts != null) {
            dataSetSort.getColumnSortList().addAll(columnSorts);
        }
        return dataSetSorts;
    }

    public List<ColumnSort> parseColumnSorts(JsonArray columnSortsJsonArray) {
        if (columnSortsJsonArray == null) {
            return null;
        }
        List<ColumnSort> columnSorts = new ArrayList<ColumnSort>(columnSortsJsonArray.length());
        for (int i = 0; i < columnSortsJsonArray.length(); i++) {
            columnSorts.add(parseColumnSort(columnSortsJsonArray.getObject(i)));
        }
        return columnSorts;
    }

    public ColumnSort parseColumnSort(JsonObject columnSortJson) {
        if (columnSortJson == null) {
            return null;
        }
        ColumnSort columnSort = new ColumnSort();
        columnSort.setColumnId(columnSortJson.getString(COLUMN));
        columnSort.setOrder(SortOrder.getByName(columnSortJson.getString(SORTORDER)));
        return columnSort;
    }

    public boolean isLogicalFilter(String functionType) {
        return logicalFunctionTypes.contains(functionType);
    }

    public boolean isCoreFilter(String functionType) {
        return coreFunctionTypes.contains(functionType);
    }

    public String twoDigits(int n) {
        String str = String.valueOf(n);
        return n < 10 ? "0" + str : str;
    }

    /**
     * @return yyyy-MM-dd HH-mm-ss
     */
    public String formatDate(Date d) {
        return (1900+d.getYear()) + "-" +
                twoDigits(d.getMonth() + 1) + "-" +
                twoDigits(d.getDate()) + " " +
                twoDigits(d.getHours()) + ":" +
                twoDigits(d.getMinutes()) + ":" +
                twoDigits(d.getSeconds());
    }

    /**
     * @param date yyyy-MM-dd HH-mm-ss
     */
    public Date parseDate(String date) {
        String str = date.trim();
        if (str.length() != 19) {
            throw new JsonException("Wrong date format: " + str);
        }
        try {
            int year = Integer.parseInt(str.substring(0, 4));
            int month = Integer.parseInt(str.substring(5, 7)) - 1;
            int day = Integer.parseInt(str.substring(8, 10));
            int hour = Integer.parseInt(str.substring(11, 13));
            int min = Integer.parseInt(str.substring(14, 16));
            int sec = Integer.parseInt(str.substring(17, 19));

            if (month > 11 || month < 0 ||
                day < 1 || day > 31 ||
                hour > 23 || hour < 0 ||
                min > 59 || min < 0 ||
                sec> 59 || sec < 0) {

                throw new JsonException("Wrong date format: " + str);
            }
            return new Date(year-1900, month, day, hour, min, sec);
        }
        catch (NumberFormatException e) {
            throw new JsonException("Wrong date format: " + str);
        }
    }

    public JsonValue formatValue(Object value) {
        // Null
        if (value == null) {
            return JsonNull.NULL_INSTANCE;
        }
        // Number
        else if (value instanceof Number) {
            return Json.create(((Number) value).doubleValue());
        }
        // Boolean
        else if (value instanceof Boolean) {
            return Json.create(((Boolean) value).booleanValue());
        }
        // Date
        else if (value instanceof Date) {
            return Json.create(formatDate((Date) value));
        }
        // String (default)
        else {
            return Json.create(value.toString());
        }
    }

    public Comparable parseValue(JsonValue jsonValue) {
        if (jsonValue == null || jsonValue.getType().equals(JsonType.NULL)) {
            // Null
            return null;
        }
        // Boolean
        if (jsonValue.getType().equals(JsonType.BOOLEAN)) {
            return jsonValue.asBoolean();
        }
        // Number
        if (jsonValue.getType().equals(JsonType.NUMBER)) {
            return jsonValue.asNumber();
        }
        try {
            // Date
            return parseDate(jsonValue.asString());
        }
        catch (Exception e1) {
            // String
            return jsonValue.asString();
        }
    }
}
