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
package org.kie.workbench.common.stunner.bpmn.backend;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import bpsim.impl.BpsimFactoryImpl;
import bpsim.impl.BpsimPackageImpl;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.util.Bpmn2Resource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jboss.drools.impl.DroolsPackageImpl;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BaseConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.GraphBuilder;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceImpl;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.service.WorkItemDefinitionBackendService;
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
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * "Direct" in the name "BaseDirectDiagramMarshaller" means "skipping json encoding".
 * The old BPMNDiagramMarshaller went through an additional step converting XML into JSON for legacy reasons.
 * The reason for the new version, beside a necessary spring cleaning, was to remove this extra step.
 * So the new version is "Direct" as in "it doesn't go through the extra step".
 *
 */
public abstract class BaseDirectDiagramMarshaller implements DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> {

    private static final Logger LOG = LoggerFactory.getLogger(BaseDirectDiagramMarshaller.class);

    private final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller;
    private final DefinitionManager definitionManager;
    private final RuleManager ruleManager;
    private final WorkItemDefinitionBackendService workItemDefinitionService;
    protected final TypedFactoryManager typedFactoryManager;
    private final GraphCommandFactory commandFactory;
    private final GraphCommandManager commandManager;

    public BaseDirectDiagramMarshaller(final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller,
                                       final DefinitionManager definitionManager,
                                       final RuleManager ruleManager,
                                       final WorkItemDefinitionBackendService workItemDefinitionService,
                                       final FactoryManager factoryManager,
                                       final GraphCommandFactory commandFactory,
                                       final GraphCommandManager commandManager) {
        this.diagramMetadataMarshaller = diagramMetadataMarshaller;
        this.definitionManager = definitionManager;
        this.ruleManager = ruleManager;
        this.workItemDefinitionService = workItemDefinitionService;
        this.typedFactoryManager = new TypedFactoryManager(factoryManager);
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String marshall(final Diagram<Graph, Metadata> diagram) throws IOException {
        LOG.debug("Starting diagram marshalling...");

        Bpmn2Resource resource = createBpmn2Resource();

        // we start converting from the root, then pull out the result
        PropertyWriterFactory propertyWriterFactory = new PropertyWriterFactory();
        DefinitionsConverter definitionsConverter =
                new DefinitionsConverter(new ConverterFactory(new DefinitionsBuildingContext(diagram.getGraph(), getDiagramClass()),
                                                              propertyWriterFactory),
                                         propertyWriterFactory);

        Definitions definitions = definitionsConverter.toDefinitions();

        resource.getContents().add(definitions);

        LOG.debug("Diagram marshalling completed successfully.");
        String outputString = renderToString(resource);
        LOG.trace(outputString);
        return outputString;
    }

    public Definitions marshallToBpmn2Definitions(final Diagram<Graph, Metadata> diagram) throws IOException {
        String marshalled = marshall(diagram);
        try (InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(marshalled.getBytes(StandardCharsets.UTF_8)))) {
            return parseDefinitions(inputStream).getDefinitions();
        }
    }

