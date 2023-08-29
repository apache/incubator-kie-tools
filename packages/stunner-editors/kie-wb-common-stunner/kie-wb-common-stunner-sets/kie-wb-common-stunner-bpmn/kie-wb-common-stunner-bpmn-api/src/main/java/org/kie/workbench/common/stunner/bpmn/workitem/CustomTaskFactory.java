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

import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.definition.DefinitionFactory;

@ApplicationScoped
public class CustomTaskFactory
        implements DefinitionFactory<CustomTask> {

    private static final String PREFIX = BindableAdapterUtils.getGenericClassName(CustomTask.class);

    private final Supplier<WorkItemDefinitionRegistry> registry;

    // CDI proxy.
    protected CustomTaskFactory() {
        this.registry = null;
    }

    @Inject
    public CustomTaskFactory(final Instance<WorkItemDefinitionRegistry> registry) {
        this.registry = registry::get;
    }

    public CustomTaskFactory(final Supplier<WorkItemDefinitionRegistry> registry) {
        this.registry = registry;
    }

    @Override
    public boolean accepts(final String identifier) {
        return identifier.startsWith(PREFIX);
    }

    @Override
    public CustomTask build(final String identifier) {
        final String name = BindableAdapterUtils.getDynamicId(CustomTask.class,
                                                              identifier);
        return null != name ?
                buildItem(name) :
                CustomTaskBuilder.newInstance();
    }

    public CustomTask buildItem(final String workItemName) {
        final WorkItemDefinition workItemDefinition = getRegistry().get(workItemName);

        return new CustomTaskBuilder(workItemDefinition).build();
    }

    @SuppressWarnings("all")
    private WorkItemDefinitionRegistry getRegistry() {
        return registry.get();
    }

    public static class CustomTaskBuilder implements Builder<CustomTask> {

        private final WorkItemDefinition workItemDefinition;

        public CustomTaskBuilder(final WorkItemDefinition workItemDefinition) {
            this.workItemDefinition = workItemDefinition;
        }

        public static CustomTask newInstance() {
            return new CustomTask();
        }

        @Override
        public CustomTask build() {
            final CustomTask customTask = newInstance();

            if (null != workItemDefinition) {
                final String name = workItemDefinition.getName();
                setProperties(workItemDefinition,
                              customTask);
                customTask.getExecutionSet().getTaskName().setValue(name);
                customTask.getGeneral().getName().setValue(workItemDefinition.getDisplayName());
                customTask.getGeneral().getDocumentation().setValue(workItemDefinition.getDocumentation());
                customTask.setDescription(workItemDefinition.getDescription());
                customTask.getDataIOSet()
                        .getAssignmentsinfo()
                        .setValue(workItemDefinition.getParameters() + workItemDefinition.getResults());
            }

            return customTask;
        }

        public static CustomTask setProperties(final WorkItemDefinition workItemDefinition,
                                               final CustomTask customTask) {
            final String name = workItemDefinition.getName();
            customTask.setName(name);
            customTask.getTaskType().setRawType(name);
            customTask.setCategory(workItemDefinition.getCategory());
            customTask.setDefaultHandler(workItemDefinition.getDefaultHandler());
            return customTask;
        }
    }
}
