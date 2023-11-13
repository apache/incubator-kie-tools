/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.workitem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;

@Dependent
@Typed(WorkItemDefinitionCacheRegistry.class)
public class WorkItemDefinitionCacheRegistry implements WorkItemDefinitionRegistry {

    private final Map<String, WorkItemDefinition> definitions;

    public WorkItemDefinitionCacheRegistry() {
        this.definitions = new HashMap<>();
    }

    @Override
    public Collection<WorkItemDefinition> items() {
        // It is done for GWT/Errai compatibility since HashMap$Values do not
        // have empty constructor. Do not simplify!
        return definitions.values().stream().collect(Collectors.toList());
    }

    @Override
    public WorkItemDefinition get(final String name) {
        return definitions.get(name);
    }

    public void register(final WorkItemDefinition def) {
        definitions.put(def.getName(), def);
    }

    public WorkItemDefinition remove(final String name) {
        return definitions.remove(name);
    }

    public boolean isEmpty() {
        return definitions.isEmpty();
    }

    public void clear() {
        definitions.clear();
    }

    @PreDestroy
    public void destroy() {
        clear();
    }
}
