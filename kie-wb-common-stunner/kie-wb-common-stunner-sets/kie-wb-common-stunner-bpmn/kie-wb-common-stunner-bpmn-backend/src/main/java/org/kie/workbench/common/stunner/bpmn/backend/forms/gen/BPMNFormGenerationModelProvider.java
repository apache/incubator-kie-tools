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

package org.kie.workbench.common.stunner.bpmn.backend.forms.gen;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceImpl;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.backend.gen.FormGenerationModelProvider;

@ApplicationScoped
public class BPMNFormGenerationModelProvider
        implements FormGenerationModelProvider<JBPMBpmn2ResourceImpl> {

    private final BPMNDiagramMarshaller bpmnDiagramMarshaller;
    private final DefinitionUtils definitionUtils;
    private String definitionSetId;

    // CDI proxy.
    protected BPMNFormGenerationModelProvider() {
        this(null,
             null);
    }

    @Inject
    public BPMNFormGenerationModelProvider(final BPMNDiagramMarshaller bpmnDiagramMarshaller,
                                           final DefinitionUtils definitionUtils) {
        this.bpmnDiagramMarshaller = bpmnDiagramMarshaller;
        this.definitionUtils = definitionUtils;
    }

    @PostConstruct
    public void init() {
        this.definitionSetId = definitionUtils.getDefinitionSetId(BPMNDefinitionSet.class);
    }

    @Override
    public boolean accepts(final Diagram diagram) {
        return this.definitionSetId.equals(diagram.getMetadata().getDefinitionSetId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public JBPMBpmn2ResourceImpl generate(final Diagram diagram) throws IOException {
        return bpmnDiagramMarshaller.marshallToBpmn2Resource(diagram);
    }
}
