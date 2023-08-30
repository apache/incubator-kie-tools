import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";

const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Can Drive",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
  excludeStories: ["BoxedExpressionEditorWrapper"],
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const Base: Story = {};
