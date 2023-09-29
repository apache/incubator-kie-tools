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
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";
import * as Literal from "../../boxedExpressions/Literal/Literal.stories";
import * as Context from "../../boxedExpressions/Context/Context.stories";
import {
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
  LiteralExpressionDefinition,
} from "../../../src/api";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Features/Resizing",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const CanDrive: Story = {
  ...Literal.CanDrive,
  args: {
    ...Literal.CanDrive.args,
    expressionDefinition: {
      ...(Literal.CanDrive.args!.expressionDefinition! as LiteralExpressionDefinition),
      width: 500,
    },
  },
};

export const Nested: Story = {
  ...Context.Nested,
  args: {
    ...Context.Nested.args,
    expressionDefinition: {
      id: "_577B0672-0DCE-48E2-A387-A06D89770346",
      name: "Post-bureau affordabillity",
      dataType: DmnBuiltInDataType.Boolean,
      logicType: ExpressionDefinitionLogicType.Context,
      contextEntries: [
        {
          entryInfo: {
            id: "_04EAD539-9830-42CF-BECC-F718D2929F16",
            name: "Affordability Calculation",
            dataType: DmnBuiltInDataType.Boolean,
          },
          entryExpression: {
            id: "_2BD8EAFA-9673-46C4-96D3-A81EE65B077E",
            name: "Affordability Calculation",
            dataType: DmnBuiltInDataType.Boolean,
            logicType: ExpressionDefinitionLogicType.Function,
            formalParameters: [
              {
                id: "_4133B011-411A-47CD-A1A4-BFC37BF379E8",
                name: "Monthly Income",
                dataType: DmnBuiltInDataType.Number,
              },
              {
                id: "_EA8C61D5-EEBD-4D51-AE09-95414645CB49",
                name: "Monthly Repayments",
                dataType: DmnBuiltInDataType.Number,
              },
              {
                id: "_59C5BA4A-A41C-4AAF-A224-1D023C211E5F",
                name: "Monthly Expenses",
                dataType: DmnBuiltInDataType.Number,
              },
              {
                id: "_23721BE2-AE12-4802-BBAC-135E40D650EA",
                name: "Risk Category",
                dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
              },
              {
                id: "_F79FEF3C-9436-4C01-81F3-7F8F325316A9",
                name: "Required Monthly Installment",
                dataType: DmnBuiltInDataType.Number,
              },
            ],
            functionKind: FunctionExpressionDefinitionKind.Feel,
            expression: {
              id: "_C1F325BF-D812-4192-AA90-B820C892EA9A",
              name: "Feel Expression",
              dataType: DmnBuiltInDataType.Undefined,
              logicType: ExpressionDefinitionLogicType.Context,
              contextEntries: [
                {
                  entryInfo: {
                    id: "_D1B671D5-DA59-4292-B407-A200CC5716B1",
                    name: "Disposable Income",
                    dataType: DmnBuiltInDataType.Number,
                  },
                  entryExpression: {
                    id: "_245FAB0B-6267-4F89-9884-144B838F8F5E",
                    name: "Disposable Income",
                    dataType: DmnBuiltInDataType.Number,
                    logicType: ExpressionDefinitionLogicType.Literal,
                    content: "Monthly Income - (Monthly Repayments + Monthly Expenses)",
                    width: 547,
                  },
                },
                {
                  entryInfo: {
                    id: "_9FDEECB8-92EB-41B1-B44A-A93105BF6181",
                    name: "Credit Contigency Factor",
                    dataType: DmnBuiltInDataType.Number,
                  },
                  entryExpression: {
                    id: "_0CF76402-465B-4ED8-889A-30ABE403E893",
                    name: "Credit Contigency Factor",
                    dataType: DmnBuiltInDataType.Number,
                    logicType: ExpressionDefinitionLogicType.Context,
                    contextEntries: [
                      {
                        entryInfo: {
                          id: "_893A101E-970A-406F-81B3-64CDF93E143F",
                          name: "Risk Category",
                          dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
                        },
                        entryExpression: {
                          id: "_38E84892-D4A0-4415-9D32-4FC432B78918",
                          name: "Risk Category",
                          dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
                          logicType: ExpressionDefinitionLogicType.Literal,
                          content: "Risk Category",
                          width: 425,
                        },
                      },
                    ],
                    result: {
                      id: "_650908EF-5842-4098-9CB0-E6BD69EFDE52",
                      name: "Result Expression",
                      dataType: DmnBuiltInDataType.Undefined,
                      logicType: ExpressionDefinitionLogicType.DecisionTable,
                      hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
                      aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
                      annotations: [{ name: "annotation-1", width: 107 }],
                      input: [
                        {
                          id: "_BB1D28F4-7C5D-481C-9CC2-D71F001FACA0",
                          name: "Risk Category",
                          dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
                          width: 155,
                          idLiteralExpression: "_0BDDD624-2B0C-4E90-8B90-5C43DA87E5F8",
                        },
                      ],
                      output: [
                        {
                          id: "_64AA2820-EC4F-4A5B-9045-A474983CC86E",
                          name: "Result Expression",
                          dataType: DmnBuiltInDataType.Undefined,
                          width: 123,
                        },
                      ],
                      rules: [
                        {
                          id: "_7FA41F37-BC03-49C7-AD96-8C03B84FE5D7",
                          inputEntries: [
                            {
                              id: "_67FB4348-7CDE-4644-9B79-EA57B62BAAE5",
                              content: '"High", "Decline"',
                            },
                          ],
                          outputEntries: [
                            {
                              id: "_70EEBEE9-E97C-4839-88C2-DDC63C02B1C5",
                              content: "0.6",
                            },
                          ],
                          annotationEntries: [""],
                        },
                        {
                          id: "_E42706FD-AFD2-4ED6-A6BD-CCEE5B07D065",
                          inputEntries: [
                            {
                              id: "_D6B0C1E2-7323-43F9-98BF-DBDA0527CEF3",
                              content: '"Medium"',
                            },
                          ],
                          outputEntries: [
                            {
                              id: "_5A3C4E6F-0D45-4559-A1D1-8109780BB38E",
                              content: "0.7",
                            },
                          ],
                          annotationEntries: [""],
                        },
                        {
                          id: "_C143C39F-1774-42EF-A967-5FAF087F2355",
                          inputEntries: [
                            {
                              id: "_245CF205-9B3A-45F3-A23A-64B9CB87DF0A",
                              content: '"Low", "Very Low"',
                            },
                          ],
                          outputEntries: [
                            {
                              id: "_987D475A-186D-4B42-A544-F86B4D5D224A",
                              content: "0.8",
                            },
                          ],
                          annotationEntries: [""],
                        },
                      ],
                    },
                    entryInfoWidth: 120,
                  },
                },
                {
                  entryInfo: {
                    id: "_F7311902-5700-4EB3-AA36-ADAFBB33752D",
                    name: "Affordability",
                    dataType: DmnBuiltInDataType.Boolean,
                  },
                  entryExpression: {
                    id: "_8571615A-3490-4341-A8A8-D934C0526104",
                    name: "Affordability",
                    dataType: DmnBuiltInDataType.Boolean,
                    logicType: ExpressionDefinitionLogicType.Literal,
                    content:
                      "if Disposable Income * Credit Contigency Factor > Required Monthly Installment\nthen true\nelse false",
                    width: 547,
                  },
                },
              ],
              result: {
                id: "_A83FF43F-CB4E-4C7F-A121-D7067A3D490A",
                name: "Result Expression",
                dataType: DmnBuiltInDataType.Undefined,
                logicType: ExpressionDefinitionLogicType.Literal,
                content: "Affordability",
                width: 547,
              },
              entryInfoWidth: 169,
            },
          },
        },
      ],
      result: {
        id: "_591A3EA4-C90E-4B58-9E66-039817DA39CA",
        name: "Result Expression",
        dataType: DmnBuiltInDataType.Undefined,
        logicType: ExpressionDefinitionLogicType.Invocation,
        invokedFunction: { id: "_9EADCC8B-C721-42AF-8784-71BF2C5B689E", name: "Affordability Calculation" },
        bindingEntries: [
          {
            entryInfo: {
              id: "_9333CA95-F4BE-479A-8B44-6D4284B5766F",
              name: "Monthly Income",
              dataType: DmnBuiltInDataType.Number,
            },
            entryExpression: {
              id: "_702B8DAF-9061-47FF-92F5-5A908824B248",
              name: "Monthly Income",
              dataType: DmnBuiltInDataType.Number,
              logicType: ExpressionDefinitionLogicType.Literal,
              content: "Applicant data.Monthly.Income",
              width: 598,
            },
          },
          {
            entryInfo: {
              id: "_0F890E57-BF62-49FC-A199-11651222AAFA",
              name: "Monthly Repayments",
              dataType: DmnBuiltInDataType.Number,
            },
            entryExpression: {
              id: "_63F6C8EF-42D7-4B2A-8D1E-C9C32E460F0C",
              name: "Monthly Repayments",
              dataType: DmnBuiltInDataType.Number,
              logicType: ExpressionDefinitionLogicType.Literal,
              content: "Applicant data.Monthly.Repayments",
              width: 598,
            },
          },
          {
            entryInfo: {
              id: "_3D98BA98-4C77-46AE-9366-32863BA0D497",
              name: "Monthly Expenses",
              dataType: DmnBuiltInDataType.Number,
            },
            entryExpression: {
              id: "_217A6034-0485-47DA-B982-75C8FF0180D9",
              name: "Monthly Expenses",
              dataType: DmnBuiltInDataType.Number,
              logicType: ExpressionDefinitionLogicType.Literal,
              content: "Applicant data.Monthly.Expenses",
              width: 598,
            },
          },
          {
            entryInfo: {
              id: "_D9DA7EBE-7CD0-4D20-BDE0-61FFFB6AD2F1",
              name: "Risk Category",
              dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
            },
            entryExpression: {
              id: "_A9926A1D-805B-4C64-BDB6-FFB01A9D3D60",
              name: "Risk Category",
              dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
              logicType: ExpressionDefinitionLogicType.Literal,
              content: "Post-bureau risk category",
              width: 598,
            },
          },
          {
            entryInfo: {
              id: "_D3DE1FD3-B767-494B-A3D8-9EDB6392D47F",
              name: "Required Monthly Installment",
              dataType: DmnBuiltInDataType.Number,
            },
            entryExpression: {
              id: "_9F2CF7EA-885B-4545-9E16-55E354B122A8",
              name: "Required Monthly Installment",
              dataType: DmnBuiltInDataType.Number,
              logicType: ExpressionDefinitionLogicType.Literal,
              content: "Required monthly installment",
              width: 598,
            },
          },
        ],
        entryInfoWidth: 180,
      },
      entryInfoWidth: 173,
    },
  },
};
