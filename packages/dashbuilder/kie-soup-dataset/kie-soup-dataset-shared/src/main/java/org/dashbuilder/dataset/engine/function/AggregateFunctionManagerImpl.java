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

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import org.dashbuilder.dataset.group.AggregateFunction;
import org.dashbuilder.dataset.group.AggregateFunctionManager;
import org.dashbuilder.dataset.group.AggregateFunctionType;

public class AggregateFunctionManagerImpl implements AggregateFunctionManager {

    /**
     * The built-in aggregate function registry.
     */
    protected Map<AggregateFunctionType, AggregateFunction> functionMap
            = new EnumMap<AggregateFunctionType, AggregateFunction>(AggregateFunctionType.class);

    public AggregateFunctionManagerImpl() {
        registerFunction(new CountFunction());
        registerFunction(new DistinctFunction());
        registerFunction(new SumFunction());
        registerFunction(new AverageFunction());
        registerFunction(new MaxFunction());
        registerFunction(new MinFunction());
    }

    public Collection<AggregateFunction> getAllFunctions() {
        return functionMap.values();
    }

    public AggregateFunction getFunctionByType(AggregateFunctionType type) {
        return functionMap.get(type);
    }

    public void registerFunction(AggregateFunction function) {
        functionMap.put(function.getType(), function);
    }
}
