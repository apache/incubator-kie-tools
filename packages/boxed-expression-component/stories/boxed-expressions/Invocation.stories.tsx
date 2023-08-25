import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../src/expressions";
import { BoxedExpressionEditorWrapper } from "../boxedExpressionWrapper";
import { Base as EmptyExpression } from "../use-cases/EmptyExpression.stories";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType, generateUuid } from "../../src/api";
import { CONTEXT_ENTRY_INFO_MIN_WIDTH } from "../../src/resizing/WidthConstants";
import {
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_LOGIC_TYPE,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
} from "../../src/expressions/InvocationExpression";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Invocation",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
  excludeStories: ["BoxedExpressionEditorWrapper"],
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
      logicType: ExpressionDefinitionLogicType.Invocation,
      entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
      bindingEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
            dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
          },
          entryExpression: {
            id: generateUuid(),
            name: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
            dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
            logicType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_LOGIC_TYPE,
          },
        },
      ],
      invokedFunction: {
        id: generateUuid(),
        name: "FUNCTION",
      },
    },
    isResetSupportedOnRootExpression: false,
  },
};
