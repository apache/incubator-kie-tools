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

package org.kie.workbench.common.stunner.standalone.backend.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNBackendService;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNDiagramFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.kogito.api.service.KogitoDiagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.commons.uuid.UUID;

@Service
@ApplicationScoped
public class BPMNStandaloneDiagramServiceImpl implements KogitoDiagramService {

    private static final Logger LOG = LoggerFactory.getLogger(BPMNStandaloneDiagramServiceImpl.class);

    private static final String DIAGRAMS_PATH = "diagrams";

    //This path is needed by DiagramsNavigatorImpl's use of AbstractClientDiagramService.lookup(..) to retrieve a list of diagrams
    private static final String ROOT = "default://master@system/stunner/" + DIAGRAMS_PATH;

    private DefinitionManager definitionManager;
    private FactoryManager factoryManager;
    private BPMNBackendService backendService;
    private BPMNDiagramFactory diagramFactory;

    protected BPMNStandaloneDiagramServiceImpl() {
        // CDI proxy.
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public BPMNStandaloneDiagramServiceImpl(final DefinitionManager definitionManager,
                                            final FactoryManager factoryManager,
                                            final BPMNBackendService backendService,
                                            final BPMNDiagramFactory diagramFactory) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.backendService = backendService;
        this.diagramFactory = diagramFactory;
    }

    @Override
    public Diagram transform(final String xml) {
        if (Objects.isNull(xml) || xml.isEmpty()) {
            return doNewDiagram();
        }
        return doTransformation(xml);
    }

    private Diagram doNewDiagram() {
        final String title = UUID.uuid();
        final String defSetId = getDefinitionSetId(backendService);
        final Metadata metadata = buildMetadataInstance(defSetId);
        metadata.setTitle(title);

        try {
            return factoryManager.newDiagram(title,
                                             defSetId,
                                             metadata);
        } catch (final Exception e) {
            LOG.error("Cannot create new diagram", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Diagram doTransformation(final String xml) {
        final String defSetId = getDefinitionSetId(backendService);
        final Metadata metadata = buildMetadataInstance(defSetId);
        final InputStream is = new ByteArrayInputStream(xml.getBytes());

        try {
            final DiagramMarshaller marshaller = backendService.getDiagramMarshaller();
            final Graph<DefinitionSet, ?> graph = marshaller.unmarshall(metadata, is);
            final Node<Definition<BPMNDiagram>, ?> diagramNode = GraphUtils.getFirstNode((Graph<?, Node>) graph, BPMNDiagramImpl.class);
            if (null == diagramNode) {
                throw new RuntimeException("No BPMN Diagram can be found.");
            }
            final String title = diagramNode.getContent().getDefinition().getDiagramSet().getName().getValue();
            metadata.setTitle(title);

            return diagramFactory.build(title,
                                        metadata,
                                        graph);
        } catch (Exception e) {
            LOG.error("Error whilst converting XML to BPMN Diagram.", e);
            throw new DiagramParsingException(metadata, xml);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String transform(final Diagram diagram) {
        try {
            final DiagramMarshaller marshaller = backendService.getDiagramMarshaller();
            return marshaller.marshall(convert(diagram));
        } catch (Exception e) {
            LOG.error("Error whilst converting BPMN Diagram to XML.", e);
            throw new RuntimeException(e);
        }
    }

    private String getDefinitionSetId(final DefinitionSetService services) {
        final Class<?> type = services.getResourceType().getDefinitionSetType();
        return BindableAdapterUtils.getDefinitionSetId(type);
    }

    private Metadata buildMetadataInstance(final String defSetId) {
        return new MetadataImpl.MetadataImplBuilder(defSetId,
                                                    definitionManager)
                .setRoot(PathFactory.newPath(".", ROOT))
                .build();
    }

    private DiagramImpl convert(final Diagram diagram) {
        return new DiagramImpl(diagram.getName(),
                               diagram.getGraph(),
                               diagram.getMetadata());
    }
}
