import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";
import { BoxedExpressionEditorWrapper } from "../../boxedExpressionWrapper";
import { Base as EmptyExpression } from "../../misc/Empty/EmptyExpression.stories";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType, generateUuid } from "../../../src/api";
import { CONTEXT_ENTRY_INFO_MIN_WIDTH } from "../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/List",
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
      logicType: ExpressionDefinitionLogicType.List,
      items: [
        {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Undefined,
          dataType: DmnBuiltInDataType.Undefined,
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};

export const AgeGroups: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Age group",
      dataType: DmnBuiltInDataType.Number,
      logicType: ExpressionDefinitionLogicType.List,
      items: [
        {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Literal,
          dataType: DmnBuiltInDataType.Undefined,
          content: `[18..30)`,
        },
        {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Literal,
          dataType: DmnBuiltInDataType.Undefined,
          content: `[30..50)`,
        },
        {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Literal,
          dataType: DmnBuiltInDataType.Undefined,
          content: `[50..70)`,
        },
        {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Literal,
          dataType: DmnBuiltInDataType.Undefined,
          content: `>= 70`,
        },
      ],
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
          logicType: ExpressionDefinitionLogicType.Literal,
          dataType: DmnBuiltInDataType.Undefined,
        },
        {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.List,
          dataType: DmnBuiltInDataType.Undefined,
          items: [
            {
              id: generateUuid(),
              logicType: ExpressionDefinitionLogicType.Literal,
              dataType: DmnBuiltInDataType.Undefined,
            },
          ],
        },
        {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Context,
          dataType: DmnBuiltInDataType.Undefined,
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