    private String renderToString(Bpmn2Resource resource) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            resource.save(outputStream, new HashMap<>());
        } finally {
            outputStream.close();
        }
        return StringEscapeUtils.unescapeHtml4(outputStream.toString("UTF-8"));
    }

    @Override
    public Graph<DefinitionSet, Node> unmarshall(final Metadata metadata,
                                                 final InputStream inputStream) throws IOException {
        LOG.debug("Starting diagram unmarshalling...");

        DefinitionResolver definitionResolver;
        try {
            // definition resolver provides utlities to access elements of the BPMN datamodel
            final DefinitionsHandler definitionsHandler = parseDefinitions(inputStream);
            definitionResolver = new DefinitionResolver(definitionsHandler.getDefinitions(),
                                                        workItemDefinitionService.execute(metadata),
                                                        definitionsHandler.isJbpm());
        } finally {
            inputStream.close();
        }

        metadata.setCanvasRootUUID(definitionResolver.getDefinitions().getId());
        metadata.setTitle(definitionResolver.getProcess().getName());

        BaseConverterFactory converterFactory = createToStunnerConverterFactory(definitionResolver, typedFactoryManager);

        // perform actual conversion. Process is the root of the diagram
        BpmnNode diagramRoot = converterFactory.rootProcessConverter().convertProcess();

        LOG.debug("Diagram unmarshalling completed successfully.");

        // the root node contains all of the information
        // needed to build the entire graph (including parent/child relationships)
        // thus, we can now walk the graph to issue all the commands
        // to draw it on our canvas
        Diagram<Graph<DefinitionSet, Node>, Metadata> diagram =
                typedFactoryManager.newDiagram(
                        definitionResolver.getDefinitions().getId(),
                        getDefinitionSetClass(),
                        metadata);
        GraphBuilder graphBuilder =
                new GraphBuilder(
                        diagram.getGraph(),
                        definitionManager,
                        typedFactoryManager,
                        ruleManager,
                        commandFactory,
                        commandManager);
        graphBuilder.render(diagramRoot);

        LOG.debug("Diagram drawing completed successfully.");
        return diagram.getGraph();
    }

    private Bpmn2Resource createBpmn2Resource() {
        DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();

        ResourceSet rSet = new ResourceSetImpl();

        rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn2", new JBPMBpmn2ResourceFactoryImpl());

        Bpmn2Resource resource = (Bpmn2Resource) rSet.createResource(URI.createURI("virtual.bpmn2"));

        rSet.getResources().add(resource);
        return resource;
    }

    @Override
    public DiagramMetadataMarshaller<Metadata> getMetadataMarshaller() {
        return diagramMetadataMarshaller;
    }

    private static DefinitionsHandler parseDefinitions(final InputStream inputStream) throws IOException {
        DroolsPackageImpl.init();
        BpsimPackageImpl.init();

        final ResourceSet resourceSet = new ResourceSetImpl();
        Resource.Factory.Registry resourceFactoryRegistry = resourceSet.getResourceFactoryRegistry();
        resourceFactoryRegistry.getExtensionToFactoryMap().put(
                Resource.Factory.Registry.DEFAULT_EXTENSION, new JBPMBpmn2ResourceFactoryImpl());

        EPackage.Registry packageRegistry = resourceSet.getPackageRegistry();
        packageRegistry.put("http://www.omg.org/spec/BPMN/20100524/MODEL", Bpmn2Package.eINSTANCE);
        packageRegistry.put("http://www.jboss.org/drools", DroolsPackage.eINSTANCE);

        final JBPMBpmn2ResourceImpl resource = (JBPMBpmn2ResourceImpl) resourceSet
                .createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));

        resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_ENCODING, "UTF-8");
        resource.setEncoding("UTF-8");

        final Map<String, Object> options = new HashMap<>();
        options.put(JBPMBpmn2ResourceImpl.OPTION_ENCODING, "UTF-8");
        options.put(JBPMBpmn2ResourceImpl.OPTION_DEFER_IDREF_RESOLUTION, true);
        options.put(JBPMBpmn2ResourceImpl.OPTION_DISABLE_NOTIFY, true);
        options.put(JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF,
                    JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF_RECORD);

        try {
            resource.load(inputStream, options);
        } finally {
            inputStream.close();
        }

        final DocumentRoot root = (DocumentRoot) resource.getContents().get(0);

        return new DefinitionsHandler(root);
    }

    protected abstract org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BaseConverterFactory createToStunnerConverterFactory(
            final DefinitionResolver definitionResolver,
            final TypedFactoryManager typedFactoryManager);

    protected abstract Class<?> getDefinitionSetClass();

    static class DefinitionsHandler {

        private static final String JBPM_PREFIX = "jBPM";
        private static final String DROOLS_NAMESPACE = "http://www.jboss.org/drools";

        private final Definitions definitions;
        private final boolean jbpm;

        DefinitionsHandler(DocumentRoot root) {
            this.definitions = root.getDefinitions();
            this.jbpm = isJbpmnDocument(root, definitions);
        }

        public Definitions getDefinitions() {
            return definitions;
        }

        public boolean isJbpm() {
            return jbpm;
        }

        private static boolean isJbpmnDocument(DocumentRoot root, Definitions definitions) {
            String exporter = definitions.getExporter();
            if (exporter != null) {
                //99% of cases
                return exporter.toLowerCase().startsWith(JBPM_PREFIX.toLowerCase());
            }
            //1% of cases, legacy jBPM processes might not have exporter value properly set.
            return root.getXMLNSPrefixMap().values().contains(DROOLS_NAMESPACE) || root.getXSISchemaLocation().keySet().contains(DROOLS_NAMESPACE);
        }
    }

    protected abstract Class<?> getDiagramClass();
}
