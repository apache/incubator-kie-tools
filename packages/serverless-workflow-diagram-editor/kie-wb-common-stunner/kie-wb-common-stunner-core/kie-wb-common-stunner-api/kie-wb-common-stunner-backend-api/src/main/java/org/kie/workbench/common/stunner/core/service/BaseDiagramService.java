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


package org.kie.workbench.common.stunner.core.service;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.uberfire.backend.vfs.Path;

/**
 * Base service type for Diagrams of type <code>D</code>.
 * @param <D> The type of the Diagram that this service supports.
 */
public interface BaseDiagramService<M extends Metadata, D extends Diagram<Graph, M>> {

    /**
     * Returns a Diagram by the given path in the service.
     * Implementations can throw unchecked exceptions.
     */
    D getDiagramByPath(final Path path);

    /**
     * Checks if this service accepts a given Diagram by its path.
     */
    boolean accepts(final Path path);

    /**
     * Creates a new Diagram for the given Definition Set identifier into the given path.
     * Implementations can throw unchecked exceptions.*
     * @param path The path for the new diagram.
     * @param name The diagram's name to create.
     * @param defSetId The diagram's and graph resulting by the Definition Set identifier.
     */
    Path create(final Path path,
                final String name,
                final String defSetId);

    /**
     * Saves or updates the diagram.
     * Save applies when diagram is not present on the VFS. A new path will be assigned and returned into
     * the resulting metadata instance (eg: when diagrams are created and authored in client side).
     * Update applies if the diagram is already present on the VFS.
     */
    M saveOrUpdate(final D diagram);

    /**
     * Saves or updates the diagram.
     * Save applies when diagram is not present on the VFS. A new path will be assigned and returned into
     * the resulting metadata instance (eg: when diagrams are created and authored in client side).
     * Update applies if the diagram is already present on the VFS.
     */
    Path saveOrUpdateSvg(final Path diagramPath, String rawDiagramSvg);

    /**
     * Deletes the diagram.
     * Implementations can throw unchecked exceptions.
     * @return <code>true</code> if the operation result is success, <code>false</code> otherwise.
     */
    boolean delete(final D diagram);

    /**
     * Gets the content of the diagram in the internal format. e.g. gets the bpmn2 representation of the process.
     * @param diagram The diagram for getting the raw content.
     * @return Returns the raw content representation for the current diagram.
     */
    String getRawContent(final D diagram);
}
