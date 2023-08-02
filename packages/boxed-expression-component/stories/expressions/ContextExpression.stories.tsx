import * as React from "react";
import { useRef } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType, generateUuid } from "../../src/api";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../src/expressions";

const defaultProps: Partial<BoxedExpressionEditorProps> = {
  decisionNodeId: generateUuid(),
  expressionDefinition: {
    id: generateUuid(),
    dataType: DmnBuiltInDataType.Any,
    logicType: ExpressionDefinitionLogicType.Context,
    contextEntries: [
      {
        entryInfo: {
          id: generateUuid(),
          name: "ContextEntry-1",
          dataType: DmnBuiltInDataType.Undefined,
        },
        entryExpression: {
          id: generateUuid(),
          name: "ContextEntry-1",
          dataType: DmnBuiltInDataType.Undefined,
          logicType: ExpressionDefinitionLogicType.Undefined,
        },
      },
    ],
    result: {
      id: generateUuid(),
      dataType: DmnBuiltInDataType.Any,
      logicType: ExpressionDefinitionLogicType.Undefined,
    },
  },
  dataTypes: [],
  setExpressionDefinition: () => {},
};

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<typeof BoxedExpressionEditor> = {
  title: "Components/Expressions/Context Expression",
  component: BoxedExpressionEditor,
  parameters: {
    // Optional parameter to center the component in the Canvas. More info: https://storybook.js.org/docs/react/configure/story-layout
    layout: "center",
  },
  // This component will have an automatically generated Autodocs entry: https://storybook.js.org/docs/react/writing-docs/autodocs
  tags: ["autodocs"],
  // More on argTypes: https://storybook.js.org/docs/react/api/argtypes
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
