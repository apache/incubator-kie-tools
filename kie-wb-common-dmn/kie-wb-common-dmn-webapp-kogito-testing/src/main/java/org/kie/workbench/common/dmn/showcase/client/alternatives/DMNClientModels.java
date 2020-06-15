/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.showcase.client.alternatives;

public class DMNClientModels {

    public static final String BASE_FILE = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<dmn:definitions xmlns:dmn=\"http://www.omg.org/spec/DMN/20180521/MODEL/\" xmlns=\"https://kiegroup.org/dmn/_C314453D-065F-4E41-920B-3B2E6E39A258\" xmlns:di=\"http://www.omg.org/spec/DMN/20180521/DI/\" xmlns:kie=\"http://www.drools.org/kie/dmn/1.2\" xmlns:feel=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" xmlns:dmndi=\"http://www.omg.org/spec/DMN/20180521/DMNDI/\" xmlns:dc=\"http://www.omg.org/spec/DMN/20180521/DC/\" id=\"_F17400FA-111C-4F39-A2A2-74B624F1B7E6\" name=\"Base Model\" expressionLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" typeLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" namespace=\"https://kiegroup.org/dmn/_C314453D-065F-4E41-920B-3B2E6E39A258\">&#xd;\n" +
            "  <dmn:extensionElements/>&#xd;\n" +
            "  <dmn:itemDefinition id=\"_F0729FCC-906F-43BF-BDC8-7ABBD096E2E5\" name=\"tMyCustomDataType\" isCollection=\"false\">&#xd;\n" +
            "    <dmn:itemComponent id=\"_3E5B0592-B4C3-4E97-BDC9-189BCC155EBB\" name=\"customProperty\" isCollection=\"false\">&#xd;\n" +
            "      <dmn:typeRef>string</dmn:typeRef>&#xd;\n" +
            "    </dmn:itemComponent>&#xd;\n" +
            "  </dmn:itemDefinition>&#xd;\n" +
            "  <dmn:inputData id=\"_8245A539-04DA-4F79-B11A-0E3ED4869F93\" name=\"My Input\">&#xd;\n" +
            "    <dmn:extensionElements/>&#xd;\n" +
            "    <dmn:variable id=\"_977104AF-D216-4610-9970-2019F6AADEF7\" name=\"My Input\" typeRef=\"tMyCustomDataType\"/>&#xd;\n" +
            "  </dmn:inputData>&#xd;\n" +
            "  <dmndi:DMNDI>&#xd;\n" +
            "    <dmndi:DMNDiagram>&#xd;\n" +
            "      <di:extension>&#xd;\n" +
            "        <kie:ComponentsWidthsExtension/>&#xd;\n" +
            "      </di:extension>&#xd;\n" +
            "      <dmndi:DMNShape id=\"dmnshape-_8245A539-04DA-4F79-B11A-0E3ED4869F93\" dmnElementRef=\"_8245A539-04DA-4F79-B11A-0E3ED4869F93\" isCollapsed=\"false\">&#xd;\n" +
            "        <dmndi:DMNStyle>&#xd;\n" +
            "          <dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/>&#xd;\n" +
            "          <dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/>&#xd;\n" +
            "          <dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/>&#xd;\n" +
            "        </dmndi:DMNStyle>&#xd;\n" +
            "        <dc:Bounds x=\"251\" y=\"174\" width=\"100\" height=\"50\"/>&#xd;\n" +
            "        <dmndi:DMNLabel/>&#xd;\n" +
            "      </dmndi:DMNShape>&#xd;\n" +
            "    </dmndi:DMNDiagram>&#xd;\n" +
            "  </dmndi:DMNDI>&#xd;\n" +
            "</dmn:definitions>";

