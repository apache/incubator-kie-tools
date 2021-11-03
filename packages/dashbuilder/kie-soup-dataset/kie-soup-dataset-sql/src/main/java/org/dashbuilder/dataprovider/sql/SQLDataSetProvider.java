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
package org.dashbuilder.dataprovider.sql;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataprovider.StaticDataSetProvider;
import org.dashbuilder.dataprovider.sql.dialect.Dialect;
import org.dashbuilder.dataprovider.sql.model.Column;
import org.dashbuilder.dataprovider.sql.model.Condition;
import org.dashbuilder.dataprovider.sql.model.Select;
import org.dashbuilder.dataprovider.sql.model.SortColumn;
import org.dashbuilder.dataprovider.sql.model.Table;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpEngine;
import org.dashbuilder.dataset.IntervalBuilderDynamicDate;
import org.dashbuilder.dataset.date.DateUtils;
import org.dashbuilder.dataset.date.TimeFrame;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.DataSetDefRegistryListener;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.def.SQLDataSourceDef;
import org.dashbuilder.dataset.engine.group.IntervalBuilder;
import org.dashbuilder.dataset.engine.group.IntervalBuilderLocator;
import org.dashbuilder.dataset.engine.group.IntervalList;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.filter.LogicalExprFilter;
import org.dashbuilder.dataset.filter.LogicalExprType;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.impl.DataColumnImpl;
import org.dashbuilder.dataset.impl.DataSetMetadataImpl;
import org.dashbuilder.dataset.impl.MemSizeEstimator;
import org.dashbuilder.dataset.sort.ColumnSort;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.dashbuilder.dataset.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  DataSetProvider implementation for JDBC-compliant data sources.
 *
 *  <p>The SQL provider resolves every data set lookup request by transforming such request into the proper SQL query.
 *  In some cases, an extra processing of the resulting data is required since some lookup requests do not map directly
 *  into the SQL world. In such cases, specially the grouping of date based data, the core data set operation engine is
 *  used.</p>
 *
 *  <p>
 *      Pending stuff:
 *      - Filter on foreign data sets
 *      - Group (fixed) by date of week
 *  </p>
 */
public class SQLDataSetProvider implements DataSetProvider, DataSetDefRegistryListener {

    private static SQLDataSetProvider SINGLETON = null;

    public static SQLDataSetProvider get() {
        if (SINGLETON == null) {
            DataSetCore dataSetCore = DataSetCore.get();

            StaticDataSetProvider staticDataSetProvider = dataSetCore.getStaticDataSetProvider();
            DataSetDefRegistry dataSetDefRegistry = dataSetCore.getDataSetDefRegistry();
            DataSetOpEngine dataSetOpEngine = dataSetCore.getSharedDataSetOpEngine();
            IntervalBuilderLocator intervalBuilderLocator = dataSetCore.getIntervalBuilderLocator();
            IntervalBuilderDynamicDate intervalBuilderDynamicDate = dataSetCore.getIntervalBuilderDynamicDate();

            SINGLETON = new SQLDataSetProvider(
                    staticDataSetProvider,
                    intervalBuilderLocator,
                    intervalBuilderDynamicDate,
                    dataSetOpEngine);

            SINGLETON.setDataSourceLocator(new SQLDataSourceLocator() {
                @Override
                public DataSource lookup(SQLDataSetDef def) throws Exception {
                    InitialContext ctx = new InitialContext();
                    return (DataSource) ctx.lookup(def.getDataSource());
                }
                @Override
                public List<SQLDataSourceDef> list() {
                    return JDBCUtils.listDatasourceDefs();
                }
            });
            dataSetDefRegistry.addListener(SINGLETON);
        }
        return SINGLETON;
    }


    protected Logger log = LoggerFactory.getLogger(SQLDataSetProvider.class);
    protected StaticDataSetProvider staticDataSetProvider;
    protected SQLDataSourceLocator dataSourceLocator;
    protected IntervalBuilderLocator intervalBuilderLocator;
    protected IntervalBuilderDynamicDate intervalBuilderDynamicDate;
    protected DataSetOpEngine opEngine;

    public SQLDataSetProvider() {
    }

    public SQLDataSetProvider(StaticDataSetProvider staticDataSetProvider,
                              IntervalBuilderLocator intervalBuilderLocator,
                              IntervalBuilderDynamicDate intervalBuilderDynamicDate,
                              DataSetOpEngine opEngine) {

        this.staticDataSetProvider = staticDataSetProvider;
        this.intervalBuilderLocator = intervalBuilderLocator;
        this.intervalBuilderDynamicDate = intervalBuilderDynamicDate;
        this.opEngine = opEngine;
    }

    public StaticDataSetProvider getStaticDataSetProvider() {
        return staticDataSetProvider;
    }

    public void setStaticDataSetProvider(StaticDataSetProvider staticDataSetProvider) {
        this.staticDataSetProvider = staticDataSetProvider;
    }

    public SQLDataSourceLocator getDataSourceLocator() {
        return dataSourceLocator;
    }

    public void setDataSourceLocator(SQLDataSourceLocator dataSourceLocator) {
        this.dataSourceLocator = dataSourceLocator;
    }

    public IntervalBuilderLocator getIntervalBuilderLocator() {
        return intervalBuilderLocator;
    }

    public void setIntervalBuilderLocator(IntervalBuilderLocator intervalBuilderLocator) {
        this.intervalBuilderLocator = intervalBuilderLocator;
    }

    public IntervalBuilderDynamicDate getIntervalBuilderDynamicDate() {
        return intervalBuilderDynamicDate;
    }

