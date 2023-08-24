import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../src/expressions";
import { BoxedExpressionEditorWrapper, dataTypes } from "../boxedExpressionWrapper";
import { Base as EmptyExpression } from "./EmptyExpression.stories";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType, generateUuid } from "../../src/api";
import { RELATION_EXPRESSION_DEFAULT_VALUE } from "../../src/expressions/RelationExpression";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Expressions/Relation Expression",
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

export const CustomerProfiles: Story = {
  render: () => {
    const dataTypes = Base.args?.dataTypes;
    dataTypes?.push({ typeRef: "tCustomer", name: "tCustomer", isCustom: true });
    return BoxedExpressionEditorWrapper({ dataTypes });
  },
  args: {
    ...Base.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Employee Information",
      dataType: "tCustomer" as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.Relation,
      columns: [
        {
          id: generateUuid(),
          name: "Name",
          dataType: DmnBuiltInDataType.String,
          width: 100,
        },
        {
          id: generateUuid(),
          name: "Age",
          dataType: DmnBuiltInDataType.Number,
          width: 100,
        },
        {
          id: generateUuid(),
          name: "Salary",
          dataType: DmnBuiltInDataType.Number,
          width: 100,
        },
      ],
      rows: [
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: "John",
            },
            {
              id: generateUuid(),
              content: "32",
            },
            {
              id: generateUuid(),
              content: "100000",
            },
          ],
        },
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: "Mary",
            },
            {
              id: generateUuid(),
              content: "42",
            },
            {
              id: generateUuid(),
              content: "120000",
            },
          ],
        },
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: "Joseph",
            },
            {
              id: generateUuid(),
              content: "30",
            },
            {
              id: generateUuid(),
              content: "110000",
            },
          ],
        },
      ],
    },
  },
};
