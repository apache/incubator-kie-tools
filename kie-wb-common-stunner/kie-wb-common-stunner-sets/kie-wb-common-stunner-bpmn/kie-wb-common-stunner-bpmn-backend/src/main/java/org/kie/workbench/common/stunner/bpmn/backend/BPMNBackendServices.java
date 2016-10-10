/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.BPMNDiagramMarshaller;
import org.kie.workbench.common.stunner.core.definition.DefinitionSetServices;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.marshall.DiagramMarshaller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.InputStream;

@ApplicationScoped
public class BPMNBackendServices implements DefinitionSetServices {

    public static final String EXTENSION = "bpmn";

    @Inject
    BPMNDiagramMarshaller bpmnDiagramMarshaller;

    @Override
    public boolean accepts( final String defSetId ) {
        final String id = BindableAdapterUtils.getDefinitionSetId( BPMNDefinitionSet.class );
        return defSetId != null && defSetId.equals( id );
    }

    @Override
    public String getFileExtension() {
        return EXTENSION;
    }

    @Override
    public DiagramMarshaller<Diagram, InputStream, String> getDiagramMarshaller() {
        return bpmnDiagramMarshaller;
    }

}
