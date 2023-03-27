/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import org.dashbuilder.dataset.group.AggregateFunctionType;

/**
 * It calculates the average value of a set of numbers.
 */
public class MedianFunction extends AbstractFunction {

    public MedianFunction() {
        super();
    }

    public AggregateFunctionType getType() {
        return AggregateFunctionType.MEDIAN;
    }

    public Object aggregate(List values) {
        if (values == null || values.isEmpty()) {
            return 0d;
        }

        var n = values.size();

        if (n == 1) {
            return ((Number) values.get(0)).doubleValue();
        }

        var sortedValues = values.stream().mapToDouble(v -> ((Number) v).doubleValue()).sorted().toArray();

        if (n % 2 == 1) {
            return sortedValues[n / 2];
        }
        var middle = n / 2;
        var ii = n == 2 ? 0 : middle - 1;
        var is = n == 2 ? 1 : middle;
        var v = (sortedValues[ii] + sortedValues[is]) / 2d;
        return round(v, precission);
    }

    public Object aggregate(List values, List<Integer> rows) {
        if (rows == null) {
            return aggregate(values);
        }
        if (rows.isEmpty()) {
            return 0d;
        }
        if (values == null || values.isEmpty()) {
            return 0d;
        }
        var _values = rows.stream()
                .map(values::get)
                .collect(Collectors.toList());
        return this.aggregate(_values);
    }

}
