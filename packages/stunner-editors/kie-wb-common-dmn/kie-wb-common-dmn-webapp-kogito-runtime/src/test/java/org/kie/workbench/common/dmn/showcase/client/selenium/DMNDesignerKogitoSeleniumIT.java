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

package org.kie.workbench.common.dmn.showcase.client.selenium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import javax.xml.XMLConstants;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.showcase.client.common.DMNDesignerBaseIT;
import org.kie.workbench.common.dmn.showcase.client.model.DecisionTableSeleniumModel;
import org.kie.workbench.common.dmn.showcase.client.model.ListSeleniumModel;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.ContextMenuXPathLocator;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.DecisionNavigatorXPathLocator;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.EditorXPathLocator;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.PropertiesPanelXPathLocator;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xmlunit.assertj.XmlAssert;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.util.Predicate;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase.Namespace.DMN;
import static org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase.Namespace.FEEL;
import static org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase.Namespace.KIE;
import static org.openqa.selenium.By.xpath;

/**
 * Selenium test for DMN Designer - client site marshalling version
 * The Designer is represented by single webpage - index.html
 */
public class DMNDesignerKogitoSeleniumIT extends DMNDesignerBaseIT {

    @Test
    public void testNewDiagram() throws Exception {
        final String expected = loadResource("new-diagram.xml");
        setContent("");

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        //Skip, id, name and namespace in the comparison as they are dynamically created at runtime
        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .withAttributeFilter(attr -> !(Objects.equals(attr.getName(), "id")
                        || Objects.equals(attr.getName(), "name")
                        || Objects.equals(attr.getName(), "namespace")))
                .areIdentical();
    }

