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

import java.io.InputStream;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingRequest;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingResponse;
import org.kie.workbench.common.stunner.core.util.UUID;

public class Unmarshalling {

    public static InputStream loadStream(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    public static Diagram<Graph, Metadata> unmarshall(DiagramMarshaller tested, String fileName) throws Exception {
        InputStream is = loadStream(fileName);
        return unmarshall(tested, is);
    }

    public static Diagram<Graph, Metadata> unmarshall(DiagramMarshaller tested, InputStream is) throws Exception {
        Metadata metadata =
                new MetadataImpl.MetadataImplBuilder(
                        BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class)).build();
        DiagramImpl diagram = new DiagramImpl(UUID.uuid(), metadata);
        MarshallingResponse<Graph> response =
                tested.unmarshallWithValidation(MarshallingRequest.builder()
                                                        .mode(MarshallingRequest.Mode.AUTO)
                                                        .input(is)
                                                        .metadata(metadata)
                                                        .build());

        diagram.setGraph(response.getResult());

        return diagram;
    }
}
