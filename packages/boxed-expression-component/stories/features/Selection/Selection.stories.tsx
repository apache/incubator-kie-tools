import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";
import * as Empty from "../../misc/Empty/EmptyExpression.stories";
import * as Literal from "../../boxedExpressions/Literal/Literal.stories";
import * as Relation from "../../boxedExpressions/Relation/Relation.stories";
import * as DecisionTable from "../../boxedExpressions/DecisionTable/DecisionTable.stories";
import { DecisionTableExpressionDefinition, generateUuid } from "../../../src/api";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Features/Selection",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const CanDrive: Story = {
  ...Literal.CanDrive,
  args: {
    ...Literal.CanDrive.args,
    isResetSupportedOnRootExpression: true,
  },
};

export const People: Story = {
  ...Relation.People,
  args: {
    ...Relation.People.args,
    isResetSupportedOnRootExpression: true,
  },
};

export const EmptyExpression: Story = {
  ...Empty.Base,
  args: {
    ...Empty.Base.args,
    isResetSupportedOnRootExpression: true,
  },
};

export const Discount: Story = {
  ...DecisionTable.Discount,
  args: {
    ...DecisionTable.Discount.args!,
    expressionDefinition: {
      ...(DecisionTable.Discount.args!.expressionDefinition as DecisionTableExpressionDefinition),
      rules: [
        {
          id: generateUuid(),
          inputEntries: [{ id: generateUuid(), content: "-" }],
          outputEntries: [{ id: generateUuid(), content: "" }],
          annotationEntries: [""],
        },
        {
          id: generateUuid(),
          inputEntries: [{ id: generateUuid(), content: "-" }],
          outputEntries: [{ id: generateUuid(), content: "" }],
          annotationEntries: [""],
        },
        {
          id: generateUuid(),
          inputEntries: [{ id: generateUuid(), content: "-" }],
          outputEntries: [{ id: generateUuid(), content: "" }],
          annotationEntries: [""],
        },
        {
          id: generateUuid(),
          inputEntries: [{ id: generateUuid(), content: "-" }],
          outputEntries: [{ id: generateUuid(), content: "" }],
          annotationEntries: [""],
        },
      ],
    },
  },
};
