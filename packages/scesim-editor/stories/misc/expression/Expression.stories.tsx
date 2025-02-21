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
import { SceSimEditorWrapper, StorybookTestScenarioEditorProps } from "../../scesimEditorStoriesWrapper";
import { normalize } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { getMarshaller as getDmnMarshaller } from "@kie-tools/dmn-marshaller";
import { SIMPLE } from "../../examples/ExternalDmnModels";

export const expressionFileName = "expression.scesim";
export const expressionScesim = `<?xml version="1.0" encoding="UTF-8" ?>
<ScenarioSimulationModel xmlns="https://kie.org/scesim/1.8" version="1.8">
  <simulation>
    <scesimModelDescriptor>
      <factMappings>
        <FactMapping>
          <expressionElements />
          <expressionIdentifier>
            <name>Index</name>
            <type>OTHER</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>#</name>
            <className>java.lang.Integer</className>
          </factIdentifier>
          <className>java.lang.Integer</className>
          <factAlias>#</factAlias>
          <columnWidth>70</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements />
          <expressionIdentifier>
            <name>Description</name>
            <type>OTHER</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Scenario description</name>
            <className>java.lang.String</className>
          </factIdentifier>
          <className>java.lang.String</className>
          <factAlias>Scenario description</factAlias>
          <columnWidth>300</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements>
            <ExpressionElement>
              <step>Input</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>_EEFE5AF7-5422-4618-8709-474976310EBA</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Input</name>
            <className>number</className>
          </factIdentifier>
          <className>number</className>
          <factAlias>Input</factAlias>
          <expressionAlias>expression &lt;/&gt;</expressionAlias>
          <columnWidth>300</columnWidth>
          <factMappingValueType>EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements>
            <ExpressionElement>
              <step>Decision</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>_897A9B62-F79C-48A7-AE65-4B414D1B6EB9</name>
            <type>EXPECT</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Decision</name>
            <className>boolean</className>
          </factIdentifier>
          <className>boolean</className>
          <factAlias>Decision</factAlias>
          <expressionAlias>expression &lt;/&gt;</expressionAlias>
          <columnWidth>300</columnWidth>
          <factMappingValueType>EXPRESSION</factMappingValueType>
        </FactMapping>
      </factMappings>
    </scesimModelDescriptor>
    <scesimData>
      <Scenario>
        <factMappingValues>
          <FactMappingValue>
            <factIdentifier>
              <name>#</name>
              <className>java.lang.Integer</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>Index</name>
              <type>OTHER</type>
            </expressionIdentifier>
            <rawValue class="string">1</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Scenario description</name>
              <className>java.lang.String</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>Description</name>
              <type>OTHER</type>
            </expressionIdentifier>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Input</name>
              <className>number</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_EEFE5AF7-5422-4618-8709-474976310EBA</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">20</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Decision</name>
              <className>boolean</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_897A9B62-F79C-48A7-AE65-4B414D1B6EB9</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">true</rawValue>
          </FactMappingValue>
        </factMappingValues>
      </Scenario>
    </scesimData>
  </simulation>
  <background>
    <scesimModelDescriptor>
      <factMappings>
        <FactMapping>
          <expressionElements />
          <expressionIdentifier>
            <name>1|1</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Empty</name>
            <className>java.lang.Void</className>
          </factIdentifier>
          <className>java.lang.Void</className>
          <factAlias>INSTANCE-1</factAlias>
          <expressionAlias>PROPERTY-1</expressionAlias>
          <columnWidth>150</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
      </factMappings>
    </scesimModelDescriptor>
    <scesimData>
      <BackgroundData>
        <factMappingValues>
          <FactMappingValue>
            <factIdentifier>
              <name>Empty</name>
              <className>java.lang.Void</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|1</name>
              <type>GIVEN</type>
            </expressionIdentifier>
          </FactMappingValue>
        </factMappingValues>
      </BackgroundData>
    </scesimData>
  </background>
  <settings>
    <dmnFilePath>dev-webapp/available-dmn-models/simple.dmn</dmnFilePath>
    <type>DMN</type>
    <dmnNamespace>https://kie.org/dmn/_0457D4F2-02B6-4A3D-957F-8F971A8321BC</dmnNamespace>
    <dmnName>Simple</dmnName>
    <skipFromBuild>false</skipFromBuild>
  </settings>
  <imports>
    <imports />
  </imports>
</ScenarioSimulationModel>`;

const meta: Meta<{}> = {
  title: "Misc/Expression",
  component: TestScenarioEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<StorybookTestScenarioEditorProps>;

const marshaller = getMarshaller(expressionScesim);
const currentModel = marshaller.parser.parse();
const dmnModel = {
  normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-dmn-models/simple.dmn",
  type: "dmn",
  model: normalize(getDmnMarshaller(SIMPLE ?? "", { upgradeTo: "latest" }).parser.parse()),
  svg: "",
};

export const Expression: Story = {
  render: (args) => SceSimEditorWrapper(args),
  args: {
    model: marshaller.parser.parse(),
    openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: expressionFileName,
    externalModelsByNamespace: new Map([["https://kie.org/dmn/_0457D4F2-02B6-4A3D-957F-8F971A8321BC", dmnModel]]),
    xml: marshaller.builder.build(currentModel),
    onRequestExternalModelsAvailableToInclude: () => Promise.resolve(["simple.dmn"]),
    onRequestExternalModelByPath: () => Promise.resolve(dmnModel),
  },
};
