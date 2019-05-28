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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.apache.tools.ant.util.ReaderInputStream;
import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.KieHelper;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.FunctionKind;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.dmndi.Bounds;
import org.kie.dmn.model.api.dmndi.Color;
import org.kie.dmn.model.api.dmndi.DMNEdge;
import org.kie.dmn.model.api.dmndi.DMNShape;
import org.kie.dmn.model.api.dmndi.DMNStyle;
import org.kie.dmn.model.api.dmndi.DiagramElement;
import org.kie.dmn.model.api.dmndi.Point;
import org.kie.dmn.model.v1_2.TDecision;
import org.kie.dmn.model.v1_2.TInputData;
import org.kie.dmn.model.v1_2.TItemDefinition;
import org.kie.dmn.model.v1_2.TTextAnnotation;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionService;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.backend.common.DMNMarshallerImportsHelper;
import org.kie.workbench.common.dmn.backend.definition.v1_1.DecisionConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.InputDataConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.TextAnnotationConverter;
import org.kie.workbench.common.dmn.backend.producers.DMNMarshallerProducer;
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
import org.kie.workbench.common.stunner.core.diagram.Diagram;
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
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.uuid.UUID;
import org.xml.sax.InputSource;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNMarshallerTest {

    private static final Logger LOG = LoggerFactory.getLogger(DMNMarshallerTest.class);

    private static final String DMN_DEF_SET_ID = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);

    @Mock
    DefinitionManager definitionManager;

    @Mock
    AdapterManager adapterManager;

    @Mock
    AdapterRegistry adapterRegistry;

    @Mock
    BackendFactoryManager applicationFactoryManager;

    @Mock
    DefinitionsCacheRegistry definitionsRegistry;

    @Mock
    DMNMarshallerImportsHelper dmnMarshallerImportsHelper;

    EdgeFactory<Object> connectionEdgeFactory;
    NodeFactory<Object> viewNodeFactory;
    DefinitionUtils definitionUtils;

    GraphCommandManager commandManager;
    GraphCommandFactory commandFactory;

    GraphFactory dmnGraphFactory;

    TestScopeModelFactory testScopeModelFactory;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        // Graph utils.
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        definitionUtils = new DefinitionUtils(definitionManager,
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
                DMNDiagram model = new DMNDiagram();
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
                Graph graph = dmnGraphFactory.build(uuid,
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

    /**
     * Two issues bellow prevents us from running marshalling tests on IBM jdk
     * https://support.oracle.com/knowledge/Middleware/1459269_1.html
     * https://www-01.ibm.com/support/docview.wss?uid=swg1PK99682
     */
    @Before
    public void doNotRunTestsOnIbmJdk() {
        final String ibmVendorName = "IBM";
        final String javaVendorPropertyKey = "java.vendor";
        Assume.assumeFalse(StringUtils.containsIgnoreCase(System.getProperty(javaVendorPropertyKey), ibmVendorName));
    }

    @Test
    public void testLoan() throws IOException {
        roundTripUnmarshalMarshalThenUnmarshalDMN(getClass().getResourceAsStream("/Loan Pre-Qualification.dmn"));
        DMNMarshaller m = getDMNMarshaller();
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
    public void test_DecisionTableInputOutputClausesWhenEmpty() throws IOException {
        DMNRuntime runtime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/qGslQdo2.dmn"));
        Assert.assertNotNull(runtime);

        DMNModel model = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn/_A2C75C01-7EAD-46B8-A499-D85D6C07D273", "_5FE8CBFD-821B-41F6-A6C7-42BE3FC45F2F");
        Assert.assertNotNull(model);
        assertThat(model.hasErrors(), is(false));

        DMNContext dmnContext = runtime.newContext();
        dmnContext.set("my number", -99);

        DMNResult dmnResult = runtime.evaluateAll(model, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("my decision").getEvaluationStatus(), is(DecisionEvaluationStatus.SUCCEEDED));
        assertThat(dmnResult.getDecisionResultByName("my decision").getResult(), is("negative"));

        org.kie.dmn.model.api.DecisionTable dmnDT = (DecisionTable) model.getDecisionByName("my decision").getDecision().getExpression();
        assertThat(dmnDT.getInput().get(0).getInputValues(), nullValue()); // DROOLS-3262
        assertThat(dmnDT.getOutput().get(0).getOutputValues(), nullValue()); // DROOLS-3262
        assertThat(dmnDT.getOutput().get(0).getDefaultOutputEntry(), nullValue()); // DROOLS-3262
        assertThat(dmnDT.getOutput().get(0).getName(), nullValue()); // DROOLS-3281
    }

    @Test
    public void test_diamond() throws IOException {
        // round trip test
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/diamondDMN12.dmn"),
                                               this::checkDiamondGraph);

        // additionally, check the marshalled is still DMN executable as expected
        DMNMarshaller m = getDMNMarshaller();
        Graph<?, ?> g = m.unmarshall(null,
                                     this.getClass().getResourceAsStream("/diamondDMN12.dmn"));
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

        // additionally, check DMN DD/DI
        org.kie.dmn.api.marshalling.DMNMarshaller dmnMarshaller = DMNMarshallerFactory.newDefaultMarshaller();
        Definitions definitions = dmnMarshaller.unmarshal(mString);

        org.kie.dmn.model.api.dmndi.DMNDiagram ddRoot = (org.kie.dmn.model.api.dmndi.DMNDiagram) definitions.getDMNDI().getDMNDiagram().get(0);

        DMNShape myname = findShapeByDMNI(ddRoot, "_4cd17e52-6253-41d6-820d-5824bf5197f3");
        assertBounds(500, 500, 100, 50, myname.getBounds());
        assertColor(255, 255, 255, ((DMNStyle) myname.getStyle()).getFillColor());
        assertColor(0, 0, 0, ((DMNStyle) myname.getStyle()).getStrokeColor());
        assertDMNStyle("Open Sans", 24, 255, 0, 0, (DMNStyle) myname.getStyle());

        DMNShape prefix = findShapeByDMNI(ddRoot, "_e920f38a-293c-41b8-adb3-69d0dc184fab");
        assertBounds(300, 400, 100, 50, prefix.getBounds());
        assertColor(0, 253, 25, ((DMNStyle) prefix.getStyle()).getFillColor());
        assertColor(253, 0, 0, ((DMNStyle) prefix.getStyle()).getStrokeColor());
        assertDMNStyle("Times New Roman", 8, 70, 60, 50, (DMNStyle) prefix.getStyle());

        DMNShape postfix = findShapeByDMNI(ddRoot, "_f49f9c34-29d5-4e72-91d2-f4f92117c8da");
        assertBounds(700, 400, 100, 50, postfix.getBounds());
        assertColor(247, 255, 0, ((DMNStyle) postfix.getStyle()).getFillColor());
        assertColor(0, 51, 255, ((DMNStyle) postfix.getStyle()).getStrokeColor());
        assertDMNStyle("Arial", 10, 50, 60, 70, (DMNStyle) postfix.getStyle());

        DMNShape mydecision = findShapeByDMNI(ddRoot, "_9b061fc3-8109-42e2-9fe4-fc39c90b654e");
        assertBounds(487.5, 275, 125, 75, mydecision.getBounds());
        assertColor(255, 255, 255, ((DMNStyle) mydecision.getStyle()).getFillColor());
        assertColor(0, 0, 0, ((DMNStyle) mydecision.getStyle()).getStrokeColor());
        assertDMNStyle("Monospaced", 32, 55, 66, 77, (DMNStyle) mydecision.getStyle());
    }

    private void assertDMNStyle(String fontName, double fontSize, int r, int g, int b, DMNStyle style) {
        assertEquals(fontName, style.getFontFamily());
        assertEquals(fontSize, style.getFontSize(), 0);
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

    private static DMNShape findShapeByDMNI(org.kie.dmn.model.api.dmndi.DMNDiagram root, String id) {
        return root.getDMNDiagramElement().stream()
                   .filter(DMNShape.class::isInstance)
                   .map(DMNShape.class::cast)
                   .filter(shape -> shape.getDmnElementRef().getLocalPart().equals(id))
                   .findFirst()
                   .orElseThrow(() -> new UnsupportedOperationException("There is no DMNShape with id '" + id + "' in DMNDiagram " + root));
    }

    private static DMNEdge findEdgeByDMNI(org.kie.dmn.model.api.dmndi.DMNDiagram root, String id) {
        return root.getDMNDiagramElement().stream()
                   .filter(DMNEdge.class::isInstance)
                   .map(DMNEdge.class::cast)
                   .filter(shape -> shape.getDmnElementRef().getLocalPart().equals(id))
                   .findFirst()
                   .orElseThrow(() -> new UnsupportedOperationException("There is no DMNEdge with id '" + id + "' in DMNDiagram " + root));
    }

    @Test
    public void test_potpourri_drawing() throws IOException {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/potpourri_drawing.dmn"),
                                               this::checkPotpourriGraph);
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

    @Test
    public void testDecisionWithContext() throws Exception {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/DecisionWithContext.dmn"),
                                               this::checkDecisionWithContext);
    }

    @Test
    public void testDecisionWithContextWithDefaultResult() throws Exception {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/DecisionWithContextWithDefaultResult.dmn"),
                                               this::checkDecisionWithContextWithDefaultResult);
    }

    @Test
    public void testDecisionWithContextWithoutDefaultResult() throws Exception {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/DecisionWithContextWithoutDefaultResult.dmn"),
                                               this::checkDecisionWithContextWithoutDefaultResult);
    }

    @Test
    public void testEdgewaypoint() throws Exception {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/edgewaypoint.dmn"),
                                               this::checkEdgewaypoint);
    }

    @Test
    public void test_decisionqa() throws IOException {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/decisionqa.dmn"),
                                               this::checkDecisionqa);
    }

    @Test
    public void test_decisionservice_1outputDecision() throws IOException {
        final DMNMarshaller m = getDMNMarshaller();
        @SuppressWarnings("unchecked")
        final Graph<?, Node<?, ?>> g = m.unmarshall(null, this.getClass().getResourceAsStream("/DROOLS-3372.dmn"));
        Node<?, ?> nodeDS = g.getNode("_659a06e2-ae80-496c-8783-f790a640bb49");
        Node<?, ?> nodeDecisionPostfix = g.getNode("_3a69915a-30af-4de3-a07f-6be514f53caa");
        moveNode(nodeDecisionPostfix, 0, -280);
        makeNodeChildOf(nodeDecisionPostfix, nodeDS);
        DiagramImpl diagram = new DiagramImpl("", null);
        diagram.setGraph(g);
        String mString = m.marshall(diagram);
        LOG.debug("MARSHALLED ROUNDTRIP RESULTING xml:\n{}\n", mString);
        roundTripUnmarshalThenMarshalUnmarshal(new ReaderInputStream(new StringReader(mString)),
                                               this::check_decisionservice_1outputDecision);
    }

    @SuppressWarnings("unchecked")
    private void check_decisionservice_1outputDecision(Graph<?, Node<?, ?>> graph) {
        Node<?, ?> node = graph.getNode("_659a06e2-ae80-496c-8783-f790a640bb49");
        assertNodeContentDefinitionIs(node, DecisionService.class);
        DecisionService definition = ((View<DecisionService>) node.getContent()).getDefinition();
        assertEquals(0, definition.getEncapsulatedDecision().size());
        assertEquals(0, definition.getInputData().size());
        assertEquals(1, definition.getOutputDecision().size());
        assertEquals("#" + "_3a69915a-30af-4de3-a07f-6be514f53caa", definition.getOutputDecision().get(0).getHref());
        assertEquals(1, definition.getInputDecision().size());
        assertEquals("#" + "_afce4fb3-9a7c-4791-bbfe-63d4b76bd61a", definition.getInputDecision().get(0).getHref());
    }

    @Test
    public void test_decisionservice_1outputDecision1encapsulatedDecision() throws IOException {
        final DMNMarshaller m = getDMNMarshaller();
        @SuppressWarnings("unchecked")
        final Graph<?, Node<?, ?>> g = m.unmarshall(null, this.getClass().getResourceAsStream("/DROOLS-3372.dmn"));
        Node<?, ?> nodeDS = g.getNode("_659a06e2-ae80-496c-8783-f790a640bb49");
        Node<?, ?> nodeDecisionPostfix = g.getNode("_3a69915a-30af-4de3-a07f-6be514f53caa");
        Node<?, ?> nodeDecisionPrefix = g.getNode("_afce4fb3-9a7c-4791-bbfe-63d4b76bd61a");
        moveNode(nodeDecisionPostfix, 0, -280);
        makeNodeChildOf(nodeDecisionPostfix, nodeDS);
        moveNode(nodeDecisionPrefix, 0, -170);
        makeNodeChildOf(nodeDecisionPrefix, nodeDS);
        DiagramImpl diagram = new DiagramImpl("", null);
        diagram.setGraph(g);
        String mString = m.marshall(diagram);
        LOG.debug("MARSHALLED ROUNDTRIP RESULTING xml:\n{}\n", mString);
        roundTripUnmarshalThenMarshalUnmarshal(new ReaderInputStream(new StringReader(mString)),
                                               this::check_decisionservice_1outputDecision1encapsulatedDecision);
    }

    @SuppressWarnings("unchecked")
    private void check_decisionservice_1outputDecision1encapsulatedDecision(Graph<?, Node<?, ?>> graph) {
        Node<?, ?> node = graph.getNode("_659a06e2-ae80-496c-8783-f790a640bb49");
        assertNodeContentDefinitionIs(node, DecisionService.class);
        DecisionService definition = ((View<DecisionService>) node.getContent()).getDefinition();
        assertEquals(1, definition.getEncapsulatedDecision().size());
        assertEquals("#" + "_afce4fb3-9a7c-4791-bbfe-63d4b76bd61a", definition.getEncapsulatedDecision().get(0).getHref());
        assertEquals(1, definition.getInputData().size());
        assertEquals("#" + "_dd5b090f-6d52-4bd8-8c7f-0c469983d44e", definition.getInputData().get(0).getHref());
        assertEquals(1, definition.getOutputDecision().size());
        assertEquals("#" + "_3a69915a-30af-4de3-a07f-6be514f53caa", definition.getOutputDecision().get(0).getHref());
        assertEquals(0, definition.getInputDecision().size());
    }

    @Test
    public void test_decisionservice2_1outputDecision1encapsulatedDecision() throws IOException {
        final DMNMarshaller m = getDMNMarshaller();
        @SuppressWarnings("unchecked")
        final Graph<?, Node<?, ?>> g = m.unmarshall(null, this.getClass().getResourceAsStream("/DROOLS-3372bis.dmn"));
        Node<?, ?> nodeDS = g.getNode("_659a06e2-ae80-496c-8783-f790a640bb49");
        Node<?, ?> nodeDecisionPostfix = g.getNode("_3a69915a-30af-4de3-a07f-6be514f53caa");
        Node<?, ?> nodeDecisionPrefix = g.getNode("_afce4fb3-9a7c-4791-bbfe-63d4b76bd61a");
        moveNode(nodeDecisionPostfix, 0, -280);
        makeNodeChildOf(nodeDecisionPostfix, nodeDS);
        moveNode(nodeDecisionPrefix, 0, -170);
        makeNodeChildOf(nodeDecisionPrefix, nodeDS);
        DiagramImpl diagram = new DiagramImpl("", null);
        diagram.setGraph(g);
        String mString = m.marshall(diagram);
        LOG.debug("MARSHALLED ROUNDTRIP RESULTING xml:\n{}\n", mString);
        roundTripUnmarshalThenMarshalUnmarshal(new ReaderInputStream(new StringReader(mString)),
                                               this::check_decisionservice2_1outputDecision1encapsulatedDecision);
    }

    @SuppressWarnings("unchecked")
    private void check_decisionservice2_1outputDecision1encapsulatedDecision(Graph<?, Node<?, ?>> graph) {
        Node<?, ?> node = graph.getNode("_659a06e2-ae80-496c-8783-f790a640bb49");
        assertNodeContentDefinitionIs(node, DecisionService.class);
        DecisionService definition = ((View<DecisionService>) node.getContent()).getDefinition();
        assertEquals(3, definition.getEncapsulatedDecision().size());
        assertEquals("#_ca9d65e7-a5fa-4a13-98b7-8404f4601147", definition.getEncapsulatedDecision().get(0).getHref());
        assertEquals("#_4b02cf97-5f9b-48ee-a4ae-229233238876", definition.getEncapsulatedDecision().get(1).getHref());
        assertEquals("#" + "_afce4fb3-9a7c-4791-bbfe-63d4b76bd61a", definition.getEncapsulatedDecision().get(2).getHref());
        assertEquals(1, definition.getInputData().size());
        assertEquals("#" + "_dd5b090f-6d52-4bd8-8c7f-0c469983d44e", definition.getInputData().get(0).getHref());
        assertEquals(3, definition.getOutputDecision().size());
        assertEquals("#_a5d0e474-083f-44ef-b00e-4ddc9a9ebd34", definition.getOutputDecision().get(0).getHref());
        assertEquals("#_8878539e-1c50-4622-b601-5878c97dc34e", definition.getOutputDecision().get(1).getHref());
        assertEquals("#" + "_3a69915a-30af-4de3-a07f-6be514f53caa", definition.getOutputDecision().get(2).getHref());
        assertEquals(0, definition.getInputDecision().size());
    }

    @Test
    public void test_decisionservice2_remove_1outputDecision1encapsulatedDecision() throws IOException {
        final DMNMarshaller m = getDMNMarshaller();
        @SuppressWarnings("unchecked")
        final Graph<?, Node<?, ?>> g = m.unmarshall(null, this.getClass().getResourceAsStream("/DROOLS-3372bis.dmn"));
        Node<?, ?> nodeDS = g.getNode("_659a06e2-ae80-496c-8783-f790a640bb49");
        Node<?, ?> nodeDecisionPostfix = g.getNode("_3a69915a-30af-4de3-a07f-6be514f53caa");
        Node<?, ?> nodeDecisionPrefix = g.getNode("_afce4fb3-9a7c-4791-bbfe-63d4b76bd61a");
        moveNode(nodeDecisionPostfix, 0, -280);
        makeNodeChildOf(nodeDecisionPostfix, nodeDS);
        moveNode(nodeDecisionPrefix, 0, -170);
        makeNodeChildOf(nodeDecisionPrefix, nodeDS);
        DiagramImpl diagram = new DiagramImpl("", null);
        Node<?, ?> nodeEncaps1 = g.getNode("_ca9d65e7-a5fa-4a13-98b7-8404f4601147");
        moveNode(nodeEncaps1, 0, +400);
        removeNodeChildOf(nodeEncaps1, nodeDS);
        Node<?, ?> nodeEncaps2 = g.getNode("_4b02cf97-5f9b-48ee-a4ae-229233238876");
        moveNode(nodeEncaps2, 0, +400);
        removeNodeChildOf(nodeEncaps2, nodeDS);
        Node<?, ?> nodeHardcoded2 = g.getNode("_8878539e-1c50-4622-b601-5878c97dc34e");
        moveNode(nodeHardcoded2, 0, +400);
        removeNodeChildOf(nodeHardcoded2, nodeDS);
        diagram.setGraph(g);
        String mString = m.marshall(diagram);
        LOG.debug("MARSHALLED ROUNDTRIP RESULTING xml:\n{}\n", mString);
        roundTripUnmarshalThenMarshalUnmarshal(new ReaderInputStream(new StringReader(mString)),
                                               this::check_decisionservice2_remove_1outputDecision1encapsulatedDecision);
    }

    private void removeNodeChildOf(Node<?, ?> childNode, Node<?, ?> nodeDS) {
        nodeDS.getOutEdges().removeIf(x -> {
            Edge<View<?>, ?> edge = x;
            return edge.getContent() instanceof Child && edge.getTargetNode().equals(childNode);
        });
        childNode.getInEdges().removeIf(x -> {
            Edge<View<?>, ?> edge = x;
            return edge.getContent() instanceof Child && edge.getSourceNode().equals(nodeDS);
        });
    }

    @SuppressWarnings("unchecked")
    private void check_decisionservice2_remove_1outputDecision1encapsulatedDecision(Graph<?, Node<?, ?>> graph) {
        Node<?, ?> node = graph.getNode("_659a06e2-ae80-496c-8783-f790a640bb49");
        assertNodeContentDefinitionIs(node, DecisionService.class);
        DecisionService definition = ((View<DecisionService>) node.getContent()).getDefinition();
        assertEquals(1, definition.getEncapsulatedDecision().size());
        // no more "#_ca9d65e7-a5fa-4a13-98b7-8404f4601147"
        // no more "#_4b02cf97-5f9b-48ee-a4ae-229233238876"
        assertEquals("#" + "_afce4fb3-9a7c-4791-bbfe-63d4b76bd61a", definition.getEncapsulatedDecision().get(0).getHref());
        assertEquals(1, definition.getInputData().size());
        assertEquals("#" + "_dd5b090f-6d52-4bd8-8c7f-0c469983d44e", definition.getInputData().get(0).getHref());
        assertEquals(2, definition.getOutputDecision().size());
        assertEquals("#_a5d0e474-083f-44ef-b00e-4ddc9a9ebd34", definition.getOutputDecision().get(0).getHref());
        // no more "#_8878539e-1c50-4622-b601-5878c97dc34e"
        assertEquals("#" + "_3a69915a-30af-4de3-a07f-6be514f53caa", definition.getOutputDecision().get(1).getHref());
        assertEquals(1, definition.getInputDecision().size());
        assertEquals("#_ca9d65e7-a5fa-4a13-98b7-8404f4601147", definition.getInputDecision().get(0).getHref());
    }

    private void makeNodeChildOf(Node nodeDecisionPostfix, Node nodeDS) {
        Edge myEdge = new EdgeImpl<>(UUID.uuid());
        myEdge.setContent(new Child());
        myEdge.setSourceNode(nodeDS);
        myEdge.setTargetNode(nodeDecisionPostfix);
        nodeDS.getOutEdges().add(myEdge);
        nodeDecisionPostfix.getInEdges().add(myEdge);
    }

    private void moveNode(Node<?, ?> nodeDecisionPostfix, int dx, int dy) {
        View content = (View) nodeDecisionPostfix.getContent();
        Bound ul = content.getBounds().getUpperLeft();
        Bound lr = content.getBounds().getLowerRight();
        content.setBounds(org.kie.workbench.common.stunner.core.graph.content.Bounds.create(ul.getX() + dx, ul.getY() + dy, lr.getX() + dx, lr.getY() + dy));
    }

    @Test
    public void test_decisionservice3_evaluate() throws IOException {
        DMNRuntime dmnRuntime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/DROOLS-3372evaluate.dmn"));
        assertThat(dmnRuntime.getModels()).hasSize(1);
        final DMNModel dmnModel = dmnRuntime.getModels().get(0);
        final DMNContext context = dmnRuntime.newContext();
        context.set("in1", "asd");
        final DMNResult result = dmnRuntime.evaluateAll(dmnModel, context);
        assertThat(result.getDecisionResultByName("outInDS").getResult()).isEqualTo("outInDSasd");
        assertThat(result.getDecisionResultByName("out1").getResult()).isEqualTo("outInDSp1 outInDSin1");
    }

    @Test
    public void test_fontsize_stunner() throws IOException {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/test-FontSize-stunner.dmn"),
                                               this::checkFontsize_stunner);
    }

    @SuppressWarnings("unchecked")
    private void checkFontsize_stunner(Graph<?, Node<?, ?>> graph) {
        Node<?, ?> node = graph.getNode("_A9D510E0-1942-4945-A945-0213EC6AAEC5");
        assertNodeContentDefinitionIs(node, InputData.class);
        InputData definition = ((View<InputData>) node.getContent()).getDefinition();
        assertEquals(Double.valueOf(21), definition.getFontSet().getFontSize().getValue());
    }

    @Test
    public void test_fontsize_sharedStyle() throws IOException {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/test-FontSize-sharedStyle.dmn"),
                                               this::checkFontsize_sharedStyle);
    }

    @SuppressWarnings("unchecked")
    private void checkFontsize_sharedStyle(Graph<?, Node<?, ?>> graph) {
        Node<?, ?> node = graph.getNode("_38b74e2e-32f8-42c5-ab51-8a3e927637e0");
        assertNodeContentDefinitionIs(node, InputData.class);
        InputData definition = ((View<InputData>) node.getContent()).getDefinition();
        assertEquals(Double.valueOf(21), definition.getFontSet().getFontSize().getValue());
    }

    @SuppressWarnings("unchecked")
    private void checkDecisionqa(Graph<?, Node<?, ?>> graph) {
        Node<?, ?> decision = graph.getNode("_7052d0f6-ccee-462b-bd89-76afc3b6f67b");
        assertNodeContentDefinitionIs(decision,
                                      Decision.class);
        Decision decisionDefinition = ((View<Decision>) decision.getContent()).getDefinition();
        assertEquals("hardcoded question", decisionDefinition.getName().getValue());
        assertEquals("What is the codename?", decisionDefinition.getQuestion().getValue());
        assertEquals("47.", decisionDefinition.getAllowedAnswers().getValue());
    }

    @SuppressWarnings("unchecked")
    private void checkEdgewaypoint(Graph<?, Node<?, ?>> graph) {
        Node<?, ?> decision = graph.getNode("_7647e26b-6c7c-46db-aa34-1a1a2b4d8d79");
        assertNodeContentDefinitionIs(decision,
                                      Decision.class);
        Decision decisionDefinition = ((View<Decision>) decision.getContent()).getDefinition();
        assertEquals("my decision",
                     decisionDefinition.getName().getValue());

        Node<?, ?> inputdata = graph.getNode("_fd528e66-e2a4-4b7f-aae1-c3ca6723d0cb");
        assertNodeEdgesTo(inputdata,
                          decision,
                          InformationRequirement.class);

        // asserted the two Stunner graph nodes are properly connected, assert location of edge.
        List<Edge<?, ?>> outEdges = (List<Edge<?, ?>>) inputdata.getOutEdges();
        Edge<?, ?> edge = outEdges.stream().filter(e -> e.getTargetNode().equals(decision)).findFirst().get();
        ViewConnector<?> connectionContent = (ViewConnector<?>) edge.getContent();
        Point2D sourceLocation = connectionContent.getSourceConnection().get().getLocation();
        assertEquals(266.9968013763428d, ((View) inputdata.getContent()).getBounds().getUpperLeft().getX() + sourceLocation.getX(), 0.1d);
        assertEquals(225.99999618530273d, ((View) inputdata.getContent()).getBounds().getUpperLeft().getY() + sourceLocation.getY(), 0.1d);

        Point2D targetLocation = connectionContent.getTargetConnection().get().getLocation();
        assertEquals(552.2411708831787d, ((View) decision.getContent()).getBounds().getUpperLeft().getX() + targetLocation.getX(), 0.1d);
        assertEquals(226d, ((View) decision.getContent()).getBounds().getUpperLeft().getY() + targetLocation.getY(), 0.1d);

        assertEquals(1, connectionContent.getControlPoints().length);
        Point2D controlPointLocation = connectionContent.getControlPoints()[0].getLocation();
        assertEquals(398.61898612976074d, controlPointLocation.getX(), 0.1d);
        assertEquals(116.99999809265137d, controlPointLocation.getY(), 0.1d);

        final Connection sourceConnection = connectionContent.getSourceConnection().get();
        assertTrue(sourceConnection instanceof MagnetConnection);
        assertTrue(((MagnetConnection) sourceConnection).isAuto());

        final Connection targetConnection = connectionContent.getTargetConnection().get();
        assertTrue(targetConnection instanceof MagnetConnection);
        assertTrue(((MagnetConnection) targetConnection).isAuto());
    }

    @SuppressWarnings("unchecked")
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
        DMNMarshaller m = getDMNMarshaller();

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
        LOG.debug(mString);

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

    @SuppressWarnings("unchecked")
    private void checkDecisionWithContext(Graph<?, Node<?, ?>> g) {
        Node<?, ?> decisionNode = g.getNode("_30810b88-8416-4c02-8ed1-8c19b7606243");
        assertNodeContentDefinitionIs(decisionNode,
                                      Decision.class);

        Node<?, ?> rootNode = DMNMarshaller.findDMNDiagramRoot((Graph) g);
        assertNotNull(rootNode);
        assertRootNodeConnectedTo(rootNode,
                                  decisionNode);
        assertEquals("decisionNode parent is Definitions DMN root",
                     "_153e2b47-3bd2-4db0-828c-db3fce0b3199",
                     ((DMNElement) ((Decision) ((View<?>) decisionNode.getContent()).getDefinition()).getParent()).getId().getValue());

        Context context = (Context) ((Decision) ((View<?>) decisionNode.getContent()).getDefinition()).getExpression();
        assertEquals("contextNode's parent is decisionNode",
                     "_30810b88-8416-4c02-8ed1-8c19b7606243",
                     ((DMNElement) context.getParent()).getId().getValue());

        Expression literalExpression1 = context.getContextEntry().get(0).getExpression();
        assertEquals("literalExpression1's parent-parent is contextNode",
                     "_0f38d114-5d6e-40dd-aa9c-9f031f9b0571",
                     ((DMNElement) (literalExpression1).getParent()
                                       .getParent()).getId().getValue());

        Expression literalExpression2 = context.getContextEntry().get(1).getExpression();
        assertEquals("literalExpression2's parent-parent is contextNode",
                     "_0f38d114-5d6e-40dd-aa9c-9f031f9b0571",
                     ((DMNElement) (literalExpression2).getParent()
                                       .getParent()).getId().getValue());
    }

    private void checkDecisionWithContextWithDefaultResult(Graph<?, Node<?, ?>> g) {
        Node<?, ?> decisionNode = g.getNode("_30810b88-8416-4c02-8ed1-8c19b7606243");
        assertNodeContentDefinitionIs(decisionNode,
                                      Decision.class);

        Context context = (Context) ((Decision) ((View<?>) decisionNode.getContent()).getDefinition()).getExpression();

        InformationItem defaultResultVariable = context.getContextEntry().get(1).getVariable();
        assertNull("Default result variable",
                   defaultResultVariable);
        Expression defaultResultExpression = context.getContextEntry().get(1).getExpression();
        assertNotNull("Default result expression",
                      defaultResultExpression);
        assertEquals("defaultResultExpression's parent-parent is contextNode",
                     "_0f38d114-5d6e-40dd-aa9c-9f031f9b0571",
                     ((DMNElement) (defaultResultExpression).getParent()
                                       .getParent()).getId().getValue());
    }

    private void checkDecisionWithContextWithoutDefaultResult(Graph<?, Node<?, ?>> g) {
        Node<?, ?> decisionNode = g.getNode("_30810b88-8416-4c02-8ed1-8c19b7606243");
        assertNodeContentDefinitionIs(decisionNode,
                                      Decision.class);

        Context context = (Context) ((Decision) ((View<?>) decisionNode.getContent()).getDefinition()).getExpression();

        InformationItem defaultResultVariable = context.getContextEntry().get(1).getVariable();
        assertNull("Default result variable",
                   defaultResultVariable);
        Expression defaultResultExpression = context.getContextEntry().get(1).getExpression();
        assertNull("Default result expression",
                   defaultResultExpression);
    }

    @SuppressWarnings("unchecked")
    private void checkDiamondGraph(Graph<?, Node<?, ?>> g) {
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

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    private void checkPotpourriGraph(Graph<?, Node<?, ?>> g) {
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
        String xml = null;
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(dmnXmlInputStream))) {
            xml = buffer.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("test utily method roundTripUnmarshalMarshalThenUnmarshalDMN failed to read XML content.", e);
        }
        LOG.debug("ORIGINAL xml:\n{}\n", xml);
        final DMNRuntime runtime0 = dmnRuntimeFromDMNXML(xml);
        assertEquals("The DMN XML file contains compilation error. If this was intentional use test method roundTripUnmarshalMarshalThenUnmarshalDMNexpectingErrors",
                     0,
                     runtime0.getModels().get(0).getMessages(DMNMessage.Severity.ERROR).size());

        DMNMarshaller m = getDMNMarshaller();

        // first unmarshal from DMN XML to Stunner DMN Graph
        @SuppressWarnings("unchecked")
        Graph<?, Node<?, ?>> g = m.unmarshall(null,
                                              new ReaderInputStream(new StringReader(xml)));

        // round trip to Stunner DMN Graph back to DMN XML
        DiagramImpl diagram = new DiagramImpl("",
                                              null);
        diagram.setGraph(g);

        String mString = m.marshall(diagram);
        LOG.debug("MARSHALLED ROUNDTRIP RESULTING xml:\n{}\n", mString);

        // now unmarshal once more, from the marshalled just done above, into a DMNRuntime
        final DMNRuntime runtime = dmnRuntimeFromDMNXML(mString);
        assertTrue(runtime.getModels().get(0).getMessages(DMNMessage.Severity.ERROR).size() == 0);
        return runtime;
    }

    private static DMNRuntime dmnRuntimeFromDMNXML(String mString) {
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

    private static class ErrorsAndDMNModelAsSerialized {

        final List<Message> messages;
        final Definitions definitions;

        public ErrorsAndDMNModelAsSerialized(List<Message> messages, Definitions definitions) {
            this.messages = Collections.unmodifiableList(messages);
            this.definitions = definitions;
        }

        public boolean hasErrors() {
            return messages.stream().filter(m -> m.getLevel().equals(Level.ERROR)).count() > 0;
        }
    }

    private ErrorsAndDMNModelAsSerialized roundTripUnmarshalMarshalThenUnmarshalDMNexpectingErrors(InputStream dmnXmlInputStream) throws IOException {
        String xml = null;
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(dmnXmlInputStream))) {
            xml = buffer.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("test utility method roundTripUnmarshalMarshalThenUnmarshalDMN failed to read XML content.", e);
        }
        LOG.debug("ORIGINAL xml:\n{}\n", xml);
        final List<Message> messages0 = kieBuilderMessagesUsingDMNXML(xml);
        assertTrue("The DMN XML content did NOT result in compilation errors and this test method expected errors to be detected. If this was intentional use test method roundTripUnmarshalMarshalThenUnmarshalDMN",
                   messages0.stream().filter(m -> m.getLevel().equals(Message.Level.ERROR)).count() > 0);

        DMNMarshaller m = getDMNMarshaller();

        // first unmarshal from DMN XML to Stunner DMN Graph
        @SuppressWarnings("unchecked")
        Graph<?, Node<?, ?>> g = m.unmarshall(null,
                                              new ReaderInputStream(new StringReader(xml)));

        // round trip to Stunner DMN Graph back to DMN XML
        DiagramImpl diagram = new DiagramImpl("",
                                              null);
        diagram.setGraph(g);

        String mString = m.marshall(diagram);
        LOG.debug("MARSHALLED ROUNDTRIP RESULTING xml:\n{}\n", mString);

        // now unmarshal once more, from the marshalled just done above, into a DMNRuntime
        final List<Message> result = kieBuilderMessagesUsingDMNXML(mString);
        assertTrue("The DMN XML content did NOT result in compilation errors and this test method expected errors to be detected. If this was intentional use test method roundTripUnmarshalMarshalThenUnmarshalDMN",
                   messages0.stream().filter(msg -> msg.getLevel().equals(Message.Level.ERROR)).count() > 0);

        Definitions definitions = DMNMarshallerFactory.newDefaultMarshaller().unmarshal(mString);
        return new ErrorsAndDMNModelAsSerialized(result, definitions);
    }

    private static List<Message> kieBuilderMessagesUsingDMNXML(String mString) {
        final KieServices ks = KieServices.Factory.get();
        String uuid = UUID.uuid(8);
        final KieContainer kieContainer = DMNRuntimeUtil.getKieContainerIgnoringErrors(ks.newReleaseId("org.kie", uuid, "1.0"),
                                                                                       ks.getResources().newByteArrayResource(mString.getBytes()).setTargetPath("src/main/resources/" + uuid + ".dmn"));
        Results verify = kieContainer.verify();
        List<Message> kie_messages = verify.getMessages();
        LOG.debug("{}", kie_messages);
        return kie_messages;
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
        final DMNMarshaller m = getDMNMarshaller();

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
            assertEquals(FunctionDefinition.Kind.JAVA, wbFunction.getKind());
        });

        final DMNRuntime runtime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/DROOLS-2372.dmn"));
        final DMNModel dmnModel = runtime.getModels().get(0);

        final BusinessKnowledgeModelNode bkmNode = dmnModel.getBusinessKnowledgeModels().iterator().next();
        final org.kie.dmn.model.api.FunctionDefinition dmnFunction = bkmNode.getBusinessKnowledModel().getEncapsulatedLogic();
        assertEquals(FunctionKind.JAVA, dmnFunction.getKind());
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
    @SuppressWarnings("unchecked")
    public void test_wrong_context() throws IOException {
        // DROOLS-2217
        final ErrorsAndDMNModelAsSerialized result = roundTripUnmarshalMarshalThenUnmarshalDMNexpectingErrors(this.getClass().getResourceAsStream("/wrong_context.dmn"));

        // although the DMN file is schema valid but is not a valid-DMN (a context-entry value is a literal expression missing text, which is null)
        // DROOLS-3152: once roundtripped through the Stunner marshaller it will receive an empty text. (empty expression, but a LiteralExpression with an empty text child xml element)
        // this will still naturally throw some error because unable to FEEL-parse/compile an empty expression.
        assertTrue(result.hasErrors());

        // identify the error message for context-entry "ciao":
        DMNMessage m0 = (DMNMessage) result.messages.get(0);
        assertTrue("expected a message identifying the problem on a context entry for 'ciao'",
                   m0.getMessage().startsWith("Error compiling FEEL expression '' for name ")); // DROOLS-3152 please notice FEEL reporting indeed an empty expression.

        org.kie.dmn.model.api.Decision d0 = (org.kie.dmn.model.api.Decision) result.definitions.getDrgElement().stream().filter(d -> d.getId().equals("_653b3426-933a-4050-9568-ab2a66b43c36")).findFirst().get();
        // the identified DMN Decision is composed of a DMN Context where the first context-entry value is a literal expression missing text (text is null).
        org.kie.dmn.model.api.Context d0c = (org.kie.dmn.model.api.Context) d0.getExpression();
        org.kie.dmn.model.api.Expression contextEntryValue = d0c.getContextEntry().get(0).getExpression();
        assertTrue(contextEntryValue instanceof org.kie.dmn.model.api.LiteralExpression);
        assertEquals("", ((org.kie.dmn.model.api.LiteralExpression) contextEntryValue).getText()); // DROOLS-3152

        // -- Stunner side.
        DMNMarshaller m = getDMNMarshaller();
        Graph<?, ?> g = m.unmarshall(null, this.getClass().getResourceAsStream("/wrong_context.dmn"));

        Node<?, ?> decisionNode = g.getNode("_653b3426-933a-4050-9568-ab2a66b43c36");
        assertNodeContentDefinitionIs(decisionNode, Decision.class);
        View<Decision> view = ((View<Decision>) decisionNode.getContent());

        // the identified DMN Decision is composed of a DMN Context where the first context-entry has missing Expression.
        Context expression = (Context) view.getDefinition().getExpression();
        assertNotNull(expression.getContextEntry().get(0).getExpression()); // DROOLS-3116 empty Literal Expression is preserved
        assertEquals(LiteralExpression.class, expression.getContextEntry().get(0).getExpression().getClass());
        LiteralExpression le = (LiteralExpression) expression.getContextEntry().get(0).getExpression();
        assertEquals("", le.getText().getValue()); // DROOLS-3152
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_wrong_decision() throws IOException {
        // DROOLS-3116 empty Literal Expression to be preserved
        final ErrorsAndDMNModelAsSerialized result = roundTripUnmarshalMarshalThenUnmarshalDMNexpectingErrors(this.getClass().getResourceAsStream("/wrong_decision.dmn"));

        // although the DMN file is schema valid but is not a valid-DMN (a context-entry value is a literal expression missing text, which is null)
        // DROOLS-3152: once roundtripped through the Stunner marshaller it will receive an empty text. (empty expression, but a LiteralExpression with an empty text child xml element)
        // this will still naturally throw some error because unable to FEEL-parse/compile an empty epression.
        assertTrue(result.hasErrors());

        // identify the error message for the Decision with a Literal Expression decision logic missing the actual expression text.
        DMNMessage m0 = (DMNMessage) result.messages.get(0);
        assertTrue("expected a message identifying the problem on the literalExpression of 'my decision'",
                   m0.getSourceId().equals("_36dd163c-4862-4308-92bf-40a998b24e39"));

        org.kie.dmn.model.api.Decision d0 = (org.kie.dmn.model.api.Decision) result.definitions.getDrgElement().stream().filter(d -> d.getId().equals("_cce32679-9395-444d-a4bf-96af8ee727a0")).findFirst().get();
        // the identified DMN Decision is composed a literal expression missing text (text is null).
        org.kie.dmn.model.api.Expression d0le = d0.getExpression();
        assertTrue(d0le instanceof org.kie.dmn.model.api.LiteralExpression);
        assertEquals("", ((org.kie.dmn.model.api.LiteralExpression) d0le).getText()); // DROOLS-3152

        // -- Stunner side.
        DMNMarshaller m = getDMNMarshaller();
        Graph<?, ?> g = m.unmarshall(null, this.getClass().getResourceAsStream("/wrong_decision.dmn"));

        Node<?, ?> decisionNode = g.getNode("_cce32679-9395-444d-a4bf-96af8ee727a0");
        assertNodeContentDefinitionIs(decisionNode, Decision.class);
        View<Decision> view = ((View<Decision>) decisionNode.getContent());

        // the identified DMN Decision is composed a literal expression missing text (text is null).
        Expression expression = view.getDefinition().getExpression();
        assertNotNull(expression); // DROOLS-3116 empty Literal Expression is preserved
        assertEquals(LiteralExpression.class, expression.getClass());
        LiteralExpression le = (LiteralExpression) expression;
        assertEquals("", le.getText().getValue()); // DROOLS-3152
    }

    @Test
    public void testOtherElements() throws IOException, XPathExpressionException {
        String original = new Scanner(this.getClass().getResourceAsStream("/dummy.dmn")).useDelimiter("\\A").next();
        DMNMarshaller marshaller = getDMNMarshaller();
        DiagramImpl diagram = new DiagramImpl("", null);
        diagram.setGraph(marshaller.unmarshall(null, getClass().getResourceAsStream("/dummy.dmn")));
        String roundtripped = marshaller.marshall(diagram);
        LOG.debug(roundtripped);
        XPath xpathOriginal = namespaceAwareXPath(
            new AbstractMap.SimpleEntry<>("semantic", "http://www.omg.org/spec/DMN/20151101/dmn.xsd"),
            new AbstractMap.SimpleEntry<>("drools", "http://www.drools.org/kie/dmn/1.1"));
        XPath xpathRountripped = namespaceAwareXPath(
            new AbstractMap.SimpleEntry<>("semantic", "http://www.omg.org/spec/DMN/20180521/MODEL/"),
            new AbstractMap.SimpleEntry<>("drools", "http://www.drools.org/kie/dmn/1.2")
        );
        assertXPathEquals(xpathOriginal, xpathRountripped, "boolean(/semantic:definitions/semantic:extensionElements)", original, roundtripped);

        assertXPathEquals(xpathOriginal, xpathRountripped, "boolean(/semantic:definitions/semantic:import)", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "/semantic:definitions/semantic:import/@namespace", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "/semantic:definitions/semantic:import/@importType", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "/semantic:definitions/semantic:import/@locationURI", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "/semantic:definitions/semantic:import/@drools:name", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "/semantic:definitions/semantic:import/@drools:modelName", original, roundtripped);

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

        assertXPathEquals(xpathOriginal, xpathRountripped, "boolean(/semantic:definitions/semantic:knowledgeSource)", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "/semantic:definitions/semantic:knowledgeSource/@name", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "/semantic:definitions/semantic:knowledgeSource/@id", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "boolean(/semantic:definitions/semantic:knowledgeSource/semantic:authorityRequirement)", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "boolean(/semantic:definitions/semantic:knowledgeSource/semantic:requiredInput)", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "/semantic:definitions/semantic:knowledgeSource/semantic:requiredInput/@href", original, roundtripped);

        assertXPathEquals(xpathOriginal, xpathRountripped, "boolean(/semantic:definitions/semantic:inputData)", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "/semantic:definitions/semantic:inputData/@id", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "/semantic:definitions/semantic:inputData/@name", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "boolean(/semantic:definitions/semantic:inputData/semantic:variable)", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "/semantic:definitions/semantic:inputData/semantic:variable/@id", original, roundtripped);
        assertXPathEquals(xpathOriginal, xpathRountripped, "/semantic:definitions/semantic:inputData/semantic:variable/@name", original, roundtripped);

        // DMN v1.2
        String inputDataVariableTypeRefOriginal = xpathOriginal.compile("/semantic:definitions/semantic:inputData/semantic:variable/@typeRef").evaluate(new InputSource(new StringReader(original)));
        String inputDataVariableTypeRefRoundtripped = xpathRountripped.compile("/semantic:definitions/semantic:inputData/semantic:variable/@typeRef").evaluate(new InputSource(new StringReader(roundtripped)));
        assertEquals("feel:number", inputDataVariableTypeRefOriginal);
        assertEquals("number", inputDataVariableTypeRefRoundtripped);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testContextEntryDataType() throws Exception {
        final DMNMarshaller marshaller = getDMNMarshaller();

        final Context context = new Context();
        context.setTypeRef(BuiltInType.DATE_TIME.asQName());

        final ContextEntry contextEntry = new ContextEntry();
        final LiteralExpression literalExpression = new LiteralExpression();
        literalExpression.setTypeRef(BuiltInType.BOOLEAN.asQName());
        literalExpression.getText().setValue("feel");
        contextEntry.setExpression(literalExpression);
        context.getContextEntry().add(contextEntry);

        final Diagram<Graph, Metadata> mockedDiagram = newDiagramDecisionWithExpression(context);

        final String marshalledSource = marshaller.marshall(mockedDiagram);

        final Graph<?, Node<View, ?>> unmarshalledGraph = marshaller.unmarshall(null,
                                                                                new StringInputStream(marshalledSource));
        assertThat(unmarshalledGraph.nodes()).hasSize(2);

        checkDecisionExpression(unmarshalledGraph, context);
    }

    @Test
    public void testDefaultObjectsAreNotCreated() throws IOException {
        final DMNRuntime dmnRuntime = roundTripUnmarshalMarshalThenUnmarshalDMN(this.getClass().getResourceAsStream("/DROOLS-2941.dmn"));
        final List<DMNMessage> dmn_messages = dmnRuntime.getModels().get(0).getMessages();
        assertThat(dmn_messages).isEmpty();

        assertThat(dmnRuntime.getModels()).hasSize(1);
        final DMNModel dmnModel = dmnRuntime.getModels().get(0);
        final DMNContext context = dmnRuntime.newContext();
        final DMNResult result = dmnRuntime.evaluateAll(dmnModel, context);
        assertThat(result.getDecisionResultByName("A Vowel").getResult()).isEqualTo("a");
    }

    /**
     * DROOLS-3184: If the "connection source/target location is null" assume it's the centre of the shape.
     * [source/target location is null] If the connection was created from the Toolbox (i.e. add a InputData and then the Decision from it using the Decision toolbox icon).
     * <p>
     * This test re-create by hard-code the behavior of the Stunner framework "Toolbox" by instrumenting API calls to achieve the same behavior.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testStunnerConstellationButtonCausingPoint2DbeingNull() throws IOException {
        final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer = (uuid, hcw) -> {/*NOP*/};

        Diagram diagram = applicationFactoryManager.newDiagram("testDiagram",
                                                               BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class),
                                                               null);
        Graph g = diagram.getGraph();
        Node diagramRoot = DMNMarshaller.findDMNDiagramRoot(g);
        testAugmentWithNSPrefixes(((DMNDiagram) ((View<?>) diagramRoot.getContent()).getDefinition()).getDefinitions());

        org.kie.dmn.model.api.InputData dmnInputData = new TInputData();
        dmnInputData.setId("inputDataID");
        dmnInputData.setName(dmnInputData.getId());
        Node inputDataNode = new InputDataConverter(applicationFactoryManager).nodeFromDMN(dmnInputData,
                                                                                           hasComponentWidthsConsumer);
        org.kie.dmn.model.api.Decision dmnDecision = new TDecision();
        dmnDecision.setId("decisionID");
        dmnDecision.setName(dmnDecision.getId());
        Node decisionNode = new DecisionConverter(applicationFactoryManager).nodeFromDMN(dmnDecision,
                                                                                         hasComponentWidthsConsumer);
        g.addNode(inputDataNode);
        g.addNode(decisionNode);
        View content = (View) decisionNode.getContent();
        content.setBounds(org.kie.workbench.common.stunner.core.graph.content.Bounds.create(200, 200, 300, 250));
        final String irID = "irID";
        Edge myEdge = applicationFactoryManager.newElement(irID, org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement.class).asEdge();
        myEdge.setSourceNode(inputDataNode);
        myEdge.setTargetNode(decisionNode);
        inputDataNode.getOutEdges().add(myEdge);
        decisionNode.getInEdges().add(myEdge);
        ViewConnector connectionContent = (ViewConnector) myEdge.getContent();
        // DROOLS-3184: If the "connection source/target location is null" assume it's the centre of the shape.
        // keep Stunner behavior of constellation button
        connectionContent.setSourceConnection(MagnetConnection.Builder.atCenter(inputDataNode).setLocation(null).setAuto(true));
        connectionContent.setTargetConnection(MagnetConnection.Builder.atCenter(decisionNode).setLocation(null).setAuto(true));

        DMNMarshaller.connectRootWithChild(diagramRoot, inputDataNode);
        DMNMarshaller.connectRootWithChild(diagramRoot, decisionNode);

        DMNMarshaller m = getDMNMarshaller();
        String output = m.marshall(diagram);
        LOG.debug(output);

        Definitions dmnDefinitions = DMNMarshallerFactory.newDefaultMarshaller().unmarshal(output);
        DMNEdge dmndiEdge = findEdgeByDMNI(dmnDefinitions.getDMNDI().getDMNDiagram().get(0), irID);
        assertThat(dmndiEdge.getWaypoint()).hasSize(2);
        Point wpSource = dmndiEdge.getWaypoint().get(0);
        assertThat(wpSource.getX()).isEqualByComparingTo(50d);
        assertThat(wpSource.getY()).isEqualByComparingTo(25d);
        Point wpTarget = dmndiEdge.getWaypoint().get(1);
        assertThat(wpTarget.getX()).isEqualByComparingTo(250d);
        assertThat(wpTarget.getY()).isEqualByComparingTo(225d);
    }

    /**
     * DROOLS-2569 [DMN Designer] Marshalling of magnet positions -- Association DMN Edge DMNDI serialization.
     * This test re-create by hard-code the graph to simulate the behavior of the Stunner framework programmatically.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAssociationEdgeDMNDI() throws IOException {
        final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer = (uuid, hcw) -> {/*NOP*/};

        Diagram diagram = applicationFactoryManager.newDiagram("testDiagram",
                                                               BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class),
                                                               null);
        Graph g = diagram.getGraph();
        Node diagramRoot = DMNMarshaller.findDMNDiagramRoot(g);
        testAugmentWithNSPrefixes(((DMNDiagram) ((View<?>) diagramRoot.getContent()).getDefinition()).getDefinitions());

        org.kie.dmn.model.api.InputData dmnInputData = new TInputData();
        dmnInputData.setId("inputDataID");
        dmnInputData.setName(dmnInputData.getId());
        Node inputDataNode = new InputDataConverter(applicationFactoryManager).nodeFromDMN(dmnInputData,
                                                                                           hasComponentWidthsConsumer);
        org.kie.dmn.model.api.TextAnnotation dmnTextAnnotation = new TTextAnnotation();
        dmnTextAnnotation.setId("textAnnotationID");
        Node textAnnotationNode = new TextAnnotationConverter(applicationFactoryManager).nodeFromDMN(dmnTextAnnotation,
                                                                                                     hasComponentWidthsConsumer);
        g.addNode(inputDataNode);
        g.addNode(textAnnotationNode);
        View content = (View) textAnnotationNode.getContent();
        content.setBounds(org.kie.workbench.common.stunner.core.graph.content.Bounds.create(200, 200, 300, 250));
        final String edgeID = "edgeID";
        final String associationID = "associationID";
        Edge myEdge = applicationFactoryManager.newElement(edgeID, org.kie.workbench.common.dmn.api.definition.v1_1.Association.class).asEdge();
        final View<?> edgeView = (View<?>) myEdge.getContent();
        ((Association) edgeView.getDefinition()).setId(new Id(associationID));
        myEdge.setSourceNode(inputDataNode);
        myEdge.setTargetNode(textAnnotationNode);
        inputDataNode.getOutEdges().add(myEdge);
        textAnnotationNode.getInEdges().add(myEdge);
        ViewConnector connectionContent = (ViewConnector) myEdge.getContent();
        connectionContent.setSourceConnection(MagnetConnection.Builder.atCenter(inputDataNode));
        connectionContent.setTargetConnection(MagnetConnection.Builder.atCenter(textAnnotationNode));

        DMNMarshaller.connectRootWithChild(diagramRoot, inputDataNode);
        DMNMarshaller.connectRootWithChild(diagramRoot, textAnnotationNode);

        DMNMarshaller m = getDMNMarshaller();
        String output = m.marshall(diagram);
        LOG.debug(output);

        Definitions dmnDefinitions = DMNMarshallerFactory.newDefaultMarshaller().unmarshal(output);
        assertThat(dmnDefinitions.getDMNDI().getDMNDiagram().get(0).getDMNDiagramElement().stream().filter(DMNEdge.class::isInstance).count()).isEqualTo(1);
        DMNEdge dmndiEdge = findEdgeByDMNI(dmnDefinitions.getDMNDI().getDMNDiagram().get(0), associationID);
        assertThat(dmndiEdge.getWaypoint()).hasSize(2);
    }

    @Test
    public void test_ExpressionComponentWidthPersistence() throws IOException {
        roundTripUnmarshalThenMarshalUnmarshal(this.getClass().getResourceAsStream("/DROOLS-2262.dmn"),
                                               this::checkComponentWidths);
    }

    @Test
    public void testGetImportedDrgElementsByShape() {

        final DMNMarshaller marshaller = spy(getDMNMarshaller());
        final List<org.kie.dmn.model.api.DRGElement> importedDRGElements = mock(List.class);
        final Map<Import, org.kie.dmn.model.api.Definitions> importDefinitions = mock(Map.class);
        final org.kie.dmn.model.api.Definitions dmnXml = mock(org.kie.dmn.model.api.Definitions.class);

        final org.kie.dmn.model.api.DRGElement ref1 = mock(org.kie.dmn.model.api.DRGElement.class);
        final org.kie.dmn.model.api.DRGElement ref2 = mock(org.kie.dmn.model.api.DRGElement.class);
        final org.kie.dmn.model.api.DRGElement ref3 = mock(org.kie.dmn.model.api.DRGElement.class);

        final List<DMNShape> dmnShapes = new ArrayList<>();
        final DMNShape shape1 = mock(DMNShape.class);
        final DMNShape shape2 = mock(DMNShape.class);
        final DMNShape shape3 = mock(DMNShape.class);
        dmnShapes.add(shape1);
        dmnShapes.add(shape2);
        dmnShapes.add(shape3);

        doReturn("REF1").when(marshaller).getDmnElementRef(shape1);
        doReturn("REF2").when(marshaller).getDmnElementRef(shape2);
        doReturn("REF3").when(marshaller).getDmnElementRef(shape3);

        when(dmnMarshallerImportsHelper.getImportedDRGElements(importDefinitions)).thenReturn(importedDRGElements);

        doNothing().when(marshaller).updateIDsWithAlias(any(), any());
        doReturn(Optional.of(ref1)).when(marshaller).getReference(importedDRGElements, "REF1");
        doReturn(Optional.of(ref2)).when(marshaller).getReference(importedDRGElements, "REF2");
        doReturn(Optional.of(ref3)).when(marshaller).getReference(importedDRGElements, "REF3");

        final List<DRGElement> actual = marshaller.getImportedDrgElementsByShape(dmnShapes, importDefinitions, dmnXml);

        assertEquals(ref1, actual.get(0));
        assertEquals(ref2, actual.get(1));
        assertEquals(ref3, actual.get(2));
    }

    @Test
    public void testGetDmnElementRef() {

        final DMNMarshaller marshaller = spy(getDMNMarshaller());
        final String expected = "localPart";
        final DMNShape shape = mock(DMNShape.class);
        final javax.xml.namespace.QName ref = mock(javax.xml.namespace.QName.class);
        when(ref.getLocalPart()).thenReturn(expected);
        when(shape.getDmnElementRef()).thenReturn(ref);

        final String actual = marshaller.getDmnElementRef(shape);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetUniqueDMNShapes() {

        final DMNMarshaller marshaller = spy(getDMNMarshaller());
        final org.kie.dmn.model.api.dmndi.DMNDiagram diagram = mock(org.kie.dmn.model.api.dmndi.DMNDiagram.class);
        final List<DiagramElement> elements = new ArrayList<>();

        final DMNShape unique1 = mock(DMNShape.class);
        when(unique1.getId()).thenReturn("unique1");

        final DMNShape unique2 = mock(DMNShape.class);
        when(unique2.getId()).thenReturn("unique2");

        final DMNShape duplicate1 = mock(DMNShape.class);
        when(duplicate1.getId()).thenReturn("duplicate");

        final DMNShape duplicate2 = mock(DMNShape.class);
        when(duplicate2.getId()).thenReturn("duplicate");

        elements.add(unique1);
        elements.add(unique2);
        elements.add(duplicate1);
        elements.add(duplicate2);

        when(diagram.getDMNDiagramElement()).thenReturn(elements);

        final List<DMNShape> actual = marshaller.getUniqueDMNShapes(diagram);

        assertEquals(3, actual.size());
        assertTrue(actual.contains(unique1));
        assertTrue(actual.contains(unique2));
        assertTrue(actual.contains(duplicate1) || actual.contains(duplicate2));
    }

    @Test
    public void testSetAllowOnlyVisualChangeToTrue() {
        testSetAllowOnlyVisualChange(true);
    }

    @Test
    public void testSetAllowOnlyVisualChangeToFalse() {
        testSetAllowOnlyVisualChange(false);
    }

    private void testSetAllowOnlyVisualChange(final boolean expected) {

        final DMNMarshaller marshaller = spy(getDMNMarshaller());
        final List<org.kie.dmn.model.api.DRGElement> importedDrgElements = mock(List.class);
        final Node node = mock(Node.class);
        final org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement element = mock(org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement.class);
        doReturn(Optional.of(element)).when(marshaller).getDRGElement(node);
        doReturn(expected).when(marshaller).isImportedDRGElement(importedDrgElements, element);

        marshaller.setAllowOnlyVisualChange(importedDrgElements, node);

        verify(element).setAllowOnlyVisualChange(expected);
    }

    @Test
    public void testIsImportedDRGElementWithDmnDRGElement() {

        final DMNMarshaller marshaller = spy(getDMNMarshaller());
        final List<org.kie.dmn.model.api.DRGElement> importedDrgElements = new ArrayList<>();

        final org.kie.dmn.model.api.DRGElement imported = mock(org.kie.dmn.model.api.DRGElement.class);
        when(imported.getId()).thenReturn("id");
        importedDrgElements.add(imported);

        final org.kie.dmn.model.api.DRGElement drgElement = mock(org.kie.dmn.model.api.DRGElement.class);
        when(drgElement.getId()).thenReturn("id");

        final boolean actual = marshaller.isImportedDRGElement(importedDrgElements, drgElement);

        assertTrue(actual);
    }

    @Test
    public void testIsImportedDRGElementWithWbDRGElement() {

        final DMNMarshaller marshaller = spy(getDMNMarshaller());
        final List<org.kie.dmn.model.api.DRGElement> importedDrgElements = new ArrayList<>();

        final org.kie.dmn.model.api.DRGElement imported = mock(org.kie.dmn.model.api.DRGElement.class);
        when(imported.getId()).thenReturn("id");
        importedDrgElements.add(imported);

        final org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement drgElement = mock(org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement.class);
        final Id id = mock(Id.class);
        when(id.getValue()).thenReturn("id");
        when(drgElement.getId()).thenReturn(id);

        final boolean actual = marshaller.isImportedDRGElement(importedDrgElements, drgElement);

        assertTrue(actual);
    }

    @Test
    public void testUpdateIDsWithAlias() {

        final DMNMarshaller marshaller = getDMNMarshaller();
        final HashMap<String, String> indexByUri = new HashMap<>();
        final String namespace1 = "https://kiegroup.org/dmn/_red";
        final String namespace2 = "https://kiegroup.org/dmn/_blue";
        final String namespace3 = "https://kiegroup.org/dmn/_yellow";
        final String missingNamespace = "missing_namespace";

        final String someWrongAlias = "some wrong alias";

        final String include1 = "include1";
        final String include2 = "include2";
        final String include3 = "include3";

        final String id1 = "id1";
        final String id2 = "id2";
        final String id3 = "id3";
        final String id4 = "id4";

        indexByUri.put(namespace1, include1);
        indexByUri.put(namespace2, include2);
        indexByUri.put(namespace3, include3);

        final List<org.kie.dmn.model.api.DRGElement> importedDrgElements = new ArrayList<>();
        final DRGElement element1 = createDRGElementWithNamespaceAndId(namespace1, someWrongAlias + ":" + id1);
        importedDrgElements.add(element1);

        final DRGElement element2 = createDRGElementWithNamespaceAndId(namespace2, someWrongAlias + ":" + id2);
        importedDrgElements.add(element2);

        final DRGElement element3 = createDRGElementWithNamespaceAndId(namespace3, someWrongAlias + ":" + id3);
        importedDrgElements.add(element3);

        final DRGElement element4 = createDRGElementWithNamespaceAndId(missingNamespace, id4);
        importedDrgElements.add(element4);

        marshaller.updateIDsWithAlias(indexByUri, importedDrgElements);

        verify(element1).setId(include1 + ":" + id1);
        verify(element2).setId(include2 + ":" + id2);
        verify(element3).setId(include3 + ":" + id3);

        verify(element4, never()).setId(any());
    }

    private org.kie.dmn.model.api.DRGElement createDRGElementWithNamespaceAndId(final String namespace,
                                                                                final String id) {

        final org.kie.dmn.model.api.DRGElement drgElement = mock(DRGElement.class);
        final Map<QName, String> additionalAttributes = new HashMap<>();

        additionalAttributes.put(new QName("Namespace"), namespace);

        when(drgElement.getAdditionalAttributes()).thenReturn(additionalAttributes);
        when(drgElement.getId()).thenReturn(id);

        return drgElement;
    }

    @Test
    public void testChangeAliasForImportedElement() {

        final DMNMarshaller marshaller = getDMNMarshaller();
        final org.kie.dmn.model.api.DRGElement drgElement = mock(org.kie.dmn.model.api.DRGElement.class);
        final String alias = "include1";
        final String id = "_01234567";

        when(drgElement.getId()).thenReturn("some another alias:" + id);

        marshaller.changeAlias(alias, drgElement);

        verify(drgElement).setId(alias + ":" + id);
    }

    @Test
    public void testChangeAliasForLocalElement() {

        final DMNMarshaller marshaller = getDMNMarshaller();
        final org.kie.dmn.model.api.DRGElement drgElement = mock(org.kie.dmn.model.api.DRGElement.class);
        final String alias = "include1";
        final String id = "_01234567";

        when(drgElement.getId()).thenReturn(id);

        marshaller.changeAlias(alias, drgElement);

        verify(drgElement, never()).setId(any());
    }

    @Test
    public void testLoadImportedItemDefinitions() {

        final org.kie.workbench.common.dmn.api.definition.v1_1.Definitions definitions = mock(org.kie.workbench.common.dmn.api.definition.v1_1.Definitions.class);
        final org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition itemDefinition1 = mock(org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition.class);
        final org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition itemDefinition2 = mock(org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition.class);
        final List<org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition> expectedItemDefinitions = asList(itemDefinition1, itemDefinition2);
        final List<org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition> actualItemDefinitions = new ArrayList<>();
        final Map<Import, org.kie.dmn.model.api.Definitions> importDefinitions = new HashMap<>();
        final DMNMarshaller dmnMarshaller = spy(getDMNMarshaller());

        doReturn(expectedItemDefinitions).when(dmnMarshaller).getWbImportedItemDefinitions(importDefinitions);
        when(definitions.getItemDefinition()).thenReturn(actualItemDefinitions);

        dmnMarshaller.loadImportedItemDefinitions(definitions, importDefinitions);

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }

    @Test
    public void testCleanImportedItemDefinitions() {

        final org.kie.workbench.common.dmn.api.definition.v1_1.Definitions definitions = mock(org.kie.workbench.common.dmn.api.definition.v1_1.Definitions.class);
        final org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition itemDefinition1 = mock(org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition.class);
        final org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition itemDefinition2 = mock(org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition.class);
        final org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition itemDefinition3 = mock(org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition.class);
        final List<org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition> actualItemDefinitions = new ArrayList<>(asList(itemDefinition1, itemDefinition2, itemDefinition3));
        final List<org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition> expectedItemDefinitions = asList(itemDefinition1, itemDefinition3);
        final DMNMarshaller dmnMarshaller = getDMNMarshaller();

        when(itemDefinition1.isAllowOnlyVisualChange()).thenReturn(false);
        when(itemDefinition2.isAllowOnlyVisualChange()).thenReturn(true);
        when(itemDefinition3.isAllowOnlyVisualChange()).thenReturn(false);
        when(definitions.getItemDefinition()).thenReturn(actualItemDefinitions);

        dmnMarshaller.cleanImportedItemDefinitions(definitions);

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }

    @Test
    public void testGetWbImportedItemDefinitions() {

        final org.kie.dmn.model.api.ItemDefinition itemDefinition1 = makeItemDefinition("model1.tUUID");
        final org.kie.dmn.model.api.ItemDefinition itemDefinition2 = makeItemDefinition("model1.tPerson");
        final org.kie.dmn.model.api.ItemDefinition itemDefinition3 = makeItemDefinition("model2.tNum");
        final Map<Import, org.kie.dmn.model.api.Definitions> importDefinitions = new HashMap<>();

        when(dmnMarshallerImportsHelper.getImportedItemDefinitions(importDefinitions)).thenReturn(asList(itemDefinition1, itemDefinition2, itemDefinition3));

        final List<ItemDefinition> actualItemDefinitions = getDMNMarshaller().getWbImportedItemDefinitions(importDefinitions);

        assertEquals(3, actualItemDefinitions.size());
        assertEquals("model1.tUUID", actualItemDefinitions.get(0).getName().getValue());
        assertEquals("model1.tPerson", actualItemDefinitions.get(1).getName().getValue());
        assertEquals("model2.tNum", actualItemDefinitions.get(2).getName().getValue());
    }

    private org.kie.dmn.model.api.ItemDefinition makeItemDefinition(final String name) {
        final org.kie.dmn.model.api.ItemDefinition itemDefinition = new TItemDefinition();
        itemDefinition.setName(name);
        return itemDefinition;
    }

    @Test
    public void testConnectorRightToLeft() throws Exception {
        final org.kie.workbench.common.stunner.core.graph.content.Bounds bounds = org.kie.workbench.common.stunner.core.graph.content.Bounds.create(0, 0, 100, 50);
        final String decisionNode1UUID = org.kie.workbench.common.stunner.core.util.UUID.uuid();
        final String decisionNode2UUID = org.kie.workbench.common.stunner.core.util.UUID.uuid();
        final String edgeUUID = org.kie.workbench.common.stunner.core.util.UUID.uuid();

        final ViewConnector edgeView = marshallAndUnMarshallConnectors(bounds,
                                                                       decisionNode1UUID,
                                                                       decisionNode2UUID,
                                                                       edgeUUID,
                                                                       (sc) -> {
                                                                           when(sc.getMagnetIndex()).thenReturn(OptionalInt.of(MagnetConnection.MAGNET_RIGHT));
                                                                           when(sc.getLocation()).thenReturn(new Point2D(bounds.getWidth(), bounds.getHeight() / 2));
                                                                       },
                                                                       (tc) -> {
                                                                           when(tc.getMagnetIndex()).thenReturn(OptionalInt.of(MagnetConnection.MAGNET_LEFT));
                                                                           when(tc.getLocation()).thenReturn(new Point2D(0, bounds.getHeight() / 2));
                                                                       });

        final MagnetConnection sourceConnection = (MagnetConnection) edgeView.getSourceConnection().get();
        assertEquals(bounds.getWidth(), sourceConnection.getLocation().getX(), 0.0);
        assertEquals(bounds.getHeight() / 2, sourceConnection.getLocation().getY(), 0.0);
        assertFalse(sourceConnection.getMagnetIndex().isPresent());
        assertTrue(sourceConnection.isAuto());

        final MagnetConnection targetConnection = (MagnetConnection) edgeView.getTargetConnection().get();
        assertEquals(0, targetConnection.getLocation().getX(), 0.0);
        assertEquals(bounds.getHeight() / 2, targetConnection.getLocation().getY(), 0.0);
        assertFalse(targetConnection.getMagnetIndex().isPresent());
        assertTrue(targetConnection.isAuto());
    }

    @Test
    public void testConnectorCentreToCentre() throws Exception {
        final org.kie.workbench.common.stunner.core.graph.content.Bounds bounds = org.kie.workbench.common.stunner.core.graph.content.Bounds.create(0, 0, 100, 50);
        final String decisionNode1UUID = org.kie.workbench.common.stunner.core.util.UUID.uuid();
        final String decisionNode2UUID = org.kie.workbench.common.stunner.core.util.UUID.uuid();
        final String edgeUUID = org.kie.workbench.common.stunner.core.util.UUID.uuid();

        final ViewConnector edgeView = marshallAndUnMarshallConnectors(bounds,
                                                                       decisionNode1UUID,
                                                                       decisionNode2UUID,
                                                                       edgeUUID,
                                                                       (sc) -> {/*NOP*/},
                                                                       (tc) -> {/*NOP*/});

        final MagnetConnection sourceConnection = (MagnetConnection) edgeView.getSourceConnection().get();
        assertEquals(bounds.getWidth() / 2, sourceConnection.getLocation().getX(), 0.0);
        assertEquals(bounds.getHeight() / 2, sourceConnection.getLocation().getY(), 0.0);
        assertTrue(sourceConnection.getMagnetIndex().isPresent());
        assertEquals(MagnetConnection.MAGNET_CENTER, sourceConnection.getMagnetIndex().getAsInt());
        assertFalse(sourceConnection.isAuto());

        final MagnetConnection targetConnection = (MagnetConnection) edgeView.getTargetConnection().get();
        assertEquals(bounds.getWidth() / 2, targetConnection.getLocation().getX(), 0.0);
        assertEquals(bounds.getHeight() / 2, targetConnection.getLocation().getY(), 0.0);
        assertTrue(targetConnection.getMagnetIndex().isPresent());
        assertEquals(MagnetConnection.MAGNET_CENTER, targetConnection.getMagnetIndex().getAsInt());
        assertFalse(targetConnection.isAuto());
    }

    @SuppressWarnings("unchecked")
    private ViewConnector marshallAndUnMarshallConnectors(final org.kie.workbench.common.stunner.core.graph.content.Bounds bounds,
                                                          final String decisionNode1UUID,
                                                          final String decisionNode2UUID,
                                                          final String edgeUUID,
                                                          final Consumer<MagnetConnection> sourceMagnetConsumer,
                                                          final Consumer<MagnetConnection> targetMagnetConsumer) throws Exception {
        final DMNMarshaller marshaller = getDMNMarshaller();

        final Diagram<Graph, Metadata> mockedDiagram = connectTwoNodes(bounds,
                                                                       decisionNode1UUID,
                                                                       decisionNode2UUID,
                                                                       edgeUUID,
                                                                       sourceMagnetConsumer,
                                                                       targetMagnetConsumer);

        final String marshalledSource = marshaller.marshall(mockedDiagram);

        final Graph<?, Node<View, ?>> unmarshalledGraph = marshaller.unmarshall(null, new StringInputStream(marshalledSource));

        assertNotNull(unmarshalledGraph);

        final Node<?, ?> decision1Node = unmarshalledGraph.getNode(decisionNode1UUID);
        final Node<?, ?> decision2Node = unmarshalledGraph.getNode(decisionNode2UUID);
        assertNotNull(decision1Node);
        assertNotNull(decision2Node);
        assertEquals(1, decision1Node.getOutEdges().size());
        assertEquals(2, decision2Node.getInEdges().size());

        final Edge decision1NodeOutEdge = decision1Node.getOutEdges().get(0);
        final Edge decision2NodeInEdge = decision2Node.getInEdges().get(0);
        assertEquals(decision1NodeOutEdge, decision2NodeInEdge);

        final ViewConnector edgeView = (ViewConnector) decision1NodeOutEdge.getContent();
        assertTrue(edgeView.getSourceConnection().isPresent());
        assertTrue(edgeView.getTargetConnection().isPresent());

        return edgeView;
    }

    @SuppressWarnings("unchecked")
    private Diagram<Graph, Metadata> connectTwoNodes(final org.kie.workbench.common.stunner.core.graph.content.Bounds bounds,
                                                     final String decisionNode1UUID,
                                                     final String decisionNode2UUID,
                                                     final String edgeUUID,
                                                     final Consumer<MagnetConnection> sourceMagnetConsumer,
                                                     final Consumer<MagnetConnection> targetMagnetConsumer) {
        final DiagramImpl diagram = new DiagramImpl("dmn graph", null);
        final Graph<DefinitionSet, Node> graph = mock(Graph.class);

        final Node<View, Edge> diagramNode = mock(Node.class);
        final View diagramView = mock(View.class);
        final DMNDiagram dmnDiagram = new DMNDiagram();
        when(diagramNode.getContent()).thenReturn(diagramView);
        when(diagramView.getDefinition()).thenReturn(dmnDiagram);

        final Node<View, Edge> decisionNode1 = mock(Node.class);
        final View decisionView1 = mock(View.class);
        final Decision decision1 = new Decision();
        decision1.getId().setValue(decisionNode1UUID);
        when(decisionNode1.getContent()).thenReturn(decisionView1);
        when(decisionView1.getDefinition()).thenReturn(decision1);
        when(decisionView1.getBounds()).thenReturn(bounds);

        final Node<View, Edge> decisionNode2 = mock(Node.class);
        final View decisionView2 = mock(View.class);
        final Decision decision2 = new Decision();
        decision2.getId().setValue(decisionNode2UUID);
        when(decisionNode2.getContent()).thenReturn(decisionView2);
        when(decisionView2.getDefinition()).thenReturn(decision2);
        when(decisionView2.getBounds()).thenReturn(bounds);

        final Edge edge = mock(Edge.class);
        final ViewConnector edgeView = mock(ViewConnector.class);
        when(edge.getUUID()).thenReturn(edgeUUID);
        when(edge.getContent()).thenReturn(edgeView);
        final MagnetConnection edgeSourceConnection = mock(MagnetConnection.class);
        final MagnetConnection edgeTargetConnection = mock(MagnetConnection.class);
        when(edgeView.getSourceConnection()).thenReturn(Optional.of(edgeSourceConnection));
        when(edgeView.getTargetConnection()).thenReturn(Optional.of(edgeTargetConnection));
        when(edgeView.getControlPoints()).thenReturn(new ControlPoint[]{});
        when(decisionNode1.getOutEdges()).thenReturn(Collections.singletonList(edge));
        when(decisionNode2.getInEdges()).thenReturn(Collections.singletonList(edge));
        when(edge.getSourceNode()).thenReturn(decisionNode1);
        when(edge.getTargetNode()).thenReturn(decisionNode2);

        sourceMagnetConsumer.accept(edgeSourceConnection);
        targetMagnetConsumer.accept(edgeTargetConnection);

        doReturn(asList(diagramNode, decisionNode1, decisionNode2)).when(graph).nodes();
        diagram.setGraph(graph);

        return diagram;
    }

    @SuppressWarnings("unchecked")
    private void checkComponentWidths(Graph<?, Node<?, ?>> graph) {
        final Node<?, ?> node = graph.getNode("_37883BDC-DB54-4925-B539-A0F19B1DDE41");
        assertThat(node).isNotNull();
        assertNodeContentDefinitionIs(node, Decision.class);

        final Decision definition = ((View<Decision>) node.getContent()).getDefinition();
        assertThat(definition.getExpression()).isNotNull();

        final HasComponentWidths expression = definition.getExpression();
        final List<Double> componentWidths = expression.getComponentWidths();
        assertThat(componentWidths.size()).isEqualTo(expression.getRequiredComponentWidthCount());
        assertThat(componentWidths.get(0)).isEqualTo(50.0);
        assertThat(componentWidths.get(1)).isEqualTo(150.0);
        assertThat(componentWidths.get(2)).isEqualTo(200.0);
        assertThat(componentWidths.get(3)).isEqualTo(250.0);
    }

    private static void testAugmentWithNSPrefixes(org.kie.workbench.common.dmn.api.definition.v1_1.Definitions definitions) {
        for (Namespace nsp : DMNModelInstrumentedBase.Namespace.values()) {
            definitions.getNsContext().put(nsp.getPrefix(), nsp.getUri());
        }
    }

    @SuppressWarnings("unchecked")
    private static Diagram<Graph, Metadata> newDiagramDecisionWithExpression(final Expression expression) {
        final Diagram<Graph, Metadata> diagram = new DiagramImpl("dmn graph", null);
        final Graph<DefinitionSet, Node> graph = mock(Graph.class);

        final Node<View, ?> diagramNode = mock(Node.class);
        final View diagramView = mock(View.class);
        final DMNDiagram dmnDiagram = new DMNDiagram();
        doReturn(diagramView).when(diagramNode).getContent();
        doReturn(dmnDiagram).when(diagramView).getDefinition();

        final Node<View, ?> decisionNode = mock(Node.class);
        final View decisionView = mock(View.class);
        final Decision decision = new Decision();
        doReturn(decisionView).when(decisionNode).getContent();
        doReturn(decision).when(decisionView).getDefinition();
        decision.setExpression(expression);

        doReturn(asList(diagramNode, decisionNode)).when(graph).nodes();
        ((DiagramImpl) diagram).setGraph(graph);

        return diagram;
    }

    private static void checkDecisionExpression(final Graph<?, Node<View, ?>> unmarshalledGraph,
                                                final Expression expression) {
        final Node<View, ?> decisionNode = nodeOfDefinition(unmarshalledGraph.nodes().iterator(), Decision.class);
        final Expression decisionNodeExpression = ((Decision) decisionNode.getContent().getDefinition()).getExpression();

        // The process of marshalling an Expression that has been programmatically instantiated (vs created in the UI)
        // leads to the _source_ Expression ComponentWidths being initialised. Therefore to ensure a like-for-like equality
        // comparison ensure the unmarshalled _target_ Expression has had it's ComponentWidths initialised too.
        decisionNodeExpression.getComponentWidths();
        ((Context) decisionNodeExpression).getContextEntry().get(0).getExpression().getComponentWidths();
        assertThat(decisionNodeExpression).isEqualTo(expression);
    }

    private static Node<View, ?> nodeOfDefinition(final Iterator<Node<View, ?>> nodesIterator, final Class aClass) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(nodesIterator, Spliterator.NONNULL), false)
                   .filter(node -> aClass.isInstance(node.getContent().getDefinition()))
                   .findFirst().get();
    }

    @SafeVarargs
    private final XPath namespaceAwareXPath(Map.Entry<String, String>... pfxAndURI) {
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

    private DMNMarshaller getDMNMarshaller() {
        return new DMNMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                                 applicationFactoryManager,
                                 dmnMarshallerImportsHelper,
                                 getMarshaller());
    }

    private org.kie.dmn.api.marshalling.DMNMarshaller getMarshaller() {
        return new DMNMarshallerProducer().get();
    }

    private void assertXPathEquals(XPath xpathOriginal, XPath xpathRoundtrip, String xpathExpression, String expectedXml, String actualXml) throws XPathExpressionException {
        InputSource expected = new InputSource(new StringReader(expectedXml));
        InputSource actual = new InputSource(new StringReader(actualXml));
        assertEquals(xpathOriginal.compile(xpathExpression).evaluate(expected), xpathRoundtrip.compile(xpathExpression).evaluate(actual));
    }
}
