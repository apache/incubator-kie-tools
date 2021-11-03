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

import org.dashbuilder.dataset.group.AggregateFunctionType;

/**
 * It calculates the average value of a set of numbers.
 */
public class AverageFunction extends SumFunction {

    public AverageFunction() {
        super();
    }

    public AggregateFunctionType getType() {
        return AggregateFunctionType.AVERAGE;
    }

    public Object aggregate(List values) {
        if (values == null || values.isEmpty()) {
            return 0d;
        }
        double average = (double) super.aggregate(values) / values.size();
        return round(average, precission);
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

        double average = (double) super.aggregate(values, rows) / rows.size();
        return round(average, precission);
    }

}