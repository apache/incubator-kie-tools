import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../src/expressions";
import { BoxedExpressionEditorWrapper } from "../boxedExpressionWrapper";
import { Base as EmptyExpression } from "../use-cases/EmptyExpression.stories";
import {
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  generateUuid,
} from "../../src/api";
import {
  DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
  DECISION_TABLE_INPUT_DEFAULT_WIDTH,
  DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
} from "../../src/resizing/WidthConstants";
import {
  DECISION_TABLE_INPUT_DEFAULT_VALUE,
  DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
} from "../../src/expressions/DecisionTableExpression";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Decision Table",
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
      logicType: ExpressionDefinitionLogicType.DecisionTable,
      hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
      aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
      input: [
        {
          id: generateUuid(),
          idLiteralExpression: generateUuid(),
          name: "input-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: DECISION_TABLE_INPUT_DEFAULT_WIDTH,
        },
      ],
      output: [
        {
          id: generateUuid(),
          name: "output-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
        },
      ],
      annotations: [
        {
          name: "annotation-1",
          width: DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
        },
      ],
      rules: [
        {
          id: generateUuid(),
          inputEntries: [{ id: generateUuid(), content: DECISION_TABLE_INPUT_DEFAULT_VALUE }],
          outputEntries: [{ id: generateUuid(), content: DECISION_TABLE_OUTPUT_DEFAULT_VALUE }],
          annotationEntries: ["// Your annotations here"],
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};
