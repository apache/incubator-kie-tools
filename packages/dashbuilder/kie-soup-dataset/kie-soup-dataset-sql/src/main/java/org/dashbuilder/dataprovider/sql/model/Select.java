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
package org.dashbuilder.dataprovider.sql.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dashbuilder.dataprovider.sql.JDBCUtils;
import org.dashbuilder.dataprovider.sql.ResultSetConsumer;
import org.dashbuilder.dataprovider.sql.ResultSetHandler;
import org.dashbuilder.dataprovider.sql.dialect.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Select extends SQLStatement<Select> {
    
    Logger logger = LoggerFactory.getLogger(Select.class);

    protected List<Column> columns = new ArrayList<Column>();
    protected String fromSelect = null;
    protected List<Condition> wheres = new ArrayList<Condition>();
    protected List<Column> groupBys = new ArrayList<Column>();
    protected List<SortColumn> orderBys = new ArrayList<SortColumn>();
    protected int limit = -1;
    protected int offset = -1;
    protected boolean offsetPostProcessing = false;
    protected List<String> quotedFields = null;

    public Select(Connection connection, Dialect dialect) {
        super(connection, dialect);
    }

    public boolean isOffsetPostProcessing() {
        return offsetPostProcessing;
    }

    public void setOffsetPostProcessing(boolean offsetPostProcessing) {
        this.offsetPostProcessing = offsetPostProcessing;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String getFromSelect() {
        return fromSelect;
    }

    public Table getFromTable() {
        return super.getTable();
    }

    public List<Condition> getWheres() {
        return wheres;
    }

    public List<Column> getGroupBys() {
        return groupBys;
    }

    public List<SortColumn> getOrderBys() {
        return orderBys;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public Select columns(Column... cols) {
        for (Column column : cols) {
            columns.add(fix(column));
        }
        return this;
    }

    public Select columns(Collection<Column> cols) {
        for (Column column : cols) {
            columns.add(fix(column));
        }
        return this;
    }

    public Select from(String sql) {
        fromSelect = sql;
        return this;
    }

    public Select from(Table table) {
        return super.table(table);
    }

    public Select where(Condition condition) {
        if (condition != null) {
            fix(condition);
            wheres.add(condition);
        }
        return this;
    }

    public Select groupBy(Column column) {
        if (column != null) {
            groupBys.add(fix(column));
        }
        return this;
    }

    public Select orderBy(SortColumn... columns) {
        for (SortColumn column : columns) {
            fix(column.getSource());
            orderBys.add(column);
        }
        return this;
    }

    public Select orderBy(List<SortColumn> columns) {
        for (SortColumn column : columns) {
            fix(column.getSource());
            orderBys.add(column);
        }
        return this;
    }

    public Select limit(int limit) {
        this.limit = limit;
        return this;
    }

    public Select offset(int offset) {
        this.offset = offset;
        return this;
    }

    public String getSQL() {
        quotedFields = JDBCUtils.getWordsBetweenQuotes(fromSelect);

        for (Column column : _columnsRefs) {
            String name = column.getName();
            if (quotedFields.contains(name)) {
                name = dialect.getColumnNameQuotedSQL(name);
            } else {
                name = JDBCUtils.fixCase(connection, name);
            }
            column.setName(name);
        }
        fromSelect = fixCase(fromSelect);
        return dialect.getSQL(this);
    }

    @Override
    public String toString() {
        return getSQL();
    }

    // Fetch
    public int fetchCount() throws SQLException {
        String countSql = dialect.getCountQuerySQL(this);
        try (ResultSetHandler handler = JDBCUtils.executeQuery(connection, countSql)) {
            ResultSet _rs = handler.getResultSet();
            return _rs.next() ? _rs.getInt(1) : 0;
        } catch (Exception e) {
            logger.debug("SQLException while fetching count with SQL command [{}]. Exception: [{}]", countSql, e);
            throw e;
        } 
    }

    public <R> R fetch(ResultSetConsumer<R> consumer) {
        try {
            String sql = getSQL();
            try (ResultSetHandler handler = JDBCUtils.executeQuery(connection, sql)){
                return consumer.consume(handler.getResultSet());
            } catch (Exception e) {
                logger.debug("SQLException while fetching results with SQL command [{}]. Exception: [{}]", sql, e);
                throw e;
            } 
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}