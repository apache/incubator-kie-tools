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

import java.util.Collection;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.lookup.criteria.AbstractCriteriaLookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;

@ApplicationScoped
@Service
public class DiagramLookupServiceImpl
        extends AbstractDiagramLookupService<Metadata, Diagram<Graph, Metadata>> {

    private static final Logger LOG = LoggerFactory.getLogger(DiagramLookupServiceImpl.class.getName());

    protected DiagramLookupServiceImpl() {
        this(null,
             null);
    }

    @Inject
    public DiagramLookupServiceImpl(final @Named("ioStrategy") IOService ioService,
                                    final DiagramServiceImpl diagramService) {
        super(ioService,
              diagramService);
    }

    protected org.uberfire.java.nio.file.Path parseCriteriaPath(final DiagramLookupRequest request) {
        String criteria = request.getCriteria();
        if (StringUtils.isEmpty(criteria)) {
            return getServiceImpl().getDiagramsPath();
        } else {
            Map<String, String> criteriaMap = AbstractCriteriaLookupManager.parseCriteria(criteria);
            String name = criteriaMap.get("name");
            if (!StringUtils.isEmpty(name)) {
                Collection<Diagram<Graph, Metadata>> diagrams = getItemsByPath(getServiceImpl().getDiagramsPath());
                if (null != diagrams) {
                    final Diagram d = diagrams
                            .stream()
                            .filter(diagram -> name.equals(diagram.getName()))
                            .findFirst()
                            .orElse(null);
                    if (null != d) {
                        return Paths.convert(d.getMetadata().getPath());
                    }
                }
                LOG.error("Diagram with name [" + name + "] not found.");
                return null;
            }
        }
        String m = "Criteria [" + criteria + "] not supported.";
        LOG.error(m);
        throw new IllegalArgumentException(m);
    }

    private DiagramServiceImpl getServiceImpl() {
        return (DiagramServiceImpl) getDiagramService();
    }
}
