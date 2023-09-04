import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";
import { BoxedExpressionEditorWrapper } from "../../boxedExpressionWrapper";
import { Base as EmptyExpression } from "../../misc/Empty/EmptyExpression.stories";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType, generateUuid } from "../../../src/api";
import { CONTEXT_ENTRY_INFO_MIN_WIDTH } from "../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Context",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Base: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Expression Name",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Context,
      entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
      result: {
        logicType: ExpressionDefinitionLogicType.Undefined,
        dataType: DmnBuiltInDataType.Undefined,
        id: generateUuid(),
      },
      contextEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: "ContextEntry-1",
            dataType: DmnBuiltInDataType.Undefined,
          },
          entryExpression: {
            id: generateUuid(),
            name: "ContextEntry-1",
            dataType: DmnBuiltInDataType.Undefined,
            logicType: ExpressionDefinitionLogicType.Undefined,
          },
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};

export const InstallmentCalculation: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Installment calculation",
      dataType: DmnBuiltInDataType.Number,
      logicType: ExpressionDefinitionLogicType.Context,
      contextEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: "Fee",
            dataType: DmnBuiltInDataType.Number,
          },
          entryExpression: {
            id: generateUuid(),
            name: "Fee",
            dataType: DmnBuiltInDataType.Number,
            logicType: ExpressionDefinitionLogicType.Literal,
            content: "25",
          },
        },
        {
          entryInfo: {
            id: generateUuid(),
            name: "Repayments",
            dataType: DmnBuiltInDataType.Number,
          },
          entryExpression: {
            id: generateUuid(),
            name: "Repayments",
            dataType: DmnBuiltInDataType.Number,
            logicType: ExpressionDefinitionLogicType.Literal,
            content: `(Amount*Rate/12) /\n(1-(1+Rate/12)**-Term)`,
          },
        },
      ],
      result: {
        id: generateUuid(),
        name: "Result Expression",
        dataType: DmnBuiltInDataType.Number,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Fee + Repayments",
      },
    },
    isResetSupportedOnRootExpression: false,
  },
};

export const Customer: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Customer",
      dataType: "tCustomer" as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.Context,
      contextEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: "Name",
            dataType: DmnBuiltInDataType.Number,
          },
          entryExpression: {
            id: generateUuid(),
            name: "Name",
            dataType: DmnBuiltInDataType.Number,
            logicType: ExpressionDefinitionLogicType.Literal,
            content: "Luiz",
          },
        },
        {
          entryInfo: {
            id: generateUuid(),
            name: "Age",
            dataType: DmnBuiltInDataType.Number,
          },
          entryExpression: {
            id: generateUuid(),
            name: "Age",
            dataType: DmnBuiltInDataType.Number,
            logicType: ExpressionDefinitionLogicType.Literal,
            content: "30",
          },
        },
      ],
      result: {
        logicType: ExpressionDefinitionLogicType.Undefined,
        dataType: DmnBuiltInDataType.Undefined,
        id: generateUuid(),
      },
    },
    isResetSupportedOnRootExpression: false,
  },
};

export const Nested: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Expression Name",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.List,
      items: [
        {
          id: generateUuid(),
          name: "Expression Name",
          dataType: DmnBuiltInDataType.Undefined,
          logicType: ExpressionDefinitionLogicType.Context,
          entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
          result: {
            logicType: ExpressionDefinitionLogicType.Undefined,
            dataType: DmnBuiltInDataType.Undefined,
            id: generateUuid(),
          },
          contextEntries: [
            {
              entryInfo: {
                id: generateUuid(),
                name: "ContextEntry-1",
                dataType: DmnBuiltInDataType.Undefined,
              },
              entryExpression: {
                id: generateUuid(),
                name: "ContextEntry-1",
                dataType: DmnBuiltInDataType.Undefined,
                logicType: ExpressionDefinitionLogicType.Undefined,
              },
            },
          ],
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};
