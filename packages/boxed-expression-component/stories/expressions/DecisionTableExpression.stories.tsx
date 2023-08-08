import * as React from "react";
import { useRef } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import {
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  generateUuid,
} from "../../src/api";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../src/expressions/";
import {
  DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
  DECISION_TABLE_INPUT_DEFAULT_WIDTH,
  DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
} from "../../src/resizing/WidthConstants";
import {
  DECISION_TABLE_INPUT_DEFAULT_VALUE,
  DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
} from "../../src/expressions/DecisionTableExpression";

const defaultProps: Partial<BoxedExpressionEditorProps> = {
  decisionNodeId: "nodeId",
  expressionDefinition: {
    id: generateUuid(),
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
  dataTypes: [],
  setExpressionDefinition: () => {},
};

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<typeof BoxedExpressionEditor> = {
  component: BoxedExpressionEditor,
};

export default meta;
type Story = StoryObj<typeof BoxedExpressionEditor>;

function BoxedExpressionEditorWrapper(props: BoxedExpressionEditorProps) {
  const ref = useRef(null);

  return (
    <div ref={ref}>
      <BoxedExpressionEditor {...props} scrollableParentRef={ref} />
    </div>
  );
}

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Default: Story = {
  render: (args) => BoxedExpressionEditorWrapper(args),
  args: {
    ...defaultProps,
  },
};
