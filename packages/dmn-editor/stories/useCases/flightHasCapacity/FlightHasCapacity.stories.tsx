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

import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { getMarshaller } from "@kie-tools/dmn-marshaller";
import { Empty } from "../../misc/empty/Empty.stories";
import { DmnEditor, DmnEditorProps } from "../../../src/DmnEditor";
import { StorybookDmnEditorProps } from "../../dmnEditorStoriesWrapper";

export const flightHasCapacityDmn = `<?xml version="1.0" encoding="UTF-8" ?>
<definitions xmlns="https://www.omg.org/spec/DMN/20230324/MODEL/" xmlns:dmndi="https://www.omg.org/spec/DMN/20230324/DMNDI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:kie="https://kie.org/dmn/extensions/1.0" expressionLanguage="https://www.omg.org/spec/DMN/20230324/FEEL/" namespace="https://kie.org/dmn/_7785B66E-B0F4-473E-84DA-82D1EF89D006" id="_BD44F978-12D8-45E6-9E15-9EC830771AEB" name="DMN_AEC0C0CF-C716-491B-8B2F-F7AD62330916">
  <itemDefinition id="_BB82C411-E1A1-4ABB-95FA-D48438AF09E8" name="tRebookedFlights" isCollection="true" typeLanguage="https://www.omg.org/spec/DMN/20230324/FEEL/">
    <itemComponent id="_1A672F6E-8172-4B6E-88FD-53DE6AD52D68" name="Flight Number" isCollection="false" typeLanguage="https://www.omg.org/spec/DMN/20230324/FEEL/">
      <typeRef>string</typeRef>
    </itemComponent>
  </itemDefinition>
  <itemDefinition id="_C041D4AC-CB51-40D0-8879-DB51FDA5EB1D" name="tFlight" isCollection="false" typeLanguage="https://www.omg.org/spec/DMN/20230324/FEEL/">
    <itemComponent id="_93C9A060-419D-46DE-925E-D9C0A8253F0C" name="Capacity" isCollection="false" typeLanguage="https://www.omg.org/spec/DMN/20230324/FEEL/">
      <typeRef>number</typeRef>
    </itemComponent>
    <itemComponent id="_16275FD6-173D-4B2A-AABC-849DD1B59239" name="Flight Number" isCollection="false" typeLanguage="https://www.omg.org/spec/DMN/20230324/FEEL/">
      <typeRef>string</typeRef>
    </itemComponent>
  </itemDefinition>
  <businessKnowledgeModel name="has capacity" id="_B0851FBB-28F4-4ED5-8973-1D98AB0E500C">
    <variable name="has capacity" id="_23EADBD1-90E4-4E3D-B778-E0388C2F7531" typeRef="boolean" />
    <encapsulatedLogic id="_61F3594A-9AB3-4B35-8376-C7285AB18BF5" kind="FEEL" label="has capacity" typeRef="boolean">
      <formalParameter id="_0FB5C070-25AC-42FA-BC98-3AB1E98AFE8D" name="flight" typeRef="tFlight" />
      <formalParameter id="_D71C5780-8346-436D-85A4-50D5A4E66084" name="rebooked list" typeRef="tRebookedFlights" />
      <context id="_3591FC67-8A44-4E38-8A89-79EACFEF3627" typeRef="boolean" label="Return">
        <contextEntry id="_3BC7BA57-350B-4A32-A70F-D8FEEF94754C">
          <variable id="_73A97498-7980-4005-88DE-50E961D6CE6B" name="rebooked list per flight" typeRef="tRebookedFlights" />
          <filter id="_D041B957-7EE3-4888-9AB8-629D6CB6B3AF" label="rebooked list per flight">
            <in id="_46659CCF-3A7F-45D6-83CB-F79EF22DAC93">
              <literalExpression id="_38FAED08-B9D7-4A96-A3F5-ED86C7B14E22" typeRef="&lt;Undefined&gt;" label="Expression Name">
                <text>rebooked list</text>
              </literalExpression>
            </in>
            <match id="_37E842D1-05D0-48F4-8B42-C068E3EFC742">
              <literalExpression id="_D4B86919-2730-4BE5-BB22-01F7244A290E" typeRef="&lt;Undefined&gt;" label="Expression Name">
                <text>item.Flight Number = flight.Flight Number</text>
              </literalExpression>
            </match>
          </filter>
        </contextEntry>
        <contextEntry id="_232BA058-8B0C-49CD-9DAA-80CAE5750ED4">
          <conditional id="_4B0C53B1-AB7D-42B8-B79C-B2DCF24FA940" label="Return">
            <if id="_31DAD263-7069-4F84-B5A7-23023A94207F">
              <literalExpression id="_838AC5EC-F8F3-49BD-A2D8-0D09B72B372E" typeRef="&lt;Undefined&gt;" label="Expression Name">
                <text>flight.Capacity &gt;= count(rebooked list per flight)</text>
              </literalExpression>
            </if>
            <then id="_223FB6B8-1C22-4923-A06E-395076E3108E">
              <literalExpression id="_FACDC71F-2E38-43E3-9437-38DF62156660" typeRef="&lt;Undefined&gt;" label="Expression Name">
                <text>true</text>
              </literalExpression>
            </then>
            <else id="_3F112475-EBFC-4C79-83C3-F41FDB27F91B">
              <literalExpression id="_5854A7AA-E153-4F7D-99E5-87C14834DEFC" typeRef="&lt;Undefined&gt;" label="Expression Name">
                <text>false</text>
              </literalExpression>
            </else>
          </conditional>
        </contextEntry>
      </context>
    </encapsulatedLogic>
  </businessKnowledgeModel>
  <decision name="Result" id="_C1A9B7EE-C890-48CF-B096-A7238AD45631">
    <variable name="Result" id="_98A3EBC4-DF5F-449F-80D1-334CA9A04FEE" typeRef="boolean" />
    <informationRequirement id="_DAFD227E-16CF-420E-8833-85B4E4B3FD2B">
      <requiredInput href="#_83553B6E-2C2D-4F5D-800F-3E48A26BB515" />
    </informationRequirement>
    <informationRequirement id="_EF50E635-6DFF-4828-8ADE-1ADD6554CA23">
      <requiredInput href="#_02F0B3DE-25A7-4439-AECF-5426452274A1" />
    </informationRequirement>
    <knowledgeRequirement id="_4F8C6D3B-BC9D-4317-8005-BBFB1BCC6469">
      <requiredKnowledge href="#_B0851FBB-28F4-4ED5-8973-1D98AB0E500C" />
    </knowledgeRequirement>
    <literalExpression id="_327BA918-B9B0-4B1B-AB64-F00AA353F72C" typeRef="boolean" label="Result">
      <text>has capacity(Flight, Rebooked list)</text>
    </literalExpression>
  </decision>
  <inputData name="Flight" id="_83553B6E-2C2D-4F5D-800F-3E48A26BB515">
    <variable name="Flight" id="_E2DB7F9F-B669-4063-85B7-5943D5B2450E" typeRef="tFlight" />
  </inputData>
  <inputData name="Rebooked list" id="_02F0B3DE-25A7-4439-AECF-5426452274A1">
    <variable name="Rebooked list" id="_BED951FB-4E92-4865-B965-F447A1482AD8" typeRef="tRebookedFlights" />
  </inputData>
  <textAnnotation id="_97A2BED1-ADD7-4FB5-8EFE-2C1EF132072D">
    <text>&apos;has capacity&apos; counts the rebooked flights (Rebooked list) targeting the flight (Flight) and then decides, if the flight has a capacity for these rebooked flights.</text>
  </textAnnotation>
  <association id="_F21A97AA-3997-4DC2-8C49-270B5C1808BC" associationDirection="Both">
    <sourceRef href="#_B0851FBB-28F4-4ED5-8973-1D98AB0E500C" />
    <targetRef href="#_97A2BED1-ADD7-4FB5-8EFE-2C1EF132072D" />
  </association>
  <dmndi:DMNDI>
    <dmndi:DMNDiagram id="_0DF9A7D6-501A-4F2F-B0F7-75C0567F4007" name="Default DRD" useAlternativeInputDataShape="false">
      <di:extension>
        <kie:ComponentsWidthsExtension>
          <kie:ComponentWidths dmnElementRef="_3591FC67-8A44-4E38-8A89-79EACFEF3627">
            <kie:width>180</kie:width>
          </kie:ComponentWidths>
          <kie:ComponentWidths dmnElementRef="_38FAED08-B9D7-4A96-A3F5-ED86C7B14E22">
            <kie:width>483</kie:width>
          </kie:ComponentWidths>
          <kie:ComponentWidths dmnElementRef="_D4B86919-2730-4BE5-BB22-01F7244A290E">
            <kie:width>443</kie:width>
          </kie:ComponentWidths>
          <kie:ComponentWidths dmnElementRef="_838AC5EC-F8F3-49BD-A2D8-0D09B72B372E">
            <kie:width>403</kie:width>
          </kie:ComponentWidths>
          <kie:ComponentWidths dmnElementRef="_FACDC71F-2E38-43E3-9437-38DF62156660">
            <kie:width>403</kie:width>
          </kie:ComponentWidths>
          <kie:ComponentWidths dmnElementRef="_5854A7AA-E153-4F7D-99E5-87C14834DEFC">
            <kie:width>403</kie:width>
          </kie:ComponentWidths>
          <kie:ComponentWidths dmnElementRef="_327BA918-B9B0-4B1B-AB64-F00AA353F72C">
            <kie:width>379</kie:width>
          </kie:ComponentWidths>
        </kie:ComponentsWidthsExtension>
      </di:extension>
      <dmndi:DMNShape id="_9039DD34-FDB3-47ED-AA62-6AA9117000DF" dmnElementRef="_B0851FBB-28F4-4ED5-8973-1D98AB0E500C" isCollapsed="false" isListedInputData="false">
        <dc:Bounds x="120" y="100" width="160" height="80" />
      </dmndi:DMNShape>
      <dmndi:DMNShape id="_5E51CC47-FD3E-4C21-A957-30D087549C7A" dmnElementRef="_C1A9B7EE-C890-48CF-B096-A7238AD45631" isCollapsed="false" isListedInputData="false">
        <dc:Bounds x="460" y="100" width="160" height="80" />
      </dmndi:DMNShape>
      <dmndi:DMNEdge id="_FCD331D3-2B42-476D-8812-4A5B920D7935" dmnElementRef="_4F8C6D3B-BC9D-4317-8005-BBFB1BCC6469" sourceElement="_9039DD34-FDB3-47ED-AA62-6AA9117000DF" targetElement="_5E51CC47-FD3E-4C21-A957-30D087549C7A">
        <di:waypoint x="200" y="140" />
        <di:waypoint x="540" y="140" />
      </dmndi:DMNEdge>
      <dmndi:DMNShape id="_8A2BEFB0-8260-48EF-A017-086CB452B4D3" dmnElementRef="_83553B6E-2C2D-4F5D-800F-3E48A26BB515" isCollapsed="false" isListedInputData="false">
        <dc:Bounds x="120" y="320" width="160" height="80" />
      </dmndi:DMNShape>
      <dmndi:DMNShape id="_D871628E-06F2-487F-A4AD-04C95806956F" dmnElementRef="_02F0B3DE-25A7-4439-AECF-5426452274A1" isCollapsed="false" isListedInputData="false">
        <dc:Bounds x="460" y="320" width="160" height="80" />
      </dmndi:DMNShape>
      <dmndi:DMNEdge id="_2D78E653-DB02-47FE-9AC2-0BE78FD9965C" dmnElementRef="_DAFD227E-16CF-420E-8833-85B4E4B3FD2B" sourceElement="_8A2BEFB0-8260-48EF-A017-086CB452B4D3" targetElement="_5E51CC47-FD3E-4C21-A957-30D087549C7A">
        <di:waypoint x="200" y="360" />
        <di:waypoint x="540" y="140" />
      </dmndi:DMNEdge>
      <dmndi:DMNEdge id="_53CD6EEE-A043-4E22-BDAE-059292173F52" dmnElementRef="_EF50E635-6DFF-4828-8ADE-1ADD6554CA23" sourceElement="_D871628E-06F2-487F-A4AD-04C95806956F" targetElement="_5E51CC47-FD3E-4C21-A957-30D087549C7A">
        <di:waypoint x="540" y="360" />
        <di:waypoint x="540" y="140" />
      </dmndi:DMNEdge>
      <dmndi:DMNShape id="_61C6BB81-E2B2-48B7-B886-290200B0E3A2" dmnElementRef="_97A2BED1-ADD7-4FB5-8EFE-2C1EF132072D" isCollapsed="false" isListedInputData="false">
        <dc:Bounds x="360" y="-60" width="440" height="120" />
      </dmndi:DMNShape>
      <dmndi:DMNEdge id="_702F01A9-9993-494D-BB76-44BDEE649DDA-AUTO-TARGET" dmnElementRef="_F21A97AA-3997-4DC2-8C49-270B5C1808BC" sourceElement="_9039DD34-FDB3-47ED-AA62-6AA9117000DF" targetElement="_61C6BB81-E2B2-48B7-B886-290200B0E3A2">
        <di:waypoint x="200" y="140" />
        <di:waypoint x="580" y="0" />
      </dmndi:DMNEdge>
    </dmndi:DMNDiagram>
  </dmndi:DMNDI>
</definitions>`;

const meta: Meta<DmnEditorProps> = {
  title: "Use cases/Flight Has Capacity",
  component: DmnEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<StorybookDmnEditorProps>;

const marshaller = getMarshaller(flightHasCapacityDmn, { upgradeTo: "latest" });
const model = marshaller.parser.parse();

export const FlightHasCapacityStories: Story = {
  render: Empty.render,
  args: {
    model: model,
    xml: marshaller.builder.build(model),
  },
};
