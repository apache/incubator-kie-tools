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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bpsim.impl.BpsimPackageImpl;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.impl.DroolsPackageImpl;
import org.jboss.drools.util.DroolsResourceFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.BPMNGraphGenerator;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;

public class Bpmn2UnMarshaller extends Bpmn2JsonMarshaller {

    final static ResourceSet resourceSet = new ResourceSetImpl();

    static {
        resourceSet.getPackageRegistry().put(DroolsPackage.eNS_URI,
                                             DroolsPackage.eINSTANCE);
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                                                                                new DroolsResourceFactoryImpl());
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                                                                                new Bpmn2ResourceFactoryImpl());
        resourceSet.getPackageRegistry().put("http://www.omg.org/spec/BPMN/20100524/MODEL",
                                             Bpmn2Package.eINSTANCE);
    }

    BPMNGraphGenerator bpmnGraphGenerator;

    public Bpmn2UnMarshaller(final GraphObjectBuilderFactory elementBuilderFactory,
                             final DefinitionManager definitionManager,
                             final FactoryManager factoryManager,
                             final DefinitionsCacheRegistry definitionsCacheRegistry,
                             final RuleManager ruleManager,
                             final OryxManager oryxManager,
                             final CommandManager<GraphCommandExecutionContext, RuleViolation> commandManager,
                             final GraphCommandFactory commandFactory,
                             final GraphIndexBuilder<?> indexBuilder,
                             final Class<?> diagramDefinitionSetClass,
                             final Class<? extends BPMNDiagram> diagramDefinitionClass) {
        this.bpmnGraphGenerator = new BPMNGraphGenerator(elementBuilderFactory,
                                                         definitionManager,
                                                         factoryManager,
                                                         definitionsCacheRegistry,
                                                         ruleManager,
                                                         oryxManager,
                                                         commandManager,
                                                         commandFactory,
                                                         indexBuilder,
                                                         diagramDefinitionSetClass,
                                                         diagramDefinitionClass);
    }

    public Graph unmarshall(final String content) throws IOException {
        final XMLResource outResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://" + UUID.uuid() + ".xml"));
        outResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING,
                                                "UTF-8");
        outResource.setEncoding("UTF-8");

        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(XMLResource.OPTION_ENCODING,
                    "UTF-8");
        outResource.load(new BufferedInputStream(new ByteArrayInputStream(content.getBytes("UTF-8"))),
                         options);

        final DocumentRoot root = (DocumentRoot) outResource.getContents().get(0);
        final Definitions definitions = root.getDefinitions();

        return unmarshall(definitions,
                          null);
    }

    public Graph unmarshall(final Definitions def,
                            final String preProcessingData) throws IOException {
        DroolsPackageImpl.init();
        BpsimPackageImpl.init();
        super.marshall(bpmnGraphGenerator,
                       def,
                       preProcessingData);
        bpmnGraphGenerator.close();
        return bpmnGraphGenerator.createGraph();
    }
}
