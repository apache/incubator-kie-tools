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
import { COMPLEX_COLLECTION } from "../../examples/ExternalDmnModels";

export const collectionFileName = "complex_collection.scesim";
export const collectionScesim = `<?xml version="1.0" encoding="UTF-8" ?>
<ScenarioSimulationModel version="1.8">
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
              <step>Team</step>
            </ExpressionElement>
            <ExpressionElement>
              <step>name</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>_39AB4241-7722-4608-A36B-39063D1CE681</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Team</name>
            <className>tTeam</className>
          </factIdentifier>
          <className>string</className>
          <factAlias>Team</factAlias>
          <expressionAlias>name</expressionAlias>
          <columnWidth>100</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements>
            <ExpressionElement>
              <step>Team</step>
            </ExpressionElement>
            <ExpressionElement>
              <step>people</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>_DB037929-348D-4631-92AB-77A9565E4646</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Team</name>
            <className>tTeam</className>
          </factIdentifier>
          <className>java.util.List</className>
          <factAlias>Team</factAlias>
          <expressionAlias>people</expressionAlias>
          <genericTypes>
            <string>tPerson</string>
          </genericTypes>
          <columnWidth>289</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements>
            <ExpressionElement>
              <step>isHuge</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>_4C87F59F-DBD2-42E6-A884-4D700AEDC2D0</name>
            <type>EXPECT</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>isHuge</name>
            <className>boolean</className>
          </factIdentifier>
          <className>boolean</className>
          <factAlias>isHuge</factAlias>
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
            <rawValue class="string">Empty team</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Team</name>
              <className>tTeam</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_39AB4241-7722-4608-A36B-39063D1CE681</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">&quot;Team1&quot;</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Team</name>
              <className>tTeam</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_DB037929-348D-4631-92AB-77A9565E4646</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">[]</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>isHuge</name>
              <className>boolean</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_4C87F59F-DBD2-42E6-A884-4D700AEDC2D0</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">false</rawValue>
          </FactMappingValue>
        </factMappingValues>
      </Scenario>
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
            <rawValue class="string">A tiny little team</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Team</name>
              <className>tTeam</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_39AB4241-7722-4608-A36B-39063D1CE681</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">&quot;Team 2&quot;</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Team</name>
              <className>tTeam</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_DB037929-348D-4631-92AB-77A9565E4646</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">[{&quot;name&quot;: &quot;\&quot;John\&quot;&quot;, &quot;age&quot;: &quot;10&quot;}, {&quot;name&quot;: &quot;\&quot;Ashley\&quot;&quot;, &quot;age&quot;: &quot;10&quot;}, {&quot;name&quot;: &quot;\&quot;Pete\&quot;&quot;, &quot;age&quot;: &quot;10&quot;}]</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>isHuge</name>
              <className>boolean</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_4C87F59F-DBD2-42E6-A884-4D700AEDC2D0</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">false</rawValue>
          </FactMappingValue>
        </factMappingValues>
      </Scenario>
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
            <rawValue class="string">A Huge team</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Team</name>
              <className>tTeam</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_39AB4241-7722-4608-A36B-39063D1CE681</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">&quot;Team 3&quot;</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Team</name>
              <className>tTeam</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_DB037929-348D-4631-92AB-77A9565E4646</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">[{&quot;name&quot;: &quot;\&quot;John\&quot;&quot;, &quot;age&quot;: &quot;10&quot;}, {&quot;name&quot;: &quot;\&quot;Ashley\&quot;&quot;, &quot;age&quot;: &quot;10&quot;}, {&quot;name&quot;: &quot;\&quot;Pete\&quot;&quot;, &quot;age&quot;: &quot;10&quot;}, {&quot;name&quot;: &quot;\&quot;Becky\&quot;&quot;, &quot;age&quot;: &quot;24&quot;}]</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>isHuge</name>
              <className>boolean</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>_4C87F59F-DBD2-42E6-A884-4D700AEDC2D0</name>
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
    <dmnFilePath>../../main/resources/ComplexCollection.dmn</dmnFilePath>
    <type>DMN</type>
    <dmnNamespace>https://kie.org/dmn/_8D6316E7-ED43-4528-BB0B-2A7587B20853</dmnNamespace>
    <dmnName>DMN_EE8B8820-C396-45D8-BB02-3317B5C43C3F</dmnName>
    <skipFromBuild>false</skipFromBuild>
  </settings>
  <imports>
    <imports />
  </imports>
</ScenarioSimulationModel>
`;

const meta: Meta<{}> = {
  title: "Misc/ComplexCollection",
  component: TestScenarioEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<StorybookTestScenarioEditorProps>;

const marshaller = getMarshaller(collectionScesim);
const currentModel = marshaller.parser.parse();
const dmnModel = {
  normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-dmn-models/complex_collection.dmn",
  type: "dmn",
  model: normalize(getDmnMarshaller(COMPLEX_COLLECTION ?? "", { upgradeTo: "latest" }).parser.parse()),
  svg: "",
};

export const ComplexCollection: Story = {
  render: (args) => SceSimEditorWrapper(args),
  args: {
    model: marshaller.parser.parse(),
    openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: collectionFileName,
    externalModelsByNamespace: new Map([["https://kie.org/dmn/_8D6316E7-ED43-4528-BB0B-2A7587B20853", dmnModel]]),
    xml: marshaller.builder.build(currentModel),
    onRequestExternalModelsAvailableToInclude: () => Promise.resolve(["complex_collection.dmn"]),
    onRequestExternalModelByPath: () => Promise.resolve(dmnModel),
  },
};
