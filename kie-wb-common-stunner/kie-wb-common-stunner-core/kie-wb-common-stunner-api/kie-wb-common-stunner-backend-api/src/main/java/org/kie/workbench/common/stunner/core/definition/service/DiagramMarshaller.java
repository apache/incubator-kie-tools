/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.definition.service;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides marshalling and unmarshalling services for a Diagram.
 *
 * @param <G> The diagram's graph type.
 * @param <M> The diagram's metadata type.
 * @param <D> The diagram's type.
 */
public interface DiagramMarshaller<G extends Graph, M extends Metadata, D extends Diagram<G, M>> {

    /**
     * Constructs a graph instance of type <code>G</code> by consuming the input stream.
     *
     * @param metadata The diagram's metadata. Marshaller classes can update metadata, if applies, here.
     * @param input    The input stream that contains the serialized graph to generate.
     * @return A graph instance of type <code>G</code>.
     * @throws IOException System I/O error.
     */
    G unmarshall( M metadata, InputStream input ) throws IOException;

    /**
     * Serializes a diagram instance of type <code>D</code> as string.
     *
     * @param diagram The diagram instance to serialize.
     * @return The serialized diagram's raw value.
     * @throws IOException System I/O error.
     */
    String marshall( D diagram ) throws IOException;

    /**
     * Provides a un/marshaller instance for the Diagram's metadata.
     *
     * @return The diagram's metadata marshaller.
     */
    DiagramMetadataMarshaller<M> getMetadataMarshaller();

}