    @Test
    public void testBasicModel() throws Exception {
        final String expected = loadResource("basic-model.xml");
        setContent(expected);

        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("CurrentIndex"));
        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("NextIndex"));

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testBusinessKnowledgeModel() throws Exception {
        final String expected = loadResource("business-knowledge-model.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testInputData() throws Exception {
        final String expected = loadResource("input-data.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testKnowledgeSource() throws Exception {
        final String expected = loadResource("knowledge-source.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testDecisionEmpty() throws Exception {
        final String expected = loadResource("decision-empty.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testDecisionExpressionLiteral() throws Exception {
        final String expected = loadResource("decision-expression-literal.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_4FEA7589-823B-4880-BCFA-AF2F9B145785']" +
                                  "/dmn:literalExpression[@id='_35DB53A6-97E7-4D48-9E5A-59CE0015CEF8']" +
                                  "/dmn:text[text()='123']");
    }

    @Test
    public void testBendPoints() throws Exception {
        final String expected = loadResource("bend-points.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        final List<String> edges = Arrays.asList("dmnedge-drg-_5FF07784-BB77-4632-985F-2805302C03D3-AUTO-TARGET",
                                                 "dmnedge-drg-_99B590D4-A82B-43D9-9DD7-A3A4D57D8CA5-AUTO-SOURCE-AUTO-TARGET",
                                                 "dmnedge-drg-_2B636F4A-3A7F-4C38-9D42-B000D1E0CCF2-AUTO-SOURCE-AUTO-TARGET");

        edges.forEach(edge ->
                              XmlAssert.assertThat(actual)
                                      .withNamespaceContext(NAMESPACES)
                                      .hasXPath("/dmn:definitions/dmndi:DMNDI/dmndi:DMNDiagram" +
                                                        "/dmndi:DMNEdge[@id='" + edge + "']" +
                                                        "/di:waypoint")
                                      .hasSize(3)
        );
    }

    @Test
    public void testMultipleDiagramsWithoutDRG() throws Exception {

        final String fixture = loadResource("multiple-diagrams-without-drg.xml");
        setContent(fixture);

        final String actual = getContent();
        assertThat(actual).isNotBlank();
        XmlAssert.assertThat(actual)
                .and(fixture)
                .ignoreComments()
                .ignoreWhitespace()
                .areNotIdentical();

        // Assert DRG diagram
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='DRG']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='DRG']" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-_99e87528-2017-4b18-9b9f-83f66a5c1b85']" +
                                  "/dc:Bounds[@x='56.5' and @y='56' and @width='153' and @height='60']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='DRG']" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-2-_99e87528-2017-4b18-9b9f-83f66a5c1b85']" +
                                  "/dc:Bounds[@x='56.5' and @y='167' and @width='153' and @height='60']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='DRG']" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-3-_99e87528-2017-4b18-9b9f-83f66a5c1b85']" +
                                  "/dc:Bounds[@x='266' and @y='57' and @width='153' and @height='60']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='DRG']" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-4-_99e87528-2017-4b18-9b9f-83f66a5c1b85']" +
                                  "/dc:Bounds[@x='266' and @y='168' and @width='153' and @height='60']");

        // Assert Page 1 diagram
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='Page 1']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='Page 1']" +
                                  "/dmndi:DMNShape[@id='dmnshape-page-1-_99e87528-2017-4b18-9b9f-83f66a5c1b85']" +
                                  "/dc:Bounds[@x='56.5' and @y='56' and @width='153' and @height='60']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='Page 1']" +
                                  "/dmndi:DMNShape[@id='dmnshape-page-1-2-_99e87528-2017-4b18-9b9f-83f66a5c1b85']" +
                                  "/dc:Bounds[@x='56.5' and @y='167' and @width='153' and @height='60']");

        // Assert Page 2 diagram
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='Page 2']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='Page 2']" +
                                  "/dmndi:DMNShape[@id='dmnshape-page-2-_99e87528-2017-4b18-9b9f-83f66a5c1b85']" +
                                  "/dc:Bounds[@x='56.5' and @y='57' and @width='153' and @height='60']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='Page 2']" +
                                  "/dmndi:DMNShape[@id='dmnshape-page-2-2-_99e87528-2017-4b18-9b9f-83f66a5c1b85']" +
                                  "/dc:Bounds[@x='56.5' and @y='168' and @width='153' and @height='60']");
    }

    @Test
    public void testMultipleDiagramsWithDRG() throws Exception {

        final String fixture = loadResource("multiple-diagrams-with-drg.xml");
        setContent(fixture);

        final String actual = getContent();
        assertThat(actual).isNotBlank();
        XmlAssert.assertThat(actual)
                .and(fixture)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testSingleDiagramWithDRG() throws Exception {

        final String fixture = loadResource("single-diagram-with-drg.xml");
        setContent(fixture);

        final String actual = getContent();
        assertThat(actual).isNotBlank();
        XmlAssert.assertThat(actual)
                .and(fixture)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testSingleDiagramWithoutDRG() throws Exception {

        final String fixture = loadResource("single-diagram-without-drg.xml");
        final String expected = loadResource("single-diagram-with-drg.xml");
        setContent(fixture);

        final String actual = getContent();
        assertThat(actual).isNotBlank();
        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testDiagramWithoutDMNDI() throws Exception {

        final String fixture = loadResource("single-diagram-without-dmndi.xml");
        setContent(fixture);

        selectAutomaticLayout();

        final String actual = getContent();
        assertThat(actual).isNotBlank();
        XmlAssert.assertThat(actual)
                .and(fixture)
                .ignoreComments()
                .ignoreWhitespace()
                .areNotIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='DRG']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='DRG']" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-_99e87528-2017-4b18-9b9f-83f66a5c1b85']" +
                                  "/dc:Bounds[@x='50' and @y='50' and @width='100' and @height='50']");
    }

    @Test
    public void testDecisionExpressionContextSimple() throws Exception {
        final String expected = loadResource("decision-expression-context-simple.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        final Predicate<Node> nodeFilter = node -> {
            final String localName = node.getLocalName();
            final String namespaceURI = node.getNamespaceURI();
            if (Objects.equals(namespaceURI, KIE.getUri())) {
                // An empty LiteralExpression is added when saving the model that creates an
                // additional ComponentWidths element compared to the original file. Therefore
                // ignore ComponentWidths for this test.
                if (Objects.equals(localName, "ComponentWidths")) {
                    return false;
                }
            }

            return true;
        };

        final Predicate<Attr> attributeFilter = attr -> {
            final String localName = attr.getLocalName();
            final String namespaceURI = Objects.nonNull(attr.getNamespaceURI()) ? attr.getNamespaceURI() : attr.getOwnerElement().getNamespaceURI();
            if (Objects.equals(namespaceURI, DMN.getUri())) {
                // See IdPropertyConverter. We have to have an ID for the default LiteralExpression when
                // loading the model back into the editor as it is used by the Decision Navigator. Therefore
                // skip it's existence in the comparison.
                if (Objects.equals(attr.getOwnerElement().getLocalName(), "literalExpression")) {
                    if (Objects.equals(localName, "id")) {
                        return false;
                    }
                }
            }
            return true;
        };

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .withNodeFilter(nodeFilter)
                .withAttributeFilter(attributeFilter)
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_4FEA7589-823B-4880-BCFA-AF2F9B145785']" +
                                  "/dmn:context[@id='_23253C3F-352C-4A00-8548-174EDC272929']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:literalExpression" +
                                  "/dmn:text[text()='null // auto-filled by the editor to avoid missing empty expression.']");
    }

    @Ignore("No more compatible with React Based Boxed Expression")
    public void testDecisionExpressionDecisionTable() throws Exception {
        final String expected = loadResource("decision-expression-decision-table.xml");
        setContent(expected);

        final String defaultDecisionTableOutput = "hello world, kogito!";
        final DecisionTableSeleniumModel decisionTable = new DecisionTableSeleniumModel();
        decisionTable.setInputsCount(1);
        decisionTable.setName("Decision-1");
        decisionTable.setDefaultOutput(defaultDecisionTableOutput);

        setDecisionTableDefaultOutput(decisionTable);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:decisionTable[@id='_1B2AE7B6-BF51-472E-99CB-A67875CE1B57']")
                .containsAnyNodeHavingXPath("//dmn:input[@id='_FCDB0235-3C5C-442D-B268-C6B34FC9967F']")
                .containsAnyNodeHavingXPath("//dmn:output[@id='_3C2E81E3-A8F7-4600-8FC1-7FACB5F85CB9']")
                .containsAnyNodeHavingXPath("//dmn:rule[@id='_2D2D5ABD-3C71-40E9-B493-73CDA49B3F53']")
                .containsAnyNodeHavingXPath("//dmn:output/dmn:defaultOutputEntry/dmn:text[text()='" + defaultDecisionTableOutput + "']");
    }

    @Test
    public void testDecisionExpressionFunctionJava() throws Exception {
        final String expected = loadResource("decision-expression-function-java.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='Java']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='Java']" +
                                  "/dmn:formalParameter[@id='_199F195B-94C2-4FEA-B97F-95BD8220B0CE' and @name='p-1' and @typeRef='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='Java']" +
                                  "/dmn:context/dmn:contextEntry/dmn:variable[@id='_C623E4C3-9E15-4298-B3C9-8053EA34EC8C' and @name='class' and @typeRef='string']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='Java']" +
                                  "/dmn:context/dmn:contextEntry/dmn:literalExpression[@id='_BD6303BA-04A6-4607-8A6D-ACC69B508E93']" +
                                  "/dmn:text[text()='aClass']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='Java']" +
                                  "/dmn:context/dmn:contextEntry/dmn:variable[@id='_8F366CDC-8F70-42E3-A527-6932DB108A67' and @name='method signature' and @typeRef='string']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='Java']" +
                                  "/dmn:context/dmn:contextEntry/dmn:literalExpression[@id='_41C4C578-10B7-40BB-AB80-C85AD272CB8E']" +
                                  "/dmn:text[text()='aMethod']");
    }

    @Test
    public void testDecisionExpressionFunctionFEEL() throws Exception {
        final String expected = loadResource("decision-expression-function-feel.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='FEEL']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='FEEL']" +
                                  "/dmn:formalParameter[@id='_199F195B-94C2-4FEA-B97F-95BD8220B0CE' and @name='p-1' and @typeRef='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='FEEL']" +
                                  "/dmn:literalExpression[@id='_07E4B4B8-8934-478C-97EE-2CAD2BEFCEEF']" +
                                  "/dmn:text[text()='123']");
    }

    @Test
    public void testDecisionExpressionFunctionPMML() throws Exception {
        final String expected = loadResource("decision-expression-function-pmml.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='PMML']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='PMML']" +
                                  "/dmn:formalParameter[@id='_199F195B-94C2-4FEA-B97F-95BD8220B0CE' and @name='p-1' and @typeRef='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='PMML']" +
                                  "/dmn:context/dmn:contextEntry/dmn:variable[@id='_49D723A5-2CE1-4B5A-A3A7-924C6C74FD02' and @name='document' and @typeRef='string']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='PMML']" +
                                  "/dmn:context/dmn:contextEntry/dmn:literalExpression[@id='_434072BA-33F3-4539-80A7-568DF0A9F7EA']" +
                                  "/dmn:text[text()='a-pmml-document']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='PMML']" +
                                  "/dmn:context/dmn:contextEntry/dmn:variable[@id='_E8305BA8-0211-42B2-BC63-3F805E98D904' and @name='model' and @typeRef='string']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:functionDefinition[@id='_A7F30775-6B62-415D-87CA-D5F7436CBB8C' and @kind='PMML']" +
                                  "/dmn:context/dmn:contextEntry/dmn:literalExpression[@id='_66EFBF48-9C93-4391-A75E-845D422316B4']" +
                                  "/dmn:text[text()='a-pmml-model']");
    }

    @Test
    public void testDecisionExpressionInvocation() throws Exception {
        final String expected = loadResource("decision-expression-invocation.xml");
        setContent(expected);
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_477827DF-E385-4963-9113-9AB47AA4AF0B']" +
                                  "/dmn:invocation[@id='_0C2A4913-CFC5-4849-8C4C-8C1559EF2157']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_477827DF-E385-4963-9113-9AB47AA4AF0B']" +
                                  "/dmn:invocation[@id='_0C2A4913-CFC5-4849-8C4C-8C1559EF2157']" +
                                  "/dmn:literalExpression[@id='_D7297A4E-BE75-446E-B846-550330CE0A42']" +
                                  "/dmn:text[text()='aFunction']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_477827DF-E385-4963-9113-9AB47AA4AF0B']" +
                                  "/dmn:invocation[@id='_0C2A4913-CFC5-4849-8C4C-8C1559EF2157']" +
                                  "/dmn:binding" +
                                  "/dmn:parameter[@id='_8821B285-8152-4657-B669-30DD9B22D0FD' and @name='p-1']");// +
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_477827DF-E385-4963-9113-9AB47AA4AF0B']" +
                                  "/dmn:invocation[@id='_0C2A4913-CFC5-4849-8C4C-8C1559EF2157']" +
                                  "/dmn:binding" +
                                  "/dmn:literalExpression[@id='_1F1EA05C-FAD9-4BDC-AE04-6F47ADA54C0D']" +
                                  "/dmn:text[text()='abc']");
    }

    @Test
    public void testDecisionExpressionRelation() throws Exception {
        final String expected = loadResource("decision-expression-relation.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_477827DF-E385-4963-9113-9AB47AA4AF0B']" +
                                  "/dmn:relation[@id='_3C5A4ABC-F019-4D58-BA2F-D5EE8982C4D4']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_477827DF-E385-4963-9113-9AB47AA4AF0B']" +
                                  "/dmn:relation[@id='_3C5A4ABC-F019-4D58-BA2F-D5EE8982C4D4']" +
                                  "/dmn:column[@id='_8807B3DE-FEA0-4B21-8262-3AE99DFE4224' and @name='column-1' and @typeRef='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_477827DF-E385-4963-9113-9AB47AA4AF0B']" +
                                  "/dmn:relation[@id='_3C5A4ABC-F019-4D58-BA2F-D5EE8982C4D4']" +
                                  "/dmn:column[@id='_E232E8B7-B270-4875-A2E9-478E35C238AE' and @name='column-2' and @typeRef='string']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_477827DF-E385-4963-9113-9AB47AA4AF0B']" +
                                  "/dmn:relation[@id='_3C5A4ABC-F019-4D58-BA2F-D5EE8982C4D4']" +
                                  "/dmn:row[@id='_40637086-42EA-4BFF-9853-6E0A88495EF1']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_477827DF-E385-4963-9113-9AB47AA4AF0B']" +
                                  "/dmn:relation[@id='_3C5A4ABC-F019-4D58-BA2F-D5EE8982C4D4']" +
                                  "/dmn:row[@id='_844990B6-2947-48F8-B196-5370A3BA44D2']");
    }

    @Test
    public void testDecisionExpressionContextLiteral() throws Exception {
        final String expected = loadResource("decision-expression-context-literal.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_477827DF-E385-4963-9113-9AB47AA4AF0B']" +
                                  "/dmn:context[@id='_BE7616BB-D11C-4D8A-8180-74A05961B868']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:literalExpression[@id='_490EF156-7828-4762-8727-73D145391871']" +
                                  "/dmn:text[text()='abc']");
    }

    @Test
    public void testDecisionExpressionContextDecisionTable() throws Exception {
        final String expected = loadResource("decision-expression-context-decision-table.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_FAAA1E11-7107-4745-8F51-804BFB7E2F96']" +
                                  "/dmn:context[@id='_AF3119C9-837D-4D56-A560-454A2F1A934D']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:decisionTable[@id='_DB23F5EF-8D43-4A04-A270-B54CF89BC13B']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_FAAA1E11-7107-4745-8F51-804BFB7E2F96']" +
                                  "/dmn:context[@id='_AF3119C9-837D-4D56-A560-454A2F1A934D']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:decisionTable[@id='_DB23F5EF-8D43-4A04-A270-B54CF89BC13B']" +
                                  "/dmn:input[@id='_87023E71-2574-44E8-BC6B-E8FDD6E4C52B']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_FAAA1E11-7107-4745-8F51-804BFB7E2F96']" +
                                  "/dmn:context[@id='_AF3119C9-837D-4D56-A560-454A2F1A934D']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:decisionTable[@id='_DB23F5EF-8D43-4A04-A270-B54CF89BC13B']" +
                                  "/dmn:output[@id='_2B19E6D4-DCA2-4792-AB67-B4AF7E6879BB']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_FAAA1E11-7107-4745-8F51-804BFB7E2F96']" +
                                  "/dmn:context[@id='_AF3119C9-837D-4D56-A560-454A2F1A934D']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:decisionTable[@id='_DB23F5EF-8D43-4A04-A270-B54CF89BC13B']" +
                                  "/dmn:rule[@id='_A8EFD493-8E86-426A-AFF1-6B6E51D823C9']");
    }

    @Test
    public void testDecisionExpressionContextFunction() throws Exception {
        final String expected = loadResource("decision-expression-context-function.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_C015997C-0A42-4218-8105-DD85E60584B5']" +
                                  "/dmn:context[@id='_4B196860-A2DF-4ACC-B9E4-FAB662CDAFE9']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:functionDefinition[@id='_9FAD2F25-EA60-4D20-B633-E03CB9A7F8C0' and @kind='FEEL']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_C015997C-0A42-4218-8105-DD85E60584B5']" +
                                  "/dmn:context[@id='_4B196860-A2DF-4ACC-B9E4-FAB662CDAFE9']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:functionDefinition[@id='_9FAD2F25-EA60-4D20-B633-E03CB9A7F8C0' and @kind='FEEL']" +
                                  "/dmn:formalParameter[@id='_CD4817CF-BAB2-497C-9984-DCA23B5ED67B' and @name='p-1' and @typeRef='string']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_C015997C-0A42-4218-8105-DD85E60584B5']" +
                                  "/dmn:context[@id='_4B196860-A2DF-4ACC-B9E4-FAB662CDAFE9']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:functionDefinition[@id='_9FAD2F25-EA60-4D20-B633-E03CB9A7F8C0' and @kind='FEEL']" +
                                  "/dmn:literalExpression[@id='_600EACC0-8898-4779-BA69-11DBF1006E90']" +
                                  "/dmn:text[text()='123']");
    }

    @Test
    public void testDecisionExpressionContextInvocation() throws Exception {
        final String expected = loadResource("decision-expression-context-invocation.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_C015997C-0A42-4218-8105-DD85E60584B5']" +
                                  "/dmn:context[@id='_4B196860-A2DF-4ACC-B9E4-FAB662CDAFE9']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:invocation[@id='_E3EC46AF-9E73-4779-AC07-48AB991C6767']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_C015997C-0A42-4218-8105-DD85E60584B5']" +
                                  "/dmn:context[@id='_4B196860-A2DF-4ACC-B9E4-FAB662CDAFE9']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:invocation[@id='_E3EC46AF-9E73-4779-AC07-48AB991C6767']" +
                                  "/dmn:literalExpression[@id='_F1463D11-DAAD-4955-B5AF-EC48E1D3A7F9']" +
                                  "/dmn:text[text()='aFunction']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_C015997C-0A42-4218-8105-DD85E60584B5']" +
                                  "/dmn:context[@id='_4B196860-A2DF-4ACC-B9E4-FAB662CDAFE9']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:invocation[@id='_E3EC46AF-9E73-4779-AC07-48AB991C6767']" +
                                  "/dmn:binding" +
                                  "/dmn:parameter[@id='_A6FE61E9-1D83-4E31-A748-EEF2A6623E95' and @name='p-1']");// +
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_C015997C-0A42-4218-8105-DD85E60584B5']" +
                                  "/dmn:context[@id='_4B196860-A2DF-4ACC-B9E4-FAB662CDAFE9']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:invocation[@id='_E3EC46AF-9E73-4779-AC07-48AB991C6767']" +
                                  "/dmn:binding" +
                                  "/dmn:literalExpression[@id='_BBBBFE1F-A6BD-4E7E-A01A-0F07E07BC41B']" +
                                  "/dmn:text[text()='abc']");
    }

    @Test
    public void testDecisionExpressionContextRelation() throws Exception {
        final String expected = loadResource("decision-expression-context-relation.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_C015997C-0A42-4218-8105-DD85E60584B5']" +
                                  "/dmn:context[@id='_4B196860-A2DF-4ACC-B9E4-FAB662CDAFE9']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:relation[@id='_B20E94B3-DC5F-4B5E-94D1-FAECFA0CC039']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_C015997C-0A42-4218-8105-DD85E60584B5']" +
                                  "/dmn:context[@id='_4B196860-A2DF-4ACC-B9E4-FAB662CDAFE9']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:relation[@id='_B20E94B3-DC5F-4B5E-94D1-FAECFA0CC039']" +
                                  "/dmn:column[@id='_59099D02-C9FA-4D3A-9E35-F6B2B1E2373B' and @name='column-1' and @typeRef='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_C015997C-0A42-4218-8105-DD85E60584B5']" +
                                  "/dmn:context[@id='_4B196860-A2DF-4ACC-B9E4-FAB662CDAFE9']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:relation[@id='_B20E94B3-DC5F-4B5E-94D1-FAECFA0CC039']" +
                                  "/dmn:column[@id='_D40C126B-17F4-41C7-BCA1-18BD49C2DEC6' and @name='column-2' and @typeRef='string']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_C015997C-0A42-4218-8105-DD85E60584B5']" +
                                  "/dmn:context[@id='_4B196860-A2DF-4ACC-B9E4-FAB662CDAFE9']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:relation[@id='_B20E94B3-DC5F-4B5E-94D1-FAECFA0CC039']" +
                                  "/dmn:row[@id='_0E6C8A75-7B47-402F-B60B-41A86BFB84E4']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_C015997C-0A42-4218-8105-DD85E60584B5']" +
                                  "/dmn:context[@id='_4B196860-A2DF-4ACC-B9E4-FAB662CDAFE9']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:relation[@id='_B20E94B3-DC5F-4B5E-94D1-FAECFA0CC039']" +
                                  "/dmn:row[@id='_5B85AC01-0DAD-4591-9962-CBDCEEA98A3A']");
    }

    @Test
    public void testBusinessKnowledgeModelExpressionLiteral() throws Exception {
        final String expected = loadResource("business-knowledge-model-expression-literal.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:formalParameter[@id='_7EA0A877-1777-4A7E-99BE-D05DBC02EB7C' and @name='p-1' and @typeRef='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:literalExpression[@id='_A08BD759-3FE9-41B1-9A32-BAF2AE587878']" +
                                  "/dmn:text[text()='abc']");
    }

    @Test
    public void testBusinessKnowledgeModelExpressionContext() throws Exception {
        final String expected = loadResource("business-knowledge-model-expression-context.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:formalParameter[@id='_7EA0A877-1777-4A7E-99BE-D05DBC02EB7C' and @name='p-1' and @typeRef='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:context[@id='_4A549FA8-E94A-43EE-B888-535D2CBFBE7C']" +
                                  "/dmn:contextEntry" +
                                  "/dmn:literalExpression[@id='_D0ABBFB8-46D7-48E7-9C36-6D6C7D4536AF']" +
                                  "/dmn:text[text()='abc']");
    }

    @Test
    public void testBusinessKnowledgeModelExpressionDecisionTable() throws Exception {
        final String expected = loadResource("business-knowledge-model-expression-decision-table.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:formalParameter[@id='_7EA0A877-1777-4A7E-99BE-D05DBC02EB7C' and @name='p-1' and @typeRef='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:decisionTable[@id='_407EA8F3-1074-47EF-A764-4B2EFDD131E5']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:decisionTable[@id='_407EA8F3-1074-47EF-A764-4B2EFDD131E5']" +
                                  "/dmn:input[@id='_9CDDEA92-D737-49B8-B253-0BE1BF7A13F3']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:decisionTable[@id='_407EA8F3-1074-47EF-A764-4B2EFDD131E5']" +
                                  "/dmn:output[@id='_82865AE0-AF73-4C6C-91C1-533C1521BF2C']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:decisionTable[@id='_407EA8F3-1074-47EF-A764-4B2EFDD131E5']" +
                                  "/dmn:annotation");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:decisionTable[@id='_407EA8F3-1074-47EF-A764-4B2EFDD131E5']" +
                                  "/dmn:rule[@id='_4E0C236E-D1C7-4354-A419-37DD368B4C40']");
    }

    /**
     * Reproducer for DROOLS-5332
     * Uses same DMN model as #testBusinessKnowledgeModelExpressionDecisionTable()
     * just without decisionTable output typeRef explicitly set
     */
    @Test
    public void testBusinessKnowledgeModelExpressionDecisionTableDefaultOutputRef() throws Exception {
        final String expected = loadResource("business-knowledge-model-expression-decision-table-default-output-ref.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:decisionTable[@id='_407EA8F3-1074-47EF-A764-4B2EFDD131E5']" +
                                  "/dmn:output[@id='_82865AE0-AF73-4C6C-91C1-533C1521BF2C']");

        assertBKMFunctionCanBeOpened("BusinessKnowledgeModel-1");
    }

    @Test
    public void testBusinessKnowledgeModelExpressionFunction() throws Exception {
        final String expected = loadResource("business-knowledge-model-expression-function.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:formalParameter[@id='_7EA0A877-1777-4A7E-99BE-D05DBC02EB7C' and @name='p-1' and @typeRef='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:functionDefinition[@id='_60A42B5A-5F64-4075-9041-F66263ACA6D6' and @kind='FEEL']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:functionDefinition[@id='_60A42B5A-5F64-4075-9041-F66263ACA6D6' and @kind='FEEL']" +
                                  "/dmn:formalParameter[@id='_613EB7B5-6787-4672-B238-BB558CBCBFC1' and @name='p-2' and @typeRef='string']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:functionDefinition[@id='_60A42B5A-5F64-4075-9041-F66263ACA6D6' and @kind='FEEL']" +
                                  "/dmn:literalExpression[@id='_3CC39922-9B4C-4646-8606-CCBBDF19FD90']" +
                                  "/dmn:text[text()='abc']");
    }

    @Test
    public void testBusinessKnowledgeModelExpressionInvocation() throws Exception {
        final String expected = loadResource("business-knowledge-model-expression-invocation.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:formalParameter[@id='_7EA0A877-1777-4A7E-99BE-D05DBC02EB7C' and @name='p-1' and @typeRef='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:invocation[@id='_67C0CA6C-3C78-46B8-89A9-B97270DB6837']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:invocation[@id='_67C0CA6C-3C78-46B8-89A9-B97270DB6837']" +
                                  "/dmn:literalExpression[@id='_3A9F48F3-BFAD-4131-B26D-E90918F7A0DC']" +
                                  "/dmn:text[text()='aFunction']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:invocation[@id='_67C0CA6C-3C78-46B8-89A9-B97270DB6837']" +
                                  "/dmn:binding" +
                                  "/dmn:parameter[@id='_46583D18-F89D-40CB-B177-24841C88062C' and @name='p-2' and @typeRef='string']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:invocation[@id='_67C0CA6C-3C78-46B8-89A9-B97270DB6837']" +
                                  "/dmn:binding" +
                                  "/dmn:literalExpression[@id='_FFF2CB0D-6913-4975-9822-B455986F00C4']" +
                                  "/dmn:text[text()='abc']");
    }

    @Test
    public void testBusinessKnowledgeModelExpressionRelation() throws Exception {
        final String expected = loadResource("business-knowledge-model-expression-relation.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:formalParameter[@id='_7EA0A877-1777-4A7E-99BE-D05DBC02EB7C' and @name='p-1' and @typeRef='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:relation[@id='_1DA91BD8-4790-4DED-8802-DAB553611D9F']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:relation[@id='_1DA91BD8-4790-4DED-8802-DAB553611D9F']" +
                                  "/dmn:column[@id='_2839974C-C732-4DEE-9B63-1A8EC54005F2' and @name='column-1' and @typeRef='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:relation[@id='_1DA91BD8-4790-4DED-8802-DAB553611D9F']" +
                                  "/dmn:column[@id='_238DFE7E-1C3B-43A6-A29F-26F10B1158A5' and @name='column-2' and @typeRef='string']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_1ACB205E-7221-4573-B555-7A7626FDFC8E']" +
                                  "/dmn:encapsulatedLogic[@id='_616FC696-1CE6-4210-A479-DEE11293ACA3' and @kind='FEEL']" +
                                  "/dmn:relation[@id='_1DA91BD8-4790-4DED-8802-DAB553611D9F']" +
                                  "/dmn:row[@id='_56CCB795-EC00-4FDA-9946-702FDE007DF5']");
    }

    @Test
    public void testConnectorAssociation() throws Exception {
        final String expected = loadResource("connector-association.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_425056D1-B0A9-46BB-BEC2-346931E341D1']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:textAnnotation[@id='_94E276C8-0D63-4E3A-86E2-3274396F31E4']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:association[@id='_EECC6338-81FC-4E36-BDF9-C70214830E6B']" +
                                  "/dmn:sourceRef[@href='#_425056D1-B0A9-46BB-BEC2-346931E341D1']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:association[@id='_EECC6338-81FC-4E36-BDF9-C70214830E6B']" +
                                  "/dmn:targetRef[@href='#_94E276C8-0D63-4E3A-86E2-3274396F31E4']");
    }

    @Test
    public void testConnectorInformationRequirement() throws Exception {
        final String expected = loadResource("connector-information-requirement.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_425056D1-B0A9-46BB-BEC2-346931E341D1']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_49F22725-E7A5-409B-9180-ECA9A5F11199']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_49F22725-E7A5-409B-9180-ECA9A5F11199']" +
                                  "/dmn:informationRequirement[@id='_9BD6BA2F-70E0-47F7-8253-4451F88277FB']" +
                                  "/dmn:requiredDecision[@href='#_425056D1-B0A9-46BB-BEC2-346931E341D1']");
    }

    @Test
    public void testConnectorAuthorityRequirement() throws Exception {
        final String expected = loadResource("connector-authority-requirement.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:inputData[@id='_8F75491D-85F7-4658-93B8-8F3D57D8A5EB']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:knowledgeSource[@id='_0B740AC1-E5A0-4BC3-BA4A-63CD2EDFABC9']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:knowledgeSource[@id='_0B740AC1-E5A0-4BC3-BA4A-63CD2EDFABC9']" +
                                  "/dmn:authorityRequirement[@id='_D0F89EE1-7646-44C4-B632-7680DDB9996B']" +
                                  "/dmn:requiredInput[@href='#_8F75491D-85F7-4658-93B8-8F3D57D8A5EB']");
    }

    @Test
    public void testConnectorKnowledgeRequirement() throws Exception {
        final String expected = loadResource("connector-knowledge-requirement.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_C78AA5FB-B1F7-4304-8654-59F8EBDAB5F8']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_C9954C3F-694F-4EA0-B958-6F5835FBA59E']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_C9954C3F-694F-4EA0-B958-6F5835FBA59E']" +
                                  "/dmn:knowledgeRequirement[@id='_A0DDD0BF-85AB-45DA-9C7C-1B242AD8D76B']" +
                                  "/dmn:requiredKnowledge[@href='#_C78AA5FB-B1F7-4304-8654-59F8EBDAB5F8']");
    }

    @Test
    public void testComplexDiagram() throws Exception {
        final String expected = loadResource("complex-diagram.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decisionService[@id='_7B7899F1-F73F-47D3-9105-4BF140C4FA33']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decisionService[@id='_7B7899F1-F73F-47D3-9105-4BF140C4FA33']" +
                                  "/dmn:variable[@id='_94534622-B260-4C43-B28A-2149E1DD33F5']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decisionService[@id='_7B7899F1-F73F-47D3-9105-4BF140C4FA33']" +
                                  "/dmn:outputDecision[@href='#_0768A321-6268-42E8-BE09-621CFD2EFA3A']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decisionService[@id='_7B7899F1-F73F-47D3-9105-4BF140C4FA33']" +
                                  "/dmn:encapsulatedDecision[@href='#_EF1815B1-954A-405F-8AA0-9F5B2A97E221']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decisionService[@id='_7B7899F1-F73F-47D3-9105-4BF140C4FA33']" +
                                  "/dmn:inputDecision[@href='#_939C7FB2-A427-4C34-B6EF-57E3890A54EA']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decisionService[@id='_7B7899F1-F73F-47D3-9105-4BF140C4FA33']" +
                                  "/dmn:inputData[@href='#_A417B73B-E1CA-4178-A1AA-DAB13EB51CA2']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_939C7FB2-A427-4C34-B6EF-57E3890A54EA']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_939C7FB2-A427-4C34-B6EF-57E3890A54EA']" +
                                  "/dmn:knowledgeRequirement[@id='_5BC92424-CE17-4983-A60D-F69DDDA7685C']" +
                                  "/dmn:requiredKnowledge[@href='#_D6C4F37A-E736-4B0D-8516-CA8758D43F79']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_939C7FB2-A427-4C34-B6EF-57E3890A54EA']" +
                                  "/dmn:authorityRequirement[@id='_A6E2D517-B0CC-4822-94BB-734BCAF11E70']" +
                                  "/dmn:requiredAuthority[@href='#_7BFFB565-3168-4DC4-9135-E519FA1843D9']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:inputData[@id='_A417B73B-E1CA-4178-A1AA-DAB13EB51CA2']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_EF1815B1-954A-405F-8AA0-9F5B2A97E221']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_EF1815B1-954A-405F-8AA0-9F5B2A97E221']" +
                                  "/dmn:informationRequirement[@id='_54AA4235-83A4-4A8F-B3BB-A33C8F2F5AF8']" +
                                  "/dmn:requiredInput[@href='#_A417B73B-E1CA-4178-A1AA-DAB13EB51CA2']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_EF1815B1-954A-405F-8AA0-9F5B2A97E221']" +
                                  "/dmn:informationRequirement[@id='_B00CCBB9-5091-4F0E-BAFF-3134F106D1EE']" +
                                  "/dmn:requiredDecision[@href='#_939C7FB2-A427-4C34-B6EF-57E3890A54EA']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_0768A321-6268-42E8-BE09-621CFD2EFA3A']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_0768A321-6268-42E8-BE09-621CFD2EFA3A']" +
                                  "/dmn:informationRequirement[@id='_E91F01E9-C6CE-4DAD-A16A-224640B27E52']" +
                                  "/dmn:requiredDecision[@href='#_EF1815B1-954A-405F-8AA0-9F5B2A97E221']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_A000C016-9748-43EF-9DAD-566476AF5734']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_A000C016-9748-43EF-9DAD-566476AF5734']" +
                                  "/dmn:informationRequirement[@id='_CF4E2334-8F8D-4870-8934-509484AB19D3']" +
                                  "/dmn:requiredDecision[@href='#_0768A321-6268-42E8-BE09-621CFD2EFA3A']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:businessKnowledgeModel[@id='_D6C4F37A-E736-4B0D-8516-CA8758D43F79']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:knowledgeSource[@id='_7BFFB565-3168-4DC4-9135-E519FA1843D9']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:association[@id='_E83BCDB7-4E87-414F-AF62-BCC7A7D8B9E3']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:textAnnotation[@id='_61297160-34B1-49B1-B944-076E44608949']");
    }

    @Test
    public void testNodeStyling() throws Exception {
        final String expected = loadResource("node-styling.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_E5D538FB-D142-4CC6-9F11-229A0A766B7C']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-_E5D538FB-D142-4CC6-9F11-229A0A766B7C' and @dmnElementRef='_E5D538FB-D142-4CC6-9F11-229A0A766B7C']" +
                                  "/dmndi:DMNStyle[@fontFamily='sans serif' and @fontSize='12']" +
                                  "/dmndi:FillColor[@red='248' and @green='4' and @blue='4']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-_E5D538FB-D142-4CC6-9F11-229A0A766B7C' and @dmnElementRef='_E5D538FB-D142-4CC6-9F11-229A0A766B7C']" +
                                  "/dmndi:DMNStyle[@fontFamily='sans serif' and @fontSize='12']" +
                                  "/dmndi:StrokeColor[@red='9' and @green='252' and @blue='17']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-_E5D538FB-D142-4CC6-9F11-229A0A766B7C' and @dmnElementRef='_E5D538FB-D142-4CC6-9F11-229A0A766B7C']" +
                                  "/dmndi:DMNStyle[@fontFamily='sans serif' and @fontSize='12']" +
                                  "/dmndi:FontColor[@red='39' and @green='16' and @blue='237']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-_E5D538FB-D142-4CC6-9F11-229A0A766B7C' and @dmnElementRef='_E5D538FB-D142-4CC6-9F11-229A0A766B7C']" +
                                  "/dc:Bounds[@x='556' and @y='156' and @width='200' and @height='100']");
    }

    @Test
    public void testDataTypeSimple() throws Exception {
        final String expected = loadResource("data-type-simple.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:itemDefinition[@id='_23710223-C425-42A3-94A0-02EFADB69075' and @name='myType' and @isCollection='false']" +
                                  "/dmn:typeRef[text()='number']");
    }

    @Test
    public void testDataTypeSimpleList() throws Exception {
        final String expected = loadResource("data-type-simple-list.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:itemDefinition[@id='_23710223-C425-42A3-94A0-02EFADB69075' and @name='myType' and @isCollection='true']" +
                                  "/dmn:typeRef[text()='number']");
    }

    @Test
    public void testDataTypeSimpleConstraintEnumeration() throws Exception {
        final String expected = loadResource("data-type-simple-constraint-enumeration.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:itemDefinition[@id='_23710223-C425-42A3-94A0-02EFADB69075' and @name='myType' and @isCollection='false']" +
                                  "/dmn:typeRef[text()='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:itemDefinition[@id='_23710223-C425-42A3-94A0-02EFADB69075' and @name='myType' and @isCollection='false']" +
                                  "/dmn:allowedValues[@kie:constraintType='enumeration' and @id='_1207D208-4944-4AF3-992B-4F55CFFF28B7']" +
                                  "/dmn:text[text()='1, 2']");
    }

    @Test
    public void testDataTypeSimpleConstraintExpression() throws Exception {
        final String expected = loadResource("data-type-simple-constraint-expression.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:itemDefinition[@id='_23710223-C425-42A3-94A0-02EFADB69075' and @name='myType' and @isCollection='false']" +
                                  "/dmn:typeRef[text()='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:itemDefinition[@id='_23710223-C425-42A3-94A0-02EFADB69075' and @name='myType' and @isCollection='false']" +
                                  "/dmn:allowedValues[@kie:constraintType='expression' and @id='_0AB2C1E7-A6DF-434C-AA83-394E202C8B2B']" +
                                  "/dmn:text[text()='> 0']");
    }

    @Test
    public void testDataTypeSimpleConstraintRange() throws Exception {
        final String expected = loadResource("data-type-simple-constraint-range.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:itemDefinition[@id='_23710223-C425-42A3-94A0-02EFADB69075' and @name='myType' and @isCollection='false']" +
                                  "/dmn:typeRef[text()='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:itemDefinition[@id='_23710223-C425-42A3-94A0-02EFADB69075' and @name='myType' and @isCollection='false']" +
                                  "/dmn:allowedValues[@kie:constraintType='range' and @id='_0509A443-BE5E-4DF5-8701-FBA42ABFF060']" +
                                  "/dmn:text[text()='[5..10)']");
    }

    @Test
    public void testDataTypeStructure() throws Exception {
        final String expected = loadResource("data-type-structure.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:itemDefinition[@id='_23710223-C425-42A3-94A0-02EFADB69075' and @name='myType1' and @isCollection='false']" +
                                  "/dmn:itemComponent[@id='_AB996B18-1E1E-4C29-800E-877FA72B450E' and @name='field1' and @isCollection='false']" +
                                  "/dmn:typeRef[text()='number']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:itemDefinition[@id='_23710223-C425-42A3-94A0-02EFADB69075' and @name='myType1' and @isCollection='false']" +
                                  "/dmn:itemComponent[@id='_6241713A-5872-4B26-9989-C52EA41963CC' and @name='field2' and @isCollection='false']" +
                                  "/dmn:typeRef[text()='myType2']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:itemDefinition[@id='_9413D717-D167-485B-8677-73419892E905' and @name='myType2' and @isCollection='false']" +
                                  "/dmn:typeRef[text()='string']");
    }

    @Test
    public void testDecisionServiceEmpty() throws Exception {
        final String expected = loadResource("decision-service-empty.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testDMNModelWithoutDMNDI_KOGITO3696() throws Exception {
        final String expected = loadResource("KOGITO-3696 (DMN model without DMNDI - expected).xml");
        final String fixture = loadResource("KOGITO-3696 (DMN model without DMNDI - fixture).xml");
        final List<String> ignoredAttributes = asList("id", "dmnElementRef");

        setContent(fixture);

        selectAutomaticLayout();

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .withAttributeFilter(attr -> !ignoredAttributes.contains(attr.getName()))
                .areIdentical();
    }

    @Test
    public void testDecisionServiceWrongLayout_KOGITO2515() throws Exception {
        final String expected = loadResource("KOGITO-2515 (DMN model with decision services - expected).xml");
        final String fixture = loadResource("KOGITO-2515 (DMN model with decision services - fixture).xml");
        final List<String> ignoredAttributes = asList("id", "dmnElementRef", "href");

        setContent(fixture);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .withAttributeFilter(attr -> !ignoredAttributes.contains(attr.getName()))
                .areIdentical();
    }

    @Test
    public void testDecisionTableInputClauseConstraints_KOGITO369() throws Exception {
        final String expected = loadResource("KOGITO-369 (Decision Table Input Clause constraints).xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_6BFBA4CD-6484-4AAE-9E28-CF54DA401C3D']" +
                                  "/dmn:decisionTable[@id='_3B5C4F51-7F86-4C25-AE10-565560188523']" +
                                  "/dmn:input[@id='_4FCD3B90-542B-4518-B541-23CEFC004D9E']" +
                                  "/dmn:inputValues[@kie:constraintType='enumeration' and @id='_79588FEE-2BA1-4CAE-B553-D96C6B2CB615']" +
                                  "/dmn:text[text()='[1, 2, 3]']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_6BFBA4CD-6484-4AAE-9E28-CF54DA401C3D']" +
                                  "/dmn:decisionTable[@id='_3B5C4F51-7F86-4C25-AE10-565560188523']" +
                                  "/dmn:input[@id='_2FEFE4CA-44EE-4BF9-8CFC-CB26A17AD52E']" +
                                  "/dmn:inputValues[@kie:constraintType='expression' and @id='_4B2A8314-4EDC-4183-BBF7-98587DAFEBFC']" +
                                  "/dmn:text[text()='< 1000']");
    }

    @Test
    public void testDocumentationLinks_KOGITO674() throws Exception {
        final String expected = loadResource("KOGITO-674 (Documentation links).xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:inputData[@id='_12A7D464-8A59-4B38-949E-96E63A3FC916']" +
                                  "/dmn:extensionElements/kie:attachment[@name='google' and @url='www.google.co.uk']");
    }

    @Test
    public void testEmptyExpression_DROOLS4724() throws Exception {
        final String expected = loadResource("DROOLS-4724 (Empty expression).xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .nodesByXPath("/dmn:definitions" +
                                      "/dmn:decision" +
                                      "/dmn:literalExpression")
                .doNotExist();
    }

    @Test
    public void testDecisionServiceNodeLocations_KOGITO371() throws Exception {
        final String expected = loadResource("KOGITO-371 (Decision Service node locations).xml");
        setContent(expected);

        final String actual = twice(this::getContent); // Call the marshaller twice (to check if it's polluting node instances).
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-_4CEF4A0F-B605-4551-9403-7FA31C97DCD1']" +
                                  "/dc:Bounds[@x='670' and @y='153' and @width='200' and @height='200']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-_B067B665-3343-495F-8257-608689084A91']" +
                                  "/dc:Bounds[@x='510' and @y='265' and @width='100' and @height='50']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-_964EF7FB-010B-4698-9954-BB95ABBDF2A2']" +
                                  "/dc:Bounds[@x='720' and @y='265' and @width='100' and @height='50']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-_A531F162-5032-40CF-B53A-E5818BD2C21A']" +
                                  "/dc:Bounds[@x='720' and @y='195' and @width='100' and @height='50']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-_4C183144-7DAC-42FB-8243-A666FFDA2177']" +
                                  "/dc:Bounds[@x='930' and @y='195' and @width='100' and @height='50']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram" +
                                  "/dmndi:DMNShape[@id='dmnshape-drg-_93FD724C-DF9A-4B85-B048-6E26F28B987A']" +
                                  "/dc:Bounds[@x='930' and @y='265' and @width='100' and @height='50']");
    }

    @Ignore("No more compatible with React Based Boxed Expression")
    public void testListExpression_DROOLS5131() throws Exception {
        final String expected = loadResource("DROOLS-5131 (List expression).xml");
        setContent(expected);

        final ListSeleniumModel listModel = new ListSeleniumModel();
        listModel.setName("Decision-1");
        listModel.setItems(asList("1", "2"));
        appendBoxedListExpressionItem(listModel, "3");

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .nodesByXPath("/dmn:definitions" +
                                      "/dmn:decision[@id='_7BBC48CA-5D14-46C4-A5B5-0328CB9C7241']" +
                                      "/dmn:list[@id='_AB660F0F-C753-4652-B8ED-B7EF82951F68']")
                .containsAnyNodeHavingXPath("//dmn:literalExpression[1]/dmn:text[text()='1']")
                .containsAnyNodeHavingXPath("//dmn:literalExpression[2]/dmn:text[text()='2']")
                .containsAnyNodeHavingXPath("//dmn:literalExpression[3]/dmn:text[contains(text(),'3')]");
    }

    /**
     * This tests that the 'Payment Date' DMN model authored by a _third party_ editor can be
     * unmarshalled and marshalled by _our_ editor. However there are some differences between
     * the original XML and that generated by _our_ marshaller leading to significant sections
     * being ignored by the 'round-trip' comparison. These need investigating.
     */
    @Test
    public void testPaymentDateFile_KOGITO404() throws Exception {
        final String expected = loadResource("KOGITO-404 (Payment Date).xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        final DifferenceEvaluator firstEvaluator = DifferenceEvaluators.Default;
        final DifferenceEvaluator secondEvaluator = new DifferenceEvaluator() {
            @Override
            public ComparisonResult evaluate(final Comparison comparison,
                                             final ComparisonResult outcome) {
                if (outcome == ComparisonResult.EQUAL) {
                    return outcome;
                }

                final Node test = comparison.getTestDetails().getTarget();
                final NamedNodeMap testAttributes = test.getAttributes();
                final List<String> testNamespaceUris = extractNamespaceUris(testAttributes);
                final List<String> testAttributeValues = extractAttributeValues(testAttributes);

                final Node control = comparison.getControlDetails().getTarget();
                final NamedNodeMap controlAttributes = control.getAttributes();
                final List<String> controlNamespaceUris = extractNamespaceUris(controlAttributes);
                final List<String> controlAttributeValues = extractAttributeValues(controlAttributes);

                switch (comparison.getType()) {
                    case NAMESPACE_URI:
                    case NAMESPACE_PREFIX:
                        if (Objects.equals("definitions", test.getLocalName()) && Objects.equals("definitions", control.getLocalName())) {
                            testNamespaceUris.removeAll(controlNamespaceUris);

                            if (testNamespaceUris.isEmpty()) {
                                return ComparisonResult.SIMILAR;
                            } else {
                                return ComparisonResult.DIFFERENT;
                            }
                        }
                        break;
                    case ATTR_NAME_LOOKUP:
                    case ELEMENT_NUM_ATTRIBUTES:
                        if (Objects.equals("definitions", test.getLocalName()) && Objects.equals("definitions", control.getLocalName())) {
                            testAttributeValues.removeAll(controlAttributeValues);

                            if (testAttributeValues.isEmpty()) {
                                return ComparisonResult.SIMILAR;
                            } else if (testAttributeValues.size() == 1) {
                                final String testAttributeValue = testAttributeValues.get(0);
                                // According to the DMN1.2 Specification - 6.3.2 Definitions metamodel:-
                                // An instance of Definitions MAY specify a typeLanguage, which is a URI that identifies
                                // the default type language used in elements within the scope of this Definitions ...
                                // If unspecified, the default typeLanguage is FEEL.
                                // It is therefore not wrong for us to include typeLanguage, just not necessary.
                                if (testAttributeValue.startsWith("typeLanguage") && testAttributeValue.endsWith(FEEL.getUri())) {
                                    return ComparisonResult.SIMILAR;
                                }
                            }
                            return ComparisonResult.DIFFERENT;
                        }
                        if (Objects.equals("itemDefinition", test.getLocalName()) && Objects.equals("itemDefinition", control.getLocalName())) {
                            testAttributeValues.removeAll(controlAttributeValues);

                            if (testAttributeValues.isEmpty()) {
                                return ComparisonResult.SIMILAR;
                            } else if (testAttributeValues.size() == 1) {
                                final String testAttributeValue = testAttributeValues.get(0);
                                // According to the DMN1.2 Specification 7.3.2 - ItemDefinition metamodel:-
                                // ...an instance of ItemDefinition HAS a name and an OPTIONAL id
                                // It is therefore not wrong for us to include id, just not necessary.
                                if (testAttributeValue.startsWith("id")) {
                                    return ComparisonResult.SIMILAR;
                                }
                            }
                            return ComparisonResult.DIFFERENT;
                        }
                        if (Objects.equals("contextEntry", test.getLocalName()) && Objects.equals("contextEntry", control.getLocalName())) {
                            controlAttributeValues.removeAll(testAttributeValues);

                            if (controlAttributeValues.isEmpty()) {
                                return ComparisonResult.SIMILAR;
                            } else if (controlAttributeValues.size() == 1) {
                                final String controlAttributeValue = controlAttributeValues.get(0);
                                // According to the DMN1.2 Specification 10.5.2 - ContextEntry metamodel:-
                                // ContextEntry is a specialization of DMNElement, from which it INHERITS the OPTIONAL id...
                                // It is therefore correct for us to exclude the id.
                                if (controlAttributeValue.startsWith("id")) {
                                    return ComparisonResult.SIMILAR;
                                }
                            }
                            return ComparisonResult.DIFFERENT;
                        }
                        if (Objects.equals("decisionTable", test.getLocalName()) && Objects.equals("decisionTable", control.getLocalName())) {
                            testAttributeValues.removeAll(controlAttributeValues);

                            if (testAttributeValues.isEmpty()) {
                                return ComparisonResult.SIMILAR;
                            } else if (testAttributeValues.size() == 1) {
                                final String testAttributeValue = testAttributeValues.get(0);
                                // According to the DMN1.2 Specification 8.3.1 - Decision Table metamodel:-
                                // ...It has a preferredOrientation, which SHALL be one of the enumerated DecisionTableOrientation.
                                // It is therefore not wrong for us to include preferredOrientation and seems mandatory.
                                if (testAttributeValue.startsWith("preferredOrientation") && testAttributeValue.endsWith("Rule-as-Row")) {
                                    return ComparisonResult.SIMILAR;
                                }
                            }
                            return ComparisonResult.DIFFERENT;
                        }
                        if (Objects.equals("inputExpression", test.getLocalName()) && Objects.equals("inputExpression", control.getLocalName())) {
                            testAttributeValues.removeAll(controlAttributeValues);

                            if (testAttributeValues.isEmpty()) {
                                return ComparisonResult.SIMILAR;
                            } else if (testAttributeValues.size() == 1) {
                                final String testAttributeValue = testAttributeValues.get(0);
                                // According to the DMN1.2 Specification 8.3.2 - Decision Table Input and Output metamodel:-
                                // ...An instance of InputClause is made of an optional inputExpression... [where an
                                // inputExpression is an Expression]. There is not mention as to whether the Expression
                                // inherits its id or needs one explicitly defined.
                                if (testAttributeValue.startsWith("id")) {
                                    return ComparisonResult.SIMILAR;
                                }
                            }
                            return ComparisonResult.DIFFERENT;
                        }
                        if (Objects.equals("outputEntry", test.getLocalName()) && Objects.equals("outputEntry", control.getLocalName())) {
                            controlAttributeValues.removeAll(testAttributeValues);

                            if (controlAttributeValues.isEmpty()) {
                                return ComparisonResult.SIMILAR;
                            } else if (controlAttributeValues.size() == 1) {
                                final String controlAttributeValue = controlAttributeValues.get(0);
                                // According to the DMN1.2 Specification 8.3.2 - Decision Table Input and Output metamodel:-
                                // OutputClause does not appear to have an expressionLanguage property; only the
                                // UnaryTests that is encapsulated by OutputClause supports it. Is this an issue
                                // with Trisotech's marshaller?
                                if (controlAttributeValue.startsWith("expressionLanguage") && controlAttributeValue.endsWith(FEEL.getUri())) {
                                    return ComparisonResult.SIMILAR;
                                }
                            }
                            return ComparisonResult.DIFFERENT;
                        }
                        break;
                }
                return outcome;
            }

            private List<String> extractNamespaceUris(final NamedNodeMap attributes) {
                final List<String> namespaceUris = new ArrayList<>();
                for (int i = 0; i < attributes.getLength(); i++) {
                    if (attributes.item(i) instanceof Attr) {
                        final Attr attribute = (Attr) attributes.item(i);
                        if (Objects.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, attribute.getNamespaceURI())) {
                            namespaceUris.add(attribute.getValue());
                        }
                    }
                }
                return namespaceUris;
            }

            private List<String> extractAttributeValues(final NamedNodeMap attributes) {
                final List<String> attributeValues = new ArrayList<>();
                for (int i = 0; i < attributes.getLength(); i++) {
                    if (attributes.item(i) instanceof Attr) {
                        final Attr attribute = (Attr) attributes.item(i);
                        if (Objects.isNull(attribute.getNamespaceURI())) {
                            attributeValues.add(attribute.getName() + "=" + attribute.getValue());
                        }
                    }
                }
                return attributeValues;
            }
        };

        final Predicate<Node> nodeFilter = node -> {
            final String localName = node.getLocalName();
            final String namespaceURI = node.getNamespaceURI();
            if (Objects.equals(namespaceURI, DMN.getUri())) {
                // We don't marshall Decision Services the same as Trisotech so ignore comparison.
                if (Objects.equals(localName, "decisionService")) {
                    return false;
                }
                // The source file lacks our extension elements so ignore comparison.
                if (Objects.equals(localName, "extensionElements")) {
                    return false;
                }
                // See https://issues.redhat.com/browse/DROOLS-5045
                // Our marshaller does not add annotations or annotationEntries for DMN1.2
                if (Objects.equals(localName, "annotation") || Objects.equals(localName, "annotationEntry")) {
                    return false;
                }
            }
            if (Objects.equals(namespaceURI, DMNModelInstrumentedBase.Namespace.DMNDI.getUri())) {
                // Exclude DMNDI from the comparison as our use of DMNShape.id, DMNStyle and Extensions are
                // different to Trisotech which makes comparison very difficult so ignore comparison.
                return false;
            }

            return true;
        };

        final Predicate<Attr> attributeFilter = attr -> {
            final String localName = attr.getLocalName();
            final String namespaceURI = Objects.nonNull(attr.getNamespaceURI()) ? attr.getNamespaceURI() : attr.getOwnerElement().getNamespaceURI();
            if (Objects.equals(namespaceURI, "http://www.trisotech.com/2015/triso/modeling")) {
                // Exclude Trisotech extensions
                return false;
            }
            if (Objects.equals(namespaceURI, "http://www.trisotech.com/2016/triso/dmn")) {
                // Exclude Trisotech extensions
                return false;
            }

            if (Objects.equals(namespaceURI, DMN.getUri())) {
                // TODO {manstis} Check DMN specification.
                // Our marshaller does not support label.
                if (Objects.equals(localName, "label")) {
                    return false;
                }
            }
            return true;
        };

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreChildNodesOrder()
                .ignoreWhitespace()
                .withDifferenceEvaluator(DifferenceEvaluators.chain(firstEvaluator, secondEvaluator))
                .withNodeFilter(nodeFilter)
                .withAttributeFilter(attributeFilter)
                .areSimilar();
    }

    @Test
    public void testDecisionTableDefaultOutputValueMissing_KOGITO1181() throws Exception {
        final String expected = loadResource("KOGITO-1181 (Default Output Value - missing).xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_97F03625-C4CB-4B07-8656-5807C18FA7EA']" +
                                  "/dmn:decisionTable[@id='_500030B0-6E85-4E9F-ADD9-CD6B6F400CBD']" +
                                  "/dmn:output[@id='_9831672B-26F3-4C2A-A4BF-A874A2BFDF9C']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .doesNotHaveXPath("/dmn:definitions" +
                                          "/dmn:decision[@id='_97F03625-C4CB-4B07-8656-5807C18FA7EA']" +
                                          "/dmn:decisionTable[@id='_500030B0-6E85-4E9F-ADD9-CD6B6F400CBD']" +
                                          "/dmn:output[@id='_9831672B-26F3-4C2A-A4BF-A874A2BFDF9C']" +
                                          "/dmn:outputValues");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .doesNotHaveXPath("/dmn:definitions" +
                                          "/dmn:decision[@id='_97F03625-C4CB-4B07-8656-5807C18FA7EA']" +
                                          "/dmn:decisionTable[@id='_500030B0-6E85-4E9F-ADD9-CD6B6F400CBD']" +
                                          "/dmn:output[@id='_9831672B-26F3-4C2A-A4BF-A874A2BFDF9C']" +
                                          "/dmn:defaultOutputEntry");
    }

    @Test
    public void testDecisionTableDefaultOutputValuePresent_KOGITO1181() throws Exception {
        final String expected = loadResource("KOGITO-1181 (Default Output Value - present).xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_97F03625-C4CB-4B07-8656-5807C18FA7EA']" +
                                  "/dmn:decisionTable[@id='_500030B0-6E85-4E9F-ADD9-CD6B6F400CBD']" +
                                  "/dmn:output[@id='_9831672B-26F3-4C2A-A4BF-A874A2BFDF9C']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_97F03625-C4CB-4B07-8656-5807C18FA7EA']" +
                                  "/dmn:decisionTable[@id='_500030B0-6E85-4E9F-ADD9-CD6B6F400CBD']" +
                                  "/dmn:output[@id='_9831672B-26F3-4C2A-A4BF-A874A2BFDF9C']" +
                                  "/dmn:outputValues" +
                                  "/dmn:text[text()='output']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_97F03625-C4CB-4B07-8656-5807C18FA7EA']" +
                                  "/dmn:decisionTable[@id='_500030B0-6E85-4E9F-ADD9-CD6B6F400CBD']" +
                                  "/dmn:output[@id='_9831672B-26F3-4C2A-A4BF-A874A2BFDF9C']" +
                                  "/dmn:defaultOutputEntry" +
                                  "/dmn:text[text()='default output']");
    }

    @Test
    public void testDecisionTableSingleOutputClauseTypeRef_DROOLS5178() throws Exception {
        final String expected = loadResource("DROOLS-5178 (Single Output Clause typeRef).xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_DB91470A-68BD-432B-ADDD-6C8A6B134227']" +
                                  "/dmn:decisionTable[@id='_3020A76F-53F3-4523-B48D-D8BE634178AF']" +
                                  "/dmn:output[@id='_76010FA4-0EB5-4B97-AECE-184EB03BCA50']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_DB91470A-68BD-432B-ADDD-6C8A6B134227']" +
                                  "/dmn:variable[@id='_78742296-0BBA-45E4-88BE-29887F6BF819' and @typeRef='string']");
    }

    @Test
    public void testRuleAnnotationsInDecisionTable_DROOLS5045() throws Exception {
        final String expected = loadResource("decision-expression-decision-table-rule-annotations.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:decisionTable[@id='_1B2AE7B6-BF51-472E-99CB-A67875CE1B57']" +
                                  "/dmn:annotation[@name='annotation-1']");
        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:decisionTable[@id='_1B2AE7B6-BF51-472E-99CB-A67875CE1B57']" +
                                  "/dmn:annotation[@name='annotation-2']");

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:decisionTable[@id='_1B2AE7B6-BF51-472E-99CB-A67875CE1B57']" +
                                  "/dmn:rule[@id='_2D2D5ABD-3C71-40E9-B493-73CDA49B3F53']" +
                                  "/dmn:annotationEntry[1]" +
                                  "/dmn:text[text()='A1']");

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:decisionTable[@id='_1B2AE7B6-BF51-472E-99CB-A67875CE1B57']" +
                                  "/dmn:rule[@id='_2D2D5ABD-3C71-40E9-B493-73CDA49B3F53']" +
                                  "/dmn:annotationEntry[2]" +
                                  "/dmn:text[text()='B1']");
    }

    /**
     * Previous DMN files generated by our tool does not have RuleAnnotation, only description.
     * This test ensures that RuleAnnotation is created when that older files is opened.
     */
    @Test
    public void testRuleAnnotationsInDecisionTableFromOlderVersion_DROOLS5045() throws Exception {
        final String expected = loadResource("decision-expression-decision-table-without-rule-annotations.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:decisionTable[@id='_1B2AE7B6-BF51-472E-99CB-A67875CE1B57']" +
                                  "/dmn:annotation");

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_395E1E92-765B-47F5-9387-179B839277B1']" +
                                  "/dmn:decisionTable[@id='_1B2AE7B6-BF51-472E-99CB-A67875CE1B57']" +
                                  "/dmn:rule[@id='_2D2D5ABD-3C71-40E9-B493-73CDA49B3F53']" +
                                  "/dmn:annotationEntry" +
                                  "/dmn:text[text()='the description']");
    }

    /**
     * This test increases KOGITO-4265 coverage
     * Input parameters order should be the same as in the Properties panel so in the DMN file
     */
    @Test
    public void testDecisionServiceParametersOrder() throws Exception {
        final String decisionService = "DecisionService-1";
        final String expected = loadResource("decision-service-three-inputs.xml");

        final String xInputHref = "#_51D20318-4883-49DC-AE68-42131A20F8B3";
        final String yInputHref = "#_4D49912C-2D15-4C30-A445-E0431AB5AB9C";
        final String zInputHref = "#_65560FB5-7AF0-4FCC-A8E8-8AEA2E307ABB";

        // assert original order
        assertDecisionServiceParametersOrder(expected, zInputHref, xInputHref, yInputHref);

        setContent(expected);

        decisionNavigator.selectItem(DecisionNavigatorXPathLocator.node(decisionService));

        expandPropertiesPanelDock();

        List<WebElement> inputs = waitUtils.waitUntilAllElementsAreVisible(
                PropertiesPanelXPathLocator.decisionServiceDetails(PropertiesPanelXPathLocator.DecisionServiceDetails.INPUT_DATA),
                "Input Data decision service details not found in properties panel");

        // assert order in UI is same as in the source
        assertThat(inputs)
                .extracting(in -> in.getText())
                .containsExactly("Z", "X", "Y");

        // assert order doesn't change when saving
        final String actual = getContent();
        assertDecisionServiceParametersOrder(actual, zInputHref, xInputHref, yInputHref);
    }

    @Test
    public void testKogito_4916() throws Exception {
        final String expected = loadResource("call-center.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        // inputs
        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("call"));
        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("employees"));
        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("incoming call"));
        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("office"));

        // decisions
        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("Accept Call"));
        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("Banned Phone Numbers"));
        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("Call Can Be Handled"));
        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("Call Purpose Accepted"));
        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("Is Banned"));

        // decision service
        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("Can Handle Call"));
    }

    private void assertDecisionServiceParametersOrder(final String dmnModelXml, final String... inputHrefs) {
        XmlAssert.assertThat(dmnModelXml)
                .withNamespaceContext(NAMESPACES)
                .nodesByXPath("/dmn:definitions/dmn:decisionService/dmn:inputData")
                .extracting(n -> n.getAttributes().getNamedItem("href").getNodeValue())
                .containsExactly(inputHrefs);
    }

    /**
     * This method serves as a reproducer for KOGITO-1181
     */
    private void setDecisionTableDefaultOutput(final DecisionTableSeleniumModel decisionTable) {
        decisionNavigator.selectItem(DecisionNavigatorXPathLocator.decisionTable(decisionTable.getName()));

        expandPropertiesPanelDock();

        final WebElement editor = getEditor();
        for (int i = 0; i < decisionTable.getInputsCount(); i++) {
            editor.sendKeys(Keys.ARROW_RIGHT);
        }

        editor.sendKeys(Keys.ARROW_UP);

        expandPropertiesPanelGroup("Default output");
        fillInProperty("Default output value", decisionTable.getDefaultOutput());
    }

    /**
     * This method extends coverage of DROOLS-5131
     */
    private void appendBoxedListExpressionItem(final ListSeleniumModel list, final String item) {
        decisionNavigator.selectItem(DecisionNavigatorXPathLocator.list(list.getName()));

        final WebElement editor = getEditor();
        for (int i = 0; i < list.getItems().size() - 1; i++) {
            editor.sendKeys(Keys.ARROW_DOWN);
        }

        // invoke context menu for adding an item
        editor.sendKeys(Keys.CONTROL, Keys.SPACE);

        waitUtils.waitUntilElementIsVisible(ContextMenuXPathLocator.insertBelow(),
                                            "Insert below entry in context menu not available")
                .click();

        // move to the new cell and start edit mode
        editor.sendKeys(Keys.ARROW_DOWN, Keys.ENTER);

        // put in new value and finish edit mode
        getAutocompleteEditor().sendKeys(item, Keys.TAB);
    }

    private void assertBKMFunctionCanBeOpened(final String nodeName) {
        decisionNavigator.selectItem(DecisionNavigatorXPathLocator.function(nodeName));

        assertThat(waitUtils.waitUntilElementIsVisible(EditorXPathLocator.expressionEditorTitle(),
                                                       "Expression editor can not be opened")
                           .getText())
                .as("Expression editor was not opened successfully")
                .isEqualTo(nodeName);
    }

    private <T> T twice(final Supplier<T> supplier) {
        supplier.get();
        return supplier.get();
    }

    private void expandPropertiesPanelDock() {
        propertiesPanel.findElement(xpath(".//button")).click();
    }

    private void expandPropertiesPanelGroup(final String groupName) {
        waitUtils.waitUntilElementIsVisible(PropertiesPanelXPathLocator.group(groupName),
                                            format("Group '%s' not found in Properties panel", groupName))
                .click();
    }

    private void fillInProperty(final String propertyName, final String value) {
        waitUtils.waitUntilElementIsVisible(PropertiesPanelXPathLocator.property(propertyName),
                                            format("Property '%s' not found in Properties panel", propertyName))
                .sendKeys(value + Keys.TAB); // TAB is used to flush the changes
    }
}
