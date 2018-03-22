/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.backend.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.core.backend.service.AbstractDiagramLookupService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.lookup.criteria.AbstractCriteriaLookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.service.BaseDiagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@Service
public class DiagramLookupServiceImpl
        extends AbstractDiagramLookupService<Metadata, Diagram<Graph, Metadata>> {

    private static final Logger LOG = LoggerFactory.getLogger(DiagramLookupServiceImpl.class.getName());

    private final IOService ioService;
    private final DiagramServiceImpl diagramService;

    protected DiagramLookupServiceImpl() {
        this(null,
             null);
    }

    @Inject
    public DiagramLookupServiceImpl(final @Named("ioStrategy") IOService ioService,
                                    final DiagramServiceImpl diagramService) {
        this.ioService = ioService;
        this.diagramService = diagramService;
    }

    @PostConstruct
    public void init() {
        initialize(ioService);
    }

    @Override
    protected BaseDiagramService<Metadata, Diagram<Graph, Metadata>> getDiagramService() {
        return diagramService;
    }

    @Override
    protected List<Diagram<Graph, Metadata>> getItems(final DiagramLookupRequest request) {
        final Path path = null != request.getPath() ?
                Paths.convert(request.getPath()) :
                getServiceImpl().getDiagramsPath();
        return getVFSLookupManager().getItemsByPath(path);
    }

    @Override
    protected boolean matches(final String criteria,
                              final Diagram<Graph, Metadata> item) {
        final Map<String, String> criteriaMap = AbstractCriteriaLookupManager.parseCriteria(criteria);
        final String name = criteriaMap.get(DiagramLookupRequest.CRITERIA_NAME);
        if (null != name && name.trim().length() > 9) {
            return name.equals(item.getName());
        }
        return true;
    }

    private DiagramServiceImpl getServiceImpl() {
        return (DiagramServiceImpl) getDiagramService();
    }
}
