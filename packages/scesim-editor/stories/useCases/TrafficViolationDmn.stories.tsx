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

import React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { normalize } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { getMarshaller as getDmnMarshaller } from "@kie-tools/dmn-marshaller";
import { getMarshaller } from "@kie-tools/scesim-marshaller";
import { TestScenarioEditor } from "../../src/TestScenarioEditor";
import { SceSimEditorWrapper, StorybookTestScenarioEditorProps } from "../scesimEditorStoriesWrapper";
import { TRAFFIC_VIOLATION } from "../examples/ExternalDmnModels";

export const trafficViolationDmnFileName = "TrafficViolation.scesim";
export const trafficViolationDmn = `<?xml version="1.0" encoding="UTF-8"?>
<ScenarioSimulationModel version="1.8" xmlns="https://kie.org/scesim/1.8">
  <simulation>
    <scesimModelDescriptor>
      <factMappings>
        <FactMapping>
          <expressionElements/>
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
          <expressionElements/>
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
              <step>Driver</step>
            </ExpressionElement>
            <ExpressionElement>
              <step>Points</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>1|5</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Driver</name>
            <className>Driver</className>
          </factIdentifier>
          <className>number</className>
          <factAlias>Driver</factAlias>
          <expressionAlias>Points</expressionAlias>
          <genericTypes/>
          <columnWidth>114</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements>
            <ExpressionElement>
              <step>Violation</step>
            </ExpressionElement>
            <ExpressionElement>
              <step>Type</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>1|8</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Violation</name>
            <className>Violation</className>
          </factIdentifier>
          <className>Type</className>
          <factAlias>Violation</factAlias>
          <expressionAlias>Type</expressionAlias>
          <genericTypes/>
          <columnWidth>114</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements>
            <ExpressionElement>
              <step>Violation</step>
            </ExpressionElement>
            <ExpressionElement>
              <step>Speed Limit</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>1|9</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Violation</name>
            <className>Violation</className>
          </factIdentifier>
          <className>number</className>
          <factAlias>Violation</factAlias>
          <expressionAlias>Speed Limit</expressionAlias>
          <genericTypes/>
          <columnWidth>114</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements>
            <ExpressionElement>
              <step>Violation</step>
            </ExpressionElement>
            <ExpressionElement>
              <step>Actual Speed</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>1|10</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Violation</name>
            <className>Violation</className>
          </factIdentifier>
          <className>number</className>
          <factAlias>Violation</factAlias>
          <expressionAlias>Actual Speed</expressionAlias>
          <genericTypes/>
          <columnWidth>114</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements>
            <ExpressionElement>
              <step>Fine</step>
            </ExpressionElement>
            <ExpressionElement>
              <step>Amount</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>1|11</name>
            <type>EXPECT</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Fine</name>
            <className>Fine</className>
          </factIdentifier>
          <className>number</className>
          <factAlias>Fine</factAlias>
          <expressionAlias>Amount</expressionAlias>
          <genericTypes/>
          <columnWidth>114</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements>
            <ExpressionElement>
              <step>Fine</step>
            </ExpressionElement>
            <ExpressionElement>
              <step>Points</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>1|12</name>
            <type>EXPECT</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Fine</name>
            <className>Fine</className>
          </factIdentifier>
          <className>number</className>
          <factAlias>Fine</factAlias>
          <expressionAlias>Points</expressionAlias>
          <genericTypes/>
          <columnWidth>114</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements>
            <ExpressionElement>
              <step>Should the driver be suspended?</step>
            </ExpressionElement>
          </expressionElements>
          <expressionIdentifier>
            <name>1|13</name>
            <type>EXPECT</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Should the driver be suspended?</name>
            <className>Should the driver be suspended?</className>
          </factIdentifier>
          <className>string</className>
          <factAlias>Should the driver be suspended?</factAlias>
          <expressionAlias>value</expressionAlias>
          <genericTypes/>
          <columnWidth>114</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
      </factMappings>
    </scesimModelDescriptor>
    <scesimData>
      <Scenario>
        <factMappingValues>
          <FactMappingValue>
            <factIdentifier>
              <name>Scenario description</name>
              <className>java.lang.String</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>Description</name>
              <type>OTHER</type>
            </expressionIdentifier>
            <rawValue class="string">Above speed limit: 10km/h and 30 km/h</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Driver</name>
              <className>Driver</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|5</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">10</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|8</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">"speed"</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|9</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">100</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|10</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">120</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Fine</name>
              <className>Fine</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|11</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">500</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Fine</name>
              <className>Fine</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|12</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">3</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Should the driver be suspended?</name>
              <className>Should the driver be suspended?</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|13</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">"No"</rawValue>
          </FactMappingValue>
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
        </factMappingValues>
      </Scenario>
      <Scenario>
        <factMappingValues>
          <FactMappingValue>
            <factIdentifier>
              <name>Scenario description</name>
              <className>java.lang.String</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>Description</name>
              <type>OTHER</type>
            </expressionIdentifier>
            <rawValue class="string">Above speed limit: more than 30 km/h</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Driver</name>
              <className>Driver</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|5</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">10</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|8</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">"speed"</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|9</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">100</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|10</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">150</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Fine</name>
              <className>Fine</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|11</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">1000</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Fine</name>
              <className>Fine</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|12</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">7</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Should the driver be suspended?</name>
              <className>Should the driver be suspended?</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|13</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">"No"</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>#</name>
              <className>java.lang.Integer</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>Index</name>
              <type>OTHER</type>
            </expressionIdentifier>
            <rawValue class="string">2</rawValue>
          </FactMappingValue>
        </factMappingValues>
      </Scenario>
      <Scenario>
        <factMappingValues>
          <FactMappingValue>
            <factIdentifier>
              <name>Scenario description</name>
              <className>java.lang.String</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>Description</name>
              <type>OTHER</type>
            </expressionIdentifier>
            <rawValue class="string">Parking violation</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Driver</name>
              <className>Driver</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|5</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">10</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|8</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">"parking"</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|9</name>
              <type>GIVEN</type>
            </expressionIdentifier>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|10</name>
              <type>GIVEN</type>
            </expressionIdentifier>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Fine</name>
              <className>Fine</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|11</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">100</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Fine</name>
              <className>Fine</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|12</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">1</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Should the driver be suspended?</name>
              <className>Should the driver be suspended?</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|13</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">"No"</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>#</name>
              <className>java.lang.Integer</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>Index</name>
              <type>OTHER</type>
            </expressionIdentifier>
            <rawValue class="string">3</rawValue>
          </FactMappingValue>
        </factMappingValues>
      </Scenario>
      <Scenario>
        <factMappingValues>
          <FactMappingValue>
            <factIdentifier>
              <name>Scenario description</name>
              <className>java.lang.String</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>Description</name>
              <type>OTHER</type>
            </expressionIdentifier>
            <rawValue class="string">DUI violation</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Driver</name>
              <className>Driver</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|5</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">10</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|8</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">"driving under the influence"</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|9</name>
              <type>GIVEN</type>
            </expressionIdentifier>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|10</name>
              <type>GIVEN</type>
            </expressionIdentifier>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Fine</name>
              <className>Fine</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|11</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">1000</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Fine</name>
              <className>Fine</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|12</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">5</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Should the driver be suspended?</name>
              <className>Should the driver be suspended?</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|13</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">"No"</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>#</name>
              <className>java.lang.Integer</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>Index</name>
              <type>OTHER</type>
            </expressionIdentifier>
            <rawValue class="string">4</rawValue>
          </FactMappingValue>
        </factMappingValues>
      </Scenario>
      <Scenario>
        <factMappingValues>
          <FactMappingValue>
            <factIdentifier>
              <name>Scenario description</name>
              <className>java.lang.String</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>Description</name>
              <type>OTHER</type>
            </expressionIdentifier>
            <rawValue class="string">Driver suspended</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Driver</name>
              <className>Driver</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|5</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">15</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|8</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">"speed"</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|9</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">100</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Violation</name>
              <className>Violation</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|10</name>
              <type>GIVEN</type>
            </expressionIdentifier>
            <rawValue class="string">140</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Fine</name>
              <className>Fine</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|11</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">1000</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Fine</name>
              <className>Fine</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|12</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">7</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Should the driver be suspended?</name>
              <className>Should the driver be suspended?</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1|13</name>
              <type>EXPECT</type>
            </expressionIdentifier>
            <rawValue class="string">"Yes"</rawValue>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>#</name>
              <className>java.lang.Integer</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>Index</name>
              <type>OTHER</type>
            </expressionIdentifier>
            <rawValue class="string">5</rawValue>
          </FactMappingValue>
        </factMappingValues>
      </Scenario>
    </scesimData>
  </simulation>
  <background>
    <scesimModelDescriptor>
      <factMappings>
        <FactMapping>
          <expressionElements/>
          <expressionIdentifier>
            <name>1|1</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Empty</name>
            <className>java.lang.Void</className>
          </factIdentifier>
          <className>java.lang.Void</className>
          <factAlias>INSTANCE 1</factAlias>
          <expressionAlias>PROPERTY-1</expressionAlias>
          <columnWidth>114</columnWidth>
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
    <dmnFilePath>dev-webapp/available-dmn-models/traffic-violation.dmn</dmnFilePath>
    <type>DMN</type>
    <dmnNamespace>https://kie.apache.org/dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF</dmnNamespace>
    <dmnName>Traffic Violation</dmnName>
    <skipFromBuild>false</skipFromBuild>
    <stateless>false</stateless>
  </settings>
  <imports>
    <imports/>
  </imports>
</ScenarioSimulationModel>`;

const meta: Meta<{}> = {
  title: "Use Cases/Traffic Violation",
  component: TestScenarioEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<StorybookTestScenarioEditorProps>;

const marshaller = getMarshaller(trafficViolationDmn);
const currentModel = marshaller.parser.parse();
const dmnModel = {
  normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-dmn-models/traffic-violation.dmn",
  type: "dmn",
  model: normalize(getDmnMarshaller(TRAFFIC_VIOLATION ?? "", { upgradeTo: "latest" }).parser.parse()),
  svg: "",
};

export const TrafficViolation: Story = {
  render: (args) => SceSimEditorWrapper(args),
  args: {
    model: marshaller.parser.parse(),
    openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: trafficViolationDmnFileName,
    externalModelsByNamespace: new Map([
      ["https://kie.apache.org/dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF", dmnModel],
    ]),
    xml: marshaller.builder.build(currentModel),
    onRequestExternalModelsAvailableToInclude: () =>
      Promise.resolve(["dev-webapp/available-dmn-models/traffic-violation.dmn"]),
    onRequestExternalModelByPath: () => Promise.resolve(dmnModel),
  },
};
