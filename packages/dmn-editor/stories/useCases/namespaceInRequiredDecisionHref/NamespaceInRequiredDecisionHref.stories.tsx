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

export const namespaceInRequiredDecisionHref = `<?xml version="1.0" encoding="UTF-8" ?>
<definitions xmlns="https://www.omg.org/spec/DMN/20240513/MODEL/" expressionLanguage="https://www.omg.org/spec/DMN/20240513/FEEL/" namespace="https://kie.org/dmn/_BCA8E83B-E3E6-432C-BFD6-44CD55A76A6C" id="_2BC7DBF7-90C6-448A-B336-05F26D1FEC8E" name="DMN_18EBF2E7-B344-47B1-827A-3A2E08B8E9CA" xmlns:dmndi="https://www.omg.org/spec/DMN/20230324/DMNDI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:kie="https://kie.org/dmn/extensions/1.0">
  <decision name="Required Decision A" id="_7A15C07E-4A30-450D-9796-A2454EC55279">
    <variable name="Required Decision A" id="_B156824F-3ABD-4387-9711-08B8A3A5241C" />
  </decision>
  <decision name="Decision in Need" id="_A26B267C-96D9-4E35-8564-DC655DC08E39">
    <variable name="Decision in Need" id="_AED5DD3A-EC35-457B-A13A-C8CC4614C46F" />
    <informationRequirement id="_4BA30E29-004F-43E2-9402-77C107CA9339">
      <requiredDecision href="https://kie.org/dmn/_BCA8E83B-E3E6-432C-BFD6-44CD55A76A6C#_7A15C07E-4A30-450D-9796-A2454EC55279" />
    </informationRequirement>
  </decision>
  <dmndi:DMNDI>
    <dmndi:DMNDiagram id="_DBBAB955-3E4F-4731-BC13-AD0A71E064D3" name="Default DRD" useAlternativeInputDataShape="false">
      <di:extension>
        <kie:ComponentsWidthsExtension>
          <kie:ComponentWidths />
        </kie:ComponentsWidthsExtension>
      </di:extension>
      <dmndi:DMNShape id="_68A19ACB-7EA6-4962-A61F-866D08E3D151" dmnElementRef="_7A15C07E-4A30-450D-9796-A2454EC55279" isCollapsed="false" isListedInputData="false">
        <dc:Bounds x="120" y="80" width="160" height="80" />
      </dmndi:DMNShape>
      <dmndi:DMNShape id="_B06617DD-EDE9-4D54-8EF9-609545A69743" dmnElementRef="_A26B267C-96D9-4E35-8564-DC655DC08E39" isCollapsed="false" isListedInputData="false">
        <dc:Bounds x="420" y="80" width="160" height="80" />
      </dmndi:DMNShape>
      <dmndi:DMNEdge id="_51E984B1-E548-4B7F-918A-802C995C7651" dmnElementRef="_4BA30E29-004F-43E2-9402-77C107CA9339" sourceElement="_68A19ACB-7EA6-4962-A61F-866D08E3D151" targetElement="_B06617DD-EDE9-4D54-8EF9-609545A69743">
        <di:waypoint x="200" y="120" />
        <di:waypoint x="500" y="120" />
      </dmndi:DMNEdge>
    </dmndi:DMNDiagram>
  </dmndi:DMNDI>
</definitions>
`;

const meta: Meta<DmnEditorProps> = {
  title: "Use cases/Namespace In Required Decision Href",
  component: DmnEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<StorybookDmnEditorProps>;

const marshaller = getMarshaller(namespaceInRequiredDecisionHref, { upgradeTo: "latest" });
const model = marshaller.parser.parse();

export const NamespaceInRequiredDecisionHref: Story = {
  render: Empty.render,
  args: {
    model: model,
    xml: marshaller.builder.build(model),
  },
};
