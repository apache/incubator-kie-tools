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

import org.dashbuilder.dataprovider.sql.model.Column;

public class MonetDBDialect extends DefaultDialect {

    @Override
    public boolean allowAliasInStatements() {
        return true;
    }

    @Override
    public String getColumnTypeSQL(Column column) {
        switch (column.getType()) {
            case NUMBER: {
                return "NUMERIC(18,3)";
            }
            case DATE: {
                return "TIMESTAMP";
            }
            default: {
                return "VARCHAR(" + column.getLength() + ")";
            }
        }
    }

    @Override
    public String getAliasForColumnSQL(String alias) {
        return "AS \"" + alias + "\"";
    }

    @Override
    public String getAliasForStatementSQL(String alias) {
        return "\"" + alias + "\"";
    }

    @Override
    public String getColumnCastSQL(Column column) {
        String columnSQL = getColumnSQL(column);
        int length = column.getLength() < 10 ? 10 : column.getLength();
        return "CAST(" + columnSQL + " AS VARCHAR(" + length + "))";
    }
}
