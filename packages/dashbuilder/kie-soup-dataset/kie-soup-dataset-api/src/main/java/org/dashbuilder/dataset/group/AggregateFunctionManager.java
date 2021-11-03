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
package org.dashbuilder.dataset.group;

import java.util.Collection;

/**
 * Manager class that it keeps a registry of AggregateFunction instances.
 */
public interface AggregateFunctionManager {

    /**
     * Get all the aggregate functions registered.
     */
    Collection<AggregateFunction> getAllFunctions();

    /**
     * Get an aggregate function by its type.
     */
    AggregateFunction getFunctionByType(AggregateFunctionType type);

    /**
     * Register an aggregate function.
     */
    void registerFunction(AggregateFunction function);
}

