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
import { TestScenarioEditor } from "../../src/TestScenarioEditor";
import { SceSimEditorWrapper, SceSimEditorWrapperProps } from "../scesimEditorStoriesWrapper";

export const unassignedIsOldEnoughDrl = `<ScenarioSimulationModel version="1.8" xmlns="https://kie.org/scesim/1.8">
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
          <expressionElements/>
          <expressionIdentifier>
            <name>1719487280878</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Empty</name>
            <className>java.lang.Void</className>
          </factIdentifier>
          <className>java.lang.Void</className>
          <factAlias>INSTANCE 7</factAlias>
          <expressionAlias>PROPERTY 7</expressionAlias>
          <columnWidth>645</columnWidth>
          <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        </FactMapping>
        <FactMapping>
          <expressionElements/>
          <expressionIdentifier>
            <name>1719487286392</name>
            <type>EXPECT</type>
          </expressionIdentifier>
          <factIdentifier>
            <name>Empty</name>
            <className>java.lang.Void</className>
          </factIdentifier>
          <className>java.lang.Void</className>
          <factAlias>INSTANCE 8</factAlias>
          <expressionAlias>PROPERTY 8</expressionAlias>
          <columnWidth>645</columnWidth>
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
          <FactMappingValue>
            <factIdentifier>
              <name>Empty</name>
              <className>java.lang.Void</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1719487280878</name>
              <type>GIVEN</type>
            </expressionIdentifier>
          </FactMappingValue>
          <FactMappingValue>
            <factIdentifier>
              <name>Empty</name>
              <className>java.lang.Void</className>
            </factIdentifier>
            <expressionIdentifier>
              <name>1719487286392</name>
              <type>EXPECT</type>
            </expressionIdentifier>
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
          <expressionAlias>PROPERTY 1</expressionAlias>
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
    <type>RULE</type>
    <skipFromBuild>false</skipFromBuild>
    <stateless>false</stateless>
  </settings>
  <imports>
    <imports/>
  </imports>
</ScenarioSimulationModel>
`;

const meta: Meta<{}> = {
  title: "Use Cases/Unassigned Is Old Enough",
  component: TestScenarioEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<SceSimEditorWrapperProps>;

export const UnassignedIsOldEnough: Story = {
  render: (args) => SceSimEditorWrapper(args),
  args: {
    pathRelativeToTheWorkspaceRoot: "unassignedIsOldEnough.scesim",
    content: unassignedIsOldEnoughDrl,
  },
};
