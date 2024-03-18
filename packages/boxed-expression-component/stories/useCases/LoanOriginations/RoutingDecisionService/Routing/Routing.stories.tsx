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

import { ContextExpressionDefinition, DmnBuiltInDataType } from "../../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { beeGwtService, BoxedExpressionEditorWrapper, pmmlDocuments } from "../../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../../src/expressions";
import { loanOriginationsDataTypes } from "../../boxedExpressionEditorBase";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "../../../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Routing Decision Service/Routing",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

const routingExpression: ContextExpressionDefinition = {
  __$$element: "context",
  "@_id": "_336E0FC5-5548-4720-A6B2-A8FA4834DB7B",
  "@_label": "Routing",
  "@_typeRef": "t.Routing",
  contextEntry: [
    {
      variable: {
        "@_id": "_6626CBDF-A422-42A1-89B3-4491EB069A3E",
        "@_name": "Bankrupt",
        "@_typeRef": DmnBuiltInDataType.Boolean,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_DC0A178E-45E0-4E9C-9C77-87E9DB05B196",
        "@_label": "Bankrupt",
        "@_typeRef": DmnBuiltInDataType.Boolean,
        text: { __$$text: "Bureau data.Bankrupt" },
      },
    },
    {
      variable: {
        "@_id": "_66A38FAC-3D28-4590-AAE8-A0EB17191B5A",
        "@_name": "Credit Score",
        "@_typeRef": DmnBuiltInDataType.Number,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_0763ECC7-68A4-4150-A958-A84C3B70EE56",
        "@_label": "Credit Score",
        "@_typeRef": DmnBuiltInDataType.Number,
        text: { __$$text: "Bureau data.CreditScore" },
      },
    },
    {
      expression: {
        __$$element: "decisionTable",
        "@_id": "_1FB1191F-F05C-4609-8BA4-77BE0D25570D",
        "@_label": "Result Expression",
        "@_hitPolicy": "PRIORITY",
        annotation: [{ "@_name": "annotation-1" }],
        input: [
          {
            "@_id": "_DAD2F39A-E70B-4AE5-8AFF-89461DC1246E",
            "@_label": "Bankrupt",
            inputExpression: {
              "@_id": "_7B05037C-CEE6-4251-966F-A12400E33A2D",
              "@_typeRef": DmnBuiltInDataType.Boolean,
            },
          },
          {
            "@_id": "_11B12DF2-1166-4F75-8E74-7A5EC2DCB24F",
            "@_label": "Credit Score",
            inputExpression: {
              "@_id": "_0CD5692B-310A-49B5-AD80-53FF445DCBD8",
              "@_typeRef": DmnBuiltInDataType.Number,
            },
          },
          {
            "@_id": "_4FBA1BD8-2EB8-4C93-A5C0-5063D7FE1C6D",
            "@_label": "Post-bureau affordability",
            inputExpression: {
              "@_id": "_FC11FEC8-9198-4A4D-8652-33DB6B60E56D",
              "@_typeRef": DmnBuiltInDataType.Boolean,
            },
          },
          {
            "@_id": "_1143D53B-53A1-47FE-8E2F-5E8CBFABEF9E",
            "@_label": "Post-bureau risk category",
            inputExpression: {
              "@_id": "_0359F8EA-EB69-4F48-96D6-8448D9A928C2",
              "@_typeRef": "t.BureauRiskCategory",
            },
          },
        ],
        output: [
          {
            "@_id": "_3E6080BE-560E-4D10-9B15-4CDE50627749",
            "@_label": "Result Expression",
          },
        ],
        rule: [
          {
            "@_id": "_807692BE-29F0-4FF5-85CE-F32BAA613216",
            inputEntry: [
              { "@_id": "_521759CE-947B-4DC1-8F7C-21508F1A4A15", text: { __$$text: "true" } },
              { "@_id": "_B323C3DE-F58D-456A-AE86-58E979DFBC07", text: { __$$text: "-" } },
              { "@_id": "_CB9CCA3E-C3AD-460B-A898-54C423226C87", text: { __$$text: "-" } },
              { "@_id": "_F26D7141-9758-4096-9B86-FB1545055523", text: { __$$text: "-" } },
            ],
            outputEntry: [
              {
                "@_id": "_66C3085A-7EC0-443E-B1CB-1F3FAB3D240F",
                text: { __$$text: '"Decline"' },
              },
            ],
          },
          {
            "@_id": "_B56C2B9A-61A7-4762-89C2-88BD9BEB387D",
            inputEntry: [
              { "@_id": "_80FB1716-503F-4B83-A2A2-DD1F8C250574", text: { __$$text: "-" } },
              { "@_id": "_8A63F79D-F52D-4B0C-81BE-177AC488C09F", text: { __$$text: "<580" } },
              { "@_id": "_08792B6C-04BD-41A9-8784-9DFA89A8BA81", text: { __$$text: "-" } },
              { "@_id": "_07089FD8-1B7D-4F2A-9963-8FC5C2B0588B", text: { __$$text: "-" } },
            ],
            outputEntry: [{ "@_id": "_53568F74-F05A-43C1-84A9-D1CE5F40473F", text: { __$$text: '"Refer"' } }],
          },
          {
            "@_id": "_4102196B-2434-4E5C-8AC9-73501A46B760",
            inputEntry: [
              { "@_id": "_03ADA3B8-F155-4D1D-9FE4-9D5ED5B12977", text: { __$$text: "-" } },
              { "@_id": "_E07A0915-AE0F-4A5D-A24E-13910F369F6E", text: { __$$text: "-" } },
              { "@_id": "_C6B292E3-EA38-4F89-B286-5B8830E9A44B", text: { __$$text: "false" } },
              { "@_id": "_3D3438BF-FE19-4174-BA25-6B4E39EF2F6D", text: { __$$text: "-" } },
            ],
            outputEntry: [
              {
                "@_id": "_F596DFA3-7E4B-4AE1-B5BB-8B971C3F5C42",
                text: { __$$text: '"Decline"' },
              },
            ],
          },
          {
            "@_id": "_F6FA9690-C7D7-445A-A4FE-B10360323C5E",
            inputEntry: [
              { "@_id": "_813F10D9-0895-4838-8325-35BEB094F05F", text: { __$$text: "-" } },
              { "@_id": "_AD55A511-0A95-4756-AD68-339D0A6DE566", text: { __$$text: "-" } },
              { "@_id": "_8CD726E7-9529-4D8A-86E3-C6F1FDC4A8C1", text: { __$$text: "-" } },
              { "@_id": "_5BD21A51-7E0C-4F58-B79C-DA8088748ACC", text: { __$$text: '"High"' } },
            ],
            outputEntry: [{ "@_id": "_51F45047-851A-4AC1-BA70-FE8A3FE57AEC", text: { __$$text: '"Refer"' } }],
          },
          {
            "@_id": "_04D4AAF3-8C71-4677-AAE7-E0AF7D16C587",
            inputEntry: [
              { "@_id": "_199CE8D5-1006-4178-8D44-CD7AD3A699FC", text: { __$$text: "-" } },
              { "@_id": "_F2E21BA9-BE4A-4E30-AAB6-9E0918984A6E", text: { __$$text: "-" } },
              { "@_id": "_EF0FEAC4-2E9B-49E7-9D96-24E2635831D1", text: { __$$text: "-" } },
              { "@_id": "_422E0283-8F0A-4F3F-9846-A1F4983B69EF", text: { __$$text: "-" } },
            ],
            outputEntry: [{ "@_id": "_E0E1708F-3F89-493A-8443-85A7853D7B4D", text: { __$$text: '"Accept"' } }],
          },
        ],
      },
    },
  ],
};

export const routingWidthsById = new Map<string, number[]>([
  ["_336E0FC5-5548-4720-A6B2-A8FA4834DB7B", [172, 811]],
  ["_DC0A178E-45E0-4E9C-9C77-87E9DB05B196", [811]],
  ["_0763ECC7-68A4-4150-A958-A84C3B70EE56", [811]],
  ["_1FB1191F-F05C-4609-8BA4-77BE0D25570D", [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, 100, 100, 172, 172, 123, 104]],
]);

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: routingExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: routingWidthsById,
  },
};
