import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../src/expressions";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType } from "../../../../src/api";
import { BaseStory } from "../storiesBase";

const meta: Meta<BoxedExpressionEditorProps> = {
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
  excludeStories: ["BoxedExpressionEditorWrapper"],
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const Base: Story = {
  ...BaseStory,
  args: {
    ...BaseStory.args,
    expressionDefinition: {
      id: "",
      name: "",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Undefined,
    },
  },
};
