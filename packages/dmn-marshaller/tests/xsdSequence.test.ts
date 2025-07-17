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

import * as fs from "fs";
import * as path from "path";
import { DmnLatestModel, getMarshaller } from "@kie-tools/dmn-marshaller";

describe("build produces elements respecting the hierarchy of the element type", () => {
  test("businessKnowledgeSource element", () => {
    const json: DmnLatestModel = {
      definitions: {
        "@_name": "myDmn",
        "@_namespace": "myDmnNamespace",
        drgElement: [
          {
            __$$element: "businessKnowledgeModel",
            authorityRequirement: [],
            "@_name": "myBkm",
            encapsulatedLogic: {
              expression: {
                __$$element: "literalExpression",
                text: { __$$text: "myBkm literal expression" },
                extensionElements: {},
              },
            },
            variable: {
              "@_name": "myBkm var",
              extensionElements: {},
              description: { __$$text: "myBkm var description" },
            },
            extensionElements: {},
            description: { __$$text: "myBkm description" },
          },
        ],
      },
    };
    expect(
      getMarshaller(
        `<?xml version="1.0" encoding="UTF-8" ?><definitions name="" namespace="" xmlns="https://www.omg.org/spec/DMN/20230324/MODEL/"/>`,
        { upgradeTo: "latest" }
      ).builder.build(json)
    ).toStrictEqual(`<?xml version="1.0" encoding="UTF-8" ?>
<definitions name="myDmn" namespace="myDmnNamespace" xmlns="https://www.omg.org/spec/DMN/20230324/MODEL/" xmlns:dmndi="https://www.omg.org/spec/DMN/20230324/DMNDI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:kie="https://kie.org/dmn/extensions/1.0">
  <businessKnowledgeModel name="myBkm">
    <description>myBkm description</description>
    <extensionElements />
    <variable name="myBkm var">
      <description>myBkm var description</description>
      <extensionElements />
    </variable>
    <encapsulatedLogic>
      <literalExpression>
        <extensionElements />
        <text>myBkm literal expression</text>
      </literalExpression>
    </encapsulatedLogic>
  </businessKnowledgeModel>
</definitions>
`);
  });
});

describe("build always produces elements in the same order", () => {
  for (const file of [
    { path: "../tests-data--manual/other/decisionAndInput.dmn" },
    { path: "../tests-data--manual/other/decisionAndInput_wrongSequenceOrder.dmn" },
  ]) {
    test(path.basename(file.path), () => {
      const xml = fs.readFileSync(path.join(__dirname, file.path), "utf-8");
      const marshaller = getMarshaller(xml, { upgradeTo: "1.5" });
      const json = marshaller.parser.parse();

      // Adding some props in an arbitrary order shouldn't alter the end result.

      json.definitions.import = [
        {
          "@_name": "some-import",
          "@_namespace": "some-namespace",
          "@_importType": "some-import-type",
        },
      ];

      json.definitions.artifact = [
        {
          __$$element: "group",
          "@_name": "some-group",
        },
      ];

      expect(marshaller.builder.build(json)).toStrictEqual(`<?xml version="1.0" encoding="UTF-8" ?>
<definitions xmlns="https://www.omg.org/spec/DMN/20230324/MODEL/" expressionLanguage="https://www.omg.org/spec/DMN/20230324/FEEL/" namespace="https://kie.org/dmn/_D19C1092-7677-427F-A493-BCED38F74A9B" id="_11655DE3-BEA5-45B1-B54E-8AD84FBBED25" name="DMN_1E889EDB-B967-4508-8DB1-E0DF5986E62F" xmlns:dmndi="https://www.omg.org/spec/DMN/20230324/DMNDI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:kie="https://kie.org/dmn/extensions/1.0">
  <import name="some-import" namespace="some-namespace" importType="some-import-type" />
  <decision name="New Decision" id="_392BEF3D-44B5-47DC-8A06-C36F15DB2984">
    <variable id="_C2C9C21A-E708-46D9-876A-52BB25692B66" typeRef="string" name="New Decision" />
    <informationRequirement id="_E781E253-D97E-4A1D-BE51-037B012B30F0">
      <requiredInput href="#_154F9E03-B180-4C87-B7D3-8745DA4336F4" />
    </informationRequirement>
    <literalExpression id="_509ED9AF-3852-48F4-89A7-3CCF221B809C" label="New Decision" typeRef="string">
      <text>&quot;New Decision&quot;</text>
    </literalExpression>
  </decision>
  <inputData name="New Input Data" id="_154F9E03-B180-4C87-B7D3-8745DA4336F4">
    <variable name="New Input Data" id="_A28401DD-9A87-4251-A1E4-C63FC3A7C729" typeRef="string" />
  </inputData>
  <group name="some-group" />
  <dmndi:DMNDI>
    <dmndi:DMNDiagram id="_0D2FD42B-91FF-4795-B71F-E501CE115389" name="Default DRD" useAlternativeInputDataShape="false">
      <di:extension>
        <kie:ComponentsWidthsExtension>
          <kie:ComponentWidths dmnElementRef="_509ED9AF-3852-48F4-89A7-3CCF221B809C">
            <kie:width>190</kie:width>
          </kie:ComponentWidths>
        </kie:ComponentsWidthsExtension>
      </di:extension>
      <dmndi:DMNShape id="_92B3305F-A892-4E38-BD92-398906A9BC24" dmnElementRef="_154F9E03-B180-4C87-B7D3-8745DA4336F4" isCollapsed="false" isListedInputData="false">
        <dc:Bounds x="100" y="280" width="160" height="80" />
      </dmndi:DMNShape>
      <dmndi:DMNShape id="_1E49EEEB-9296-4AE5-B37C-2EE0044C0CC2" dmnElementRef="_392BEF3D-44B5-47DC-8A06-C36F15DB2984" isCollapsed="false" isListedInputData="false">
        <dc:Bounds x="100" y="100" width="160" height="80" />
      </dmndi:DMNShape>
      <dmndi:DMNEdge id="_C54C8ED9-7DB2-47BC-844A-E79D7142844B-AUTO-TARGET" dmnElementRef="_E781E253-D97E-4A1D-BE51-037B012B30F0" sourceElement="_92B3305F-A892-4E38-BD92-398906A9BC24" targetElement="_1E49EEEB-9296-4AE5-B37C-2EE0044C0CC2">
        <di:waypoint x="180" y="320" />
        <di:waypoint x="180" y="140" />
      </dmndi:DMNEdge>
    </dmndi:DMNDiagram>
  </dmndi:DMNDI>
</definitions>
`);
    });
  }
});