    public void setIntervalBuilderDynamicDate(IntervalBuilderDynamicDate intervalBuilderDynamicDate) {
        this.intervalBuilderDynamicDate = intervalBuilderDynamicDate;
    }

    public DataSetOpEngine getOpEngine() {
        return opEngine;
    }

    public void setOpEngine(DataSetOpEngine opEngine) {
        this.opEngine = opEngine;
    }

    public DataSetProviderType getType() {
        return DataSetProviderType.SQL;
    }

    public DataSet lookupDataSet(DataSetDef def, DataSetLookup lookup) throws Exception {
        SQLDataSetDef sqlDef = (SQLDataSetDef) def;
        if (StringUtils.isBlank(sqlDef.getDataSource())) {
            throw new IllegalArgumentException("Missing data source in SQL data set definition: " + sqlDef);
        }
        if (StringUtils.isBlank(sqlDef.getDbSQL()) && StringUtils.isBlank(sqlDef.getDbTable())) {
            throw new IllegalArgumentException("Missing DB table or SQL in the data set definition: " + sqlDef);
        }

        // Look first into the static data set provider cache.
        if (sqlDef.isCacheEnabled()) {
            DataSet dataSet = staticDataSetProvider.lookupDataSet(def.getUUID(), null);
            if (dataSet != null) {

                // Lookup from cache.
                return staticDataSetProvider.lookupDataSet(def.getUUID(), lookup);
            } else  {

                // Fetch always from database if existing rows are greater than the cache max. rows
                DataSetMetadata metadata = getDataSetMetadata(def);
                int rows = metadata.getNumberOfRows();
                if (rows > sqlDef.getCacheMaxRows()) {
                    return _lookupDataSet(sqlDef, lookup);
                }
                // Fetch from database and register into the static cache. Further requests will lookup from cache.
                dataSet = _lookupDataSet(sqlDef, null);
                dataSet.setUUID(def.getUUID());
                dataSet.setDefinition(def);
                staticDataSetProvider.registerDataSet(dataSet);
                return staticDataSetProvider.lookupDataSet(def.getUUID(), lookup);
            }
        }

        // If cache is disabled then always fetch from database.
        return _lookupDataSet(sqlDef, lookup);
    }

    public boolean isDataSetOutdated(DataSetDef def) {

        // Non fetched data sets can't get outdated.
        MetadataHolder last = _metadataMap.remove(def.getUUID());
        if (last == null) return false;

        // Check if the metadata has changed since the last time it was fetched.
        try {
            DataSetMetadata current = getDataSetMetadata(def);
            return !current.equals(last.metadata);
        }
        catch (Exception e) {
            log.error("Error fetching metadata: " + def, e);
            return false;
        }
    }

    public DataSetMetadata getDataSetMetadata(DataSetDef def) throws Exception {
        SQLDataSetDef sqlDef = (SQLDataSetDef) def;
        DataSource ds = dataSourceLocator.lookup(sqlDef);
        Connection conn = ds.getConnection();
        try {
            return _getDataSetMetadata(sqlDef, conn, true);
        } finally {
            conn.close();
        }
    }

    // Listen to changes on the data set definition registry

    @Override
    public void onDataSetDefStale(DataSetDef def) {
        if (DataSetProviderType.SQL.equals(def.getProvider())) {
            staticDataSetProvider.removeDataSet(def.getUUID());
        }
    }

    @Override
    public void onDataSetDefModified(DataSetDef olDef, DataSetDef newDef) {
        if (DataSetProviderType.SQL.equals(olDef.getProvider())) {
            String uuid = olDef.getUUID();
            _metadataMap.remove(uuid);
            staticDataSetProvider.removeDataSet(uuid);
        }
    }

    @Override
    public void onDataSetDefRemoved(DataSetDef oldDef) {
        if (DataSetProviderType.SQL.equals(oldDef.getProvider())) {
            String uuid = oldDef.getUUID();
            _metadataMap.remove(uuid);
            staticDataSetProvider.removeDataSet(uuid);
        }
    }

    @Override
    public void onDataSetDefRegistered(DataSetDef newDef) {

    }

    // Internal implementation logic

    protected class MetadataHolder {
        DataSetMetadataImpl metadata;
        List<Column> columns;
    }

    protected transient Map<String,MetadataHolder> _metadataMap = new HashMap<String,MetadataHolder>();

    protected Column _getDbColumn(Collection<Column> dbColumns, String columnId) {
        for (Column dbColumn: dbColumns) {
            if (dbColumn.getName().equalsIgnoreCase(columnId)) {
                return dbColumn;
            }
        }
        return null;
    }

