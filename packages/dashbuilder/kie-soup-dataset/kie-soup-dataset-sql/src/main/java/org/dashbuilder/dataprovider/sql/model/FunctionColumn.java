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

public class FunctionColumn extends Column {

    public static final String LOWER = "lower";
    public static final String CONCAT = "concat";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String SECOND = "second";

    protected Column[] columns = null;
    protected String function = null;

    public FunctionColumn(String function, Column... columns) {
        super(function);
        this.function = function;
        this.columns = columns;
    }

    public String getFunction() {
        return function;
    }

    public Column[] getColumns() {
        return columns;
    }
}
