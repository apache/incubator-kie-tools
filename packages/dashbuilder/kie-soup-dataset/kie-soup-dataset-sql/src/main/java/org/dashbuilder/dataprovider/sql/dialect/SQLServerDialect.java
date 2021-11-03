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
package org.dashbuilder.dataprovider.sql.dialect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.dashbuilder.dataprovider.sql.JDBCUtils;
import org.dashbuilder.dataprovider.sql.model.Column;
import org.dashbuilder.dataprovider.sql.model.Select;
import org.dashbuilder.dataprovider.sql.model.SortColumn;

/**
 * Microsoft SQL Server dialect
 */
public class SQLServerDialect extends DefaultDialect {

    @Override
    public String getColumnTypeSQL(Column column) {
        switch (column.getType()) {
            case NUMBER: {
                return "NUMERIC(28,2)";
            }
            case DATE: {
                return "DATETIME";
            }
            default: {
                return "VARCHAR(" + column.getLength() + ")";
            }
        }
    }

    @Override
    public String getConcatFunctionSQL(Column[] columns) {
        return super.getConcatFunctionSQL(columns, "CONCAT(", ")", ",");
    }

    @Override
    public String getDatePartFunctionSQL(String part, Column column) {
        String columnSQL = getColumnSQL(column);
        return "DATEPART(" + part + "," + columnSQL + ")";
    }

    SimpleDateFormat sqlServerDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    @Override
    public String getDateParameterSQL(Date param) {
        // '2015-08-24 13:14:36'
        return "'" + sqlServerDateFormat.format(param) + "'";
    }

    @Override
    public String getCountQuerySQL(Select select) {
        int offset = select.getOffset();
        int limit = select.getLimit();
        if (limit <= 0 && offset <= 0 && !select.getOrderBys().isEmpty()) {
            List<SortColumn> sortColumns = new ArrayList<SortColumn>();
            sortColumns.addAll(select.getOrderBys());
            try {
                // ORDER BY clauses within nested queries are not supported
                select.getOrderBys().clear();
                return "SELECT COUNT(*) FROM (" + select.getSQL() + ") \"dbSQL\"";
            } finally {
                select.orderBy(sortColumns);
            }
        }
        return "SELECT COUNT(*) FROM (" + select.getSQL() + ") \"dbSQL\"";
    }

    /**
     * Since SQL Server 2012 pagination queries are resolved as follows:
     *
     * <ul>
     *      <li>1. offset <= 0 limit > 0</li>
     *      <p>SELECT <b>TOP limit</b> * FROM "EXPENSE_REPORTS"</p>
     *
     *      <li>2. offset > 0 limit > 0</li>
     *
     *      <p>SELECT * FROM "EXPENSE_REPORTS" ORDER BY DEPARTMENT <b>OFFSET offset ROWS FETCH NEXT limit ROWS ONLY</b></p>
     *      <p>This second case requires a mandatory order by clause.</p>
     * </ul>
     *
     * The methods below implement the above requirements.
     */

    @Override
    public String getSQL(Select select) {
        int offset = select.getOffset();
        int limit = select.getLimit();
        if ((limit > 0 || offset > 0) && select.getOrderBys().isEmpty()) {
            List<Column> columns = select.getColumns();
            if (columns.isEmpty()) {
                columns = fetchColumns(select);
            }
            if (!columns.isEmpty()) {
                select.orderBy(columns.get(0).asc());
            }
        }
        return super.getSQL(select);
    }

    public List<Column> fetchColumns(Select select) {
        int offset = select.getOffset();
        int limit = select.getLimit();
        try {
            // Disable limits & fetch results
            select.limit(0).offset(0);
            return select.fetch(rs -> {
                try {
                    return JDBCUtils.getColumns(rs, null);
                } catch (Exception e) {
                    return Collections.emptyList();
                }
            });
        }
        finally {
            // Restore original limits
            select.limit(limit).offset(offset);
        }
    }

    @Override
    public String getSelectStatement(Select select) {
        int offset = select.getOffset();
        int limit = select.getLimit();
        if (offset <= 0 && limit >= 0) {
            return "SELECT TOP " + limit;
        } else {
            return "SELECT";
        }
    }

    @Override
    public String getOffsetLimitSQL(Select select) {
        int offset = select.getOffset();
        int limit = select.getLimit();
        StringBuilder out = new StringBuilder();
        if (offset > 0) {
            if (offset > 0) out.append(" OFFSET ").append(offset).append(" ROWS");
            if (limit >= 0) out.append(" FETCH FIRST ").append(limit).append(" ROWS ONLY");
        }
        return out.toString();
    }
}