    protected DataSetMetadata _getDataSetMetadata(SQLDataSetDef def, Connection conn, boolean skipCache) throws Exception {

        // Check the cache
        if (!skipCache) {
            MetadataHolder result = _metadataMap.get(def.getUUID());
            if (result != null) {
                return result.metadata;
            }
        }

        // Fetch the DB columns from the table or sql
        List<Column> dbColumns = _getColumns(def, conn);
        List<String> targetDbColumnIds = new ArrayList<String>();
        List<ColumnType> targetDbColumnTypes = new ArrayList<ColumnType>();
        List<Integer> targetDbColumnsLength = new ArrayList<Integer>();

        // Check the definition columns match those in the DB
        if (def.getColumns() != null) {
            for (DataColumnDef column : def.getColumns()) {
                Column dbColumn = _getDbColumn(dbColumns, column.getId());
                if (dbColumn == null) {
                    throw new IllegalArgumentException("The DataSetDef's column does not exist in DB: " + column.getId());
                }
                targetDbColumnIds.add(dbColumn.getName());
                targetDbColumnTypes.add(column.getColumnType());
                targetDbColumnsLength.add(dbColumn.getLength());
            }
        }

        // Add or skip non-existing columns depending on the data set definition.
        for (Column dbColumn : dbColumns) {
            String dbColumnId = dbColumn.getName();
            int columnIdx = targetDbColumnIds.indexOf(dbColumnId);
            boolean columnExists  = columnIdx != -1;

            if (!columnExists) {

                // Add any table column
                if (def.isAllColumnsEnabled()) {
                    targetDbColumnIds.add(dbColumnId);
                    targetDbColumnTypes.add(dbColumn.getType());
                    targetDbColumnsLength.add(dbColumn.getLength());
                }
                // Skip non existing columns
                else {
                    continue;
                }
            }
        }

        // Ensure the column set is valid
        if (targetDbColumnIds.isEmpty()) {
            throw new IllegalArgumentException("No data set columns found: " + def);
        }

        // Creates a brand new metadata holder instance
        MetadataHolder result = new MetadataHolder();
        result.columns = dbColumns;
        result.metadata = new DataSetMetadataImpl(def,
                def.getUUID(), 0,
                targetDbColumnIds.size(), targetDbColumnIds,
                targetDbColumnTypes, 0);


        // Calculate the estimated size
        if(def.isEstimateSize()) {
            int rowCount = _getRowCount(result.metadata, def, conn);
            int estimatedSize = 0;
            for (int i = 0; i < targetDbColumnIds.size(); i++) {
                ColumnType cType = targetDbColumnTypes.get(i);

                if (ColumnType.DATE.equals(cType)) {
                    estimatedSize += MemSizeEstimator.sizeOf(Date.class) * rowCount;
                } else if (ColumnType.NUMBER.equals(cType)) {
                    estimatedSize += MemSizeEstimator.sizeOf(Double.class) * rowCount;
                } else {
                    int length = targetDbColumnsLength.get(i);
                    estimatedSize += length / 2 * rowCount;
                }
            }

            // Update the metadata
            result.metadata.setNumberOfRows(rowCount);
            result.metadata.setEstimatedSize(estimatedSize);
        }

        // Store in the cache
        if (!skipCache) {
            _metadataMap.put(def.getUUID(), result);
        } else if (log.isDebugEnabled()) {
            log.debug("Using look-up in test mode. Skipping adding data set metadata for uuid [" + def.getUUID() + "] into cache.");
        }
        return result.metadata;
    }

    protected List<Column> _getColumns(SQLDataSetDef def, Connection conn) {
        final Dialect dialect = JDBCUtils.dialect(conn);
        Select q = SQLFactory.select(conn);
        q = (!StringUtils.isBlank(def.getDbSQL()) ? q.from(def.getDbSQL()) : q.from(_createTable(def))).limit(0);
        return logSQL(q).fetch(new ResultSetConsumer<List<Column>>() {
            public List<Column> consume(ResultSet _rs) {
                return JDBCUtils.getColumns(_rs, dialect.getExcludedColumns());
            }
        });
    }

    protected int _getRowCount(DataSetMetadata metadata, SQLDataSetDef def, Connection conn) throws Exception {

        // Count rows, either on an SQL or a DB table
        Select _query = SQLFactory.select(conn);
        _appendFrom(def, _query);

        // Filters set must be taken into account
        DataSetFilter filterOp = def.getDataSetFilter();
        if (filterOp != null) {
            List<ColumnFilter> filterList = filterOp.getColumnFilterList();
            for (ColumnFilter filter : filterList) {
                _appendFilterBy(metadata, def, filter, _query);
            }
        }
        return _query.fetchCount();
    }

    protected DataSet _lookupDataSet(SQLDataSetDef def, DataSetLookup lookup) throws Exception {
        LookupProcessor processor = new LookupProcessor(def, lookup);
        return processor.run();
    }

    protected Table _createTable(SQLDataSetDef def) {
        if (StringUtils.isBlank(def.getDbSchema())) return SQLFactory.table(def.getDbTable());
        else return SQLFactory.table(def.getDbSchema(), def.getDbTable());
    }

    protected void _appendFrom(SQLDataSetDef def, Select _query) {
        if (!StringUtils.isBlank(def.getDbSQL())) _query.from(def.getDbSQL());
        else _query.from(_createTable(def));
    }

    protected void _appendFilterBy(DataSetMetadata metadata, SQLDataSetDef def, DataSetFilter filterOp, Select _query) {
        List<ColumnFilter> filterList = filterOp.getColumnFilterList();
        for (ColumnFilter filter : filterList) {
            _appendFilterBy(metadata, def, filter, _query);
        }
    }

    protected void _appendFilterBy(DataSetMetadata metadata, SQLDataSetDef def, ColumnFilter filter, Select _query) {
        Condition condition = _createCondition(metadata, def, filter);
        if (condition != null) {
            _query.where(condition);
        }
    }

