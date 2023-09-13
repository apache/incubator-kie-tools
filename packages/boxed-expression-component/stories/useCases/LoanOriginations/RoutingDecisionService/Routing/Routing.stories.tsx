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
import {
  ContextExpressionDefinition,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
} from "../../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditorWrapper, beeGwtService, pmmlParams } from "../../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../../src/expressions";
import { loanOriginationsDataTypes } from "../../boxedExpressionEditorBase";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Routing Decision Service/Routing",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

const routingExpression: ContextExpressionDefinition = {
  id: "_336E0FC5-5548-4720-A6B2-A8FA4834DB7B",
  name: "Routing",
  dataType: "t.Routing" as DmnBuiltInDataType,
  logicType: ExpressionDefinitionLogicType.Context,
  contextEntries: [
    {
      entryInfo: {
        id: "_6626CBDF-A422-42A1-89B3-4491EB069A3E",
        name: "Bankrupt",
        dataType: DmnBuiltInDataType.Boolean,
      },
      entryExpression: {
        id: "_DC0A178E-45E0-4E9C-9C77-87E9DB05B196",
        name: "Bankrupt",
        dataType: DmnBuiltInDataType.Boolean,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Bureau data.Bankrupt",
        width: 811,
      },
    },
    {
      entryInfo: {
        id: "_66A38FAC-3D28-4590-AAE8-A0EB17191B5A",
        name: "Credit Score",
        dataType: DmnBuiltInDataType.Number,
      },
      entryExpression: {
        id: "_0763ECC7-68A4-4150-A958-A84C3B70EE56",
        name: "Credit Score",
        dataType: DmnBuiltInDataType.Number,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Bureau data.CreditScore",
        width: 811,
      },
    },
  ],
  result: {
    id: "_1FB1191F-F05C-4609-8BA4-77BE0D25570D",
    name: "Result Expression",
    dataType: DmnBuiltInDataType.Undefined,
    logicType: ExpressionDefinitionLogicType.DecisionTable,
    hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Priority,
    aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
    annotations: [{ name: "annotation-1", width: 104 }],
    input: [
      {
        id: "_DAD2F39A-E70B-4AE5-8AFF-89461DC1246E",
        name: "Bankrupt",
        dataType: DmnBuiltInDataType.Boolean,
        width: 100,
        idLiteralExpression: "_7B05037C-CEE6-4251-966F-A12400E33A2D",
      },
      {
        id: "_11B12DF2-1166-4F75-8E74-7A5EC2DCB24F",
        name: "Credit Score",
        dataType: DmnBuiltInDataType.Number,
        width: 100,
        idLiteralExpression: "_0CD5692B-310A-49B5-AD80-53FF445DCBD8",
      },
      {
        id: "_4FBA1BD8-2EB8-4C93-A5C0-5063D7FE1C6D",
        name: "Post-bureau affordability",
        dataType: DmnBuiltInDataType.Boolean,
        width: 172,
        idLiteralExpression: "_FC11FEC8-9198-4A4D-8652-33DB6B60E56D",
      },
      {
        id: "_1143D53B-53A1-47FE-8E2F-5E8CBFABEF9E",
        name: "Post-bureau risk category",
        dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
        width: 172,
        idLiteralExpression: "_0359F8EA-EB69-4F48-96D6-8448D9A928C2",
      },
    ],
    output: [
      {
        id: "_3E6080BE-560E-4D10-9B15-4CDE50627749",
        name: "Result Expression",
        dataType: DmnBuiltInDataType.Undefined,
        width: 123,
      },
    ],
    rules: [
      {
        id: "_807692BE-29F0-4FF5-85CE-F32BAA613216",
        inputEntries: [
          { id: "_521759CE-947B-4DC1-8F7C-21508F1A4A15", content: "true" },
          { id: "_B323C3DE-F58D-456A-AE86-58E979DFBC07", content: "-" },
          { id: "_CB9CCA3E-C3AD-460B-A898-54C423226C87", content: "-" },
          { id: "_F26D7141-9758-4096-9B86-FB1545055523", content: "-" },
        ],
        outputEntries: [
          {
            id: "_66C3085A-7EC0-443E-B1CB-1F3FAB3D240F",
            content: '"Decline"',
          },
        ],
        annotationEntries: [""],
      },
      {
        id: "_B56C2B9A-61A7-4762-89C2-88BD9BEB387D",
        inputEntries: [
          { id: "_80FB1716-503F-4B83-A2A2-DD1F8C250574", content: "-" },
          { id: "_8A63F79D-F52D-4B0C-81BE-177AC488C09F", content: "<580" },
          { id: "_08792B6C-04BD-41A9-8784-9DFA89A8BA81", content: "-" },
          { id: "_07089FD8-1B7D-4F2A-9963-8FC5C2B0588B", content: "-" },
        ],
        outputEntries: [{ id: "_53568F74-F05A-43C1-84A9-D1CE5F40473F", content: '"Refer"' }],
        annotationEntries: [""],
      },
      {
        id: "_4102196B-2434-4E5C-8AC9-73501A46B760",
        inputEntries: [
          { id: "_03ADA3B8-F155-4D1D-9FE4-9D5ED5B12977", content: "-" },
          { id: "_E07A0915-AE0F-4A5D-A24E-13910F369F6E", content: "-" },
          { id: "_C6B292E3-EA38-4F89-B286-5B8830E9A44B", content: "false" },
          { id: "_3D3438BF-FE19-4174-BA25-6B4E39EF2F6D", content: "-" },
        ],
        outputEntries: [
          {
            id: "_F596DFA3-7E4B-4AE1-B5BB-8B971C3F5C42",
            content: '"Decline"',
          },
        ],
        annotationEntries: [""],
      },
      {
        id: "_F6FA9690-C7D7-445A-A4FE-B10360323C5E",
        inputEntries: [
          { id: "_813F10D9-0895-4838-8325-35BEB094F05F", content: "-" },
          { id: "_AD55A511-0A95-4756-AD68-339D0A6DE566", content: "-" },
          { id: "_8CD726E7-9529-4D8A-86E3-C6F1FDC4A8C1", content: "-" },
          { id: "_5BD21A51-7E0C-4F58-B79C-DA8088748ACC", content: '"High"' },
        ],
        outputEntries: [{ id: "_51F45047-851A-4AC1-BA70-FE8A3FE57AEC", content: '"Refer"' }],
        annotationEntries: [""],
      },
      {
        id: "_04D4AAF3-8C71-4677-AAE7-E0AF7D16C587",
        inputEntries: [
          { id: "_199CE8D5-1006-4178-8D44-CD7AD3A699FC", content: "-" },
          { id: "_F2E21BA9-BE4A-4E30-AAB6-9E0918984A6E", content: "-" },
          { id: "_EF0FEAC4-2E9B-49E7-9D96-24E2635831D1", content: "-" },
          { id: "_422E0283-8F0A-4F3F-9846-A1F4983B69EF", content: "-" },
        ],
        outputEntries: [{ id: "_E0E1708F-3F89-493A-8443-85A7853D7B4D", content: '"Accept"' }],
        annotationEntries: [""],
      },
    ],
  },
  entryInfoWidth: 172,
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: routingExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlParams,
    isResetSupportedOnRootExpression: false,
  },
};
