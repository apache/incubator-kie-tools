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

import java.util.Date;

import org.dashbuilder.dataprovider.sql.model.Column;
import org.dashbuilder.dataprovider.sql.model.Condition;
import org.dashbuilder.dataprovider.sql.model.CoreCondition;
import org.dashbuilder.dataprovider.sql.model.CreateTable;
import org.dashbuilder.dataprovider.sql.model.Delete;
import org.dashbuilder.dataprovider.sql.model.DynamicDateColumn;
import org.dashbuilder.dataprovider.sql.model.FixedDateColumn;
import org.dashbuilder.dataprovider.sql.model.FunctionColumn;
import org.dashbuilder.dataprovider.sql.model.Insert;
import org.dashbuilder.dataprovider.sql.model.LogicalCondition;
import org.dashbuilder.dataprovider.sql.model.SQLStatement;
import org.dashbuilder.dataprovider.sql.model.Select;
import org.dashbuilder.dataprovider.sql.model.SimpleColumn;
import org.dashbuilder.dataprovider.sql.model.SortColumn;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.sort.SortOrder;

public interface Dialect {

    String getCountQuerySQL(Select select);

    String getSQL(CreateTable create);

    String getSQL(Select select);

    String getSQL(Insert insert);

    String getSQL(Delete delete);

    String getSelectSQL(Select select);

    String getFromSQL(Select select);

    String getWhereSQL(Select select);

    String getWhereSQL(Delete delete);

    String getGroupBySQL(Select select);

    String getOrderBySQL(Select select);

    String getOffsetLimitSQL(Select select);

    String getSelectStatement(Select select);

    String getInsertStatement(Insert insert);

    String getDeleteStatement(Delete delete);

    String getFromStatement(Select select);

    String getWhereStatement(Select select);

    String getWhereStatement(Delete delete);

    String getGroupByStatement(Select select);

    String getOrderByStatement(Select select);

    String getColumnSQL(Column column);

    String getColumnTypeSQL(Column column);

    String convertToString(Object value);

    Double convertToDouble(Object value);

    Date convertToDate(Object value);

    String[] getExcludedColumns();

    String getTableSQL(SQLStatement<?> stmt);

    String getTableNameSQL(String name);

    String getSchemaNameSQL(String name);

    String getSimpleColumnSQL(SimpleColumn column);

    String getFunctionColumnSQL(FunctionColumn column);

    String getLowerFunctionSQL(Column column);

    String getConcatFunctionSQL(Column[] columns);

    String getDatePartFunctionSQL(String part, Column column);

    String getSortColumnSQL(SortColumn column);

    String getSortOrderSQL(SortOrder order);

    String getDynamicDateColumnSQL(DynamicDateColumn column);

    String getFixedDateColumnSQL(FixedDateColumn column);

    String getColumnNameSQL(String name);

    String getColumnNameQuotedSQL(String name);

    String getAliasForColumnSQL(String alias);

    String getAliasForStatementSQL(String alias);

    String getConditionSQL(Condition condition);

    String getCoreConditionSQL(CoreCondition condition);

    String getNotNullConditionSQL(String column);

    String getIsNullConditionSQL(String column);

    String getIsEqualsToConditionSQL(String column, Object param);

    String getNotEqualsToConditionSQL(String column, Object param);

    String getLikeToConditionSQL(String column, Object param);

    String getGreaterThanConditionSQL(String column, Object param);

    String getGreaterOrEqualsConditionSQL(String column, Object param);

    String getLowerThanConditionSQL(String column, Object param);

    String getLowerOrEqualsConditionSQL(String column, Object param);

    String getBetweenConditionSQL(String column, Object from, Object to);

    String getInConditionSQL(String column, Object param);

    String getNotInConditionSQL(String column, Object param);

    String getParameterSQL(Object param);

    String getNumberParameterSQL(Number param);

    String getDateParameterSQL(Date param);

    String getStringParameterSQL(String param);

    String getLogicalConditionSQL(LogicalCondition condition);

    String getNotExprConditionSQL(Condition condition);

    String getAndExprConditionSQL(Condition[] conditions);

    String getOrExprConditionSQL(Condition[] conditions);

    String getColumnFunctionSQL(String column, AggregateFunctionType function);
}
