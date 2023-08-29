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

package org.kie.workbench.common.stunner.core.definition.service;

import java.io.IOException;
import java.io.InputStream;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessage;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingRequest;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingResponse;
import org.kie.workbench.common.stunner.core.validation.Violation;

/**
 * Provides marshalling and unmarshalling services for a Diagram.
 * @param <G> The diagram's graph type.
 * @param <M> The diagram's metadata type.
 * @param <D> The diagram's type.
 */
public interface DiagramMarshaller<G extends Graph, M extends Metadata, D extends Diagram<G, M>> {

    /**
     * Constructs a graph instance of type <code>G</code> by consuming the input stream.
     * @param metadata The diagram's metadata. Marshaller classes can update metadata, if applies, here.
     * @param input The input stream that contains the serialized graph to generate.
     * @return A graph instance of type <code>G</code>.
     * @throws IOException System I/O error.
     */
    G unmarshall(final M metadata,
                 final InputStream input) throws IOException;

    /**
     * Default implementation that returns a {@link MarshallingResponse} with the result based on
     * {@link DiagramMarshaller#unmarshall(Metadata, InputStream)} with an error in case of any exception.
     * @param request
     * @return
     */
    default MarshallingResponse<G> unmarshallWithValidation(final MarshallingRequest<InputStream, M> request) {
        try {
            final G result = unmarshall(request.getMetadata(), request.getInput());
            return MarshallingResponse.builder()
                    .result(result)
                    .state(MarshallingResponse.State.SUCCESS)
                    .build();
        } catch (Exception e) {
            final String message = e.getMessage();
            return MarshallingResponse.builder()
                    .state(MarshallingResponse.State.ERROR)
                    .addMessage(MarshallingMessage.builder()
                                        .type(Violation.Type.ERROR)
                                        .message(message)
                                        .build())
                    .build();
        }
    }

    /**
     * Serializes a diagram instance of type <code>D</code> as string.
     * @param diagram The diagram instance to serialize.
     * @return The serialized diagram's raw value.
     * @throws IOException System I/O error.
     */
    String marshall(final D diagram) throws IOException;

    /**
     * Provides a un/marshaller instance for the Diagram's metadata.
     * @return The diagram's metadata marshaller.
     */
    DiagramMetadataMarshaller<M> getMetadataMarshaller();
}