    public static final String MODEL_WITH_IMPORTS ="<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<dmn:definitions xmlns:dmn=\"http://www.omg.org/spec/DMN/20180521/MODEL/\" xmlns=\"https://kiegroup.org/dmn/_DFD02668-B34B-4DC3-B950-0138AE3F3531\" xmlns:included1=\"https://kiegroup.org/dmn/_C314453D-065F-4E41-920B-3B2E6E39A258\" xmlns:di=\"http://www.omg.org/spec/DMN/20180521/DI/\" xmlns:kie=\"http://www.drools.org/kie/dmn/1.2\" xmlns:feel=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" xmlns:dmndi=\"http://www.omg.org/spec/DMN/20180521/DMNDI/\" xmlns:dc=\"http://www.omg.org/spec/DMN/20180521/DC/\" id=\"_AADAB3B3-8E20-403E-B64B-C21FBD907844\" name=\"Model With Imports\" expressionLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" typeLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" namespace=\"https://kiegroup.org/dmn/_DFD02668-B34B-4DC3-B950-0138AE3F3531\">&#xd;\n" +
            "  <dmn:extensionElements/>&#xd;\n" +
            "  <dmn:import id=\"_783618F9-8C99-49F3-ADA4-A229F5138710\" name=\"Base Model Imported\" namespace=\"https://kiegroup.org/dmn/_C314453D-065F-4E41-920B-3B2E6E39A258\" locationURI=\"Base Model.dmn\" importType=\"http://www.omg.org/spec/DMN/20180521/MODEL/\"/>&#xd;\n" +
            "  <dmn:decision id=\"_73FC571F-191F-4FFD-B757-2B1108C0A33F\" name=\"Local Decision\">&#xd;\n" +
            "    <dmn:extensionElements/>&#xd;\n" +
            "    <dmn:variable id=\"_5F5C70DC-08E4-4080-BFAE-46B402DE968D\" name=\"Local Decision\" typeRef=\"boolean\"/>&#xd;\n" +
            "    <dmn:informationRequirement id=\"_9901DDBA-D046-409B-881B-A961C90517F6\">&#xd;\n" +
            "      <dmn:requiredInput href=\"https://kiegroup.org/dmn/_C314453D-065F-4E41-920B-3B2E6E39A258#_8245A539-04DA-4F79-B11A-0E3ED4869F93\"/>&#xd;\n" +
            "    </dmn:informationRequirement>&#xd;\n" +
            "    <dmn:decisionTable id=\"_84404E41-97C0-45E2-9208-B5E8E4F6FF01\" hitPolicy=\"UNIQUE\" preferredOrientation=\"Rule-as-Row\">&#xd;\n" +
            "      <dmn:input id=\"_46ED11C4-4FB8-4BCD-BF07-2229ED1584F6\">&#xd;\n" +
            "        <dmn:inputExpression id=\"_A8C23A5F-D961-4C90-A216-430F0C153B15\" typeRef=\"boolean\">&#xd;\n" +
            "          <dmn:text>Base Model Imported.My Input</dmn:text>&#xd;\n" +
            "        </dmn:inputExpression>&#xd;\n" +
            "      </dmn:input>&#xd;\n" +
            "      <dmn:output id=\"_91EF1652-3440-49CE-98FE-2641F9346A48\"/>&#xd;\n" +
            "      <dmn:annotation name=\"annotation-1\"/>&#xd;\n" +
            "      <dmn:rule id=\"_E307C538-A5E3-4046-9CDB-AC73FBDBEAEA\">&#xd;\n" +
            "        <dmn:inputEntry id=\"_BAE22F3C-331D-4BB0-B836-5057FCA11BDE\">&#xd;\n" +
            "          <dmn:text>true</dmn:text>&#xd;\n" +
            "        </dmn:inputEntry>&#xd;\n" +
            "        <dmn:outputEntry id=\"_22D5C019-813E-4394-9A09-6110C5D3859C\">&#xd;\n" +
            "          <dmn:text>true</dmn:text>&#xd;\n" +
            "        </dmn:outputEntry>&#xd;\n" +
            "        <dmn:annotationEntry>&#xd;\n" +
            "          <dmn:text>all true!</dmn:text>&#xd;\n" +
            "        </dmn:annotationEntry>&#xd;\n" +
            "      </dmn:rule>&#xd;\n" +
            "      <dmn:rule id=\"_983F9362-060A-4AA9-8586-F2A27F022F46\">&#xd;\n" +
            "        <dmn:inputEntry id=\"_A8D20584-36E7-45BA-B2E9-FFED24553AA5\">&#xd;\n" +
            "          <dmn:text>false</dmn:text>&#xd;\n" +
            "        </dmn:inputEntry>&#xd;\n" +
            "        <dmn:outputEntry id=\"_EF049310-62CD-4E27-AB85-F84F961BE74F\">&#xd;\n" +
            "          <dmn:text>false</dmn:text>&#xd;\n" +
            "        </dmn:outputEntry>&#xd;\n" +
            "        <dmn:annotationEntry>&#xd;\n" +
            "          <dmn:text>all false!</dmn:text>&#xd;\n" +
            "        </dmn:annotationEntry>&#xd;\n" +
            "      </dmn:rule>&#xd;\n" +
            "    </dmn:decisionTable>&#xd;\n" +
            "  </dmn:decision>&#xd;\n" +
            "  <dmndi:DMNDI>&#xd;\n" +
            "    <dmndi:DMNDiagram>&#xd;\n" +
            "      <di:extension>&#xd;\n" +
            "        <kie:ComponentsWidthsExtension>&#xd;\n" +
            "          <kie:ComponentWidths dmnElementRef=\"_84404E41-97C0-45E2-9208-B5E8E4F6FF01\">&#xd;\n" +
            "            <kie:width>50.0</kie:width>&#xd;\n" +
            "            <kie:width>341.0</kie:width>&#xd;\n" +
            "            <kie:width>100.0</kie:width>&#xd;\n" +
            "            <kie:width>441.0</kie:width>&#xd;\n" +
            "          </kie:ComponentWidths>&#xd;\n" +
            "        </kie:ComponentsWidthsExtension>&#xd;\n" +
            "      </di:extension>&#xd;\n" +
            "      <dmndi:DMNShape id=\"dmnshape-included1:_8245A539-04DA-4F79-B11A-0E3ED4869F93\" dmnElementRef=\"included1:_8245A539-04DA-4F79-B11A-0E3ED4869F93\" isCollapsed=\"false\">&#xd;\n" +
            "        <dmndi:DMNStyle>&#xd;\n" +
            "          <dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/>&#xd;\n" +
            "          <dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/>&#xd;\n" +
            "          <dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/>&#xd;\n" +
            "        </dmndi:DMNStyle>&#xd;\n" +
            "        <dc:Bounds x=\"631\" y=\"289\" width=\"100\" height=\"50\"/>&#xd;\n" +
            "        <dmndi:DMNLabel/>&#xd;\n" +
            "      </dmndi:DMNShape>&#xd;\n" +
            "      <dmndi:DMNShape id=\"dmnshape-_73FC571F-191F-4FFD-B757-2B1108C0A33F\" dmnElementRef=\"_73FC571F-191F-4FFD-B757-2B1108C0A33F\" isCollapsed=\"false\">&#xd;\n" +
            "        <dmndi:DMNStyle>&#xd;\n" +
            "          <dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/>&#xd;\n" +
            "          <dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/>&#xd;\n" +
            "          <dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/>&#xd;\n" +
            "        </dmndi:DMNStyle>&#xd;\n" +
            "        <dc:Bounds x=\"631\" y=\"113\" width=\"100\" height=\"50\"/>&#xd;\n" +
            "        <dmndi:DMNLabel/>&#xd;\n" +
            "      </dmndi:DMNShape>&#xd;\n" +
            "      <dmndi:DMNEdge id=\"dmnedge-_9901DDBA-D046-409B-881B-A961C90517F6\" dmnElementRef=\"_9901DDBA-D046-409B-881B-A961C90517F6\">&#xd;\n" +
            "        <di:waypoint x=\"681\" y=\"314\"/>&#xd;\n" +
            "        <di:waypoint x=\"681\" y=\"163\"/>&#xd;\n" +
            "      </dmndi:DMNEdge>&#xd;\n" +
            "    </dmndi:DMNDiagram>&#xd;\n" +
            "  </dmndi:DMNDI>&#xd;\n" +
            "</dmn:definitions>";
}
