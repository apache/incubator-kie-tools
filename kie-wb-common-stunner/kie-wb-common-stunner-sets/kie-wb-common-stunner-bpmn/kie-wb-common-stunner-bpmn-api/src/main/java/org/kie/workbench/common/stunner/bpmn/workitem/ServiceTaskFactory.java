/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class ServiceTaskFactory
        implements DefinitionFactory<ServiceTask> {

    private static final String PREFIX = BindableAdapterUtils.getGenericClassName(ServiceTask.class);

    private final Supplier<WorkItemDefinitionRegistry> registry;

    // CDI proxy.
    protected ServiceTaskFactory() {
        this.registry = null;
    }

    @Inject
    public ServiceTaskFactory(final Instance<WorkItemDefinitionRegistry> registry) {
        this.registry = registry::get;
    }

    public ServiceTaskFactory(final Supplier<WorkItemDefinitionRegistry> registry) {
        this.registry = registry;
    }

    @Override
    public boolean accepts(final String identifier) {
        return identifier.startsWith(PREFIX);
    }

    @Override
    public ServiceTask build(final String identifier) {
        final String name = BindableAdapterUtils.getDynamicId(ServiceTask.class,
                                                              identifier);
        return null != name ?
                buildItem(name) :
                ServiceTaskBuilder.newInstance();
    }

    private ServiceTask buildItem(final String workItemName) {
        final WorkItemDefinition workItemDefinition = getRegistry().get(workItemName);
        if (null != workItemDefinition) {
            return new ServiceTaskBuilder(workItemDefinition)
                    .build();
        }
        throw new RuntimeException("No service task builder found for [" + workItemName + "]");
    }

    @SuppressWarnings("all")
    private WorkItemDefinitionRegistry getRegistry() {
        return registry.get();
    }

    public static class ServiceTaskBuilder implements Builder<ServiceTask> {

        private final WorkItemDefinition workItemDefinition;

        public ServiceTaskBuilder(final WorkItemDefinition workItemDefinition) {
            this.workItemDefinition = workItemDefinition;
        }

        public static ServiceTask newInstance() {
            return new ServiceTask();
        }

        @Override
        public ServiceTask build() {
            final ServiceTask serviceTask = newInstance();
            final String name = workItemDefinition.getName();
            setProperties(workItemDefinition,
                          serviceTask);
            serviceTask.getExecutionSet().getTaskName().setValue(name);
            serviceTask.getGeneral().getName().setValue(workItemDefinition.getDisplayName());
            serviceTask.getGeneral().getDocumentation().setValue(workItemDefinition.getDocumentation());
            serviceTask.setDescription(workItemDefinition.getDescription());
            serviceTask.getDataIOSet()
                    .getAssignmentsinfo()
                    .setValue(workItemDefinition.getParameters() + workItemDefinition.getResults());
            return serviceTask;
        }

        public static ServiceTask setProperties(final WorkItemDefinition workItemDefinition,
                                                final ServiceTask serviceTask) {
            final String name = workItemDefinition.getName();
            serviceTask.setName(name);
            serviceTask.getTaskType().setRawType(name);
            serviceTask.setCategory(workItemDefinition.getCategory());
            serviceTask.setDefaultHandler(workItemDefinition.getDefaultHandler());
            return serviceTask;
        }
    }
}
