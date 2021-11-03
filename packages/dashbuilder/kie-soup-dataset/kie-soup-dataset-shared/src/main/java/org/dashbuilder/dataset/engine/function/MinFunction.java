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
package org.dashbuilder.dataset.engine.function;

import java.util.List;
import java.util.stream.Collectors;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.group.AggregateFunctionType;

/**
 * It calculates the min. number of a set of values.
 */
public class MinFunction extends AbstractFunction {

    public MinFunction() {
        super();
    }

    public AggregateFunctionType getType() {
        return AggregateFunctionType.MIN;
    }

    public Object aggregate(List values) {
        if (values == null || values.isEmpty()) {
            return null;
        }

        // Get the min. value from the collection.
        Comparable result = null;
        for (Object obj : values) {
            Comparable val = (Comparable) obj;
            if (val == null) {
                continue;
            }
            if (result == null || val.compareTo(result) < 0) {
                result = val;
            }
        }

        // Adjust to the specified precision.
        return result instanceof Number ? round((Number) result, precission) : result;
    }

    public Object aggregate(List values, List<Integer> rows) {
        if (rows == null) {
            return aggregate(values);
        }
        if (rows.isEmpty() || values == null || values.isEmpty()) {
            return null;
        }

        // Get the min. value within the target rows.
        Comparable result = null;
        for (Integer row : rows) {
            Comparable val = (Comparable) values.get(row);
            if (val == null) {
                continue;
            }
            if (result == null || val.compareTo(result) < 0) {
                result = val;
            }
        }
        // Adjust to the specified precision.
        return result instanceof Number ? round((Number) result, precission) : result;
    }
}