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


package org.kie.workbench.common.stunner.bpmn.client.marshall.service;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.jboss.drools.DroolsPackage;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.client.emf.Bpmn2Marshalling;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.DefinitionsConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BaseConverterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.GraphBuilder;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.processes.DataTypeCache;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

@ApplicationScoped
public class BPMNClientMarshalling {

    private static Logger LOGGER = Logger.getLogger(BPMNClientMarshalling.class.getName());

    private final DefinitionManager definitionManager;
    private final RuleManager ruleManager;
    private final TypedFactoryManager typedFactoryManager;
    private final GraphCommandFactory commandFactory;
    private final GraphCommandManager commandManager;
    private final ManagedInstance<WorkItemDefinitionRegistry> widRegistries;
    private final DataTypeCache dataTypeCache;

    @Inject
    public BPMNClientMarshalling(final DefinitionManager definitionManager,
                                 final RuleManager ruleManager,
                                 final FactoryManager factoryManager,
                                 final GraphCommandFactory commandFactory,
                                 final GraphCommandManager commandManager,
                                 final ManagedInstance<WorkItemDefinitionRegistry> widRegistries,
                                 final DataTypeCache dataTypeCache) {
        this.definitionManager = definitionManager;
        this.ruleManager = ruleManager;
        this.typedFactoryManager = new TypedFactoryManager(factoryManager);
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.widRegistries = widRegistries;
        this.dataTypeCache = dataTypeCache;
    }

    @PostConstruct
    public void init() {
        Bpmn2Marshalling.setLogger(message -> LOGGER.log(Level.SEVERE, message));
    }

    @SuppressWarnings("unchecked")
    public String marshall(final Diagram<Graph, Metadata> diagram) {
        final PropertyWriterFactory propertyWriterFactory = new PropertyWriterFactory();
        final DefinitionsBuildingContext buildingContext = new DefinitionsBuildingContext(diagram.getGraph(), getDiagramClass());
        final ConverterFactory converterFactory = new ConverterFactory(buildingContext, propertyWriterFactory);
        final DefinitionsConverter definitionsConverter = new DefinitionsConverter(converterFactory, propertyWriterFactory);
        final Definitions definitions = definitionsConverter.toDefinitions();
        return Bpmn2Marshalling.marshall(definitions);
    }

    public Graph<DefinitionSet, Node> unmarshall(final Metadata metadata,
                                                 final String raw) {
        final MarshallingRequest.Mode mode = MarshallingRequest.Mode.AUTO;
        final Graph<DefinitionSet, Node> graph = unmarshall(metadata,
                                                            mode,
                                                            raw);
        return graph;
    }

    @SuppressWarnings("unchecked")
    private Graph<DefinitionSet, Node> unmarshall(final Metadata metadata,
                                                  final MarshallingRequest.Mode mode,
                                                  final String raw) {
        final DocumentRoot documentRoot = Bpmn2Marshalling.unmarshall(raw);
        final DefinitionsHandler definitionsHandler = new DefinitionsHandler(documentRoot);
        final DefinitionResolver definitionResolver = new DefinitionResolver(definitionsHandler.getDefinitions(),
                                                                             getWorkItemDefinitions(),
                                                                             definitionsHandler.isJbpm(),
                                                                             mode);

        metadata.setCanvasRootUUID(definitionResolver.getDefinitionsId());
        metadata.setTitle(definitionResolver.getProcess().getName());

        final BaseConverterFactory converterFactory = new org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.ConverterFactory(definitionResolver, typedFactoryManager);
        // perform actual conversion. Process is the root of the diagram
        final Result<BpmnNode> result = converterFactory.rootProcessConverter().convertProcess();
        final BpmnNode diagramRoot = result.value();
        dataTypeCache.initCache(diagramRoot);

        // the root node contains all the information
        // needed to build the entire graph (including parent/child relationships)
        // thus, we can now walk the graph to issue all the commands
        // to draw it on our canvas
        final Diagram<Graph<DefinitionSet, Node>, Metadata> diagram =
                typedFactoryManager.newDiagram(
                        definitionResolver.getDefinitionsId(),
                        getDefinitionSetClass(),
                        metadata);
        final Graph<DefinitionSet, Node> graph = diagram.getGraph();
        final GraphBuilder graphBuilder =
                new GraphBuilder(
                        graph,
                        definitionManager,
                        typedFactoryManager,
                        ruleManager,
                        commandFactory,
                        commandManager);
        graphBuilder.render(diagramRoot);

        return graph;
    }

    private Collection<WorkItemDefinition> getWorkItemDefinitions() {
        return widRegistries.get().items();
    }

    public static Class<?> getDiagramClass() {
        return BPMNDiagramImpl.class;
    }

    public static String getDefinitionSetId() {
        return BindableAdapterUtils.getDefinitionSetId(getDefinitionSetClass());
    }

    public static Class<?> getDefinitionSetClass() {
        return BPMNDefinitionSet.class;
    }

    static class DefinitionsHandler {

        private static final String JBPM_PREFIX = "jBPM";
        private static final String DROOLS_NAMESPACE = DroolsPackage.eNS_URI;

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
}
