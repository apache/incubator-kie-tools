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

export const sumBkm = `<definitions 
  xmlns="https://www.omg.org/spec/DMN/20230324/MODEL/"
  xmlns:dmndi="https://www.omg.org/spec/DMN/20230324/DMNDI/" 
  xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" 
  xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" 
  xmlns:kie="https://kie.apache.org/dmn/extensions/1.0"
  expressionLanguage="https://www.omg.org/spec/DMN/20211108/FEEL/"
  typeLanguage="http://www.omg.org/spec/DMN/20211108/FEEL/"
  namespace="https://kie.apache.org/dmn/_923784BD-CD31-488A-9C31-C1A83C5483C0" 
  id="_0E6F4D88-B955-404B-A31A-72BB7E1E6A77" 
  name="Sum BKM">
<businessKnowledgeModel name="Sum BKM" id="_3646600D-035F-47B9-8DC6-3FABE844234F">
  <variable name="New BKM" id="_94BAC9FD-8519-49F9-855A-F4FCBACD2A04" typeRef="number" />
  <functionDefinition label="Sum BKM" kind="FEEL" typeRef="number">
    <formalParameter id="_F3A70A69-09FF-417C-8F6C-BD9129231377" name="a" typeRef="number" />
    <formalParameter id="_E51B1C6E-896B-41C0-B03E-85B3CD7B6655" name="b" typeRef="number" />
    <literalExpression id="_FA1EE22B-CC6B-499F-95EE-68158A2F233B" label="Expression Name" typeRef="&lt;Undefined&gt;">
      <text>a + b</text>
    </literalExpression>
  </functionDefinition>
</businessKnowledgeModel>
<dmndi:DMNDI>
  <dmndi:DMNDiagram>
    <dmndi:DMNShape id="_3506A087-AB60-428D-9C75-1E5F5D6F0947" dmnElementRef="_3646600D-035F-47B9-8DC6-3FABE844234F" isCollapsed="false" isListedInputData="false">
      <dc:Bounds x="40" y="60" width="160" height="80" />
    </dmndi:DMNShape>
    <di:extension>
      <kie:ComponentsWidthsExtension>
        <kie:ComponentWidths dmnElementRef="_FA1EE22B-CC6B-499F-95EE-68158A2F233B">
          <kie:width>190</kie:width>
        </kie:ComponentWidths>
      </kie:ComponentsWidthsExtension>
    </di:extension>
  </dmndi:DMNDiagram>
</dmndi:DMNDI>
</definitions>
`;

