/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.bpmn.project.backend.service.indexing;

import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.bpmn.project.backend.indexing.BpmnFileIndexer;
import org.kie.workbench.common.stunner.bpmn.resource.BPMNDefinitionSetResourceType;
import org.uberfire.io.IOService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestBpmnFileIndexer extends BpmnFileIndexer implements TestIndexer<BPMNDefinitionSetResourceType> {

    public TestBpmnFileIndexer() {
        this.classLoaderHelper = mock(ModuleClassLoaderHelper.class);
        when(this.classLoaderHelper.getModuleClassLoader(any())).thenReturn(this.getClass().getClassLoader());
    }

    @Override
    public void setIOService(IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public void setModuleService(KieModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @Override
    public void setResourceTypeDefinition(BPMNDefinitionSetResourceType type) {
        this.bpmnTypeDefinition = type;
    }
}
