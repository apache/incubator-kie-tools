import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../src/expressions";
import { BoxedExpressionEditorWrapper, beeGwtService, dataTypes, pmmlParams } from "../boxedExpressionWrapper";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType, generateUuid } from "../../src/api";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Empty Boxed Expression",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
  excludeStories: ["BoxedExpressionEditorWrapper"],
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Base: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: {
      id: generateUuid(),
      name: "Expression Name",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Undefined,
    },
    dataTypes,
    beeGwtService,
    pmmlParams,
  },
};