    protected Condition _createCondition(DataSetMetadata metadata, SQLDataSetDef def, ColumnFilter filter) {

        if (filter instanceof CoreFunctionFilter) {
            String filterId = _columnFromMetadata(metadata, filter.getColumnId());
            Column _column = SQLFactory.column(filterId);
            CoreFunctionFilter f = (CoreFunctionFilter) filter;
            CoreFunctionType type = f.getType();
            List params = f.getParameters();

            if (CoreFunctionType.IS_NULL.equals(type)) {
                return _column.isNull();
            }
            if (CoreFunctionType.NOT_NULL.equals(type)) {
                return _column.notNull();
            }
            if (CoreFunctionType.EQUALS_TO.equals(type)) {
                if (params.isEmpty()) {
                    return null;
                }
                if (params.size() == 1) {
                    return _column.equalsTo(params.get(0));
                }
                return _column.in(params);
            }
            if (CoreFunctionType.NOT_EQUALS_TO.equals(type)) {
                if (params.isEmpty()) {
                    return null;
                }
                if (params.size() == 1) {
                    return _column.notEquals(params.get(0));
                }
                return _column.in(params).not();
            }
            if (CoreFunctionType.LIKE_TO.equals(type)) {
                String pattern = (String) params.get(0);
                boolean caseSensitive = params.size() < 2 || Boolean.parseBoolean(params.get(1).toString());
                if (caseSensitive) {
                    return _column.like(pattern);
                } else {
                    return _column.lower().like(pattern.toLowerCase());
                }
            }
            if (CoreFunctionType.LOWER_THAN.equals(type)) {
                return _column.lowerThan(params.get(0));
            }
            if (CoreFunctionType.LOWER_OR_EQUALS_TO.equals(type)) {
                return _column.lowerOrEquals(params.get(0));
            }
            if (CoreFunctionType.GREATER_THAN.equals(type)) {
                return _column.greaterThan(params.get(0));
            }
            if (CoreFunctionType.GREATER_OR_EQUALS_TO.equals(type)) {
                return _column.greaterOrEquals(params.get(0));
            }
            if (CoreFunctionType.BETWEEN.equals(type)) {
                Object low = params.get(0);
                Object high= params.get(1);
                if (low == null && high == null) {
                    return null;
                }
                if (low != null && high == null) {
                    return _column.greaterOrEquals(low);
                }
                if (low == null && high != null) {
                    return _column.lowerOrEquals(high);
                }
                return _column.between(low, high);
            }
            if (CoreFunctionType.TIME_FRAME.equals(type)) {
                TimeFrame timeFrame = TimeFrame.parse(params.get(0).toString());
                if (timeFrame != null) {
                    java.sql.Date past = new java.sql.Date(timeFrame.getFrom().getTimeInstant().getTime());
                    java.sql.Date future = new java.sql.Date(timeFrame.getTo().getTimeInstant().getTime());
                    return _column.between(past, future);
                }
            }
            if (CoreFunctionType.IN.equals(type) && params instanceof List) {
                if (params.isEmpty()) {
                    return null;
                }
                return _column.inSql((List<?>)params);
            }
            if (CoreFunctionType.NOT_IN.equals(type) && params instanceof List) {
                if (params.isEmpty()) {
                    return null;
                }
                return _column.notInSql((List<?>)params);
            }
        }
        if (filter instanceof LogicalExprFilter) {
            LogicalExprFilter f = (LogicalExprFilter) filter;
            LogicalExprType type = f.getLogicalOperator();

            Condition condition = null;
            List<ColumnFilter> logicalTerms = f.getLogicalTerms();
            for (int i=0; i<logicalTerms.size(); i++) {
                Condition next = _createCondition(metadata, def, logicalTerms.get(i));

                if (LogicalExprType.AND.equals(type)) {
                    if (condition == null) condition = next;
                    else condition = condition.and(next);
                }
                if (LogicalExprType.OR.equals(type)) {
                    if (condition == null) condition = next;
                    else condition = condition.or(next);
                }
                if (LogicalExprType.NOT.equals(type)) {
                    if (condition == null) condition = next.not();
                    else condition = condition.and(next.not());
                }
            }
            return condition;
        }
        throw new IllegalArgumentException("Filter not supported: " + filter);
    }

    protected String _columnFromMetadata(DataSetMetadata metadata, String columnId) {
        int idx = _assertColumnExists(metadata, columnId);
        return metadata.getColumnId(idx);
    }

    protected int _assertColumnExists(DataSetMetadata metadata, String columnId) {
        for (int i = 0; i < metadata.getNumberOfColumns(); i++) {
            if (metadata.getColumnId(i).equalsIgnoreCase(columnId)) {
                return i;
            }
        }
        throw new RuntimeException("Column '" + columnId +
                "' not found in data set: " + metadata.getUUID());
    }

    public Select logSQL(Select q) {
        String sql = q.getSQL();
        log.debug(sql);
        return q;
    }

    /**
     * Class that provides an isolated context for the processing of a single lookup request.
     */
    private class LookupProcessor {

        private static final String NOT_SUPPORTED = "' not supported";
		SQLDataSetDef def;
        DataSetLookup lookup;
        DataSetMetadata metadata;
        Select _query;
        Connection conn;
        Date[] dateLimits;
        DateIntervalType dateIntervalType;
        List<DataSetOp> postProcessingOps = new ArrayList<DataSetOp>();

        public LookupProcessor(SQLDataSetDef def, DataSetLookup lookup) {
            this.def = def;
            this.lookup = lookup;
            DataSetFilter dataSetFilter = def.getDataSetFilter();
            if (dataSetFilter != null) {
                if (lookup == null) {
                    this.lookup = new DataSetLookup(def.getUUID(), dataSetFilter);
                } else {
                    this.lookup.addOperation(dataSetFilter);
                }
            }
        }

