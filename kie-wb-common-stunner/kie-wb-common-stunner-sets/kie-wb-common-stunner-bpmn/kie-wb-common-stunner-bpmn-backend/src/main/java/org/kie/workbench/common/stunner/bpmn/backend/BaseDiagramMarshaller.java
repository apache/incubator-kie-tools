/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import bpsim.impl.BpsimPackageImpl;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.impl.DroolsPackageImpl;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.profile.impl.DefaultProfileImpl;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceImpl;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.Bpmn2Marshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.Bpmn2UnMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseDiagramMarshaller<D> implements DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> {

    private static final Logger LOG = LoggerFactory.getLogger(BaseDiagramMarshaller.class);

    private final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller;
    private final GraphObjectBuilderFactory bpmnGraphBuilderFactory;
    private final GraphIndexBuilder<?> indexBuilder;
    private final FactoryManager factoryManager;
    private final DefinitionsCacheRegistry definitionsCacheRegistry;
    private final RuleManager rulesManager;
    private final GraphCommandManager graphCommandManager;
    private final GraphCommandFactory commandFactory;

    protected final DefinitionManager definitionManager;
    protected final OryxManager oryxManager;

    public BaseDiagramMarshaller(final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller,
                                 final GraphObjectBuilderFactory bpmnGraphBuilderFactory,
                                 final DefinitionManager definitionManager,
                                 final GraphIndexBuilder<?> indexBuilder,
                                 final OryxManager oryxManager,
                                 final FactoryManager factoryManager,
                                 final DefinitionsCacheRegistry definitionsCacheRegistry,
                                 final RuleManager rulesManager,
                                 final GraphCommandManager graphCommandManager,
                                 final GraphCommandFactory commandFactory) {
        this.diagramMetadataMarshaller = diagramMetadataMarshaller;
        this.bpmnGraphBuilderFactory = bpmnGraphBuilderFactory;
        this.definitionManager = definitionManager;
        this.indexBuilder = indexBuilder;
        this.oryxManager = oryxManager;
        this.factoryManager = factoryManager;
        this.definitionsCacheRegistry = definitionsCacheRegistry;
        this.rulesManager = rulesManager;
        this.graphCommandManager = graphCommandManager;
        this.commandFactory = commandFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String marshall(final Diagram diagram) {
        LOG.debug("Starting diagram marshalling...");

        final Bpmn2Marshaller marshaller = createBpmn2Marshaller(definitionManager, oryxManager);
        String result = null;
        try {
            // Marshall the diagram definition
            result = marshaller.marshall(diagram,
                                         getPreProcessingData(diagram.getMetadata()));

            // Update diagram's settings.
            updateRootUUID(diagram.getMetadata(),
                           diagram.getGraph());
        } catch (IOException e) {
            LOG.error("Error marshalling file.",
                      e);
        }

        LOG.debug("Diagram marshalling finished successfully.");
        return result;
    }

    protected Bpmn2Marshaller createBpmn2Marshaller(DefinitionManager definitionManager, OryxManager oryxManager) {
        return new Bpmn2Marshaller(definitionManager, oryxManager);
    }

    protected abstract String getPreProcessingData(Metadata metadata);

    public JBPMBpmn2ResourceImpl marshallToBpmn2Resource(final Diagram<Graph, Metadata> diagram) throws IOException {
        final Bpmn2Marshaller marshaller = new Bpmn2Marshaller(definitionManager,
                                                               oryxManager);
        return marshaller.marshallToBpmn2Resource(diagram,
                                                  getPreProcessingData(diagram.getMetadata()));
    }

    @Override
    public Graph unmarshall(final Metadata metadata,
                            final InputStream inputStream) {
        LOG.debug("Starting diagram unmarshalling...");

        // No rule checking for marshalling/unmarshalling, current jbpm designer marshallers should do it for us.
        final Bpmn2UnMarshaller parser = new Bpmn2UnMarshaller(bpmnGraphBuilderFactory,
                                                               definitionManager,
                                                               factoryManager,
                                                               definitionsCacheRegistry,
                                                               rulesManager,
                                                               oryxManager,
                                                               graphCommandManager,
                                                               commandFactory,
                                                               indexBuilder,
                                                               getDiagramDefinitionSetClass(),
                                                               getDiagramDefinitionClass());

        Graph result = null;
        try {
            // Unmarshall the diagram definition
            final Definitions definitions = parseDefinitions(inputStream);
            parser.setProfile(new DefaultProfileImpl());
            result = parser.unmarshall(definitions,
                                       getPreProcessingData(metadata));

            // Update diagram's settings.
            updateRootUUID(metadata,
                           result);
        } catch (IOException e) {
            LOG.error("Error unmarshalling file.",
                      e);
        }

        LOG.debug("Diagram unmarshalling finished successfully.");
        return result;
    }

    public abstract Class<?> getDiagramDefinitionSetClass();

    public abstract Class<? extends BPMNDiagram> getDiagramDefinitionClass();

    public void updateRootUUID(final Metadata settings,
                               final Graph graph) {
        // Update settings's root UUID.
        final String rootUUID = getRootUUID(graph);
        settings.setCanvasRootUUID(rootUUID);
    }

    public void updateTitle(final Metadata metadata,
                            final Graph graph) {
        // Update metadata's title.
        final String title = getTitle(graph);
        metadata.setTitle(title);
    }

    private String getTitle(final Graph graph) {
        final Node<Definition<BPMNDiagram>, ?> diagramNode = getFirstDiagramNode(graph);
        final BPMNDiagram diagramBean = null != diagramNode ? (BPMNDiagram) ((Definition) diagramNode.getContent()).getDefinition() : null;
        if (diagramBean == null) {
            return null;
        }

        return getTitle(diagramBean);
    }

    private String getTitle(final BPMNDiagram diagram) {
        final String title = diagram != null ? diagram.getDiagramSet().getName().getValue() : null;
        return title != null && title.trim().length() > 0 ? title : "-- Untitled diagram --";
    }

    @SuppressWarnings("unchecked")
    private Node<Definition<BPMNDiagram>, ?> getFirstDiagramNode(final Graph graph) {
        return GraphUtils.getFirstNode(graph,
                                       getDiagramDefinitionClass());
    }

    private String getRootUUID(final Graph graph) {
        final Node diagramNode = getFirstDiagramNode(graph);
        return null != diagramNode ? diagramNode.getUUID() : null;
    }

    private Definitions parseDefinitions(final InputStream inputStream) throws IOException {
        try {
            DroolsPackageImpl.init();
            BpsimPackageImpl.init();

            final ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                                                                                    new JBPMBpmn2ResourceFactoryImpl());
            resourceSet.getPackageRegistry().put("http://www.omg.org/spec/BPMN/20100524/MODEL",
                                                 Bpmn2Package.eINSTANCE);
            resourceSet.getPackageRegistry().put("http://www.jboss.org/drools",
                                                 DroolsPackage.eINSTANCE);

            final JBPMBpmn2ResourceImpl resource = (JBPMBpmn2ResourceImpl) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
            resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_ENCODING,
                                                 "UTF-8");
            resource.setEncoding("UTF-8");

            final Map<String, Object> options = new HashMap<>();
            options.put(JBPMBpmn2ResourceImpl.OPTION_ENCODING,
                        "UTF-8");
            options.put(JBPMBpmn2ResourceImpl.OPTION_DEFER_IDREF_RESOLUTION,
                        true);
            options.put(JBPMBpmn2ResourceImpl.OPTION_DISABLE_NOTIFY,
                        true);
            options.put(JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF,
                        JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF_RECORD);

            resource.load(inputStream,
                          options);

            final DocumentRoot root = (DocumentRoot) resource.getContents().get(0);
            return root.getDefinitions();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return null;
    }

    @Override
    public DiagramMetadataMarshaller<Metadata> getMetadataMarshaller() {
        return diagramMetadataMarshaller;
    }
}
