/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package com.kie.workbench.common.stunner.cm.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.cm.project.service.CaseManagementSwitchViewService;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.diagram.AbstractMetadata;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Service
public class CaseManagementSwitchViewServiceImpl implements CaseManagementSwitchViewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseManagementSwitchViewServiceImpl.class);

    private final FactoryManager factoryManager;
    private final Instance<DefinitionSetService> definitionSetServiceInstances;

    Collection<DefinitionSetService> definitionSetServices;

    @Inject
    public CaseManagementSwitchViewServiceImpl(final FactoryManager factoryManager,
                                               final Instance<DefinitionSetService> definitionSetServiceInstances) {
        this.factoryManager = factoryManager;
        this.definitionSetServiceInstances = definitionSetServiceInstances;

        this.definitionSetServices = new LinkedList<>();
    }

    @PostConstruct
    public void init() {
        definitionSetServiceInstances.forEach(i -> definitionSetServices.add(i));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<ProjectDiagram> switchView(final Diagram diagram, final String mappedDefSetId, final String mappedShapeSetId) {
        final Metadata metadata = diagram.getMetadata();
        final String defSetId = metadata.getDefinitionSetId();

        final Optional<DefinitionSetService> definitionSetServiceOptional =
                definitionSetServices.stream().filter(s -> s.accepts(defSetId)).findAny();

        return definitionSetServiceOptional.map(service -> {
            try {
                // Marshall the diagram
                final String rawData = service.getDiagramMarshaller().marshall(diagram);

                // Get the mapped unmarshaller
                final Optional<DefinitionSetService> mappedDefinitionSetServiceOptional = definitionSetServices.stream()
                        .filter(s -> s.accepts(mappedDefSetId)).findAny();

                return mappedDefinitionSetServiceOptional.map(mappedService -> {
                    ((AbstractMetadata) metadata).setDefinitionSetId(mappedDefSetId);

                    // Unmarshall the diagram
                    try (final InputStream inputStream = new ByteArrayInputStream(rawData.getBytes())) {
                        final Graph<DefinitionSet, Node> graph = mappedService.getDiagramMarshaller().unmarshall(metadata, inputStream);

                        final DiagramFactory factory = factoryManager.registry().getDiagramFactory(graph.getContent().getDefinition(),
                                                                                                   metadata.getMetadataType());
                        final ProjectDiagram mappedDiagram = (ProjectDiagram) factory.build(metadata.getTitle(), metadata, graph);

                        mappedDiagram.getMetadata().setShapeSetId(mappedShapeSetId);

                        return mappedDiagram;
                    } catch (IOException e) {
                        return CaseManagementSwitchViewServiceImpl.this.handleError(Optional.of(e));
                    }
                }).orElseGet(() -> CaseManagementSwitchViewServiceImpl.this.handleError(Optional.empty()));
            } catch (IOException e) {
                return CaseManagementSwitchViewServiceImpl.this.handleError(Optional.of(e));
            }
        });
    }

    private ProjectDiagram handleError(final Optional<Exception> e) {
        if (e.isPresent()) {
            LOGGER.error("Error converting diagram.", e.get());
        } else {
            LOGGER.error("Error converting diagram.");
        }

        return null;
    }
}