        public boolean groupColumnMustBeIncluded(DataSetGroup groupOp) {
            if (groupOp != null) {
                ColumnGroup cg = groupOp.getColumnGroup();
                if (cg != null) {
                    for (GroupFunction gf : groupOp.getGroupFunctions()) {
                        if (cg.getSourceId().equals(gf.getSourceId()) && gf.getFunction() == null) {
                            return false;
                        }
                    }
                    for (GroupFunction gf : groupOp.getGroupFunctions()) {
                        if (!cg.getSourceId().equals(gf.getSourceId()) && gf.getFunction() == null) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public DataSet run() throws Exception {
            DataSource ds = dataSourceLocator.lookup(def);
            conn = ds.getConnection();
            try {
                boolean skipCache = lookup != null && lookup.testMode();
                metadata = _getDataSetMetadata(def, conn, skipCache);
                int totalRows = metadata.getNumberOfRows();
                boolean trim = (lookup != null && (lookup.getNumberOfRows() > 0 || lookup.getRowOffset() > 0));

                // The whole data set
                if (lookup == null || lookup.getOperationList().isEmpty()) {

                    // Prepare the select
                    _query = SQLFactory.select(conn).columns(_createAllColumns());
                    _appendFrom(def, _query);

                    // Row limits
                    if (trim && postProcessingOps.isEmpty()) {
                        if(def.isEstimateSize()) {
                            totalRows = _query.fetchCount();
                        }
                        _query.limit(lookup.getNumberOfRows()).offset(lookup.getRowOffset());
                    }

                    // Fetch the results and build the data set
                    List<DataColumn> columns = calculateColumns(null);
                    return buildDataSet(columns, trim, totalRows);
                }
                // ... or a list of operations.
                else {
                    DataSetGroup groupOp = null;
                    int groupIdx = lookup.getFirstGroupOpIndex(0, null, false);
                    if (groupIdx != -1) groupOp = lookup.getOperation(groupIdx);

                    // Prepare the select
                    _query = SQLFactory.select(conn).columns(_createColumns(groupOp));
                    _appendFrom(def, _query);

                    // Append the filter clauses
                    for (DataSetFilter filterOp : lookup.getOperationList(DataSetFilter.class)) {
                        _appendFilterBy(metadata, def, filterOp, _query);
                    }

                    // Append the interval selections
                    List<DataSetGroup> intervalSelects = lookup.getFirstGroupOpSelections();
                    for (DataSetGroup intervalSelect : intervalSelects) {
                        _appendIntervalSelection(intervalSelect, _query);
                    }

                    // ... the group by clauses
                    ColumnGroup cg = null;
                    boolean groupColumnAdded = groupColumnMustBeIncluded(groupOp);
                    if (groupOp != null) {
                        cg = groupOp.getColumnGroup();
                        if (cg != null) {
                            groupColumnAdded &= cg.isPostEnabled();
                            _appendGroupBy(groupOp);

                            // The in-memory post processing requires that the group column is also included.
                            // (see DASHBUILDE-181: Error "Column not found" when adding group by column from SQL dataset)
                            if (groupColumnAdded) {
                                GroupFunction gf = new GroupFunction(cg.getSourceId(), cg.getColumnId(), null);
                                groupOp.getGroupFunctions().add(gf);
                                _query.columns(_createColumn(cg));
                            }
                        }
                    }

                    // ... the sort clauses
                    DataSetSort sortOp = lookup.getFirstSortOp();
                    if (sortOp != null) {
                        if (cg != null) {
                            _appendOrderGroupBy(groupOp, sortOp, groupColumnAdded);
                        } else {
                            _appendOrderBy(sortOp);
                        }
                    } else if (cg != null) {
                        _appendOrderGroupBy(groupOp);
                    }

                    // ... and the row limits.
                    // If post-processing then defer the trim operation in order to not leave out rows
                    if (trim && postProcessingOps.isEmpty()) {
                        if (def.isEstimateSize()) {
                            totalRows = _query.fetchCount();
                        }
                        _query.limit(lookup.getNumberOfRows()).offset(lookup.getRowOffset());
                    }

                    // Fetch the results and build the data set
                    List<DataColumn> columns = calculateColumns(groupOp);
                    return buildDataSet(columns, trim, totalRows);
                }
            } finally {
                conn.close();
            }
        }
        
        protected DataSet buildDataSet(final List<DataColumn> columns, boolean trim, int totalRows) throws Exception {
            DataSet dataSet = logSQL(_query).fetch(new ResultSetConsumer<DataSet>() {
                public DataSet consume(ResultSet _rs) {
                    try {
                        return _buildDataSet(columns, _rs);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            if (trim && postProcessingOps.isEmpty()) {
                dataSet.setRowCountNonTrimmed(totalRows);
            }
            return dataSet;
        }

        protected DateIntervalType calculateDateInterval(ColumnGroup cg) {
            if (dateIntervalType != null) {
                return dateIntervalType;
            }

            if (GroupStrategy.DYNAMIC.equals(cg.getStrategy())) {
                Date[] limits = calculateDateLimits(cg.getSourceId());
                if (limits != null) {
                    dateIntervalType = intervalBuilderDynamicDate.calculateIntervalSize(limits[0], limits[1], cg);
                    return dateIntervalType;
                }
            }
            dateIntervalType = DateIntervalType.getByName(cg.getIntervalSize());
            return dateIntervalType;
        }

        protected Date[] calculateDateLimits(String dateColumnId) {
            if (dateLimits != null) {
                return dateLimits;
            }

            Date minDate = calculateDateLimit(dateColumnId, true);
            Date maxDate = calculateDateLimit(dateColumnId, false);
            return dateLimits = new Date[] {minDate, maxDate};
        }

        protected Date calculateDateLimit(String dateColumnId, boolean min) {
            String dbColumnId = _columnFromMetadata(metadata, dateColumnId);
            Column _dateColumn = SQLFactory.column(dbColumnId);
            Select _limitsQuery = SQLFactory.select(conn).columns(_dateColumn);
            _appendFrom(def, _limitsQuery);

            // Append the filter clauses
            for (DataSetFilter filterOp : lookup.getOperationList(DataSetFilter.class)) {
                _appendFilterBy(metadata, def, filterOp, _limitsQuery);
            }

            // Append group interval selection filters
            List<DataSetGroup> intervalSelects = lookup.getFirstGroupOpSelections();
            for (DataSetGroup intervalSelect : intervalSelects) {
                _appendIntervalSelection(intervalSelect, _limitsQuery);
            }

            _limitsQuery = _limitsQuery.where(_dateColumn.notNull())
                    .orderBy(min ? _dateColumn.asc() : _dateColumn.desc())
                    .limit(1);

            return logSQL(_limitsQuery).fetch(new ResultSetConsumer<Date>() {
                        public Date consume(ResultSet rs) {
                            try {
                                return rs.next() ? rs.getDate(1) : null;
                            } catch (Exception e) {
                                log.error("Error reading date limit from query results", e);
                                return null;
                            }
                    }
                });
        }

        protected List<DataColumn> calculateColumns(DataSetGroup gOp) {
            List<DataColumn> result = new ArrayList<>();

            if (gOp == null) {
                for (int i = 0; i < metadata.getNumberOfColumns(); i++) {
                    String columnId = metadata.getColumnId(i);
                    ColumnType columnType = metadata.getColumnType(i);
                    DataColumn column = new DataColumnImpl(columnId, columnType);
                    result.add(column);
                }
            }
            else {
                ColumnGroup cg = gOp.getColumnGroup();
                for (GroupFunction gf : gOp.getGroupFunctions()) {

                    String sourceId = gf.getSourceId();
                    String columnId = _getTargetColumnId(gf);
                    ColumnType columnType = metadata.getColumnType(sourceId);

                    DataColumnImpl column = new DataColumnImpl();
                    column.setId(columnId);
                    column.setGroupFunction(gf);
                    result.add(column);

                    // Group column
                    if (cg != null && cg.getSourceId().equals(sourceId) && gf.getFunction() == null) {
                        column.setColumnType(ColumnType.LABEL);
                        column.setColumnGroup(cg);
                        if (ColumnType.DATE.equals(columnType)) {
                            column.setIntervalType(dateIntervalType != null ? dateIntervalType.toString() : null);
                            column.setMinValue(dateLimits != null ? dateLimits[0] : null);
                            column.setMaxValue(dateLimits != null ? dateLimits[1] : null);
                        }
                    }
                    // Function column
                    else if (gf.getFunction() != null) {
                        ColumnType resultType = gf.getFunction().getResultType(columnType);
                        column.setColumnType(resultType);
                    }
                    // Existing Column
                    else {
                        column.setColumnType(columnType);
                    }
                }
                // DASHBUILDE-181: Error "Column not found" when adding group by column from SQL dataset
                if (groupColumnMustBeIncluded(gOp)) {
                    GroupFunction gf = new GroupFunction(cg.getSourceId(), cg.getColumnId(), null);
                    gOp.getGroupFunctions().add(gf);
                }
            }
            return result;
        }

        protected void _appendOrderBy(DataSetSort sortOp) {
            List<SortColumn> _columns = new ArrayList<>();
            List<ColumnSort> sortList = sortOp.getColumnSortList();
            for (ColumnSort columnSort : sortList) {
                String dbColumnId = _columnFromMetadata(metadata, columnSort.getColumnId());

                if (SortOrder.DESCENDING.equals(columnSort.getOrder())) {
                    _columns.add(SQLFactory.column(dbColumnId).desc());
                } else {
                    _columns.add(SQLFactory.column(dbColumnId).asc());
                }
            }
            _query.orderBy(_columns);
        }

        protected boolean isDynamicDateGroup(DataSetGroup groupOp) {
            ColumnGroup cg = groupOp.getColumnGroup();
            if (!ColumnType.DATE.equals(metadata.getColumnType(cg.getSourceId()))) {
                return false;
            }
            if (!GroupStrategy.DYNAMIC.equals(cg.getStrategy())) {
                return false;
            }
            return true;
        }

        protected void _appendOrderGroupBy(DataSetGroup groupOp) {
            if (isDynamicDateGroup(groupOp)) {
                ColumnGroup cg = groupOp.getColumnGroup();
                _query.orderBy(_createColumn(cg).asc());
            }
        }

        protected void _appendOrderGroupBy(DataSetGroup groupOp, DataSetSort sortOp, boolean post) {
            List<SortColumn> _columns = new ArrayList<>();
            List<ColumnSort> sortList = sortOp.getColumnSortList();
            ColumnGroup cg = groupOp.getColumnGroup();
            boolean sortPost = post;
            if (post) {
                postProcessingOps.add(sortOp);
                sortPost = false;
            }
            for (ColumnSort cs : sortList) {
                GroupFunction gf = groupOp.getGroupFunction(cs.getColumnId());

                // Sort by the group column
                if (cg.getSourceId().equals(cs.getColumnId()) || cg.getColumnId().equals(cs.getColumnId())) {
                    if (SortOrder.DESCENDING.equals(cs.getOrder())) {
                        _columns.add(_createColumn(cg).desc());
                        if (isDynamicDateGroup(groupOp) && !sortPost) {
                            postProcessingOps.add(sortOp);
                        }
                    } else {
                        _columns.add(_createColumn(cg).asc());
                        if (isDynamicDateGroup(groupOp) && !sortPost) {
                            postProcessingOps.add(sortOp);
                        }
                    }
                }
                // Sort by an aggregation
                else if (gf != null) {
                    // In SQL, sort is only permitted for columns belonging to the result set.
                    if (SortOrder.DESCENDING.equals(cs.getOrder())) {
                        _columns.add(_createColumn(gf).desc());
                    } else {
                        _columns.add(_createColumn(gf).asc());
                    }
                }
            }
            _query.orderBy(_columns);
        }

        protected void _appendIntervalSelection(DataSetGroup intervalSel, Select _query) {
            if (intervalSel != null && intervalSel.isSelect()) {
                ColumnGroup cg = intervalSel.getColumnGroup();
                List<Interval> intervalList = intervalSel.getSelectedIntervalList();

                // Get the filter values
                List<Comparable> names = new ArrayList<Comparable>();
                Comparable min = null;
                Comparable max = null;
                for (Interval interval : intervalList) {
                    names.add(interval.getName());
                    Comparable intervalMin = (Comparable) interval.getMinValue();
                    Comparable intervalMax = (Comparable) interval.getMaxValue();

                    if (intervalMin != null) {
                        if (min == null) min = intervalMin;
                        else if (min.compareTo(intervalMin) > 0) min = intervalMin;
                    }
                    if (intervalMax != null) {
                        if (max == null) max = intervalMax;
                        else if (max.compareTo(intervalMax) > 0) max = intervalMax;
                    }
                }
                // Min can't be greater than max.
                if (min != null && max != null && min.compareTo(max) > 0) {
                    min = max;
                }

                // Apply the filter
                ColumnFilter filter;
                if (min != null && max != null) {
                    filter = FilterFactory.between(cg.getSourceId(), min, max);
                }
                else if (min != null) {
                    filter = FilterFactory.greaterOrEqualsTo(cg.getSourceId(), min);
                }
                else if (max != null) {
                    filter = FilterFactory.lowerOrEqualsTo(cg.getSourceId(), max);
                }
                else {
                    filter = FilterFactory.equalsTo(cg.getSourceId(), names);
                }
                _appendFilterBy(metadata, def, filter, _query);
            }
        }

        protected void _appendGroupBy(DataSetGroup groupOp) {
            ColumnGroup cg = groupOp.getColumnGroup();
            String sourceId = cg.getSourceId();
            String dbColumnId = _columnFromMetadata(metadata, sourceId);
            ColumnType columnType = metadata.getColumnType(dbColumnId);
            boolean postProcessing = false;

            // Group by Text => not supported
            if (ColumnType.TEXT.equals(columnType)) {
                throw new IllegalArgumentException("Group by text '" + sourceId + NOT_SUPPORTED);
            }
            // Group by Date
            else if (ColumnType.DATE.equals(columnType)) {
                _query.groupBy(_createColumn(cg));
                postProcessing = true;
            }
            // Group by Label or Number (treated as label)
            else {
                _query.groupBy(SQLFactory.column(dbColumnId));
                for (GroupFunction gf : groupOp.getGroupFunctions()) {
                    if (!sourceId.equals(gf.getSourceId()) && gf.getFunction() == null) {
                        postProcessing = cg.isPostEnabled();
                    }
                }
            }

            // Also add any non-aggregated column (columns pick up) to the group statement
            for (GroupFunction gf : groupOp.getGroupFunctions()) {
                if (gf.getFunction() == null && !gf.getSourceId().equalsIgnoreCase(cg.getSourceId())) {
                    String dbGfId = _columnFromMetadata(metadata, gf.getSourceId());
                    _query.groupBy(SQLFactory.column(dbGfId));
                }
            }
            // The group operation might require post processing
            if (postProcessing) {
                DataSetGroup postGroup = groupOp.cloneInstance();
                GroupFunction gf = postGroup.getGroupFunction(sourceId);
                if (gf != null) {
                    String targetId = _getTargetColumnId(gf);
                    postGroup.getColumnGroup().setSourceId(targetId);
                    postGroup.getColumnGroup().setColumnId(targetId);
                }
                for (GroupFunction pgf : postGroup.getGroupFunctions()) {
                    AggregateFunctionType pft = pgf.getFunction();
                    pgf.setSourceId(_getTargetColumnId(pgf));
                    if (pft != null && (AggregateFunctionType.DISTINCT.equals(pft) || AggregateFunctionType.COUNT.equals(pft))) {
                        pgf.setFunction(AggregateFunctionType.SUM);
                    }
                }
                postProcessingOps.add(postGroup);
            }
        }

        protected DataSet _buildDataSet(List<DataColumn> columns, ResultSet _rs) throws Exception {
            DataSet dataSet = DataSetFactory.newEmptyDataSet();
            dataSet.setUUID(def.getUUID());
            dataSet.setDefinition(def);
            DataColumn dateGroupColumn = null;
            boolean dateIncludeEmptyIntervals = false;

            // Create an empty data set
            for (int i = 0; i < columns.size(); i++) {
                DataColumn column = columns.get(i).cloneEmpty();
                dataSet.addColumn(column);
            }

            // Offset post-processing
            if (_query.isOffsetPostProcessing() && _query.getOffset() > 0) {
                // Move the cursor to the specified offset or until the end of the result set is reached
                for (int i=0; i<_query.getOffset() && _rs.next(); i++);
            }

            // Populate the data set
            int rowIdx = 0;
            int numRows = _query.getLimit();
            while (_rs.next() && (numRows < 0 || rowIdx++ < numRows)) {
                for (int i=0; i<columns.size(); i++) {
                    DataColumn column = dataSet.getColumnByIndex(i);
                    Object value = _rs.getObject(i+1);
                    // Clob conversion must be done when object is still open
                    if (value instanceof Clob) {
                        value = JDBCUtils.clobToString((Clob) value);
                    }
                    column.getValues().add(value);
                }
            }

            // Process the data set values according to each column type and the JDBC dialect
            Dialect dialect = JDBCUtils.dialect(conn);
            for (DataColumn column : dataSet.getColumns()) {
                ColumnType columnType = column.getColumnType();
                List values = column.getValues();

                if (ColumnType.LABEL.equals(columnType)) {
                    ColumnGroup cg = column.getColumnGroup();
                    if (cg != null && ColumnType.DATE.equals(metadata.getColumnType(cg.getSourceId()))) {
                        dateGroupColumn = column;
                        dateIncludeEmptyIntervals = cg.areEmptyIntervalsAllowed();

                        // If grouped by date then convert back to absolute dates
                        // in order to allow the post processing of the data set.
                        column.setColumnType(ColumnType.DATE);
                        for (int j=0; j<values.size(); j++) {
                            Object val = values.remove(j);
                            Date dateObj = DateUtils.parseDate(column, val);
                            values.add(j, dateObj);
                        }
                    }
                    else {
                        for (int j=0; j<values.size(); j++) {
                            Object value = dialect.convertToString(values.remove(j));
                            values.add(j, value);
                        }
                    }
                }
                else if (ColumnType.NUMBER.equals(columnType)) {
                    for (int j=0; j<values.size(); j++) {
                        Object value = dialect.convertToDouble(values.remove(j));
                        values.add(j, value);
                    }
                }
                else if (ColumnType.DATE.equals(columnType)) {
                    for (int j=0; j<values.size(); j++) {
                        Object value = dialect.convertToDate(values.remove(j));
                        values.add(j, value);
                    }
                }
                else {
                    for (int j=0; j<values.size(); j++) {
                        Object value = dialect.convertToString(values.remove(j));
                        values.add(j, value);
                    }
                }

                column.setValues(values);
            }
            // Some operations requires some in-memory post-processing
            if (!postProcessingOps.isEmpty()) {
                dataSet = opEngine.execute(dataSet, postProcessingOps);
                dataSet = dataSet.trim(lookup.getRowOffset(), lookup.getNumberOfRows());
                dataSet.setUUID(def.getUUID());
                dataSet.setDefinition(def);
            }
            // Group by date might require to include empty intervals
            if (dateIncludeEmptyIntervals)  {
                IntervalBuilder intervalBuilder = intervalBuilderLocator.lookup(ColumnType.DATE, dateGroupColumn.getColumnGroup().getStrategy());
                IntervalList intervalList = intervalBuilder.build(dateGroupColumn);
                if (intervalList.size() > dataSet.getRowCount() && dataSet.getRowCountNonTrimmed() < 0) {
                    List values = dateGroupColumn.getValues();
                    int valueIdx = 0;

                    for (int intervalIdx = 0; intervalIdx < intervalList.size(); intervalIdx++) {
                        String interval = intervalList.get(intervalIdx).getName();
                        String value = values.isEmpty() ? null : (String) values.get(valueIdx++);
                        if (value == null || !value.equals(interval)) {
                            dataSet.addEmptyRowAt(intervalIdx);
                            dateGroupColumn.getValues().set(intervalIdx, interval);
                        }
                    }
                }
            }
            return dataSet;
        }

        protected Collection<Column> _createAllColumns() {
            Collection<Column> columns = new ArrayList<>();
            for (int i = 0; i < metadata.getNumberOfColumns(); i++) {
                String columnId = metadata.getColumnId(i);
                columns.add(SQLFactory.column(columnId));
            }
            return columns;
        }

        protected Collection<Column> _createColumns(DataSetGroup gOp) {
            if (gOp == null) {
                return _createAllColumns();
            }

            ColumnGroup cg = gOp.getColumnGroup();
            Collection<Column> _columns = new ArrayList<Column>();
            for (GroupFunction gf : gOp.getGroupFunctions()) {

                String sourceId = gf.getSourceId();
                if (StringUtils.isBlank(sourceId)) {
                    sourceId = metadata.getColumnId(0);
                } else {
                    _assertColumnExists(metadata, sourceId);
                }

                String targetId = gf.getColumnId();
                if (StringUtils.isBlank(targetId)) {
                    targetId = sourceId;
                }

                if (cg != null && cg.getSourceId().equals(sourceId) && gf.getFunction() == null) {
                    _columns.add(_createColumn(cg).as(targetId));
                } else {
                    _columns.add(_createColumn(gf).as(targetId));
                }
            }
            return _columns;
        }

        protected Column _createColumn(GroupFunction gf) {
            String sourceId = gf.getSourceId();
            if (sourceId == null) {
                sourceId = metadata.getColumnId(0);
            }

            AggregateFunctionType ft = gf.getFunction();
            String dbColumnId = _columnFromMetadata(metadata, sourceId);
            return SQLFactory.column(dbColumnId).function(ft);
        }

        protected Column _createColumn(ColumnGroup cg) {
            String sourceId = cg.getSourceId();
            String dbColumnId = _columnFromMetadata(metadata, sourceId);
            ColumnType columnType = metadata.getColumnType(dbColumnId);

            if (ColumnType.DATE.equals(columnType)) {
                DateIntervalType intervalType = calculateDateInterval(cg);
                if (DateIntervalType.DAY_OF_WEEK.equals(intervalType)) {
                    throw new IllegalArgumentException("Group by DAY_OF_WEEK not supported in SQL data sets");
                }
                return SQLFactory.column(dbColumnId, cg.getStrategy(), intervalType);
            }
            if (ColumnType.TEXT.equals(columnType)) {
                throw new IllegalArgumentException("Group by text '" + sourceId + NOT_SUPPORTED);
            }
            return SQLFactory.column(dbColumnId);
        }

        protected String _getTargetColumnId(GroupFunction gf) {
            String sourceId = gf.getSourceId();
            if (sourceId != null) {
                _assertColumnExists(metadata, sourceId);
            }
            return gf.getColumnId() == null ?  sourceId : gf.getColumnId();
        }
    }
}
