import * as React from "react";
import { useState, useEffect } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import {
  ContextExpressionDefinition,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  generateUuid,
} from "../../src/api";
import { ContextExpression } from "../../src/expressions/ContextExpression/ContextExpression";
import { BoxedExpressionEditor } from "../../src/expressions";
import { beeGwtService, dataTypes, pmmlParams } from "../../.storybook/preview";

const contextExpressionDefinition: ContextExpressionDefinition = {
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
const meta: Meta<ContextExpressionDefinition & { isNested: boolean }> = {
  component: ContextExpression,
};
export default meta;
type Story = StoryObj<ContextExpressionDefinition>;

function ContextExpressionWrapper(args: ContextExpressionDefinition) {
  const emptyRef = React.useRef<HTMLElement>(null);
  const [expression, setExpression] = useState<ExpressionDefinition>(contextExpressionDefinition);

  return (
    <BoxedExpressionEditor
      decisionNodeId="_00000000-0000-0000-0000-000000000000"
      expressionDefinition={expression}
      setExpressionDefinition={setExpression}
      dataTypes={dataTypes}
      scrollableParentRef={emptyRef}
      beeGwtService={beeGwtService}
      pmmlParams={pmmlParams}
    />
  );
}

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Default: Story = {
  render: (args) => {
    return ContextExpressionWrapper(args);
  },
  args: {
    ...contextExpressionDefinition,
  },
};
