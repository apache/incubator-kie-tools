import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";
import { BoxedExpressionEditorWrapper } from "../../boxedExpressionStoriesWrapper";
import { Base as EmptyExpression } from "../../misc/Empty/EmptyExpression.stories";
import {
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  generateUuid,
} from "../../../src/api";
import {
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
  DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
  DECISION_TABLE_INPUT_DEFAULT_WIDTH,
  DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
} from "../../../src/resizing/WidthConstants";
import {
  DECISION_TABLE_INPUT_DEFAULT_VALUE,
  DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
} from "../../../src/expressions/DecisionTableExpression";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Decision Table",
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

export const Discount: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Discount",
      dataType: DmnBuiltInDataType.Number,
      logicType: ExpressionDefinitionLogicType.DecisionTable,
      hitPolicy: DecisionTableExpressionDefinitionHitPolicy.First,
      aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
      input: [
        {
          id: generateUuid(),
          idLiteralExpression: generateUuid(),
          name: "Customer.Category",
          dataType: "tCategory" as DmnBuiltInDataType,
          width: 120,
        },
      ],
      output: [
        {
          id: generateUuid(),
          name: "Discount",
          dataType: DmnBuiltInDataType.Number,
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
          inputEntries: [{ id: generateUuid(), content: `"New Client"` }],
          outputEntries: [{ id: generateUuid(), content: "0.15" }],
          annotationEntries: [""],
        },
        {
          id: generateUuid(),
          inputEntries: [{ id: generateUuid(), content: `"Promo"` }],
          outputEntries: [{ id: generateUuid(), content: "0.25" }],
          annotationEntries: [""],
        },
        {
          id: generateUuid(),
          inputEntries: [{ id: generateUuid(), content: `"Student"` }],
          outputEntries: [{ id: generateUuid(), content: "0.30" }],
          annotationEntries: [""],
        },
        {
          id: generateUuid(),
          inputEntries: [{ id: generateUuid(), content: `"Default"` }],
          outputEntries: [{ id: generateUuid(), content: "0" }],
          annotationEntries: [""],
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
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};
