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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.dataprovider.sql.JDBCUtils;
import org.dashbuilder.dataprovider.sql.dialect.Dialect;

public class Delete extends SQLStatement<Delete> {

    protected List<Condition> wheres = new ArrayList<Condition>();

    public Delete(Connection connection, Dialect dialect) {
        super(connection, dialect);
    }

    public Table getFromTable() {
        return super.getTable();
    }

    public List<Condition> getWheres() {
        return wheres;
    }

    public Delete from(Table table) {
        return super.table(table);
    }

    public Delete where(Condition condition) {
        if (condition != null) {
            if (condition instanceof CoreCondition) {
                fix(((CoreCondition) condition).getColumn());
            }
            wheres.add(condition);
        }
        return this;
    }

    public String getSQL() {
        return dialect.getSQL(this);
    }

    @Override
    public String toString() {
        return getSQL();
    }

    public void execute() throws SQLException {
        String sql = getSQL();
        JDBCUtils.execute(connection, sql);
    }
}