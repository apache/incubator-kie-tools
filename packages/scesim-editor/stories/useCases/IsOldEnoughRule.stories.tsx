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

export const isOldEnoughDrl = `<ScenarioSimulationModel version="1.8" xmlns="https://kie.org/scesim/1.8">
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
        <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        <columnWidth>70.0</columnWidth>
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
        <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        <columnWidth>300.0</columnWidth>
      </FactMapping>
      <FactMapping>
        <expressionElements>
          <ExpressionElement>
            <step>Applicant</step>
          </ExpressionElement>
          <ExpressionElement>
            <step>age</step>
          </ExpressionElement>
        </expressionElements>
        <expressionIdentifier>
          <name>1|1</name>
          <type>GIVEN</type>
        </expressionIdentifier>
        <factIdentifier>
          <name>1|1</name>
          <className>mortgages.mortgages.Applicant</className>
        </factIdentifier>
        <className>java.lang.Integer</className>
        <factAlias>Applicant</factAlias>
        <expressionAlias>age</expressionAlias>
        <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        <columnWidth>212.60000000000002</columnWidth>
      </FactMapping>
      <FactMapping>
        <expressionElements>
          <ExpressionElement>
            <step>LoanApplication</step>
          </ExpressionElement>
          <ExpressionElement>
            <step>approved</step>
          </ExpressionElement>
        </expressionElements>
        <expressionIdentifier>
          <name>1591876615315</name>
          <type>GIVEN</type>
        </expressionIdentifier>
        <factIdentifier>
          <name>1591622209590</name>
          <className>mortgages.mortgages.LoanApplication</className>
        </factIdentifier>
        <className>java.lang.Boolean</className>
        <factAlias>LoanApplication</factAlias>
        <expressionAlias>approved</expressionAlias>
        <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        <columnWidth>212.60000000000002</columnWidth>
      </FactMapping>
      <FactMapping>
        <expressionElements>
          <ExpressionElement>
            <step>IncomeSource</step>
          </ExpressionElement>
          <ExpressionElement>
            <step>amount</step>
          </ExpressionElement>
        </expressionElements>
        <expressionIdentifier>
          <name>1591622221147</name>
          <type>GIVEN</type>
        </expressionIdentifier>
        <factIdentifier>
          <name>1591622221147</name>
          <className>mortgages.mortgages.IncomeSource</className>
        </factIdentifier>
        <className>java.lang.Integer</className>
        <factAlias>IncomeSource</factAlias>
        <expressionAlias>amount</expressionAlias>
        <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        <columnWidth>212.60000000000002</columnWidth>
      </FactMapping>
      <FactMapping>
        <expressionElements>
          <ExpressionElement>
            <step>LoanApplication</step>
          </ExpressionElement>
          <ExpressionElement>
            <step>approved</step>
          </ExpressionElement>
        </expressionElements>
        <expressionIdentifier>
          <name>1|2</name>
          <type>EXPECT</type>
        </expressionIdentifier>
        <factIdentifier>
          <name>1591622209590</name>
          <className>mortgages.mortgages.LoanApplication</className>
        </factIdentifier>
        <className>java.lang.Boolean</className>
        <factAlias>LoanApplication</factAlias>
        <expressionAlias>approved</expressionAlias>
        <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        <columnWidth>212.60000000000002</columnWidth>
      </FactMapping>
      <FactMapping>
        <expressionElements>
          <ExpressionElement>
            <step>LoanApplication</step>
          </ExpressionElement>
          <ExpressionElement>
            <step>explanation</step>
          </ExpressionElement>
        </expressionElements>
        <expressionIdentifier>
          <name>1591874776961</name>
          <type>EXPECT</type>
        </expressionIdentifier>
        <factIdentifier>
          <name>1591622209590</name>
          <className>mortgages.mortgages.LoanApplication</className>
        </factIdentifier>
        <className>java.lang.String</className>
        <factAlias>LoanApplication</factAlias>
        <expressionAlias>explanation</expressionAlias>
        <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        <columnWidth>212.60000000000002</columnWidth>
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
          <rawValue class="string">Young Bob want a loan</rawValue>
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
            <name>1|1</name>
            <className>mortgages.mortgages.Applicant</className>
          </factIdentifier>
          <expressionIdentifier>
            <name>1|1</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <rawValue class="string">17</rawValue>
        </FactMappingValue>
        <FactMappingValue>
          <factIdentifier>
            <name>1591622221147</name>
            <className>mortgages.mortgages.IncomeSource</className>
          </factIdentifier>
          <expressionIdentifier>
            <name>1591622221147</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <rawValue class="string">0</rawValue>
        </FactMappingValue>
        <FactMappingValue>
          <factIdentifier>
            <name>1591622209590</name>
            <className>mortgages.mortgages.LoanApplication</className>
          </factIdentifier>
          <expressionIdentifier>
            <name>1|2</name>
            <type>EXPECT</type>
          </expressionIdentifier>
          <rawValue class="string">false</rawValue>
        </FactMappingValue>
        <FactMappingValue>
          <factIdentifier>
            <name>1591622209590</name>
            <className>mortgages.mortgages.LoanApplication</className>
          </factIdentifier>
          <expressionIdentifier>
            <name>1591874776961</name>
            <type>EXPECT</type>
          </expressionIdentifier>
          <rawValue class="string">Underage</rawValue>
        </FactMappingValue>
        <FactMappingValue>
          <factIdentifier>
            <name>1591622209590</name>
            <className>mortgages.mortgages.LoanApplication</className>
          </factIdentifier>
          <expressionIdentifier>
            <name>1591876615315</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <rawValue class="string">true</rawValue>
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
          <rawValue class="string">Adult Anna  want a loan</rawValue>
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
        <FactMappingValue>
          <factIdentifier>
            <name>1|1</name>
            <className>mortgages.mortgages.Applicant</className>
          </factIdentifier>
          <expressionIdentifier>
            <name>1|1</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <rawValue class="string">27</rawValue>
        </FactMappingValue>
        <FactMappingValue>
          <factIdentifier>
            <name>1591622221147</name>
            <className>mortgages.mortgages.IncomeSource</className>
          </factIdentifier>
          <expressionIdentifier>
            <name>1591622221147</name>
            <type>GIVEN</type>
          </expressionIdentifier>
          <rawValue class="string">0</rawValue>
        </FactMappingValue>
        <FactMappingValue>
          <factIdentifier>
            <name>1591622209590</name>
            <className>mortgages.mortgages.LoanApplication</className>
          </factIdentifier>
          <expressionIdentifier>
            <name>1|2</name>
            <type>EXPECT</type>
          </expressionIdentifier>
          <rawValue class="string">true</rawValue>
        </FactMappingValue>
        <FactMappingValue>
          <factIdentifier>
            <name>1591622209590</name>
            <className>mortgages.mortgages.LoanApplication</className>
          </factIdentifier>
          <expressionIdentifier>
            <name>1591874776961</name>
            <type>EXPECT</type>
          </expressionIdentifier>
          <rawValue class="string">null</rawValue>
        </FactMappingValue>
        <FactMappingValue>
          <factIdentifier>
            <name>1591622209590</name>
            <className>mortgages.mortgages.LoanApplication</className>
          </factIdentifier>
          <expressionIdentifier>
            <name>1591876615315</name>
            <type>GIVEN</type>
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
        <factMappingValueType>NOT_EXPRESSION</factMappingValueType>
        <columnWidth>114.0</columnWidth>
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
  title: "Use Cases/Is Old Enough",
  component: TestScenarioEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<SceSimEditorWrapperProps>;

export const IsOldEnough: Story = {
  render: (args) => SceSimEditorWrapper(args),
  args: {
    pathRelativeToTheWorkspaceRoot: "isOldEnough.scesim",
    content: isOldEnoughDrl,
  },
};
