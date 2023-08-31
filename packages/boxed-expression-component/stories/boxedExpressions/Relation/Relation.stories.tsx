import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";
import { BoxedExpressionEditorWrapper } from "../../boxedExpressionWrapper";
import { Base as EmptyExpression } from "../../misc/Empty/EmptyExpression.stories";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType, generateUuid } from "../../../src/api";
import { RELATION_EXPRESSION_DEFAULT_VALUE } from "../../../src/expressions/RelationExpression";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Relation",
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
      logicType: ExpressionDefinitionLogicType.Relation,
      columns: [
        {
          id: generateUuid(),
          name: "column-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: 100,
        },
      ],
      rows: [
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: RELATION_EXPRESSION_DEFAULT_VALUE,
            },
          ],
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};

export const BiggerRelation: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...Base.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Example",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Relation,
      columns: [
        {
          id: generateUuid(),
          name: "column-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: 100,
        },
        {
          id: generateUuid(),
          name: "column-2",
          dataType: DmnBuiltInDataType.Undefined,
          width: 100,
        },
        {
          id: generateUuid(),
          name: "column-3",
          dataType: DmnBuiltInDataType.Undefined,
          width: 100,
        },
      ],
      rows: [
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
          ],
        },
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
          ],
        },
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
          ],
        },
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
          ],
        },
      ],
    },
  },
};

export const NestedRelation: Story = {
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
          logicType: ExpressionDefinitionLogicType.Relation,
          dataType: DmnBuiltInDataType.Undefined,
          columns: [
            {
              id: generateUuid(),
              name: "column-1",
              dataType: DmnBuiltInDataType.Undefined,
              width: 100,
            },
          ],
          rows: [
            {
              id: generateUuid(),
              cells: [
                {
                  id: generateUuid(),
                  content: RELATION_EXPRESSION_DEFAULT_VALUE,
                },
              ],
            },
          ],
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};
