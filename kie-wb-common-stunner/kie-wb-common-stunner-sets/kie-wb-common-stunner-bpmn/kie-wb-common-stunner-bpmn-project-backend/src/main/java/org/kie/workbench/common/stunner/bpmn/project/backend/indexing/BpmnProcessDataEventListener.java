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
package org.kie.workbench.common.stunner.bpmn.project.backend.indexing;

import org.kie.api.definition.process.Process;
import org.kie.workbench.common.services.refactoring.backend.server.impact.ResourceReferenceCollector;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

/**
 * This listener is called by the build process and immediately stores the indexing information (as it is also a
 * {@link ResourceReferenceCollector})
 * </p>
 * In the {@link #onProcessAdded(Process)} method, it stores itself in the {@link Process}, allowing the {@link BpmnFileIndexer}
 * to retrieve the {@link BpmnProcessDataEventListener} instance later and add it to the indexing information.
 */
public class BpmnProcessDataEventListener extends AbstractBpmnProcessDataEventListener {

    static final String NAME = "BPMNProcessInfoCollector";

    @Override
    protected String getProcessDescriptorName() {
        return NAME;
    }

    protected ResourceType getProcessIdResourceType() {
        return ResourceType.BPMN2;
    }

    protected ResourceType getProcessNameResourceType() {
        return ResourceType.BPMN2_NAME;
    }
}
