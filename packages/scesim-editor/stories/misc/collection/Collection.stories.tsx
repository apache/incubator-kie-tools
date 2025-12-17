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
import { COLLECTION } from "../../examples/ExternalDmnModels";

export const collectionFileName = "collection.scesim";
export const collectionScesim = `<?xml version="1.0" encoding="UTF-8" ?>
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
              <step>input</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>_33694C8E-C795-435F-B0F0-F1101F9DE403</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>input</name>
            <className>tNumber</className>
          </factIdentifier>
          <className>java.util.List</className>
          <factAlias>input</factAlias>
          <expressionAlias>value</expressionAlias>
          <genericTypes>
            <string>tNumber</string>
          </genericTypes>
          <columnWidth>141</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements>
            <ExpressionElement>
              <step>input2</step>
            </ExpressionElement>
            <ExpressionElement>
              <step>nums</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>_11DBB39A-5749-420D-A007-3A0D19A66916</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>input2</name>
            <className>tComplex</className>
          </factIdentifier>
          <className>java.util.List</className>
          <factAlias>input2</factAlias>
          <expressionAlias>nums</expressionAlias>
          <genericTypes>
            <string>number</string>
          </genericTypes>
          <columnWidth>157</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements>
            <ExpressionElement>
              <step>output</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>_E38BFF0C-040B-464E-8C18-7BD1C7372EEA</name>
            <type>EXPECT</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>output</name>
            <className>boolean</className>
          </factIdentifier>
          <className>boolean</className>
          <factAlias>output</factAlias>
          <expressionAlias>value</expressionAlias>
          <columnWidth>100</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
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
              <name>input</name>
              <className>tNumber</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_33694C8E-C795-435F-B0F0-F1101F9DE403</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">[10,20,30]</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>input2</name>
              <className>tComplex</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_11DBB39A-5749-420D-A007-3A0D19A66916</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">[40,50,60]</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>output</name>
              <className>boolean</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_E38BFF0C-040B-464E-8C18-7BD1C7372EEA</name>
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
    <dmnFilePath>dev-webapp/available-dmn-models/collection.dmn</dmnFilePath>
    <type>DMN</type>
    <dmnNamespace>https://kie.apache.org/dmn/_D1C37750-2078-4CB7-AF77-850947718867</dmnNamespace>
    <dmnName>Collection</dmnName>
    <skipFromBuild>false</skipFromBuild>
  </settings>
  <imports>
    <imports />
  </imports>
</ScenarioSimulationModel>
`;

const meta: Meta<{}> = {
  title: "Misc/Collection",
  component: TestScenarioEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<StorybookTestScenarioEditorProps>;

const marshaller = getMarshaller(collectionScesim);
const currentModel = marshaller.parser.parse();
const dmnModel = {
  normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-dmn-models/collection.dmn",
  type: "dmn",
  model: normalize(getDmnMarshaller(COLLECTION ?? "", { upgradeTo: "latest" }).parser.parse()),
  svg: "",
};

export const Collection: Story = {
  render: (args) => SceSimEditorWrapper(args),
  args: {
    model: marshaller.parser.parse(),
    openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: collectionFileName,
    externalModelsByNamespace: new Map([
      ["https://kie.apache.org/dmn/_D1C37750-2078-4CB7-AF77-850947718867", dmnModel],
    ]),
    xml: marshaller.builder.build(currentModel),
    onRequestExternalModelsAvailableToInclude: () => Promise.resolve(["collection.dmn"]),
    onRequestExternalModelByPath: () => Promise.resolve(dmnModel),
  },
};
