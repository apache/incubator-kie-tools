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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTaskFactory;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;

public class BPMNTestScopeModelFactory extends TestScopeModelFactory {

    private final ServiceTaskFactory workItemFactory;

    public BPMNTestScopeModelFactory(final Object definitionSet,
                                     final WorkItemDefinitionRegistry workItemDefinitionRegistry) {
        super(definitionSet);
        this.workItemFactory = new ServiceTaskFactory(() -> workItemDefinitionRegistry);
    }

    @Override
    public Object build(final String id) {
        if (workItemFactory.accepts(id)) {
            return workItemFactory.build(id);
        }
        return super.build(id);
    }

    @Override
    public boolean accepts(String id) {
        return workItemFactory.accepts(id) || super.accepts(id);
    }
}
