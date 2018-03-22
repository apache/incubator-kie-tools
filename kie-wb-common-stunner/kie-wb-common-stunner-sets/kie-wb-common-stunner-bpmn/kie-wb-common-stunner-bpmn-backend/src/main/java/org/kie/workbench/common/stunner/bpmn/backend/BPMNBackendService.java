/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.resource.BPMNDefinitionSetResourceType;
import org.kie.workbench.common.stunner.core.backend.service.AbstractDefinitionSetService;
import org.kie.workbench.common.stunner.core.definition.DefinitionSetResourceType;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BPMNBackendService extends AbstractDefinitionSetService {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BPMNBackendService.class);
    private static final String MARSHALLER_EXPERIMENTAL_PROPERTY = "bpmn.marshaller.experimental";

    private final BPMNDefinitionSetResourceType bpmnResourceType;

    protected BPMNBackendService() {
        this(null,
             null,
             null);
    }

    @Inject
    public BPMNBackendService(
            final BPMNDiagramMarshaller bpmnDiagramMarshaller,
            final BPMNDirectDiagramMarshaller bpmnDirectDiagramMarshaller,
            final BPMNDefinitionSetResourceType bpmnResourceType) {
        super(chooseMarshaller(
                bpmnDiagramMarshaller,
                bpmnDirectDiagramMarshaller));
        this.bpmnResourceType = bpmnResourceType;
    }

    private static DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> chooseMarshaller(
            final BPMNDiagramMarshaller bpmnDiagramMarshaller,
            final BPMNDirectDiagramMarshaller bpmnDirectDiagramMarshaller) {

        Boolean enableExperimentalBpmnMarshaller = Optional.ofNullable(
                System.getProperty(MARSHALLER_EXPERIMENTAL_PROPERTY))
                .map(Boolean::parseBoolean)
                .orElse(false);

        LOG.info("{} = {}", MARSHALLER_EXPERIMENTAL_PROPERTY, enableExperimentalBpmnMarshaller);

        return (enableExperimentalBpmnMarshaller) ?
                bpmnDirectDiagramMarshaller :
                bpmnDiagramMarshaller;
    }

    @Override
    public DefinitionSetResourceType getResourceType() {
        return bpmnResourceType;
    }
}
