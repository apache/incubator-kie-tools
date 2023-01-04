import {
  ExpressionDefinition,
  generateUuid,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
  DmnBuiltInDataType,
} from "../../api";
import { BoxedExpressionEditorI18n } from "../../i18n";
import { LITERAL_EXPRESSION_MIN_WIDTH } from "../../resizing/WidthValues";

export const javaContextExpression = (
  prev: ExpressionDefinition,
  i18n: BoxedExpressionEditorI18n
): ExpressionDefinition => {
  const id = generateUuid();
  if (
    !(
      prev.logicType === ExpressionDefinitionLogicType.Function &&
      prev.functionKind === FunctionExpressionDefinitionKind.Java
    )
  ) {
    return {
      id,
      logicType: ExpressionDefinitionLogicType.Undefined,
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
          id: prev.classFieldId ?? `${id}-classFieldId`,
          name: i18n.class,
          dataType: DmnBuiltInDataType.String,
        },
        entryExpression: {
          id: prev.classFieldId ?? `${id}-classFieldId`,
          logicType: ExpressionDefinitionLogicType.Literal,
          dataType: DmnBuiltInDataType.Undefined,
          width: LITERAL_EXPRESSION_MIN_WIDTH,
          content: prev.className ?? "",
        },
      },
      {
        entryInfo: {
          id: prev.methodFieldId ?? `${id}-methodFieldId`,
          name: i18n.methodSignature,
          dataType: DmnBuiltInDataType.String,
        },
        entryExpression: {
          id: prev.methodFieldId ?? `${id}-methodFieldId`,
          logicType: ExpressionDefinitionLogicType.Literal,
          dataType: DmnBuiltInDataType.Undefined,
          content: prev.methodName ?? "",
          width: LITERAL_EXPRESSION_MIN_WIDTH,
        },
      },
    ],
  };
};
