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
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/BoxedExpressionEditor";
import * as Literal from "../../boxedExpressions/Literal/Literal.stories";
import * as Context from "../../boxedExpressions/Context/Context.stories";
import { DmnBuiltInDataType, BoxedExpression, BoxedFunctionKind, BoxedLiteral } from "../../../src/api";
import { BoxedExpressionEditorStoryArgs } from "../../boxedExpressionStoriesWrapper";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Features/Resizing",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorStoryArgs>;

export const CanDrive: Story = {
  ...Literal.CanDrive,
  args: {
    ...Literal.CanDrive.args,
    expression: {
      ...(Literal.CanDrive.args!.expression! as BoxedLiteral),
    },
  },
};

const expression: BoxedExpression = {
  "@_id": "_577B0672-0DCE-48E2-A387-A06D89770346",
  "@_typeRef": DmnBuiltInDataType.Boolean,
  __$$element: "context",
  contextEntry: [
    {
      variable: {
        "@_id": "_04EAD539-9830-42CF-BECC-F718D2929F16",
        "@_name": "Affordability Calculation",
        "@_typeRef": DmnBuiltInDataType.Boolean,
      },
      expression: {
        "@_id": "_2BD8EAFA-9673-46C4-96D3-A81EE65B077E",
        "@_typeRef": DmnBuiltInDataType.Boolean,
        __$$element: "functionDefinition",
        formalParameter: [
          {
            "@_id": "_4133B011-411A-47CD-A1A4-BFC37BF379E8",
            "@_name": "Monthly Income",
            "@_typeRef": DmnBuiltInDataType.Number,
          },
          {
            "@_id": "_EA8C61D5-EEBD-4D51-AE09-95414645CB49",
            "@_name": "Monthly Repayments",
            "@_typeRef": DmnBuiltInDataType.Number,
          },
          {
            "@_id": "_59C5BA4A-A41C-4AAF-A224-1D023C211E5F",
            "@_name": "Monthly Expenses",
            "@_typeRef": DmnBuiltInDataType.Number,
          },
          {
            "@_id": "_23721BE2-AE12-4802-BBAC-135E40D650EA",
            "@_name": "Risk Category",
            "@_typeRef": "t.BureauRiskCategory" as DmnBuiltInDataType,
          },
          {
            "@_id": "_F79FEF3C-9436-4C01-81F3-7F8F325316A9",
            "@_name": "Required Monthly Installment",
            "@_typeRef": DmnBuiltInDataType.Number,
          },
        ],
        "@_kind": BoxedFunctionKind.Feel,
        expression: {
          "@_id": "_C1F325BF-D812-4192-AA90-B820C892EA9A",
          "@_typeRef": DmnBuiltInDataType.Undefined,
          __$$element: "context",
          contextEntry: [
            {
              variable: {
                "@_id": "_D1B671D5-DA59-4292-B407-A200CC5716B1",
                "@_name": "Disposable Income",
                "@_typeRef": DmnBuiltInDataType.Number,
              },
              expression: {
                "@_id": "_245FAB0B-6267-4F89-9884-144B838F8F5E",
                "@_typeRef": DmnBuiltInDataType.Number,
                __$$element: "literalExpression",
                text: { __$$text: "Monthly Income - (Monthly Repayments + Monthly Expenses)" },
              },
            },
            {
              variable: {
                "@_id": "_9FDEECB8-92EB-41B1-B44A-A93105BF6181",
                "@_name": "Credit Contigency Factor",
                "@_typeRef": DmnBuiltInDataType.Number,
              },
              expression: {
                "@_id": "_0CF76402-465B-4ED8-889A-30ABE403E893",
                "@_typeRef": DmnBuiltInDataType.Number,
                __$$element: "context",
                contextEntry: [
                  {
                    variable: {
                      "@_id": "_893A101E-970A-406F-81B3-64CDF93E143F",
                      "@_name": "Risk Category",
                      "@_typeRef": "t.BureauRiskCategory" as DmnBuiltInDataType,
                    },
                    expression: {
                      "@_id": "_38E84892-D4A0-4415-9D32-4FC432B78918",
                      "@_typeRef": "t.BureauRiskCategory" as DmnBuiltInDataType,
                      __$$element: "literalExpression",
                      text: { __$$text: "Risk Category" },
                    },
                  },
                  {
                    expression: {
                      "@_id": "_B7FA5008-05B1-4A5D-AAF5-EA3447C6307B",
                      __$$element: "decisionTable",
                      "@_hitPolicy": "UNIQUE",
                      "@_aggregation": undefined,
                      input: [
                        {
                          "@_id": "_BB1D28F4-7C5D-481C-9CC2-D71F001FACA0",
                          inputExpression: {
                            "@_id": "_0BDDD624-2B0C-4E90-8B90-5C43DA87E5F8",
                            text: { __$$text: "Risk Category" },
                            "@_typeRef": "t.BureauRiskCategory" as DmnBuiltInDataType,
                          },
                        },
                      ],
                      output: [
                        {
                          "@_id": "_64AA2820-EC4F-4A5B-9045-A474983CC86E",
                          "@_name": "Result Expression",
                          "@_typeRef": DmnBuiltInDataType.Undefined,
                        },
                      ],
                      annotation: [
                        {
                          "@_name": "Annotations",
                        },
                      ],
                      rule: [
                        {
                          "@_id": "_7FA41F37-BC03-49C7-AD96-8C03B84FE5D7",
                          inputEntry: [
                            {
                              "@_id": "_67FB4348-7CDE-4644-9B79-EA57B62BAAE5",
                              text: { __$$text: '"High", "Decline"' },
                            },
                          ],
                          outputEntry: [
                            {
                              "@_id": "_70EEBEE9-E97C-4839-88C2-DDC63C02B1C5",
                              text: { __$$text: "0.6" },
                            },
                          ],
                          annotationEntry: [{ text: { __$$text: "" } }],
                        },
                        {
                          "@_id": "_E42706FD-AFD2-4ED6-A6BD-CCEE5B07D065",
                          inputEntry: [
                            {
                              "@_id": "_D6B0C1E2-7323-43F9-98BF-DBDA0527CEF3",
                              text: { __$$text: '"Medium"' },
                            },
                          ],
                          outputEntry: [
                            {
                              "@_id": "_5A3C4E6F-0D45-4559-A1D1-8109780BB38E",
                              text: { __$$text: "0.7" },
                            },
                          ],
                          annotationEntry: [{ text: { __$$text: "" } }],
                        },
                        {
                          "@_id": "_C143C39F-1774-42EF-A967-5FAF087F2355",
                          inputEntry: [
                            {
                              "@_id": "_245CF205-9B3A-45F3-A23A-64B9CB87DF0A",
                              text: { __$$text: '"Low", "Very Low"' },
                            },
                          ],
                          outputEntry: [
                            {
                              "@_id": "_987D475A-186D-4B42-A544-F86B4D5D224A",
                              text: { __$$text: "0.8" },
                            },
                          ],
                          annotationEntry: [{ text: { __$$text: "" } }],
                        },
                      ],
                    },
                  },
                ],
              },
            },
            {
              variable: {
                "@_id": "_F7311902-5700-4EB3-AA36-ADAFBB33752D",
                "@_name": "Affordability",
                "@_typeRef": DmnBuiltInDataType.Boolean,
              },
              expression: {
                "@_id": "_8571615A-3490-4341-A8A8-D934C0526104",
                "@_typeRef": DmnBuiltInDataType.Boolean,
                __$$element: "literalExpression",
                text: {
                  __$$text:
                    "if Disposable Income * Credit Contigency Factor > Required Monthly Installment\nthen true\nelse false",
                },
              },
            },
            {
              expression: {
                "@_id": "_002EF0D0-D8AE-4086-82FC-526E9B5028CA",
                __$$element: "literalExpression",
                text: { __$$text: "Affordability" },
              },
            },
          ],
        },
      },
    },
    {
      expression: {
        __$$element: "invocation",
        "@_id": "_DC4F55F0-5650-427A-B0F6-6ED93E73E66F",
        expression: {
          __$$element: "literalExpression",
          "@_id": "_9EADCC8B-C721-42AF-8784-71BF2C5B689E",
          text: { __$$text: "Affordability Calculation" },
        },
        binding: [
          {
            parameter: {
              "@_id": "_9333CA95-F4BE-479A-8B44-6D4284B5766F",
              "@_name": "Monthly Income",
              "@_typeRef": DmnBuiltInDataType.Number,
            },
            expression: {
              "@_id": "_702B8DAF-9061-47FF-92F5-5A908824B248",
              "@_typeRef": DmnBuiltInDataType.Number,
              __$$element: "literalExpression",
              text: { __$$text: "Applicant data.Monthly.Income" },
            },
          },
          {
            parameter: {
              "@_id": "_0F890E57-BF62-49FC-A199-11651222AAFA",
              "@_name": "Monthly Repayments",
              "@_typeRef": DmnBuiltInDataType.Number,
            },
            expression: {
              "@_id": "_63F6C8EF-42D7-4B2A-8D1E-C9C32E460F0C",
              "@_typeRef": DmnBuiltInDataType.Number,
              __$$element: "literalExpression",
              text: { __$$text: "Applicant data.Monthly.Repayments" },
            },
          },
          {
            parameter: {
              "@_id": "_3D98BA98-4C77-46AE-9366-32863BA0D497",
              "@_name": "Monthly Expenses",
              "@_typeRef": DmnBuiltInDataType.Number,
            },
            expression: {
              "@_id": "_217A6034-0485-47DA-B982-75C8FF0180D9",
              "@_typeRef": DmnBuiltInDataType.Number,
              __$$element: "literalExpression",
              text: { __$$text: "Applicant data.Monthly.Expenses" },
            },
          },
          {
            parameter: {
              "@_id": "_D9DA7EBE-7CD0-4D20-BDE0-61FFFB6AD2F1",
              "@_name": "Risk Category",
              "@_typeRef": "t.BureauRiskCategory" as DmnBuiltInDataType,
            },
            expression: {
              "@_id": "_A9926A1D-805B-4C64-BDB6-FFB01A9D3D60",
              "@_typeRef": "t.BureauRiskCategory" as DmnBuiltInDataType,
              __$$element: "literalExpression",
              text: { __$$text: "Post-bureau risk category" },
            },
          },
          {
            parameter: {
              "@_id": "_D3DE1FD3-B767-494B-A3D8-9EDB6392D47F",
              "@_name": "Required Monthly Installment",
              "@_typeRef": DmnBuiltInDataType.Number,
            },
            expression: {
              "@_id": "_9F2CF7EA-885B-4545-9E16-55E354B122A8",
              "@_typeRef": DmnBuiltInDataType.Number,
              __$$element: "literalExpression",
              text: { __$$text: "Required monthly installment" },
            },
          },
        ],
      },
    },
  ],
};

export const Nested: Story = {
  ...Context.Nested,
  args: {
    ...Context.Nested.args,
    expression,
    widthsById: {}, //FIXME: Tiago
  },
};
