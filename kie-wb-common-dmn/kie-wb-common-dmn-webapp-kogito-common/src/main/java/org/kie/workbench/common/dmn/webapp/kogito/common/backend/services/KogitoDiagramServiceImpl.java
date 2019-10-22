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
package org.kie.workbench.common.dmn.webapp.kogito.common.backend.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactory;
import org.kie.workbench.common.dmn.backend.DMNBackendService;
import org.kie.workbench.common.dmn.backend.DMNMarshallerStandalone;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
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
public class KogitoDiagramServiceImpl implements KogitoDiagramService {

    private static final Logger LOG = LoggerFactory.getLogger(KogitoDiagramServiceImpl.class);

    private static final String DIAGRAMS_PATH = "diagrams";

    //This path is needed by DiagramsNavigatorImpl's use of AbstractClientDiagramService.lookup(..) to retrieve a list of diagrams
    private static final String ROOT = "default://master@system/stunner/" + DIAGRAMS_PATH;

    private DefinitionManager definitionManager;
    private FactoryManager factoryManager;
    private DMNBackendService dmnBackendService;
    private DMNDiagramFactory dmnDiagramFactory;

    protected KogitoDiagramServiceImpl() {
        // CDI proxy.
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public KogitoDiagramServiceImpl(final DefinitionManager definitionManager,
                                    final FactoryManager factoryManager,
                                    final DMNBackendService dmnBackendService,
                                    final DMNDiagramFactory dmnDiagramFactory) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.dmnBackendService = dmnBackendService;
        this.dmnDiagramFactory = dmnDiagramFactory;
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
        final String defSetId = getDefinitionSetId(dmnBackendService);
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
        final String defSetId = getDefinitionSetId(dmnBackendService);
        final Metadata metadata = buildMetadataInstance(defSetId);

        try (final InputStream is = new ByteArrayInputStream(xml.getBytes())) {
            final DMNMarshallerStandalone dmnMarshaller = (DMNMarshallerStandalone) dmnBackendService.getDiagramMarshaller();
            final Graph<DefinitionSet, ?> graph = dmnMarshaller.unmarshall(metadata, is);
            final Node<Definition<DMNDiagram>, ?> diagramNode = GraphUtils.getFirstNode((Graph<?, Node>) graph, DMNDiagram.class);
            final String title = diagramNode.getContent().getDefinition().getDefinitions().getName().getValue();
            metadata.setTitle(title);

            return dmnDiagramFactory.build(title,
                                           metadata,
                                           graph);
        } catch (Exception e) {
            LOG.error("Error whilst converting XML to DMNDiagram.", e);
            throw new DiagramParsingException(metadata, xml);
        }
    }

    @Override
    public String transform(final Diagram diagram) {
        try {
            final DMNMarshallerStandalone dmnMarshaller = (DMNMarshallerStandalone) dmnBackendService.getDiagramMarshaller();
            return dmnMarshaller.marshall(convert(diagram));
        } catch (Exception e) {
            LOG.error("Error whilst converting DMNDiagram to XML.", e);
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
