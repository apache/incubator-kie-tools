import * as React from "react";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType, InvocationExpressionDefinition } from "../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditorWrapper, beeGwtService, pmmlParams } from "../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../src/expressions";
import { loanOriginationsDataTypes } from "../boxedExpressionEditorBase";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Required monthly installment",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const requiredMonthlyInstallmentExpression: InvocationExpressionDefinition = {
  id: "_EF51A747-D5E1-414E-9DD5-964362FB2AEC",
  name: "Required monthly installment",
  dataType: DmnBuiltInDataType.Number,
  logicType: ExpressionDefinitionLogicType.Invocation,
  invokedFunction: { id: "_99D8F2CD-01CF-499B-9D51-410C59865F15", name: "f.Installment calculation" },
  bindingEntries: [
    {
      entryInfo: {
        id: "_189F1672-7BED-47F9-884F-6A1FB31DFE34",
        name: "Product Type",
        dataType: "t.ProductType" as DmnBuiltInDataType,
      },
      entryExpression: {
        id: "_EE98C97E-4CFE-463D-A500-B8D6D7FC69BD",
        name: "Product Type",
        dataType: "t.ProductType" as DmnBuiltInDataType,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Requested product.ProductType",
        width: 250,
      },
    },
    {
      entryInfo: { id: "_38FC5DFF-390B-42FE-A29D-35EF44EC6BB8", name: "Rate", dataType: DmnBuiltInDataType.Number },
      entryExpression: {
        id: "_25149F92-AA1B-45F0-92F6-8B478803E61C",
        name: "Rate",
        dataType: DmnBuiltInDataType.Number,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Requested product.Rate",
        width: 250,
      },
    },
    {
      entryInfo: { id: "_3C364710-888A-455C-B70C-6DE20914A6F4", name: "Term", dataType: DmnBuiltInDataType.Number },
      entryExpression: {
        id: "_A245D6C9-1A2D-48E1-B001-B7CA9318A82F",
        name: "Term",
        dataType: DmnBuiltInDataType.Number,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Requested product.Term",
        width: 250,
      },
    },
    {
      entryInfo: { id: "_DCCB50E0-CB3D-483D-9D13-3BD221A236E3", name: "Amount", dataType: DmnBuiltInDataType.Number },
      entryExpression: {
        id: "_CCBA629B-04C7-4B45-9FBF-AF9AED2FE10D",
        name: "Amount",
        dataType: DmnBuiltInDataType.Number,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Requested product.Amount",
        width: 250,
      },
    },
  ],
  entryInfoWidth: 120,
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: requiredMonthlyInstallmentExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlParams,
    isResetSupportedOnRootExpression: false,
  },
};