export const sumDiffDs = `<definitions 
  xmlns="https://www.omg.org/spec/DMN/20230324/MODEL/" 
  xmlns:dmndi="https://www.omg.org/spec/DMN/20230324/DMNDI/"
  xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" 
  xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" 
  xmlns:kie="https://kie.apache.org/dmn/extensions/1.0"
  expressionLanguage="https://www.omg.org/spec/DMN/20211108/FEEL/" 
  typeLanguage="http://www.omg.org/spec/DMN/20211108/FEEL/"
  namespace="https://kie.apache.org/dmn/_D19B0015-2CBD-4BA8-84A9-5F554D84A9E1" 
  id="_F360CECB-5DF2-4546-A7A9-E5ECF0F5A872" 
  name="Sum and Diff DS">
<decisionService name="Sum and Diff DS" id="_721B7634-7227-42B4-AAA4-17DE60A7A967">
  <variable name="New Decision Service" id="_A996D4BD-CF69-4CC5-89C0-37CF2BD6BA6E" typeRef="context" />
  <inputData href="#_18FAE913-2B2A-479D-A53A-14A2D57C46E4" />
  <inputData href="#_0D6E83A0-171F-4D7C-B0DF-8464DDE05FD1" />
  <outputDecision href="#_1991FB34-1253-4A54-AD3D-89697938DDFA" />
  <outputDecision href="#_05621ED4-9236-47F1-B93A-164A4527B136" />
</decisionService>
<decision name="Sum" id="_05621ED4-9236-47F1-B93A-164A4527B136">
  <variable name="New Decision" id="_88ECA891-3837-40E6-BCBF-77BF24B26FD0" typeRef="number" />
  <informationRequirement id="_81BD49F4-E279-48A4-AC71-89CE92EEDE9C">
    <requiredInput href="#_18FAE913-2B2A-479D-A53A-14A2D57C46E4" />
  </informationRequirement>
  <informationRequirement id="_115B4C89-EBA8-4136-A70E-9D303D8E92C5">
    <requiredInput href="#_0D6E83A0-171F-4D7C-B0DF-8464DDE05FD1" />
  </informationRequirement>
  <literalExpression id="_2812E0EA-FD32-480F-89CD-D0FE5FA02172" label="Sum" typeRef="&lt;Undefined&gt;">
    <text>a + b</text>
  </literalExpression>
</decision>
<inputData name="a" id="_18FAE913-2B2A-479D-A53A-14A2D57C46E4">
  <variable name="New Input Data" id="_250DB850-97F5-45C8-AFBB-207218BDAF7E" typeRef="number" />
</inputData>
<inputData name="b" id="_0D6E83A0-171F-4D7C-B0DF-8464DDE05FD1">
  <variable name="New Input Data" id="_0FD3E491-93E4-4AC1-AB46-19A6C6A3E4F1" typeRef="number" />
</inputData>
<decision name="Diff" id="_1991FB34-1253-4A54-AD3D-89697938DDFA">
  <variable name="New Decision" id="_47AD6FB1-1892-4E27-B46D-0AF95B18602E" typeRef="number" />
  <informationRequirement id="_5CA8EA11-FDFF-410A-AB4F-AA7ACB9F7FDC">
    <requiredInput href="#_18FAE913-2B2A-479D-A53A-14A2D57C46E4" />
  </informationRequirement>
  <informationRequirement id="_D9E5FB0F-1A8C-4733-BBAB-E4A55113F806">
    <requiredInput href="#_0D6E83A0-171F-4D7C-B0DF-8464DDE05FD1" />
  </informationRequirement>
  <literalExpression id="_C6BBFC2E-695F-4668-8A0E-D175FE7302C7" label="Diff" typeRef="&lt;Undefined&gt;">
    <text>a - b</text>
  </literalExpression>
</decision>
<itemDefinition id="_842F4E16-C9A2-47D4-A221-081673B98C2D" name="SumDiffContext" isCollection="false">
  <itemComponent id="_4882D4FB-2146-4198-8640-33B951B893A7" name="Sum" isCollection="false">
    <typeRef>number</typeRef>
  </itemComponent>
  <itemComponent id="_3EF27B7D-3378-429E-B6DB-B856F5EB0AE7" name="Diff" isCollection="false">
    <typeRef>number</typeRef>
  </itemComponent>
</itemDefinition>
<itemDefinition id="_2B4E9593-3239-4E04-A213-345F0AA0AF9E" name="Fine" isCollection="false">
  <itemComponent id="_3EF27B7D-3378-429E-B6DB-B856F5EB0AEE" name="Value" isCollection="false">
    <typeRef>number</typeRef>
    <allowedValues id="_5BD13D9D-412F-4E6B-914A-3D8AAAC6A701">
      <text>100,500,1000</text>
    </allowedValues>
  </itemComponent>
</itemDefinition>
<dmndi:DMNDI>
  <dmndi:DMNDiagram>
    <dmndi:DMNShape id="_495CB0A7-017A-4A2F-A4D7-3133C82708E0" dmnElementRef="_721B7634-7227-42B4-AAA4-17DE60A7A967" isCollapsed="false" isListedInputData="false">
      <dc:Bounds x="80" y="120" width="520" height="320" />
      <dmndi:DMNDecisionServiceDividerLine>
        <di:waypoint x="80" y="280" />
        <di:waypoint x="500" y="280" />
      </dmndi:DMNDecisionServiceDividerLine>
    </dmndi:DMNShape>
    <dmndi:DMNShape id="_80C42026-FF56-44C6-84F9-F7A03686F5E8" dmnElementRef="_05621ED4-9236-47F1-B93A-164A4527B136" isCollapsed="false" isListedInputData="false">
      <dc:Bounds x="140" y="180" width="160" height="80" />
    </dmndi:DMNShape>
    <dmndi:DMNShape id="_218ED5E8-FB01-417F-A1F6-867D60BCBF19" dmnElementRef="_18FAE913-2B2A-479D-A53A-14A2D57C46E4" isCollapsed="false" isListedInputData="false">
      <dc:Bounds x="80" y="480" width="160" height="80" />
    </dmndi:DMNShape>
    <dmndi:DMNShape id="_7C134BBF-2B96-4C9A-B985-D1FE10DC30D2" dmnElementRef="_0D6E83A0-171F-4D7C-B0DF-8464DDE05FD1" isCollapsed="false" isListedInputData="false">
      <dc:Bounds x="440" y="480" width="160" height="80" />
    </dmndi:DMNShape>
    <dmndi:DMNEdge id="_768C5D6E-9A41-4AA3-921C-A545E8F11597" dmnElementRef="_81BD49F4-E279-48A4-AC71-89CE92EEDE9C" sourceElement="_218ED5E8-FB01-417F-A1F6-867D60BCBF19" targetElement="_80C42026-FF56-44C6-84F9-F7A03686F5E8">
      <di:waypoint x="160" y="520" />
      <di:waypoint x="220" y="260" />
    </dmndi:DMNEdge>
    <dmndi:DMNEdge id="_79E2E532-8D84-40C9-91DA-07EA606798E5" dmnElementRef="_115B4C89-EBA8-4136-A70E-9D303D8E92C5" sourceElement="_7C134BBF-2B96-4C9A-B985-D1FE10DC30D2" targetElement="_80C42026-FF56-44C6-84F9-F7A03686F5E8">
      <di:waypoint x="520" y="520" />
      <di:waypoint x="220" y="260" />
    </dmndi:DMNEdge>
    <dmndi:DMNShape id="_1553EB6C-2BE8-4BC3-8972-A87308B98C86" dmnElementRef="_1991FB34-1253-4A54-AD3D-89697938DDFA" isCollapsed="false" isListedInputData="false">
      <dc:Bounds x="380" y="180" width="160" height="80" />
    </dmndi:DMNShape>
    <dmndi:DMNEdge id="_C6979483-DBBA-4AC0-AB28-E12EFEF08654" dmnElementRef="_5CA8EA11-FDFF-410A-AB4F-AA7ACB9F7FDC" sourceElement="_218ED5E8-FB01-417F-A1F6-867D60BCBF19" targetElement="_1553EB6C-2BE8-4BC3-8972-A87308B98C86">
      <di:waypoint x="160" y="520" />
      <di:waypoint x="460" y="260" />
    </dmndi:DMNEdge>
    <dmndi:DMNEdge id="_378C5213-084B-4A1D-ADCC-CEFE41F7E9FC" dmnElementRef="_D9E5FB0F-1A8C-4733-BBAB-E4A55113F806" sourceElement="_7C134BBF-2B96-4C9A-B985-D1FE10DC30D2" targetElement="_1553EB6C-2BE8-4BC3-8972-A87308B98C86">
      <di:waypoint x="520" y="520" />
      <di:waypoint x="460" y="260" />
    </dmndi:DMNEdge>
    <di:extension>
      <kie:ComponentsWidthsExtension>
        <kie:ComponentWidths dmnElementRef="_2812E0EA-FD32-480F-89CD-D0FE5FA02172">
          <kie:width>190</kie:width>
        </kie:ComponentWidths>
        <kie:ComponentWidths dmnElementRef="_C6BBFC2E-695F-4668-8A0E-D175FE7302C7">
          <kie:width>190</kie:width>
        </kie:ComponentWidths>
      </kie:ComponentsWidthsExtension>
    </di:extension>
  </dmndi:DMNDiagram>
</dmndi:DMNDI>
</definitions>
`;
// Copied from kogito-examples/kogito-quarkus-examples/dmn-pmml-quarkus-example/src/main/resources
export const testTreePmml = `<PMML version="4.2" xsi:schemaLocation="http://www.dmg.org/PMML-4_2 http://www.dmg.org/v4-2-1/pmml-4-2.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns="http://www.dmg.org/PMML-4_2">
<Header>
 <Application name="Drools-PMML" version="7.0.0-SNAPSHOT" />
</Header>

<DataDictionary numberOfFields="3">
 <DataField name="temperature" dataType="double" optype="continuous" />
 <DataField name="humidity" dataType="double" optype="continuous" />   
 <DataField name="decision" dataType="string" optype="categorical"> 
   <Value value="sunglasses" />
   <Value value="umbrella" />
   <Value value="nothing" />     
 </DataField>
</DataDictionary>

<TreeModel modelName="DecisionTree" functionName="classification">
 <MiningSchema>
   <MiningField name="temperature"  usageType="active" />
   <MiningField name="humidity"  usageType="active" />
   <MiningField name="decision" usageType="predicted" />
 </MiningSchema>
 <Output>
   <OutputField name="weatherdecision" targetField="decision" />
 </Output>
 
 <Node score="nothing" id="1">
   <True />
   <Node score="sunglasses" id="2">
     <CompoundPredicate booleanOperator="and">
       <SimplePredicate field="temperature" operator="greaterThan" value="25" />
       <SimplePredicate field="humidity" operator="lessOrEqual" value="20" />
     </CompoundPredicate>
   </Node>
   <Node score="umbrella" id="3">
     <SimplePredicate field="humidity" operator="greaterThan" value="50" />
   </Node>
   <Node score="nothing" id="4">
     <True />
   </Node>
 </Node>
</TreeModel>
</PMML>
`;
