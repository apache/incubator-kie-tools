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

package org.kie.workbench.common.stunner.core.backend.service;

import org.kie.workbench.common.stunner.core.backend.lookup.impl.VFSLookupManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.lookup.AbstractLookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.kie.workbench.common.stunner.core.service.BaseDiagramService;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.uberfire.io.IOService;

public abstract class AbstractDiagramLookupService<M extends Metadata, D extends Diagram<Graph, M>>
        extends AbstractLookupManager<D, DiagramRepresentation, DiagramLookupRequest>
        implements DiagramLookupManager,
                   DiagramLookupService {

    private VFSLookupManager<D> vfsLookupManager;

    public void initialize(final IOService ioService) {
        this.vfsLookupManager =
                new VFSLookupManager<D>(ioService)
                        .setPathAcceptor(getDiagramService()::accepts)
                        .setItemSupplier(getDiagramService()::getDiagramByPath);
    }

    protected abstract BaseDiagramService<M, D> getDiagramService();

    protected VFSLookupManager<D> getVFSLookupManager() {
        return vfsLookupManager;
    }

    protected DiagramRepresentation buildResult(final D item) {
        return new DiagramRepresentation.DiagramRepresentationBuilder(item).build();
    }
}
