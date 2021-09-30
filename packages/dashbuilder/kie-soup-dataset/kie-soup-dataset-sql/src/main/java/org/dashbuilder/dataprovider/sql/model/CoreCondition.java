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

import org.dashbuilder.dataset.filter.CoreFunctionType;

public class CoreCondition extends Condition {

    protected Column column;
    protected CoreFunctionType function;
    protected Object[] parameters;

    public CoreCondition(Column column, CoreFunctionType function, Object... params) {
        this.column = column;
        this.function = function;
        this.parameters = params;
    }

    public Column getColumn() {
        return column;
    }

    public CoreFunctionType getFunction() {
        return function;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
