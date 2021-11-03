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

import java.util.Collection;
import java.util.ArrayList;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.LogicalExprType;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.sort.SortOrder;

public class Column {

    protected String name;
    protected String alias;
    protected ColumnType type;
    protected int length;

    public Column(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ColumnType getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public String getAlias() {
        return alias;
    }

    public Column setName(String name) {
        this.name = name;
        return this;
    }

    public Column setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public Column setType(ColumnType type) {
        this.type = type;
        return this;
    }

    public Column setLength(int length) {
        this.length = length;
        return this;
    }

    // Column modifier methods

    public Column as(String alias) {
        setAlias(alias);
        return this;
    }

    public Column lower() {
        return new FunctionColumn(FunctionColumn.LOWER, this);
    }

    public SortColumn asc() {
        return new SortColumn(this, SortOrder.ASCENDING);
    }

    public SortColumn desc() {
        return new SortColumn(this, SortOrder.DESCENDING);
    }

    public Column function(AggregateFunctionType functionType) {
        SimpleColumn simpleColumn = new SimpleColumn(name, type, length);
        simpleColumn.setFunctionType(functionType);
        return simpleColumn;
    }

    // Condition factory methods

    public Condition isNull() {
        return new CoreCondition(this, CoreFunctionType.IS_NULL);
    }

    public Condition notNull() {
        return new CoreCondition(this, CoreFunctionType.NOT_NULL);
    }

    public Condition equalsTo(Object param) {
        return new CoreCondition(this, CoreFunctionType.EQUALS_TO, param);
    }

    public Condition in(Collection params) {
        Collection<Condition> conditions = new ArrayList<Condition>();
        for (Object param : params) {
            conditions.add(equalsTo(param));
        }
        return new LogicalCondition(LogicalExprType.OR, conditions);
    }

    public Condition inSql(Collection params) {
        return new CoreCondition(this, CoreFunctionType.IN, params);
    }

    public Condition notInSql(Collection params) {
        return new CoreCondition(this, CoreFunctionType.NOT_IN, params);
    }

    public Condition notEquals(Object param) {
        return new CoreCondition(this, CoreFunctionType.NOT_EQUALS_TO, param);
    }

    public Condition like(String param) {
        return new CoreCondition(this, CoreFunctionType.LIKE_TO, param);
    }

    public Condition greaterThan(Object param) {
        return new CoreCondition(this, CoreFunctionType.GREATER_THAN, param);
    }

    public Condition greaterOrEquals(Object param) {
        return new CoreCondition(this, CoreFunctionType.GREATER_OR_EQUALS_TO, param);
    }

    public Condition lowerThan(Object param) {
        return new CoreCondition(this, CoreFunctionType.LOWER_THAN, param);
    }

    public Condition lowerOrEquals(Object param) {
        return new CoreCondition(this, CoreFunctionType.LOWER_OR_EQUALS_TO, param);
    }

    public Condition between(Object from, Object to) {
        return new CoreCondition(this, CoreFunctionType.BETWEEN, from, to);
    }

    // Date functions

    public Column year() {
        return new FunctionColumn(FunctionColumn.YEAR, this);
    }

    public Column month() {
        return new FunctionColumn(FunctionColumn.MONTH, this);
    }

    public Column day() {
        return new FunctionColumn(FunctionColumn.DAY, this);
    }

    public Column hour() {
        return new FunctionColumn(FunctionColumn.HOUR, this);
    }

    public Column minute() {
        return new FunctionColumn(FunctionColumn.MINUTE, this);
    }

    public Column second() {
        return new FunctionColumn(FunctionColumn.SECOND, this);
    }
}
