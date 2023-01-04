import {
  ExpressionDefinition,
  generateUuid,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
  DmnBuiltInDataType,
  PmmlLiteralExpressionDefinitionKind,
} from "../../api";
import { BoxedExpressionEditorI18n } from "../../i18n";

export const pmmlContextExpression = (
  prev: ExpressionDefinition,
  i18n: BoxedExpressionEditorI18n
): ExpressionDefinition => {
  const id = generateUuid();

  if (
    !(
      prev.logicType === ExpressionDefinitionLogicType.Function &&
      prev.functionKind === FunctionExpressionDefinitionKind.Pmml
    )
  ) {
    return {
      logicType: ExpressionDefinitionLogicType.Undefined,
      id,
      dataType: DmnBuiltInDataType.Undefined,
    };
  }

  return {
    id,
    logicType: ExpressionDefinitionLogicType.Context,
    dataType: DmnBuiltInDataType.Undefined,
    renderResult: false,
    result: {
      id: `${id}-result`,
      logicType: ExpressionDefinitionLogicType.Undefined,
      dataType: DmnBuiltInDataType.Undefined,
    },
    contextEntries: [
      {
        entryInfo: {
          id: prev.documentFieldId ?? `${id}-document`,
          name: i18n.document,
          dataType: DmnBuiltInDataType.String,
        },
        entryExpression: {
          dataType: DmnBuiltInDataType.Undefined,
          id: prev.documentFieldId ?? `${id}-document`,
          logicType: ExpressionDefinitionLogicType.PmmlLiteral,
          testId: "pmml-selector-document",
          noOptionsLabel: i18n.pmml.firstSelection,
          kind: PmmlLiteralExpressionDefinitionKind.Document,
          selected: prev.document ?? "",
          isHeadless: true,
        },
      },
      {
        entryInfo: {
          id: prev.modelFieldId ?? `${id}-model`,
          name: i18n.model,
          dataType: DmnBuiltInDataType.String,
        },
        entryExpression: {
          id: prev.modelFieldId ?? `${id}-model`,
          logicType: ExpressionDefinitionLogicType.PmmlLiteral,
          dataType: DmnBuiltInDataType.Undefined,
          noOptionsLabel: i18n.pmml.secondSelection,
          testId: "pmml-selector-model",
          kind: PmmlLiteralExpressionDefinitionKind.Model,
          selected: prev.model ?? "",
          isHeadless: true,
        },
      },
    ],
    isHeadless: true,
  };
};
