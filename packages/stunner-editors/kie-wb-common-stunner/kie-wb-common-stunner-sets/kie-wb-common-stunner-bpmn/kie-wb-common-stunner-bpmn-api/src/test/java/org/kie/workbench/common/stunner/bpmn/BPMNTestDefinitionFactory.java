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


package org.kie.workbench.common.stunner.bpmn;

import org.kie.workbench.common.stunner.bpmn.definition.factory.BPMNDefinitionSetModelFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTaskFactory;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.factory.definition.TypeDefinitionFactory;

public class BPMNTestDefinitionFactory implements TypeDefinitionFactory<Object> {

    private final BPMNDefinitionSetModelFactoryImpl modelFactory;
    private final CustomTaskFactory workItemFactory;

    public BPMNTestDefinitionFactory() {
        this(new WorkItemDefinitionMockRegistry());
    }

    public BPMNTestDefinitionFactory(final WorkItemDefinitionRegistry workItemDefinitionRegistry) {
        this.modelFactory = new BPMNDefinitionSetModelFactoryImpl();
        this.workItemFactory = new CustomTaskFactory(() -> workItemDefinitionRegistry);
    }

    @Override
    public boolean accepts(Class<?> type) {
        return CustomTask.class.equals(type) || modelFactory.accepts(type);
    }

    @Override
    public Object build(Class<?> type) {
        return modelFactory.build(type);
    }

    @Override
    public boolean accepts(String id) {
        return workItemFactory.accepts(id) || modelFactory.accepts(id);
    }

    @Override
    public Object build(String id) {
        if (workItemFactory.accepts(id)) {
            return workItemFactory.build(id);
        }
        return modelFactory.build(id);
    }
}
