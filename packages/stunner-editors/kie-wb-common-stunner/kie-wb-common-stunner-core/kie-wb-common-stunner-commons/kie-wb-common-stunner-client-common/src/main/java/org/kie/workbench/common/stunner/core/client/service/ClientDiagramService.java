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


package org.kie.workbench.common.stunner.core.client.service;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.kie.workbench.common.stunner.core.service.BaseDiagramService;
import org.uberfire.backend.vfs.Path;

public interface ClientDiagramService<M extends Metadata, D extends Diagram<Graph, M>, S extends BaseDiagramService<M, D>> {

    void create(Path path,
                String name,
                String defSetId,
                ServiceCallback<Path> callback);

    @SuppressWarnings("unchecked")
    void saveOrUpdate(D diagram,
                      ServiceCallback<D> callback);

    void saveOrUpdateSvg(Path diagramPath, String rawSvg, ServiceCallback<Path> callback);

    void add(D diagram,
             ServiceCallback<D> callback);

    @SuppressWarnings("unchecked")
    void getByPath(Path path,
                   ServiceCallback<D> callback);

    @SuppressWarnings("unchecked")
    void lookup(DiagramLookupRequest request,
                ServiceCallback<LookupManager.LookupResponse<DiagramRepresentation>> callback);

    void getRawContent(D diagram,
                       ServiceCallback<String> callback);
}
