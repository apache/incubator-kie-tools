import * as React from "react";
import { useRef } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import {
  ContextExpressionDefinition,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  generateUuid,
} from "../../api";
import { ContextExpression } from "./ContextExpression";

const defaultProps: ContextExpressionDefinition = {
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
};

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<ContextExpressionDefinition> = {
  title: "Boxed Expression/Expressions/Context Expression",
  component: ContextExpression,
  parameters: {
    layout: "center",
  },
};

export default meta;
type Story = StoryObj<ContextExpressionDefinition>;
function ContextExpressionWrapper(props: ContextExpressionDefinition) {
  const ref = useRef(null);

  return (
    <div ref={ref}>
      <ContextExpression {...props} isNested={false} />
    </div>
  );
}

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Default: Story = {
  render: (args) => ContextExpressionWrapper(args),
  args: {
    ...defaultProps,
  },
};
