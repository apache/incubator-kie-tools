import * as React from "react";
import { useRef } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType, generateUuid } from "../../src/api";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../src/expressions/";

const defaultProps: Partial<BoxedExpressionEditorProps> = {
  decisionNodeId: "nodeId",
  expressionDefinition: {
    id: generateUuid(),
    dataType: DmnBuiltInDataType.Any,
    logicType: ExpressionDefinitionLogicType.Literal,
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
