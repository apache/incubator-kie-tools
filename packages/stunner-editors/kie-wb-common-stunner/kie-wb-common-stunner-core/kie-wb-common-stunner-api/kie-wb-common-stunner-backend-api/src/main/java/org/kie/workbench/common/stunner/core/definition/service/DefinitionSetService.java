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

import org.kie.workbench.common.stunner.core.definition.DefinitionSetResourceType;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

/**
 * DefinitionSet's must provide an implementation for this type if they require persistence.
 * This type provides the resource type and the marshaller implementations for the DefinitionSet.
 */
public interface DefinitionSetService {

    /**
     * Check if the service implementation supports the <code>defSetId</code>.
     * @param defSetId The DefinitionSet's-
     * @return <code>true</code> in case this services are supported for the given <code>defSetId</code>, <code>false</code> otherwise.
     */
    boolean accepts(final String defSetId);

    /**
     * Provides the resource type implementation for the DefinitionSet accepted by this service.
     * @return The resource type.
     */
    DefinitionSetResourceType getResourceType();

    /**
     * The diagram marshaller implementation for the DefinitionSet accepted by this service.
     * @return An instance of a diagram marshaller.
     */
    DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> getDiagramMarshaller();
}
