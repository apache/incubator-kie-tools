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

import {
  DmnBuiltInDataType,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
} from "../../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { beeGwtService, BoxedExpressionEditorWrapper, pmmlDocuments } from "../../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../../src/expressions";
import { loanOriginationsDataTypes } from "../../boxedExpressionEditorBase";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "../../../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Functions/Affordability Calculation",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const affordabilityCalculationExpression: FunctionExpressionDefinition = {
  __$$element: "functionDefinition",
  "@_id": "_72FC1D95-7AB8-4459-9815-D2EC186DD40A",
  "@_label": "Affordability calculation",
  "@_typeRef": DmnBuiltInDataType.Boolean,
  formalParameter: [
    {
      "@_id": "_FCF7D50D-CE15-4F25-92A0-CCD1A4D8FE39",
      "@_name": "Monthly Income",
      "@_typeRef": DmnBuiltInDataType.Number,
    },
    {
      "@_id": "_2E6DEF97-E9F0-4FFC-AD59-55BFFB4237BF",
      "@_name": "Monthly Repayments",
      "@_typeRef": DmnBuiltInDataType.Number,
    },
    {
      "@_id": "_30C11588-0245-42E2-BAA5-4E50DB935D46",
      "@_name": "Monthly Expenses",
      "@_typeRef": DmnBuiltInDataType.Number,
    },
    {
      "@_id": "_ABAA1C74-7B19-497D-879F-492EB9F015FB",
      "@_name": "Risk Category",
      "@_typeRef": "t.BureauRiskCategory",
    },
    {
      "@_id": "_1D44F721-DB97-421B-B9FA-AB5A5246FF97",
      "@_name": "Required Monthly Installment",
      "@_typeRef": DmnBuiltInDataType.Number,
    },
  ],
  "@_kind": FunctionExpressionDefinitionKind.Feel,
  expression: {
    __$$element: "context",
    "@_id": "_C46876F8-8596-4064-9291-AE059DE73F84",
    "@_label": "Feel Expression",
    contextEntry: [
      {
        variable: {
          "@_id": "_AC69745A-2636-4D69-9402-5B5DBBEB789F",
          "@_name": "Disposable Income",
          "@_typeRef": DmnBuiltInDataType.Number,
        },
        expression: {
          __$$element: "literalExpression",
          "@_id": "_4D8F65CA-89A3-449B-830F-1241E838A428",
          "@_label": "Disposable Income",
          "@_typeRef": DmnBuiltInDataType.Number,
          text: { __$$text: "Monthly Income - (Monthly Repayments + Monthly Expenses)" },
        },
      },
      {
        variable: {
          "@_id": "_D7DDD7CE-91AD-451D-8F23-1F38C336B4F4",
          "@_name": "Credit Contigency Factor",
          "@_typeRef": DmnBuiltInDataType.Number,
        },
        expression: {
          __$$element: "decisionTable",
          "@_id": "_AC320F29-8043-4A9E-9A5A-7E07C873084F",
          "@_label": "Result Expression",
          "@_hitPolicy": "UNIQUE",
          input: [
            {
              "@_id": "_7283628F-FDB6-4DEC-95D4-B7678B86DCAA",
              "@_label": "Risk Category",

              inputExpression: {
                "@_typeRef": "t.BureauRiskCategory",
                "@_id": "_0BDDD624-2B0C-4E90-8B90-5C43DA87E5F8",
              },
            },
          ],
          output: [
            {
              "@_id": "_58BFBB2D-C78F-4D6A-AA3E-26D1A2ABA7EA",
              "@_label": "Result Expression",
            },
          ],
          annotation: [{ "@_name": "annotation-1" }],
          rule: [
            {
              "@_id": "_52890D5C-85F5-422B-B771-54CD0FAD4A40",
              inputEntry: [
                {
                  "@_id": "_53A9B5E0-6856-4D6F-BD72-59C3466D361B",
                  text: { __$$text: '"High", "Decline"' },
                },
              ],
              outputEntry: [
                {
                  "@_id": "_0F5156D3-8A67-49DD-95FC-EEECE54075C5",
                  text: { __$$text: "0.6" },
                },
              ],
              annotationEntry: [],
            },
            {
              "@_id": "_64813FBD-87E7-4A67-AF78-DAAC2086C3DF",
              inputEntry: [
                {
                  "@_id": "_2FBB1F6F-0DBA-4F5D-B0B2-983D72C624EE",
                  text: { __$$text: '"Medium"' },
                },
              ],
              outputEntry: [
                {
                  "@_id": "_EB8648E9-C865-4048-BF9A-944571BD8D37",
                  text: { __$$text: "0.7" },
                },
              ],
              annotationEntry: [],
            },
            {
              "@_id": "_AEF602C0-ABC0-4553-A792-5C2140E6F07E",
              inputEntry: [
                {
                  "@_id": "_DA84E1D2-B550-4C6A-A4E4-49B832F378BF",
                  text: { __$$text: '"Low", "Very Low"' },
                },
              ],
              outputEntry: [
                {
                  "@_id": "_59478925-F88C-4DC5-81E6-70D70815C949",
                  text: { __$$text: "0.8" },
                },
              ],
              annotationEntry: [],
            },
          ],
        },
      },
      {
        expression: {
          __$$element: "literalExpression",
          "@_id": "_B325C569-762E-408B-AAB5-D55AF7FF01C4",
          "@_label": "Result Expression",
          text: {
            __$$text:
              "if Disposable Income * Credit Contigency Factor > Required Monthly Installment\nthen true\nelse false",
          },
        },
      },
    ],
  },
};

export const affordabilityCalculationWidthsById = new Map<string, number[]>([
  ["_72FC1D95-7AB8-4459-9815-D2EC186DD40A", []],
  ["_C46876F8-8596-4064-9291-AE059DE73F84", [169, 636]],
  ["_4D8F65CA-89A3-449B-830F-1241E838A428", [636]],
  ["_AC320F29-8043-4A9E-9A5A-7E07C873084F", [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, 194, 154, 126]],
  ["_B325C569-762E-408B-AAB5-D55AF7FF01C4", [636]],
]);

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: affordabilityCalculationExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: affordabilityCalculationWidthsById,
  },
};
