import {
  ExpressionDefinitionLogicType,
  ExpressionDefinition,
  LiteralExpressionDefinition,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  generateUuid,
  ContextExpressionDefinition,
  DmnBuiltInDataType,
  ListExpressionDefinition,
  InvocationExpressionDefinition,
  RelationExpressionDefinition,
  DecisionTableExpressionDefinition,
  DecisionTableExpressionDefinitionHitPolicy,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  ExpressionDefinitionBase,
} from "../api";
import {
  LITERAL_EXPRESSION_MIN_WIDTH,
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
  RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
  DECISION_TABLE_INPUT_DEFAULT_WIDTH,
  DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
  DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
} from "../resizing/WidthValues";
import { DECISION_TABLE_INPUT_DEFAULT_VALUE, DECISION_TABLE_OUTPUT_DEFAULT_VALUE } from "./DecisionTableExpression";
import {
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_LOGIC_TYPE,
} from "./InvocationExpression";
import { RELATION_EXPRESSION_DEFAULT_VALUE } from "./RelationExpression";

export function getDefaultExpressionDefinitionByLogicType(
  logicType: ExpressionDefinitionLogicType,
  prev: ExpressionDefinitionBase
): ExpressionDefinition {
  if (logicType === ExpressionDefinitionLogicType.Literal) {
    const literalExpression: LiteralExpressionDefinition = {
      ...prev,
      logicType,
      width: LITERAL_EXPRESSION_MIN_WIDTH,
    };
    return literalExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Function) {
    const functionExpression: FunctionExpressionDefinition = {
      ...prev,
      logicType,
      functionKind: FunctionExpressionDefinitionKind.Feel,
      formalParameters: [],
      expression: {
        id: generateUuid(),
        logicType: ExpressionDefinitionLogicType.Undefined,
        dataType: DmnBuiltInDataType.Undefined,
      },
    };
    return functionExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Context) {
    const contextExpression: ContextExpressionDefinition = {
      ...prev,
      logicType,
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
            name: "ContextEntry-1",
            dataType: DmnBuiltInDataType.Undefined,
            logicType: ExpressionDefinitionLogicType.Undefined,
          },
          nameAndDataTypeSynchronized: true,
        },
        {
          entryInfo: {
            id: generateUuid(),
            name: "ContextEntry-2",
            dataType: DmnBuiltInDataType.Undefined,
          },
          entryExpression: {
            id: generateUuid(),
            name: "ContextEntry-2",
            dataType: DmnBuiltInDataType.Undefined,
            logicType: ExpressionDefinitionLogicType.Undefined,
          },
          nameAndDataTypeSynchronized: true,
        },
      ],
    };
    return contextExpression;
  } else if (logicType === ExpressionDefinitionLogicType.List) {
    const listExpression: ListExpressionDefinition = {
      ...prev,
      logicType,
      items: [
        {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Undefined,
          dataType: DmnBuiltInDataType.Undefined,
        },
        {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Undefined,
          dataType: DmnBuiltInDataType.Undefined,
        },
        {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Undefined,
          dataType: DmnBuiltInDataType.Undefined,
        },
      ],
    };
    return listExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Invocation) {
    const invocationExpression: InvocationExpressionDefinition = {
      ...prev,
      logicType,
      entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
      bindingEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
            dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
          },
          entryExpression: {
            id: generateUuid(),
            name: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
            dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
            logicType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_LOGIC_TYPE,
          },
          nameAndDataTypeSynchronized: true,
        },
      ],
    };
    return invocationExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Relation) {
    const relationExpression: RelationExpressionDefinition = {
      ...prev,
      logicType,
      columns: [
        {
          id: generateUuid(),
          name: "column-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
        },
        {
          id: generateUuid(),
          name: "column-2",
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
        },
        {
          id: generateUuid(),
          name: "column-3",
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
        },
      ],
      rows: [
        {
          id: generateUuid(),
          cells: [
            RELATION_EXPRESSION_DEFAULT_VALUE,
            RELATION_EXPRESSION_DEFAULT_VALUE,
            RELATION_EXPRESSION_DEFAULT_VALUE,
            RELATION_EXPRESSION_DEFAULT_VALUE,
          ],
        },
      ],
    };
    return relationExpression;
  } else if (logicType === ExpressionDefinitionLogicType.DecisionTable) {
    const decisionTableExpression: DecisionTableExpressionDefinition = {
      ...prev,
      logicType,
      hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
      aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
      input: [
        {
          id: generateUuid(),
          name: "input-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: DECISION_TABLE_INPUT_DEFAULT_WIDTH,
        },
        {
          id: generateUuid(),
          name: "input-2",
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
        {
          id: generateUuid(),
          name: "output-2",
          dataType: DmnBuiltInDataType.Undefined,
          width: DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
        },
        {
          id: generateUuid(),
          name: "output-3",
          dataType: DmnBuiltInDataType.Undefined,
          width: DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
        },
      ],
      annotations: [
        {
          id: generateUuid(),
          name: "annotation-1",
          width: DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
        },
      ],
      rules: [
        {
          id: generateUuid(),
          inputEntries: [DECISION_TABLE_INPUT_DEFAULT_VALUE, DECISION_TABLE_INPUT_DEFAULT_VALUE],
          outputEntries: [
            DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
            DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
            DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
          ],
          annotationEntries: ["// Your annotations here"],
        },
      ],
    };
    return decisionTableExpression;
  } else {
    throw new Error(`No default expression available for ${logicType}`);
  }
}
