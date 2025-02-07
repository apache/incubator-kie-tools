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

import type { Meta, StoryObj } from "@storybook/react";
import { getMarshaller } from "@kie-tools/scesim-marshaller";
import { TestScenarioEditor } from "../../../src/TestScenarioEditor";
import { EMPTY_ONE_EIGHT } from "../../../src/resources/EmptyScesimFile";
import { SceSimEditorWrapper, StorybookTestScenarioEditorProps } from "../../scesimEditorStoriesWrapper";
import { normalize } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { getMarshaller as getDmnMarshaller } from "@kie-tools/dmn-marshaller";

export const simpleFileName = "simple.scesim";
export const simpleDmn = `<?xml version="1.0" encoding="UTF-8" ?>
<definitions xmlns="https://www.omg.org/spec/DMN/20230324/MODEL/" expressionLanguage="https://www.omg.org/spec/DMN/20230324/FEEL/" namespace="https://kie.org/dmn/_0457D4F2-02B6-4A3D-957F-8F971A8321BC" id="_DE888083-9CD8-44DC-BE0A-0EC85244DE3B" name="DMN_44D5E2A8-EC16-45A8-8797-14373283EF69" xmlns:dmndi="https://www.omg.org/spec/DMN/20230324/DMNDI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:kie="https://kie.org/dmn/extensions/1.0">
  <inputData name="INSTANCE-1" id="_926B9E31-3CEA-4A0E-8A66-97F875EFB428">
    <variable name="INSTANCE-1" id="_9B4713BA-E468-447E-9649-91E108CB2F4F" />
  </inputData>
  <decision name="INSTANCE-2" id="_FBA6E903-26B7-40FA-99C1-7230A3E61D7D">
    <variable name="INSTANCE-2" id="_AC19E28B-2336-46E9-8D5B-62AC2D3BF182" />
    <informationRequirement id="_6F72FA6A-A6E8-403D-8AB7-192FB474878E">
      <requiredInput href="#_926B9E31-3CEA-4A0E-8A66-97F875EFB428" />
    </informationRequirement>
  </decision>
  <dmndi:DMNDI>
    <dmndi:DMNDiagram id="_27FA44E1-3CD0-4B6F-A0A4-1D9862D52B87" name="Default DRD" useAlternativeInputDataShape="false">
      <di:extension>
        <kie:ComponentsWidthsExtension>
          <kie:ComponentWidths />
        </kie:ComponentsWidthsExtension>
      </di:extension>
      <dmndi:DMNShape id="_01BEE78F-F02F-4EC3-9CC8-6A5B69E963A2" dmnElementRef="_926B9E31-3CEA-4A0E-8A66-97F875EFB428" isCollapsed="false" isListedInputData="false">
        <dc:Bounds x="440" y="240" width="160" height="80" />
      </dmndi:DMNShape>
      <dmndi:DMNShape id="_1F89C8D2-9957-497D-BAC3-6FDA237FD2BA" dmnElementRef="_FBA6E903-26B7-40FA-99C1-7230A3E61D7D" isCollapsed="false" isListedInputData="false">
        <dc:Bounds x="440" y="40" width="160" height="80" />
      </dmndi:DMNShape>
      <dmndi:DMNEdge id="_FCC85D46-43CA-4F3A-A91A-8A5638E05213" dmnElementRef="_6F72FA6A-A6E8-403D-8AB7-192FB474878E" sourceElement="_01BEE78F-F02F-4EC3-9CC8-6A5B69E963A2" targetElement="_1F89C8D2-9957-497D-BAC3-6FDA237FD2BA">
        <di:waypoint x="520" y="280" />
        <di:waypoint x="520" y="120" />
      </dmndi:DMNEdge>
    </dmndi:DMNDiagram>
  </dmndi:DMNDI>
</definitions>
`;

const meta: Meta<{}> = {
  title: "Misc/Simple",
  component: TestScenarioEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<StorybookTestScenarioEditorProps>;

const marshaller = getMarshaller(EMPTY_ONE_EIGHT);
const model = marshaller.parser.parse();

export const Simple: Story = {
  render: (args) => SceSimEditorWrapper(args),
  args: {
    model: marshaller.parser.parse(),
    openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: simpleFileName,
    xml: marshaller.builder.build(model),
    onRequestExternalModelsAvailableToInclude: () => Promise.resolve(["simple.dmn"]),
    onRequestExternalModelByPath: () =>
      Promise.resolve({
        normalizedPosixPathRelativeToTheOpenFile: "simple.dmn",
        type: "dmn",
        model: normalize(getDmnMarshaller(simpleDmn ?? "", { upgradeTo: "latest" }).parser.parse()),
        svg: "",
      }),
  },
};
