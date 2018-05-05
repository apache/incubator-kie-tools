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

package org.kie.workbench.common.dmn.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.inject.spi.BeanManager;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tools.ant.filters.StringInputStream;
import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.core.util.KieHelper;
import org.kie.dmn.model.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DDExtensionsRegister;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNShape;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNStyle;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.org.omg.spec.CMMN_20151109_DC.Bounds;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.org.omg.spec.CMMN_20151109_DC.Color;
import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.backend.BackendFactoryManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertyAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertySetAdapter;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.commons.uuid.UUID;
import org.xml.sax.InputSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNMarshallerTest {

    private static final String DMN_DEF_SET_ID = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);

    @Mock
    DefinitionManager definitionManager;

    @Mock
    AdapterManager adapterManager;

    @Mock
    AdapterRegistry adapterRegistry;

    @Mock
    BeanManager beanManager;

    @Mock
    RuleManager rulesManager;

    @Mock
    BackendFactoryManager applicationFactoryManager;

    @Mock
    DefinitionsCacheRegistry definitionsRegistry;

    EdgeFactory<Object> connectionEdgeFactory;
    NodeFactory<Object> viewNodeFactory;
    DefinitionUtils definitionUtils;

    GraphCommandManager commandManager;
    GraphCommandFactory commandFactory;

    GraphFactory dmnGraphFactory;

    TestScopeModelFactory testScopeModelFactory;

    @Before
    public void setup() throws Exception {
        // Graph utils.
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        definitionUtils = new DefinitionUtils(definitionManager,
                                              applicationFactoryManager,
                                              definitionsRegistry);
        testScopeModelFactory = new TestScopeModelFactory(new DMNDefinitionSet.DMNDefinitionSetBuilder().build());
        // Definition manager.
        final BackendDefinitionAdapter definitionAdapter = new BackendDefinitionAdapter(definitionUtils);
        final BackendDefinitionSetAdapter definitionSetAdapter = new BackendDefinitionSetAdapter(definitionAdapter);
        final BackendPropertySetAdapter propertySetAdapter = new BackendPropertySetAdapter();
        final BackendPropertyAdapter propertyAdapter = new BackendPropertyAdapter();
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertySetAdapter(any(Class.class))).thenReturn(propertySetAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
        commandManager = new GraphCommandManagerImpl(null,
                                                     null,
                                                     null);
        commandFactory = new GraphCommandFactory();
        connectionEdgeFactory = new EdgeFactoryImpl(definitionManager);
        viewNodeFactory = new NodeFactoryImpl(definitionUtils);
        dmnGraphFactory = new GraphFactoryImpl(definitionManager);
        doAnswer(invocationOnMock -> {
            String id = (String) invocationOnMock.getArguments()[0];
            return testScopeModelFactory.build(id);
        }).when(applicationFactoryManager).newDefinition(anyString());
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            String id = (String) invocationOnMock.getArguments()[1];
            if (DMNDefinitionSet.class.getName().equals(id)) {
                // Emulate DMNGraphFactoryImpl, that adds a DMNDiagram to new Graphs
                // Please note this is different from the stunner jbpm test which this dmn test is based on
                Graph graph = (Graph) dmnGraphFactory.build(uuid,
                                                            DMN_DEF_SET_ID);
                DMNDiagram model = new DMNDiagram.DMNDiagramBuilder().build();
                Node node = viewNodeFactory.build(uuid,
                                                  model);
                graph.addNode(node);
                return graph;
            }
            Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
            if (null != model) {
                Class<? extends ElementFactory> element = BackendDefinitionAdapter.getGraphFactory(model.getClass());
                if (element.isAssignableFrom(NodeFactory.class)) {
                    Node node = viewNodeFactory.build(uuid,
                                                      model);
                    return node;
                } else if (element.isAssignableFrom(EdgeFactory.class)) {
                    Edge edge = connectionEdgeFactory.build(uuid,
                                                            model);
                    return edge;
                }
            }
            return null;
        }).when(applicationFactoryManager).newElement(anyString(),
                                                      anyString());
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            Class type = (Class) invocationOnMock.getArguments()[1];
            String id = BindableAdapterUtils.getGenericClassName(type);
            if (DMNDefinitionSet.class.equals(type)) {
                Graph graph = (Graph) dmnGraphFactory.build(uuid,
                                                            DMN_DEF_SET_ID);
                return graph;
            }
            Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
            if (null != model) {
                Class<? extends ElementFactory> element = BackendDefinitionAdapter.getGraphFactory(model.getClass());
                if (element.isAssignableFrom(NodeFactory.class)) {
                    Node node = viewNodeFactory.build(uuid,
                                                      model);
                    return node;
                } else if (element.isAssignableFrom(EdgeFactory.class)) {
                    Edge edge = connectionEdgeFactory.build(uuid,
                                                            model);
                    return edge;
                }
            }
            return null;
        }).when(applicationFactoryManager).newElement(anyString(),
                                                      any(Class.class));
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            String defSetId = (String) invocationOnMock.getArguments()[1];
            final Graph graph = (Graph) applicationFactoryManager.newElement(uuid,
                                                                             defSetId);
            final DiagramImpl result = new DiagramImpl(uuid,
                                                       new MetadataImpl.MetadataImplBuilder(defSetId).build());
            result.setGraph(graph);
            return result;
        }).when(applicationFactoryManager).newDiagram(anyString(),
                                                      anyString(),
                                                      any(Metadata.class));

        MappingContextSingleton.loadDynamicMarshallers();
    }

    @Test
    public void testLoan() throws IOException {
        roundTripUnmarshalMarshalThenUnmarshalDMN(getClass().getResourceAsStream("/Loan Pre-Qualification.dmn"));
        DMNMarshaller m = new DMNMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                                            applicationFactoryManager);
        Graph<?, ?> g = m.unmarshall(null, this.getClass().getResourceAsStream("/Loan Pre-Qualification.dmn"));
        DiagramImpl diagram = new DiagramImpl("", null);
        diagram.setGraph(g);
        String mString = m.marshall(diagram);
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-testLoan", "1.0"),
                                                                    ks.getResources().newByteArrayResource(mString.getBytes()).setTargetPath("src/main/resources/Loan Pre-Qualification.dmn"));

        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_4e0a7f15-3176-427e-add8-68d30903c84c", "Loan Pre-Qualification");
        DMNContext dmnContext = runtime.newContext();
        dmnContext.set("Credit Score", new HashMap<String, Object>() {{
            put("FICO", 400);
        }});
        DMNResult dmnResult = runtime.evaluateByName(model, dmnContext, "Credit Score Rating");
        assertFalse(dmnResult.getMessages().toString(), dmnResult.hasErrors());
    }

    @Test
    public void test_diamond() throws IOException {
        // round trip test
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/diamond.dmn"),
                                               this::checkDiamongGraph);

        // additionally, check the marshalled is still DMN executable as expected

        DMNMarshaller m = new DMNMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                                            applicationFactoryManager);
        Graph<?, ?> g = m.unmarshall(null,
                                     this.getClass().getResourceAsStream("/diamond.dmn"));
        DiagramImpl diagram = new DiagramImpl("",
                                              null);
        diagram.setGraph(g);
        String mString = m.marshall(diagram);

        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie",
                                                                                    "dmn-test_diamond",
                                                                                    "1.0"),
                                                                    ks.getResources().newByteArrayResource(mString.getBytes()).setTargetPath("src/main/resources/diamond.dmn"));

        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        DMNModel diamondModel = runtime.getModel("http://www.trisotech.com/definitions/_8afa6c24-55c8-43cf-8a02-fdde7fc5d1f2",
                                                 "three decisions in a diamond shape");
        DMNContext dmnContext = runtime.newContext();
        dmnContext.set("My Name",
                       "John Doe");
        DMNResult dmnResult = runtime.evaluateAll(diamondModel,
                                                  dmnContext);
        assertFalse(dmnResult.getMessages().toString(),
                    dmnResult.hasErrors());
        DMNContext result = dmnResult.getContext();
        assertEquals("Hello, John Doe.",
                     result.get("My Decision"));

        // additionally, check DMN DD/DI for version 1.1

        org.kie.dmn.api.marshalling.v1_1.DMNMarshaller dmnMarshaller = DMNMarshallerFactory.newMarshallerWithExtensions(Arrays.asList(new DDExtensionsRegister()));
        Definitions definitions = dmnMarshaller.unmarshal(mString);

        assertNotNull(definitions.getExtensionElements());
        assertNotNull(definitions.getExtensionElements().getAny());
        assertEquals(1, definitions.getExtensionElements().getAny().size());
        org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDiagram ddRoot = (org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDiagram) definitions.getExtensionElements().getAny().get(0);

        DMNShape myname = findShapeByDMNI(ddRoot, "_4cd17e52-6253-41d6-820d-5824bf5197f3");
        assertBounds(500, 500, 100, 50, myname.getBounds());
        assertColor(255, 255, 255, myname.getBgColor());
        assertColor(0, 0, 0, myname.getBorderColor());
        assertEquals(0.5, myname.getBorderSize().getValue(), 0);
        assertDMNStyle("Open Sans", 24, 1, 255, 0, 0, myname.getFontStyle());

        DMNShape prefix = findShapeByDMNI(ddRoot, "_e920f38a-293c-41b8-adb3-69d0dc184fab");
        assertBounds(300, 400, 100, 50, prefix.getBounds());
        assertColor(0, 253, 25, prefix.getBgColor());
        assertColor(253, 0, 0, prefix.getBorderColor());
        assertEquals(1, prefix.getBorderSize().getValue(), 0);
        assertDMNStyle("Times New Roman", 8, 2.5, 70, 60, 50, prefix.getFontStyle());

        DMNShape postfix = findShapeByDMNI(ddRoot, "_f49f9c34-29d5-4e72-91d2-f4f92117c8da");
        assertBounds(700, 400, 100, 50, postfix.getBounds());
        assertColor(247, 255, 0, postfix.getBgColor());
        assertColor(0, 51, 255, postfix.getBorderColor());
        assertEquals(2, postfix.getBorderSize().getValue(), 0);
        assertDMNStyle("Arial", 10, 1.5, 50, 60, 70, postfix.getFontStyle());

        DMNShape mydecision = findShapeByDMNI(ddRoot, "_9b061fc3-8109-42e2-9fe4-fc39c90b654e");
        assertBounds(487.5, 275, 125, 75, mydecision.getBounds());
        assertColor(255, 255, 255, mydecision.getBgColor());
        assertColor(0, 0, 0, mydecision.getBorderColor());
        assertEquals(0.5, mydecision.getBorderSize().getValue(), 0);
        assertDMNStyle("Monospaced", 32, 3.5, 55, 66, 77, mydecision.getFontStyle());
    }

    private void assertDMNStyle(String fontName, double fontSize, double fontBorderSize, int r, int g, int b, DMNStyle style) {
        assertEquals(fontName, style.getFontName());
        assertEquals(fontSize, style.getFontSize(), 0);
        assertEquals(fontBorderSize, style.getFontBorderSize(), 0);
        assertColor(r, g, b, style.getFontColor());
    }

    private static void assertBounds(double x, double y, double width, double height, Bounds bounds) {
        assertEquals(x, bounds.getX(), 0);
        assertEquals(y, bounds.getY(), 0);
        assertEquals(width, bounds.getWidth(), 0);
        assertEquals(height, bounds.getHeight(), 0);
    }

    private static void assertColor(int r, int g, int b, Color color) {
        assertEquals(r, color.getRed());
        assertEquals(g, color.getGreen());
        assertEquals(b, color.getBlue());
    }

    private DMNShape findShapeByDMNI(org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDiagram root, String id) {
        return root.getAny().stream().filter(shape -> shape.getDmnElementRef().equals(id)).findFirst().get();
    }

    @Test
    public void test_potpourri_drawing() throws IOException {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/potpourri_drawing.dmn"),
                                               this::checkPotpourryGraph);
    }

    @Test
    public void testAssociations() throws IOException {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/associations.dmn"),
                                               this::checkAssociationsGraph);
    }

    @Test
    public void testTextAnnotation() throws Exception {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/textAnnotation.dmn"),
                                               this::checkTextAnnotationGraph);
    }

    @Test
    public void testTextAnnotationWithCDATA() throws Exception {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/textAnnotationCDATA.dmn"),
                                               this::checkTextAnnotationGraph);
    }

    private void checkTextAnnotationGraph(Graph<?, Node<?, ?>> graph) {
        Node<?, ?> textAnnotation = graph.getNode("60915990-9E1D-42DF-B7F6-0D28383BE9D1");
        assertNodeContentDefinitionIs(textAnnotation,
                                      TextAnnotation.class);
        TextAnnotation textAnnotationDefinition = ((View<TextAnnotation>) textAnnotation.getContent()).getDefinition();
        assertEquals("描述",
                     textAnnotationDefinition.getDescription().getValue());
        assertEquals("<b>This Annotation holds some Long text and also UTF-8 characters</b>",
                     textAnnotationDefinition.getText().getValue());
        assertEquals("text/html",
                     textAnnotationDefinition.getTextFormat().getValue());
    }

    public void testTreeStructure() throws IOException {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/treeStructure.dmn"),
                                               this::checkTreeStructureGraph);
    }

    public void roundTripUnmarshalThenMarshalUnmarshal(InputStream dmnXmlInputStream,
                                                       Consumer<Graph<?, Node<?, ?>>> checkGraphConsumer) throws IOException {
        DMNMarshaller m = new DMNMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                                            applicationFactoryManager);

        // first unmarshal from DMN XML to Stunner DMN Graph
        @SuppressWarnings("unchecked")
        Graph<?, Node<?, ?>> g = m.unmarshall(null,
                                              dmnXmlInputStream);
        checkGraphConsumer.accept(g);

        // round trip to Stunner DMN Graph back to DMN XML
        DiagramImpl diagram = new DiagramImpl("",
                                              null);
        diagram.setGraph(g);

        String mString = m.marshall(diagram);
        System.out.println(mString);

        // now unmarshal once more, from the marshalled just done above, back again to Stunner DMN Graph to complete check for round-trip
        @SuppressWarnings("unchecked")
        Graph<?, Node<?, ?>> g2 = m.unmarshall(null,
                                               new StringInputStream(mString));
        checkGraphConsumer.accept(g2);
    }

    private void checkTreeStructureGraph(Graph<?, Node<?, ?>> graph) {
        Node<?, ?> root = graph.getNode("BBF2B56F-A0AF-4428-AA6A-61A655D72883");
        Node<?, ?> decisionOne = graph.getNode("B7DD0DC9-7FAC-4510-9031-FFEE067CC2F5");
        Node<?, ?> decisionTwo = graph.getNode("DF84B353-A2F6-46B9-B680-EBD98F5084C8");
        Node<?, ?> decisionThree = graph.getNode("1B6EF5EB-CA09-45A5-AB03-21CD70F941AD");
        Node<?, ?> bkmRoot = graph.getNode("AD910446-56AD-49A5-99CE-F7B9C6F74E5E");
        Node<?, ?> bkmOne = graph.getNode("C1D0937E-96F4-4EAF-8A85-45B129F38E9B");
        Node<?, ?> bkmTwo = graph.getNode("47E47E51-4509-4A3B-86E9-D690F397B69C");
        Node<?, ?> annotation = graph.getNode("47C5A244-EF6D-473D-99B6-629F70A49FCC");
        Node<?, ?> knowledgeSource = graph.getNode("CFE44FA9-7309-4F28-81E9-5C1EF455B4C2");
        Node<?, ?> knowledgeSourceInput = graph.getNode("BC0D715A-ADD4-4136-AB3D-226EABC840A2");
        Node<?, ?> decisionOneInput = graph.getNode("CF65CEB9-433F-402F-A485-90AC34E2FE39");

        assertNodeEdgesTo(decisionOne,
                          root,
                          InformationRequirement.class);

        assertNodeEdgesTo(decisionTwo,
                          decisionOne,
                          InformationRequirement.class);

        assertNodeEdgesTo(decisionThree,
                          decisionOne,
                          InformationRequirement.class);

        assertNodeEdgesTo(bkmRoot,
                          annotation,
                          Association.class);

        assertNodeEdgesTo(bkmRoot,
                          decisionOne,
                          KnowledgeRequirement.class);

        assertNodeEdgesTo(bkmOne,
                          bkmRoot,
                          KnowledgeRequirement.class);

        assertNodeEdgesTo(bkmTwo,
                          bkmRoot,
                          KnowledgeRequirement.class);

        assertNodeEdgesTo(decisionOneInput,
                          decisionOne,
                          InformationRequirement.class);

        assertNodeEdgesTo(knowledgeSource,
                          decisionOne,
                          AuthorityRequirement.class);

        assertNodeEdgesTo(knowledgeSourceInput,
                          knowledgeSource,
                          AuthorityRequirement.class);
    }

    private void checkDiamongGraph(Graph<?, Node<?, ?>> g) {
        Node<?, ?> idNode = g.getNode("_4cd17e52-6253-41d6-820d-5824bf5197f3");
        assertNodeContentDefinitionIs(idNode,
                                      InputData.class);
        assertNodeEdgesTo(idNode,
                          g.getNode("_e920f38a-293c-41b8-adb3-69d0dc184fab"),
                          InformationRequirement.class);
        assertNodeEdgesTo(idNode,
                          g.getNode("_f49f9c34-29d5-4e72-91d2-f4f92117c8da"),
                          InformationRequirement.class);
        assertNodeEdgesTo(idNode,
                          g.getNode("_9b061fc3-8109-42e2-9fe4-fc39c90b654e"),
                          InformationRequirement.class);

        Node<?, ?> prefixDecisionNode = g.getNode("_e920f38a-293c-41b8-adb3-69d0dc184fab");
        assertNodeContentDefinitionIs(prefixDecisionNode,
                                      Decision.class);
        assertNodeEdgesTo(prefixDecisionNode,
                          g.getNode("_9b061fc3-8109-42e2-9fe4-fc39c90b654e"),
                          InformationRequirement.class);

        Node<?, ?> postfixDecisionNode = g.getNode("_f49f9c34-29d5-4e72-91d2-f4f92117c8da");
        assertNodeContentDefinitionIs(postfixDecisionNode,
                                      Decision.class);
        assertNodeEdgesTo(postfixDecisionNode,
                          g.getNode("_9b061fc3-8109-42e2-9fe4-fc39c90b654e"),
                          InformationRequirement.class);

        Node<?, ?> myDecisionNode = g.getNode("_9b061fc3-8109-42e2-9fe4-fc39c90b654e");
        assertNodeContentDefinitionIs(myDecisionNode,
                                      Decision.class);

        Node<?, ?> rootNode = DMNMarshaller.findDMNDiagramRoot((Graph) g);
        assertNotNull(rootNode);
        assertRootNodeConnectedTo(rootNode,
                                  idNode);
        assertRootNodeConnectedTo(rootNode,
                                  prefixDecisionNode);
        assertRootNodeConnectedTo(rootNode,
                                  postfixDecisionNode);
        assertRootNodeConnectedTo(rootNode,
                                  myDecisionNode);
    }

    private void checkAssociationsGraph(Graph<?, Node<?, ?>> g) {
        Node<?, ?> inputData = g.getNode("BD168F8B-4398-4478-8BEA-E67AA5F90FAF");
        assertNodeContentDefinitionIs(inputData,
                                      InputData.class);

        Node<?, ?> decision = g.getNode("A960E2D2-FBC1-4D11-AA33-064F6A0B5CB9");
        assertNodeContentDefinitionIs(decision,
                                      Decision.class);

        Node<?, ?> knowledgeSource = g.getNode("FB99ED65-BC43-4750-999F-7FE24690845B");
        assertNodeContentDefinitionIs(knowledgeSource,
                                      KnowledgeSource.class);

        Node<?, ?> bkm = g.getNode("2F07453C-854F-436F-8378-4CFCE63BB124");
        assertNodeContentDefinitionIs(bkm,
                                      BusinessKnowledgeModel.class);

        Node<?, ?> textAnnotation = g.getNode("7F4B8130-6F3D-4A16-9F6C-01D01DA481D2");
        assertNodeContentDefinitionIs(textAnnotation,
                                      TextAnnotation.class);

        Edge fromInput = assertNodeEdgesTo(inputData,
                                           textAnnotation,
                                           Association.class);
        assertEquals("From Input",
                     ((View<Association>) fromInput.getContent()).getDefinition().getDescription().getValue());

        Edge fromDecision = assertNodeEdgesTo(decision,
                                              textAnnotation,
                                              Association.class);
        assertEquals("From Decision",
                     ((View<Association>) fromDecision.getContent()).getDefinition().getDescription().getValue());

        Edge fromBkm = assertNodeEdgesTo(bkm,
                                         textAnnotation,
                                         Association.class);
        assertEquals("From BKM",
                     ((View<Association>) fromBkm.getContent()).getDefinition().getDescription().getValue());

        Edge fromKnowledgeSource = assertNodeEdgesTo(knowledgeSource,
                                                     textAnnotation,
                                                     Association.class);
        assertEquals("From Knowledge Source",
                     ((View<Association>) fromKnowledgeSource.getContent()).getDefinition().getDescription().getValue());
    }

    private void checkPotpourryGraph(Graph<?, Node<?, ?>> g) {
        Node<?, ?> _My_Input_Data = g.getNode("_My_Input_Data");
        assertNodeContentDefinitionIs(_My_Input_Data,
                                      InputData.class);
        assertNodeEdgesTo(_My_Input_Data,
                          g.getNode("_My_Decision_1"),
                          InformationRequirement.class);
        assertNodeEdgesTo(_My_Input_Data,
                          g.getNode("_KS_of_Input_Data"),
                          AuthorityRequirement.class);
        assertNodeEdgesTo(_My_Input_Data,
                          g.getNode("_Annotation_for_Input_Data"),
                          Association.class);

        Node<?, ?> _Annotation_for_Input_Data = g.getNode("_Annotation_for_Input_Data");
        assertNodeContentDefinitionIs(_Annotation_for_Input_Data,
                                      TextAnnotation.class);

        Node<?, ?> _KS_of_Input_Data = g.getNode("_KS_of_Input_Data");
        assertNodeContentDefinitionIs(_KS_of_Input_Data,
                                      KnowledgeSource.class);

        Node<?, ?> _KS_of_KS_of_InputData = g.getNode("_KS_of_KS_of_InputData");
        assertNodeContentDefinitionIs(_KS_of_KS_of_InputData,
                                      KnowledgeSource.class);
        assertNodeEdgesTo(_KS_of_KS_of_InputData,
                          g.getNode("_KS_of_Input_Data"),
                          AuthorityRequirement.class);

        Node<?, ?> _KS_of_KS_of_Decision_1 = g.getNode("_KS_of_KS_of_Decision_1");
        assertNodeContentDefinitionIs(_KS_of_KS_of_Decision_1,
                                      KnowledgeSource.class);
        assertNodeEdgesTo(_KS_of_KS_of_Decision_1,
                          g.getNode("_KS_of_Decision_1"),
                          AuthorityRequirement.class);

        Node<?, ?> _KS_of_Decision_1 = g.getNode("_KS_of_Decision_1");
        assertNodeContentDefinitionIs(_KS_of_Decision_1,
                                      KnowledgeSource.class);
        assertNodeEdgesTo(_KS_of_Decision_1,
                          g.getNode("_My_Decision_1"),
                          AuthorityRequirement.class);

        Node<?, ?> _My_Decision_2 = g.getNode("_My_Decision_2");
        assertNodeContentDefinitionIs(_My_Decision_2,
                                      Decision.class);
        assertNodeEdgesTo(_My_Decision_2,
                          g.getNode("_KS_of_Decision_2"),
                          AuthorityRequirement.class);
        assertNodeEdgesTo(_My_Decision_2,
                          g.getNode("_Annotation_for_Decision_2"),
                          Association.class);

        Node<?, ?> _KS_of_Decision_2 = g.getNode("_KS_of_Decision_2");
        assertNodeContentDefinitionIs(_KS_of_Decision_2,
                                      KnowledgeSource.class);

        Node<?, ?> _Annotation_for_Decision_2 = g.getNode("_Annotation_for_Decision_2");
        assertNodeContentDefinitionIs(_Annotation_for_Decision_2,
                                      TextAnnotation.class);

        Node<?, ?> _Annotation_for_BKM_1 = g.getNode("_Annotation_for_BKM_1");
        assertNodeContentDefinitionIs(_Annotation_for_BKM_1,
                                      TextAnnotation.class);
        assertNodeEdgesTo(_Annotation_for_BKM_1,
                          g.getNode("_My_BKM_1_of_Decision_1"),
                          Association.class);

        Node<?, ?> _My_BKM_1_of_Decision_1 = g.getNode("_My_BKM_1_of_Decision_1");
        assertNodeContentDefinitionIs(_My_BKM_1_of_Decision_1,
                                      BusinessKnowledgeModel.class);
        assertNodeEdgesTo(_My_BKM_1_of_Decision_1,
                          g.getNode("_My_Decision_1"),
                          KnowledgeRequirement.class);

        Node<?, ?> _KS_of_BKM_1 = g.getNode("_KS_of_BKM_1");
        assertNodeContentDefinitionIs(_KS_of_BKM_1,
                                      KnowledgeSource.class);
        assertNodeEdgesTo(_KS_of_BKM_1,
                          g.getNode("_My_BKM_1_of_Decision_1"),
                          AuthorityRequirement.class);

        Node<?, ?> _KS_of_KS_of_BKM_1 = g.getNode("_KS_of_KS_of_BKM_1");
        assertNodeContentDefinitionIs(_KS_of_KS_of_BKM_1,
                                      KnowledgeSource.class);
        assertNodeEdgesTo(_KS_of_KS_of_BKM_1,
                          g.getNode("_KS_of_BKM_1"),
                          AuthorityRequirement.class);

        Node<?, ?> _My_BKM_2_of_BKM_1 = g.getNode("_My_BKM_2_of_BKM_1");
        assertNodeContentDefinitionIs(_My_BKM_2_of_BKM_1,
                                      BusinessKnowledgeModel.class);
        assertNodeEdgesTo(_My_BKM_2_of_BKM_1,
                          g.getNode("_My_BKM_1_of_Decision_1"),
                          KnowledgeRequirement.class);

        Node<?, ?> _KS_of_BKM_2 = g.getNode("_KS_of_BKM_2");
        assertNodeContentDefinitionIs(_KS_of_BKM_2,
                                      KnowledgeSource.class);
        assertNodeEdgesTo(_KS_of_BKM_2,
                          g.getNode("_My_BKM_2_of_BKM_1"),
                          AuthorityRequirement.class);
        assertNodeEdgesTo(_KS_of_BKM_2,
                          g.getNode("_Annotation_for_KS_of_BKM_2"),
                          Association.class);

        Node<?, ?> _Annotation_for_KS_of_BKM_2 = g.getNode("_Annotation_for_KS_of_BKM_2");
        assertNodeContentDefinitionIs(_Annotation_for_KS_of_BKM_2,
                                      TextAnnotation.class);

        Node<?, ?> _My_Decision_1 = g.getNode("_My_Decision_1");
        assertNodeContentDefinitionIs(_My_Decision_1,
                                      Decision.class);

        Node<?, ?> rootNode = DMNMarshaller.findDMNDiagramRoot((Graph) g);
        assertNotNull(rootNode);
        assertRootNodeConnectedTo(rootNode,
                                  _My_Input_Data);
        assertRootNodeConnectedTo(rootNode,
                                  _Annotation_for_Input_Data);
        assertRootNodeConnectedTo(rootNode,
                                  _KS_of_Input_Data);
        assertRootNodeConnectedTo(rootNode,
                                  _KS_of_KS_of_InputData);
        assertRootNodeConnectedTo(rootNode,
                                  _KS_of_KS_of_Decision_1);
        assertRootNodeConnectedTo(rootNode,
                                  _KS_of_Decision_1);
        assertRootNodeConnectedTo(rootNode,
                                  _My_Decision_2);
        assertRootNodeConnectedTo(rootNode,
                                  _KS_of_Decision_2);
        assertRootNodeConnectedTo(rootNode,
                                  _Annotation_for_Decision_2);
        assertRootNodeConnectedTo(rootNode,
                                  _Annotation_for_BKM_1);
        assertRootNodeConnectedTo(rootNode,
                                  _My_BKM_1_of_Decision_1);
        assertRootNodeConnectedTo(rootNode,
                                  _KS_of_BKM_1);
        assertRootNodeConnectedTo(rootNode,
                                  _KS_of_KS_of_BKM_1);
        assertRootNodeConnectedTo(rootNode,
                                  _My_BKM_2_of_BKM_1);
        assertRootNodeConnectedTo(rootNode,
                                  _KS_of_BKM_2);
        assertRootNodeConnectedTo(rootNode,
                                  _Annotation_for_KS_of_BKM_2);
        assertRootNodeConnectedTo(rootNode,
                                  _My_Decision_1);
    }

    private static void assertRootNodeConnectedTo(Node<?, ?> rootNode,
                                                  Node<?, ?> to) {
        @SuppressWarnings("unchecked")
        List<Edge<?, ?>> outEdges = (List<Edge<?, ?>>) rootNode.getOutEdges();
        Optional<Edge<?, ?>> optEdge = outEdges.stream().filter(e -> e.getTargetNode().equals(to)).findFirst();
        assertTrue(optEdge.isPresent());

        Edge<?, ?> edge = optEdge.get();
        assertTrue(edge.getContent() instanceof Child);

        assertTrue(to.getInEdges().contains(edge));
    }

    private static Edge<?, ?> assertNodeEdgesTo(Node<?, ?> from,
                                                Node<?, ?> to,
                                                Class<?> clazz) {
        @SuppressWarnings("unchecked")
        List<Edge<?, ?>> outEdges = (List<Edge<?, ?>>) from.getOutEdges();
        Optional<Edge<?, ?>> optEdge = outEdges.stream().filter(e -> e.getTargetNode().equals(to)).findFirst();
        assertTrue(optEdge.isPresent());

        Edge<?, ?> edge = optEdge.get();
        assertTrue(edge.getContent() instanceof View);
        assertTrue(clazz.isInstance(((View<?>) edge.getContent()).getDefinition()));

        assertTrue(to.getInEdges().contains(edge));

        ViewConnector<?> connectionContent = (ViewConnector<?>) edge.getContent();
        assertTrue(connectionContent.getSourceConnection().isPresent());
        assertTrue(connectionContent.getTargetConnection().isPresent());
        return edge;
    }

    private static void assertNodeContentDefinitionIs(Node<?, ?> node,
                                                      Class<?> clazz) {
        assertTrue(node.getContent() instanceof View);
        assertTrue(clazz.isInstance(((View<?>) node.getContent()).getDefinition()));
    }

    @Test
    public void test_Simple_structured_context() throws IOException {
        final DMNRuntime runtime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/Simple_structured_context.dmn"));
        DMNModel dmnModel = runtime.getModels().get(0);

        DMNContext dmnContext = runtime.newContext();
        dmnContext.set("Input Name",
                       "John Doe");
        DMNResult dmnResult = runtime.evaluateAll(dmnModel,
                                                  dmnContext);
        assertFalse(dmnResult.getMessages().toString(),
                    dmnResult.hasErrors());
        DMNContext result = dmnResult.getContext();
        assertEquals("Hello, John Doe!",
                     result.get("Decision Logic 1"));
    }

    private DMNRuntime roundTripUnmarshalMarshalThenUnmarshalDMN(InputStream dmnXmlInputStream) throws IOException {
        DMNMarshaller m = new DMNMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                                            applicationFactoryManager);

        // first unmarshal from DMN XML to Stunner DMN Graph
        @SuppressWarnings("unchecked")
        Graph<?, Node<?, ?>> g = m.unmarshall(null,
                                              dmnXmlInputStream);

        // round trip to Stunner DMN Graph back to DMN XML
        DiagramImpl diagram = new DiagramImpl("",
                                              null);
        diagram.setGraph(g);

        String mString = m.marshall(diagram);
        System.out.println(mString);

        // now unmarshal once more, from the marshalled just done above, into a DMNRuntime
        final KieServices ks = KieServices.Factory.get();
        String uuid = UUID.uuid(8);
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie",
                                                                                    uuid,
                                                                                    "1.0"),
                                                                    ks.getResources().newByteArrayResource(mString.getBytes()).setTargetPath("src/main/resources/" + uuid + ".dmn"));

        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        assertNotNull(runtime);
        assertFalse(runtime.getModels().isEmpty());

        return runtime;
    }

    @Test
    public void test_relation_literal_expression() throws IOException {
        final DMNRuntime runtime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/hardcoded_relation_and_literal_expression.dmn"));
        DMNModel dmnModel = runtime.getModels().get(0);

        DMNContext dmnContext = runtime.newContext();
        DMNResult dmnResult = runtime.evaluateAll(dmnModel,
                                                  dmnContext);
        assertFalse(dmnResult.getMessages().toString(),
                    dmnResult.hasErrors());

        DMNContext result = dmnResult.getContext();
        Object hardCodedRelation = result.get("hardcoded relation");
        assertNotNull(hardCodedRelation);
        assertEquals(3, ((Collection) hardCodedRelation).size());

        DMNDecisionResult adultResult = dmnResult.getDecisionResultByName("Adults");
        assertEquals(DecisionEvaluationStatus.SUCCEEDED, adultResult.getEvaluationStatus());
        assertEquals(1, ((Collection) adultResult.getResult()).size());
    }

    @Test
    public void test_invocation() throws IOException {
        final DMNRuntime runtime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/hardcoded_invokation.dmn"));
        DMNModel dmnModel = runtime.getModels().get(0);

        DMNContext dmnContext = runtime.newContext();
        DMNResult dmnResult = runtime.evaluateAll(dmnModel,
                                                  dmnContext);
        assertFalse(dmnResult.getMessages().toString(),
                    dmnResult.hasErrors());

        DMNDecisionResult adultResult = dmnResult.getDecisionResultByName("hardcoded invokation");
        assertEquals(DecisionEvaluationStatus.SUCCEEDED, adultResult.getEvaluationStatus());
        assertEquals(new BigDecimal(11), adultResult.getResult());
    }

    @Test
    public void test_function_definition_and_invocation() throws IOException {
        final DMNRuntime runtime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/hardcoded_function_definition.dmn"));
        DMNModel dmnModel = runtime.getModels().get(0);

        DMNContext emptyContext = runtime.newContext();
        DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        assertFalse(dmnResult.getMessages().toString(), dmnResult.hasErrors());

        DMNDecisionResult adultResult = dmnResult.getDecisionResultByName("hardcoded decision");
        assertEquals(DecisionEvaluationStatus.SUCCEEDED, adultResult.getEvaluationStatus());
        assertEquals(47, ((BigDecimal) adultResult.getResult()).intValue());
    }

    @Test
    public void test_function_java_WB_model() throws IOException {
        final DMNMarshaller m = new DMNMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                                                  applicationFactoryManager);

        @SuppressWarnings("unchecked")
        final Graph<?, Node<?, ?>> g = m.unmarshall(null,
                                                    this.getClass().getResourceAsStream("/DROOLS-2372.dmn"));

        final Stream<Node<?, ?>> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(g.nodes().iterator(), Spliterator.ORDERED),
                                                               false);
        final Optional<Decision> wbDecision = stream
                .filter(n -> n.getContent() instanceof ViewImpl)
                .map(n -> (ViewImpl) n.getContent())
                .filter(n -> n.getDefinition() instanceof Decision)
                .map(n -> (Decision) n.getDefinition())
                .findFirst();

        wbDecision.ifPresent(d -> {
            assertTrue(d.getExpression() instanceof FunctionDefinition);
            final FunctionDefinition wbFunction = (FunctionDefinition) d.getExpression();

            //This is what the WB expects
            assertTrue(wbFunction.getAdditionalAttributes().containsKey(FunctionDefinition.KIND_QNAME));
            assertEquals("J",
                         wbFunction.getAdditionalAttributes().get(FunctionDefinition.KIND_QNAME));
        });

        final DMNRuntime runtime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/DROOLS-2372.dmn"));
        final DMNModel dmnModel = runtime.getModels().get(0);

        final DecisionNode dmnDecision = dmnModel.getDecisions().iterator().next();
        assertTrue(dmnDecision.getDecision().getExpression() instanceof org.kie.dmn.model.v1_1.FunctionDefinition);
        final org.kie.dmn.model.v1_1.FunctionDefinition dmnFunction = (org.kie.dmn.model.v1_1.FunctionDefinition) dmnDecision.getDecision().getExpression();
        assertTrue(dmnFunction.getAdditionalAttributes().containsKey(org.kie.dmn.model.v1_1.FunctionDefinition.KIND_QNAME));
        assertEquals("J",
                     dmnFunction.getAdditionalAttributes().get(org.kie.dmn.model.v1_1.FunctionDefinition.KIND_QNAME));
    }

    @Test
    public void test_function_definition_and_invoke_in_ctx() throws IOException {
        final DMNRuntime runtime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/function_definition_and_invoke_in_ctx.dmn"));
        DMNModel dmnModel = runtime.getModels().get(0);

        DMNContext emptyContext = runtime.newContext();
        DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        assertFalse(dmnResult.getMessages().toString(), dmnResult.hasErrors());

        DMNDecisionResult adultResult = dmnResult.getDecisionResultByName("hardcoded decision");
        assertEquals(DecisionEvaluationStatus.SUCCEEDED, adultResult.getEvaluationStatus());
        assertEquals(3, ((BigDecimal) adultResult.getResult()).intValue());
    }

    @Test
    public void test_hardcoded_decision_a_function() throws IOException {
        final DMNRuntime runtime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/hardcoded_decision_a_function.dmn"));
        DMNModel dmnModel = runtime.getModels().get(0);

        DMNContext emptyContext = runtime.newContext();
        DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        assertFalse(dmnResult.getMessages().toString(), dmnResult.hasErrors());

        DMNDecisionResult adultResult = dmnResult.getDecisionResultByName("hardcoded invokation");
        assertEquals(DecisionEvaluationStatus.SUCCEEDED, adultResult.getEvaluationStatus());
        assertEquals(3, ((BigDecimal) adultResult.getResult()).intValue());
    }

    @Test
    public void test_decision_table() throws IOException {
        final DMNRuntime runtime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/positive_or_negative.dmn"));
        DMNModel dmnModel = runtime.getModels().get(0);

        checkDecisionTableForPositiveOrNegative(runtime, dmnModel, 47, "positive");
        checkDecisionTableForPositiveOrNegative(runtime, dmnModel, -1, "negative");
    }

    private void checkDecisionTableForPositiveOrNegative(final DMNRuntime runtime, DMNModel dmnModel, int number, String result) {
        DMNContext context = runtime.newContext();
        context.set("a number", number);
        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertFalse(dmnResult.getMessages().toString(), dmnResult.hasErrors());

        DMNDecisionResult adultResult = dmnResult.getDecisionResultByName("positive or negative");
        assertEquals(DecisionEvaluationStatus.SUCCEEDED, adultResult.getEvaluationStatus());
        assertEquals(result, adultResult.getResult());
    }

    @Test
    public void test_wrong_context() throws IOException {
        // DROOLS-2217
        // SPECIAL CASE: to represent a partially edited DMN file.
        // consider a LiteralExpression with null text as missing expression altogether.

        final DMNRuntime runtime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/wrong_context.dmn"));
        DMNModel dmnModel = runtime.getModels().get(0);

        // the DMN file is schema valid but is not a valid-DMN (a context-entry value is a literal expression missing text, which is null)
        assertTrue(dmnModel.hasErrors());

        // identify the error message for context-entry "ciao":
        DMNMessage m0 = dmnModel.getMessages(DMNMessage.Severity.ERROR).get(0);
        assertTrue("expected a message identifying the problem on a context entry for 'ciao'",
                   m0.getMessage().startsWith("No expression defined for name 'ciao'"));

        DecisionNode d0 = dmnModel.getDecisionById("_653b3426-933a-4050-9568-ab2a66b43c36");
        // the identified DMN Decision is composed of a DMN Context where the first context-entry value is a literal expression missing text (text is null).
        org.kie.dmn.model.v1_1.Context d0c = (org.kie.dmn.model.v1_1.Context) d0.getDecision().getExpression();
        org.kie.dmn.model.v1_1.Expression contextEntryValue = d0c.getContextEntry().get(0).getExpression();
        assertTrue(contextEntryValue instanceof org.kie.dmn.model.v1_1.LiteralExpression);
        assertEquals(null, ((org.kie.dmn.model.v1_1.LiteralExpression) contextEntryValue).getText());

        // -- Stunner side.

        DMNMarshaller m = new DMNMarshaller(new XMLEncoderDiagramMetadataMarshaller(), applicationFactoryManager);
        Graph<?, ?> g = m.unmarshall(null, this.getClass().getResourceAsStream("/wrong_context.dmn"));
        DiagramImpl diagram = new DiagramImpl("", null);

        Node<?, ?> decisionNode = g.getNode("_653b3426-933a-4050-9568-ab2a66b43c36");
        assertNodeContentDefinitionIs(decisionNode, Decision.class);
        View<Decision> view = ((View<Decision>) decisionNode.getContent());

        // the identified DMN Decision is composed of a DMN Context where the first context-entry has missing Expression.
        Context expression = (Context) view.getDefinition().getExpression();
        assertEquals("a literalexpression with null text is threated as a missing expression altogether.",
                     null,
                     expression.getContextEntry().get(0).getExpression());
    }

    @Test
    public void testOtherElements() throws IOException, XPathExpressionException {
        String original = new Scanner(this.getClass().getResourceAsStream("/dummy.dmn")).useDelimiter("\\A").next();
        DMNMarshaller marshaller = new DMNMarshaller(new XMLEncoderDiagramMetadataMarshaller(), applicationFactoryManager);
        DiagramImpl diagram = new DiagramImpl("", null);
        diagram.setGraph(marshaller.unmarshall(null, getClass().getResourceAsStream("/dummy.dmn")));
        String roundtripped = marshaller.marshall(diagram);
        XPath xpath = namespaceAwareXPath(
                new AbstractMap.SimpleEntry<>("semantic", "http://www.omg.org/spec/DMN/20151101/dmn.xsd"),
                new AbstractMap.SimpleEntry<>("drools", "http://www.drools.org/kie/dmn/1.1")
        );
        assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:extensionElements)"), original, roundtripped);

        assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:import)"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:import/@namespace"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:import/@importType"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:import/@locationURI"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:import/@drools:name"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:import/@drools:modelName"), original, roundtripped);

        //assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:elementCollection)"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:elementCollection/@name"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:elementCollection/@id"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:elementCollection/@label"), original, roundtripped);
        //assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:elementCollection/drgElement)"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:elementCollection/semantic:drgElement/@href"), original, roundtripped);

        //assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:performanceIndicator)"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:performanceIndicator/@name"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:performanceIndicator/@id"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:performanceIndicator/@label"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:performanceIndicator/@URI"), original, roundtripped);
        //assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:performanceIndicator/semantic:impactingDecision)"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:performanceIndicator/semantic:impactingDecision/@href"), original, roundtripped);

        //assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:organizationUnit)"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:organizationUnit/@name"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:organizationUnit/@id"), original, roundtripped);
        //assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:organizationUnit/semantic:decisionMade)"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:organizationUnit/semantic:decisionMade/@href"), original, roundtripped);
        //assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:organizationUnit/semantic:decisionOwned)"), original, roundtripped);
        //assertXPathEquals(xpath.compile("/semantic:definitions/semantic:organizationUnit/semantic:decisionOwned/@href"), original, roundtripped);

        assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:knowledgeSource)"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:knowledgeSource/@name"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:knowledgeSource/@id"), original, roundtripped);
        assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:knowledgeSource/semantic:authorityRequirement)"), original, roundtripped);
        assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:knowledgeSource/semantic:requiredInput)"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:knowledgeSource/semantic:requiredInput/@href"), original, roundtripped);

        assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:inputData)"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:inputData/@id"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:inputData/@name"), original, roundtripped);
        assertXPathEquals(xpath.compile("boolean(/semantic:definitions/semantic:inputData/semantic:variable)"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:inputData/semantic:variable/@id"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:inputData/semantic:variable/@name"), original, roundtripped);
        assertXPathEquals(xpath.compile("/semantic:definitions/semantic:inputData/semantic:variable/@typeRef"), original, roundtripped);
    }

    private XPath namespaceAwareXPath(Map.Entry<String, String>... pfxAndURI) {
        XPath result = XPathFactory.newInstance().newXPath();
        result.setNamespaceContext(new NamespaceContext() {
            final Map<String, String> pfxToURI = new HashMap<String, String>() {{
                for (Map.Entry<String, String> pair : pfxAndURI) {
                    put(pair.getKey(), pair.getValue());
                }
            }};
            final Map<String, String> URItoPfx = new HashMap<String, String>() {{
                for (Map.Entry<String, String> pair : pfxAndURI) {
                    put(pair.getValue(), pair.getKey());
                }
            }};

            @Override
            public String getNamespaceURI(String prefix) {
                return pfxToURI.get(prefix);
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return URItoPfx.get(namespaceURI);
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return pfxToURI.keySet().iterator();
            }
        });
        return result;
    }

    private void assertXPathEquals(XPathExpression expression, String expectedXml, String actualXml) throws XPathExpressionException {
        InputSource expected = new InputSource(new StringReader(expectedXml));
        InputSource actual = new InputSource(new StringReader(actualXml));
        assertEquals(expression.evaluate(expected), expression.evaluate(actual));
    }
}