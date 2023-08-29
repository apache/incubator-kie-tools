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

import org.kie.workbench.common.stunner.core.diagram.Metadata;

/**
 * Provides marshalling and unmarshalling services for the diagram's metadata.
 * @param <M> The type of the metadata.
 */
public interface DiagramMetadataMarshaller<M extends Metadata> {

    /**
     * Constructs a metadata instance of type <code>M</code> by consuming the input stream.
     * @param input The input stream that contains metadata's raw data.
     * @return A metadata instance of type <code>M</code.
     * @throws IOException System I/O error.
     */
    M unmarshall(final InputStream input) throws IOException;

    /**
     * Serializes a metadata instance of type <code>M</code> as string.
     * @param metadata The metadata instance.
     * @return The serialized metadata's raw value.
     * @throws IOException System I/O error.
     */
    String marshall(final M metadata) throws IOException;
}
