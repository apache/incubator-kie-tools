import * as React from "react";
import {
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
} from "../../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditorWrapper, beeGwtService, pmmlParams } from "../../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../../src/expressions";
import { loanOriginationsDataTypes } from "../../boxedExpressionEditorBase";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Functions/Affordability Calculation",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const affordabilityCalculationExpression: FunctionExpressionDefinition = {
  id: "_72FC1D95-7AB8-4459-9815-D2EC186DD40A",
  name: "Affordability calculation",
  dataType: DmnBuiltInDataType.Boolean,
  logicType: ExpressionDefinitionLogicType.Function,
  formalParameters: [
    { id: "_FCF7D50D-CE15-4F25-92A0-CCD1A4D8FE39", name: "Monthly Income", dataType: DmnBuiltInDataType.Number },
    { id: "_2E6DEF97-E9F0-4FFC-AD59-55BFFB4237BF", name: "Monthly Repayments", dataType: DmnBuiltInDataType.Number },
    { id: "_30C11588-0245-42E2-BAA5-4E50DB935D46", name: "Monthly Expenses", dataType: DmnBuiltInDataType.Number },
    {
      id: "_ABAA1C74-7B19-497D-879F-492EB9F015FB",
      name: "Risk Category",
      dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
    },
    {
      id: "_1D44F721-DB97-421B-B9FA-AB5A5246FF97",
      name: "Required Monthly Installment",
      dataType: DmnBuiltInDataType.Number,
    },
  ],
  functionKind: FunctionExpressionDefinitionKind.Feel,
  expression: {
    id: "_C46876F8-8596-4064-9291-AE059DE73F84",
    name: "Feel Expression",
    dataType: DmnBuiltInDataType.Undefined,
    logicType: ExpressionDefinitionLogicType.Context,
    contextEntries: [
      {
        entryInfo: {
          id: "_AC69745A-2636-4D69-9402-5B5DBBEB789F",
          name: "Disposable Income",
          dataType: DmnBuiltInDataType.Number,
        },
        entryExpression: {
          id: "_4D8F65CA-89A3-449B-830F-1241E838A428",
          name: "Disposable Income",
          dataType: DmnBuiltInDataType.Number,
          logicType: ExpressionDefinitionLogicType.Literal,
          content: "Monthly Income - (Monthly Repayments + Monthly Expenses)",
          width: 636,
        },
      },
      {
        entryInfo: {
          id: "_D7DDD7CE-91AD-451D-8F23-1F38C336B4F4",
          name: "Credit Contigency Factor",
          dataType: DmnBuiltInDataType.Number,
        },
        entryExpression: {
          id: "_AC320F29-8043-4A9E-9A5A-7E07C873084F",
          name: "Result Expression",
          dataType: DmnBuiltInDataType.Undefined,
          logicType: ExpressionDefinitionLogicType.DecisionTable,
          hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
          aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
          annotations: [{ name: "annotation-1", width: 126 }],
          input: [
            {
              id: "_7283628F-FDB6-4DEC-95D4-B7678B86DCAA",
              name: "Risk Category",
              dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
              width: 194,
              idLiteralExpression: "_0BDDD624-2B0C-4E90-8B90-5C43DA87E5F8",
            },
          ],
          output: [
            {
              id: "_58BFBB2D-C78F-4D6A-AA3E-26D1A2ABA7EA",
              name: "Result Expression",
              dataType: DmnBuiltInDataType.Undefined,
              width: 154,
            },
          ],
          rules: [
            {
              id: "_52890D5C-85F5-422B-B771-54CD0FAD4A40",
              inputEntries: [
                {
                  id: "_53A9B5E0-6856-4D6F-BD72-59C3466D361B",
                  content: '"High", "Decline"',
                },
              ],
              outputEntries: [
                {
                  id: "_0F5156D3-8A67-49DD-95FC-EEECE54075C5",
                  content: "0.6",
                },
              ],
              annotationEntries: [""],
            },
            {
              id: "_64813FBD-87E7-4A67-AF78-DAAC2086C3DF",
              inputEntries: [
                {
                  id: "_2FBB1F6F-0DBA-4F5D-B0B2-983D72C624EE",
                  content: '"Medium"',
                },
              ],
              outputEntries: [
                {
                  id: "_EB8648E9-C865-4048-BF9A-944571BD8D37",
                  content: "0.7",
                },
              ],
              annotationEntries: [""],
            },
            {
              id: "_AEF602C0-ABC0-4553-A792-5C2140E6F07E",
              inputEntries: [
                {
                  id: "_DA84E1D2-B550-4C6A-A4E4-49B832F378BF",
                  content: '"Low", "Very Low"',
                },
              ],
              outputEntries: [
                {
                  id: "_59478925-F88C-4DC5-81E6-70D70815C949",
                  content: "0.8",
                },
              ],
              annotationEntries: [""],
            },
          ],
        },
      },
    ],
    result: {
      id: "_B325C569-762E-408B-AAB5-D55AF7FF01C4",
      name: "Result Expression",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Literal,
      content: "if Disposable Income * Credit Contigency Factor > Required Monthly Installment\nthen true\nelse false",
      width: 636,
    },
    entryInfoWidth: 169,
  },
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: affordabilityCalculationExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlParams,
    isResetSupportedOnRootExpression: false,
  },
};
