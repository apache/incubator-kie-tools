import type { StoryObj } from "@storybook/react";
import { BoxedExpressionEditorProps } from "../../../src/expressions";
import { BoxedExpressionEditorWrapper, pmmlParams, beeGwtService, dataTypes } from "../../boxedExpressionWrapper";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType } from "../../../src/api";

export const loanOriginationsDataTypes = [
  ...dataTypes,
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
  { typeRef: "tRisk", name: "tRisk", isCustom: true },
];

type Story = StoryObj<BoxedExpressionEditorProps>;

export const BaseStory: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    dataTypes: loanOriginationsDataTypes,
    pmmlParams,
    beeGwtService,
    expressionDefinition: {
      id: "",
      name: "",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Undefined,
    },
  },
};
