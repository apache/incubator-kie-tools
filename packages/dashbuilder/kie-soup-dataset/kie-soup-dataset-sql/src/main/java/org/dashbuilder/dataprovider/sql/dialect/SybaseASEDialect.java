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
import java.util.Date;

import org.dashbuilder.dataprovider.sql.model.Column;
import org.dashbuilder.dataprovider.sql.model.Select;

public class SybaseASEDialect extends DefaultDialect {

    @Override
    public String getColumnTypeSQL(Column column) {
        switch (column.getType()) {
            case NUMBER: {
                return "NUMERIC(28,3)";
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
    public Date convertToDate(Object value) {
        if (value == null) {
            return null;
        }
        // new Date( ((com.sybase.jdbc4.tds.SybTimestamp) value).getTime() )
        Long time = (Long) invokeMethod(value, "getTime", null);
        return new Date(time);
    }

    SimpleDateFormat sybaseDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    @Override
    public String getDateParameterSQL(Date param) {
        // '2015-08-24 13:14:36'
        return "'" + sybaseDateFormat.format(param) + "'";
    }

    public String getColumnCastSQL(Column column) {
        String columnSQL = getColumnSQL(column);
        return "CONVERT(VARCHAR, " + columnSQL + ")";
    }

    @Override
    public String getDatePartFunctionSQL(String part, Column column) {
        String columnSQL = getColumnSQL(column);
        return "DATEPART(" + part + "," + columnSQL + ")";
    }

    @Override public String getConcatFunctionSQL(Column[] columns, String begin, String end, String separator) {
        return super.getConcatFunctionSQL(columns, begin, end, separator);
    }

    /**
     * Sybase ASE pagination queries are resolved as follows:
     *
     * <ul>
     *      <li>1. offset <= 0 limit >= 0</li>
     *      <p>SELECT <b>TOP limit</b> * FROM "EXPENSE_REPORTS" ORDER BY DEPARTMENT ASC</p>
     *
     *      <li>2. offset > 0 limit >= 0</li>
     *      <p>SELECT <b>TOP limit+offset</b> * FROM "EXPENSE_REPORTS" ORDER BY DEPARTMENT ASC
     *
     *      <li>3. offset > 0 limit < 0</li>
     *      <p>SELECT * FROM "EXPENSE_REPORTS" ORDER BY DEPARTMENT ASC
     * </ul>
     *
     * Case #2 and #3 requires a further post-prorcessing of the query results in order to apply offset.
     * Since Sybase ASE does not provide an easy way to implement a native offset mechanism the solution is to
     * let dashbuilder to resolve it during the results post-processing stage.
     */

    @Override
    public String getSelectStatement(Select select) {
        int offset = select.getOffset();
        int limit = select.getLimit();
        if (limit >= 0) {
            if (offset > 0) {
                select.setOffsetPostProcessing(true);
                return "SELECT TOP " + (offset + limit);
            } else {
                return "SELECT TOP " + limit;
            }
        } else {
            if (offset > 0) {
                select.setOffsetPostProcessing(true);
            }
            return "SELECT";
        }
    }

    @Override
    public String getOffsetLimitSQL(Select select) {
        return null;
    }
}
